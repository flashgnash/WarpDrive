package cr0s.warpdrive.event;

import cr0s.warpdrive.BreathingManager;
import cr0s.warpdrive.Commons;
import cr0s.warpdrive.LocalProfiler;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.WarpDriveText;
import cr0s.warpdrive.block.force_field.BlockForceField;
import cr0s.warpdrive.data.CelestialObjectManager;
import cr0s.warpdrive.config.Dictionary;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.CelestialObject;
import cr0s.warpdrive.data.EnumTier;
import cr0s.warpdrive.data.OfflineAvatarManager;
import cr0s.warpdrive.data.StateAir;
import cr0s.warpdrive.data.Vector3;
import cr0s.warpdrive.data.VectorI;
import cr0s.warpdrive.item.ItemWarpArmor;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class LivingHandler {
	
	private static final HashMap<UUID, Integer> player_cloakTicks = new HashMap<>();
	private static final HashMap<UUID, Integer> player_borderBypassTicks = new HashMap<>();
	private static final HashMap<ResourceLocation, HashMap<Integer, Double>> entity_yMotionByDimensionId = new HashMap<>();
	
	private static final int CLOAK_CHECK_TIMEOUT_TICKS = 100;
	
	private static final int BORDER_WARNING_RANGE_BLOCKS_SQUARED = 20 * 20;
	private static final int BORDER_BYPASS_RANGE_BLOCKS_SQUARED = 32 * 32;
	private static final int BORDER_BYPASS_PULL_BACK_BLOCKS = 16;
	private static final int BORDER_BYPASS_DAMAGES_PER_TICK = 9000;
	private static final int BORDER_BYPASS_DELAY_TICK = 10 * 20;
	
	private static final int PURGE_PERIOD_TICKS = 6000; // 5 mn
	
	private static int tickUpdate = PURGE_PERIOD_TICKS;
	
	public static void updateTick() {
		tickUpdate--;
		if (tickUpdate < 0) {
			tickUpdate = PURGE_PERIOD_TICKS;
			LocalProfiler.start("LivingHandler cleanup");
			final Iterator<ResourceLocation> iteratorDimensionId = entity_yMotionByDimensionId.keySet().iterator();
			while (iteratorDimensionId.hasNext()) {
				final ResourceLocation dimensionId = iteratorDimensionId.next();
				final ServerWorld worldServer = Commons.getLoadedWorldServer(dimensionId);
				if (worldServer == null) {
					iteratorDimensionId.remove();
					continue;
				}
				
				final Iterator<Integer> iteratorEntity = entity_yMotionByDimensionId.get(dimensionId).keySet().iterator();
				while (iteratorEntity.hasNext()) {
					final int entityId = iteratorEntity.next();
					final Entity entity = worldServer.getEntityByID(entityId);
					if (entity == null) {
						iteratorEntity.remove();
						// continue;
					}
				}
			}
			LocalProfiler.stop(1000);
		}
	}
	
	@SubscribeEvent
	public void onLivingUpdate(@Nonnull final LivingUpdateEvent event) {
		if ( event.getEntityLiving() == null
		  || event.getEntityLiving().world.isRemote() ) {
			return;
		}
		
		final LivingEntity entityLivingBase = event.getEntityLiving();
		final int x = MathHelper.floor(entityLivingBase.getPosX());
		final int y = MathHelper.floor(entityLivingBase.getPosY());
		final int z = MathHelper.floor(entityLivingBase.getPosZ());
		
		// *** save motion for fall damage computation
		// note: dead entities don't tick. Checking health level is too taxing. Hence, cleanup is done indirectly.
		if (!entityLivingBase.onGround) {
			final HashMap<Integer, Double> entity_yMotion = entity_yMotionByDimensionId.computeIfAbsent(entityLivingBase.dimension.getRegistryName(), k -> new HashMap<>(300));
			entity_yMotion.put(entityLivingBase.getEntityId(), entityLivingBase.getMotion().y);
		}
		
		// *** world border handling
		// Instant kill if entity exceeds world's limit
		final CelestialObject celestialObject = CelestialObjectManager.get(entityLivingBase.world, x, z);
		if (celestialObject == null) {
			// unregistered dimension => exit
			return;
		}
		final double distanceSquared = celestialObject.getSquareDistanceOutsideBorder(x, z);
		if (distanceSquared <= 0.0D) {
			if (entityLivingBase instanceof PlayerEntity) {
				// are we close to the border?
				if ( Math.abs(distanceSquared) <= BORDER_WARNING_RANGE_BLOCKS_SQUARED
				  && entityLivingBase.ticksExisted % 40 == 0) {
					((PlayerEntity) entityLivingBase).sendStatusMessage(
							new WarpDriveText(Commons.getStyleWarning(), "warpdrive.world_border.in_range",
							                  (int) Math.sqrt(Math.abs(distanceSquared)) ), true );
				}
				// clear bypass counter
				player_borderBypassTicks.remove(entityLivingBase.getUniqueID());
			}
			
		} else {
			if (entityLivingBase instanceof PlayerEntity) {
				if (((PlayerEntity) entityLivingBase).abilities.disableDamage) {
					if (entityLivingBase.ticksExisted % 100 == 0) {
						((PlayerEntity) entityLivingBase).sendStatusMessage(
								new WarpDriveText(Commons.getStyleWarning(), "warpdrive.world_border.outside",
								                  (int) Math.sqrt(Math.abs(distanceSquared)) ), true );
					}
					return;
				}
			}
			
			// pull back the entity
			final double relativeX = entityLivingBase.getPosX() - celestialObject.dimensionCenterX;
			final double relativeZ = entityLivingBase.getPosZ() - celestialObject.dimensionCenterZ;
			final double newAbsoluteX = Math.min(Math.abs(relativeX), Math.max(0.0D, celestialObject.borderRadiusX - BORDER_BYPASS_PULL_BACK_BLOCKS));
			final double newAbsoluteZ = Math.min(Math.abs(relativeZ), Math.max(0.0D, celestialObject.borderRadiusZ - BORDER_BYPASS_PULL_BACK_BLOCKS));
			final double newEntityX = celestialObject.dimensionCenterX + Math.signum(relativeX) * newAbsoluteX;
			final double newEntityY = entityLivingBase.getPosY() + 0.1D;
			final double newEntityZ = celestialObject.dimensionCenterX + Math.signum(relativeZ) * newAbsoluteZ;
			// entityLivingBase.isAirBorne = true;
			Commons.moveEntity(entityLivingBase, entityLivingBase.world, new Vector3(newEntityX, newEntityY, newEntityZ));
			
			// give a survival chance for players, notably when switching dimension
			boolean isDelayed = false;
			if ( entityLivingBase instanceof PlayerEntity
			  && entityLivingBase.isAlive()
			  && entityLivingBase.deathTime <= 0 ) {
				// spam player's chat
				Commons.addChatMessage(entityLivingBase, new WarpDriveText(Commons.getStyleWarning(), "warpdrive.world_border.reached"));
				
				// check the instant death delay
				final int borderBypassTicks = player_borderBypassTicks.getOrDefault(entityLivingBase.getUniqueID(), 0);
				player_borderBypassTicks.put(entityLivingBase.getUniqueID(), borderBypassTicks + 1);
				isDelayed = borderBypassTicks < BORDER_BYPASS_DELAY_TICK;
			}
			
			// delay damage for 'fast moving' players
			if ( distanceSquared < BORDER_BYPASS_RANGE_BLOCKS_SQUARED
			  || isDelayed ) {
				// just set on fire
				entityLivingBase.setFire(1);
			} else {
				// full damage
				entityLivingBase.attackEntityFrom(DamageSource.OUT_OF_WORLD, BORDER_BYPASS_DAMAGES_PER_TICK);
				return;
			}
		}
		
		if (entityLivingBase instanceof ServerPlayerEntity) {
			// *** cloak handling
			updatePlayerCloakState(entityLivingBase);
			
			// *** air handling
			if ( WarpDriveConfig.BREATHING_AIR_AT_ENTITY_DEBUG
			  && entityLivingBase.world.getGameTime() % 20 == 0 ) {
				StateAir.dumpAroundEntity((PlayerEntity) entityLivingBase);
			}
			
			// *** offline avatar handling
			if (WarpDriveConfig.OFFLINE_AVATAR_ENABLE) {
				OfflineAvatarManager.onTick((PlayerEntity) entityLivingBase);
			}
		}
		
		// skip dead or invulnerable entities
		if ( !entityLivingBase.isAlive()
		  || entityLivingBase.isInvulnerableTo(WarpDrive.damageAsphyxia) ) {
			return;
		}
		
		// If entity is in vacuum, check and start consuming air cells
		if (!celestialObject.hasAtmosphere()) {
			// skip players in creative
			if ( !(entityLivingBase instanceof ServerPlayerEntity)
			  || !((ServerPlayerEntity) entityLivingBase).abilities.disableDamage ) {
				BreathingManager.onLivingUpdateEvent(entityLivingBase, x, y, z);
			}
		}
		
		
		// *** world transition handling
		// If entity is falling down, teleport to child celestial object or wrap around, as needed
		if (entityLivingBase.getPosY() < -10.0D) {
			// skip blacklisted entities (lookup of entities id is slow, use with care)
			if ( Dictionary.isLeftBehind(entityLivingBase)
			  || Dictionary.isAnchor(entityLivingBase) ) {
				return;
			}
			
			final CelestialObject celestialObjectChild = CelestialObjectManager.getClosestChild(entityLivingBase.world, x, z);
			// are we actually in orbit?
			assert entityLivingBase.world.getDimension().getType().getRegistryName() != null;
			if ( celestialObjectChild != null
			  && !celestialObjectChild.isVirtual()
			  && !celestialObject.isHyperspace()
			  && celestialObjectChild.isInOrbit(entityLivingBase.world.getDimension().getType().getRegistryName(), x, z) ) {
				
				final ServerWorld worldTarget = Commons.getOrCreateWorldServer(celestialObjectChild.dimensionId);
				if (worldTarget == null) {
					WarpDrive.logger.error(String.format("Unable to initialize dimension %s for %s",
					                                     celestialObjectChild.dimensionId,
					                                     entityLivingBase));
					// inform player then roll around to cooldown
					Commons.addChatMessage( entityLivingBase,
					                        new WarpDriveText(Commons.getStyleWarning(), "warpdrive.ship.guide.exception_loading_dimension",
					                                          celestialObjectChild.dimensionId ));
					entityLivingBase.setPositionAndUpdate(entityLivingBase.getPosX(), 260.0D, entityLivingBase.getPosZ());
				} else {
					final VectorI vEntry = celestialObjectChild.getEntryOffset();
					final double xTarget = entityLivingBase.getPosX() + vEntry.x;
					final double yTarget = celestialObject.isSpace() ? 1500 : worldTarget.getActualHeight() + 5;
					final double zTarget = entityLivingBase.getPosZ() + vEntry.z;
					
					// add tolerance to fall distance
					entityLivingBase.fallDistance = -5.0F;
					
					// actually move
					Commons.moveEntity(entityLivingBase, worldTarget, new Vector3(xTarget, yTarget, zTarget));
					
					// add fire if we're entering an atmosphere
					if (!celestialObject.hasAtmosphere() && celestialObjectChild.hasAtmosphere()) {
						applyAtmosphericEntryEffect(entityLivingBase);
					}
				}
				
			} else if ( celestialObject.isHyperspace()
			         || celestialObject.isSpace() ) {
				// player is in space or hyperspace, let's roll around
				entityLivingBase.setPositionAndUpdate(entityLivingBase.getPosX(), 260.0D, entityLivingBase.getPosZ());
			}
		}
	}
	
	private void applyAtmosphericEntryEffect(@Nonnull final LivingEntity entityLivingBase) {
		final Iterable<ItemStack> inventoryArmor = entityLivingBase.getArmorInventoryList();
		int countReentryArmor = 0;
		for (final ItemStack itemStack : inventoryArmor) {
			if ( itemStack.getItem() instanceof ItemWarpArmor
			  && ((ItemWarpArmor) itemStack.getItem()).getTier(itemStack).getIndex() > EnumTier.BASIC.getIndex() ) {
				countReentryArmor++;
			}
		}
		// no damage for player in full armor or entity with at least 1 element
		if ( countReentryArmor == 4
		  || ( !(entityLivingBase instanceof PlayerEntity)
		    && countReentryArmor>= 1 ) ) {
			return;
		}
		entityLivingBase.setFire(30);
	}
	
	private void updatePlayerCloakState(final LivingEntity entity) {
		try {
			final ServerPlayerEntity player = (ServerPlayerEntity) entity;
			final Integer cloakTicks = player_cloakTicks.get(player.getUniqueID());
			
			if (cloakTicks == null) {
				player_cloakTicks.put(player.getUniqueID(), 0);
				return;
			}
			
			if (cloakTicks >= CLOAK_CHECK_TIMEOUT_TICKS) {
				player_cloakTicks.put(player.getUniqueID(), 0);
				
				WarpDrive.cloaks.updatePlayer(player);
			} else {
				player_cloakTicks.put(player.getUniqueID(), cloakTicks + 1);
			}
		} catch (final Exception exception) {
			exception.printStackTrace(WarpDrive.printStreamError);
		}
	}
	
	@SubscribeEvent
	public void onLivingFall(@Nonnull final LivingFallEvent event) {
		// player in the overworld falling from
		// 3 blocks high is motionY = -0,6517088xxx
		// 4 blocks high is motionY = -0,717
		// 5 blocks high is motionY = -0.844
		// slime in the overworld falling from
		// 2.177279 blocks high is motionY -0.6517000126838683
		// spider in the overworld falling from
		// 3.346 blocks high is motionY -0.717
		
		// cancel in case of very low speed
		final LivingEntity entityLivingBase = event.getEntityLiving();
		final HashMap<Integer, Double> entity_yMotion = entity_yMotionByDimensionId.get(entityLivingBase.dimension.getRegistryName());
		Double motionY = entity_yMotion == null ? null : entity_yMotion.get(entityLivingBase.getEntityId());
		if (motionY == null) {
			motionY = entityLivingBase.getMotion().y;
		}
		if (motionY > -0.65170D) {
			event.setCanceled(true); // Don't damage entity
			if ( WarpDriveConfig.LOGGING_GRAVITY
			  && entityLivingBase instanceof ServerPlayerEntity ) {
				WarpDrive.logger.warn(String.format("(low speed     ) Entity fall damage at motionY %.3f from distance %.3f of %s, isCancelled %s",
				                                    motionY, event.getDistance(), entityLivingBase, event.isCanceled()));
			}
			return;
		}
		
		// get vanilla check for fall distance, as found in EntityLivingBase.fall()
		// we're ignoring the jump potion effect bonus
		final float distance = event.getDistance();
		final int check = MathHelper.ceil(distance - 3.0F);
		// ignore small jumps
		if (check <= 0) {
			event.setCanceled(true); // Don't damage entity
			if ( WarpDriveConfig.LOGGING_GRAVITY
			  && entityLivingBase instanceof ServerPlayerEntity ) {
				WarpDrive.logger.warn(String.format("(short distance) Entity fall damage at motionY %.3f from distance %.3f of %s, isCancelled %s",
				                                    motionY, event.getDistance(), entityLivingBase, event.isCanceled()));
			}
			return;
		}
		
		if (WarpDriveConfig.LOGGING_GRAVITY) {
			WarpDrive.logger.warn(String.format("Entity fall damage at motionY %.3f from distance %.3f of %s, isCancelled %s",
			                                    motionY, event.getDistance(), entityLivingBase, event.isCanceled()));
		}
		
		// check for equipment with NOFALLDAMAGE tag
		for (final EquipmentSlotType entityEquipmentSlot : EquipmentSlotType.values()) {
			final ItemStack itemStackInSlot = entityLivingBase.getItemStackFromSlot(entityEquipmentSlot);
			if (!itemStackInSlot.isEmpty()) {
				if (Dictionary.ITEMS_NOFALLDAMAGE.contains(itemStackInSlot.getItem())) {
					event.setCanceled(true); // Don't damage entity
					if ( WarpDrive.isDev && WarpDriveConfig.LOGGING_GRAVITY
					  && entityLivingBase instanceof ServerPlayerEntity ) {
						WarpDrive.logger.warn(String.format("(boots absorbed) Entity fall damage at motionY %.3f from distance %.3f of %s, isCancelled %s",
						                                    motionY, event.getDistance(), entityLivingBase, event.isCanceled()));
					}
				}
			}
		}
		
		// (entity has significant speed, above minimum distance and there's no absorption)
		
		// adjust distance to 'vanilla' scale and let it fall...
		event.setDistance( (float) (-6.582D + 4.148D * Math.exp(1.200D * Math.abs(motionY))) );
		if ( WarpDriveConfig.LOGGING_GRAVITY
		  && entityLivingBase instanceof ServerPlayerEntity ) {
			WarpDrive.logger.warn(String.format("(full damage   ) Entity fall damage at motionY %.3f from distance %.3f of %s, isCancelled %s",
			                                    motionY, event.getDistance(), entityLivingBase, event.isCanceled()));
		}
	}
	
	@SubscribeEvent
	public void onEnderTeleport(@Nonnull final EnderTeleportEvent event) {
		if ( event.getEntityLiving() == null
		  || event.getEntityLiving().world.isRemote() ) {
			return;
		}
		
		final World world = event.getEntityLiving().world;
		final int x = MathHelper.floor(event.getTargetX());
		final int y = MathHelper.floor(event.getTargetY());
		final int z = MathHelper.floor(event.getTargetZ());
		
		final BlockPos.Mutable mutableBlockPos = new BlockPos.Mutable(x, y, z);
		for (int xLoop = x - 1; xLoop <= x + 1; xLoop++) {
			for (int zLoop = z - 1; zLoop <= z + 1; zLoop++) {
				for (int yLoop = y - 1; yLoop <= y + 1; yLoop++) {
					if (yLoop <= 0 || yLoop > 255) {
						continue;
					}
					mutableBlockPos.setPos(xLoop, yLoop, zLoop);
					final Block block = world.getBlockState(mutableBlockPos).getBlock();
					if (block instanceof BlockForceField) {
						event.setCanceled(true);
						return;
					}
				}
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	void onLivingDeath(@Nonnull final LivingDeathEvent event) {
		final LivingEntity entityLivingBase = event.getEntityLiving();
		if (entityLivingBase != null) {
			BreathingManager.onEntityLivingDeath(entityLivingBase);
		}
	}
}
