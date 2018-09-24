package cr0s.warpdrive.block.energy;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.computer.IEnanReactorLaser;
import cr0s.warpdrive.block.TileEntityAbstractLaser;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.BlockProperties;
import cr0s.warpdrive.data.EnumReactorFace;
import cr0s.warpdrive.data.Vector3;
import cr0s.warpdrive.network.PacketHandler;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;

import javax.annotation.Nonnull;

import java.lang.ref.WeakReference;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;

import net.minecraftforge.fml.common.Optional;

public class TileEntityEnanReactorLaser extends TileEntityAbstractLaser implements IEnanReactorLaser {
	
	// persistent properties
	private EnumReactorFace reactorFace = EnumReactorFace.UNKNOWN;
	private int energyStabilizationRequest = 0;
	
	// computed properties
	private Vector3 vLaser;
	private Vector3 vReactorCore;
	private WeakReference<TileEntityEnanReactorCore> weakReactorCore;
	
	public TileEntityEnanReactorLaser() {
		super();
		
		addMethods(new String[] {
				"isAssemblyValid",
				"side",
				"stabilize"
		});
		peripheralName = "warpdriveEnanReactorLaser";
		laserMedium_maxCount = 1;
		laserMedium_directionsValid = new EnumFacing[] { EnumFacing.UP, EnumFacing.DOWN };
		updateInterval_ticks = WarpDriveConfig.ENAN_REACTOR_UPDATE_INTERVAL_TICKS;
	}
	
	@Override
	protected void onFirstUpdateTick() {
		super.onFirstUpdateTick();
		
		if (reactorFace == EnumReactorFace.UNKNOWN) {
			final MutableBlockPos mutableBlockPos = new MutableBlockPos(pos);
			// laser isn't linked yet, let's try to update nearby reactors
			for (final EnumReactorFace reactorFace : EnumReactorFace.values()) {
				if (reactorFace.indexStability < 0) {
					continue;
				}
				
				mutableBlockPos.setPos(pos.getX() - reactorFace.x,
				                       pos.getY() - reactorFace.y,
				                       pos.getZ() - reactorFace.z);
				if (world.isBlockLoaded(mutableBlockPos, true)) {
					final TileEntity tileEntity = world.getTileEntity(mutableBlockPos);
					if (tileEntity instanceof TileEntityEnanReactorCore) {
						((TileEntityEnanReactorCore) tileEntity).onBlockUpdateDetected();
					}
				}
			}
		}
		
		vLaser = new Vector3(this).translate(0.5);
	}
	
	@Override
	public void update() {
		super.update();
		
		if (energyStabilizationRequest > 0) {
			doStabilize(energyStabilizationRequest);
			energyStabilizationRequest = 0;
		}
	}
	
	@Nonnull 
	public EnumReactorFace getReactorFace() {
		return reactorFace != null ? reactorFace : EnumReactorFace.UNKNOWN;
	}
	
	public void setReactorFace(@Nonnull final EnumReactorFace reactorFace, final TileEntityEnanReactorCore reactorCore) {
		this.reactorFace = reactorFace;
		this.weakReactorCore = reactorCore != null && reactorFace != EnumReactorFace.UNKNOWN ? new WeakReference<>(reactorCore) : null;
		
		// refresh blockstate
		final IBlockState blockState_old = world.getBlockState(pos);
		final IBlockState blockState_new;
		if (reactorFace.facingLaserProperty != null) {
			blockState_new = blockState_old.withProperty(BlockProperties.ACTIVE, true)
			                               .withProperty(BlockProperties.FACING, reactorFace.facingLaserProperty);
		} else {
			blockState_new = blockState_old.withProperty(BlockProperties.ACTIVE, false)
			                               .withProperty(BlockProperties.FACING, EnumFacing.DOWN);
		}
		updateBlockState(blockState_old, blockState_new);
		
		// cache reactor coordinates
		if (reactorCore != null) {
			vReactorCore = new Vector3(reactorCore).translate(0.5);
		}
	}
	
	private TileEntityEnanReactorCore getReactorCore() {
		if (reactorFace == EnumReactorFace.UNKNOWN) {
			return null;
		}
		TileEntityEnanReactorCore reactorCore = weakReactorCore != null ? weakReactorCore.get() : null;
		if (reactorCore == null) {
			final BlockPos blockPos = pos.add(- reactorFace.x, - reactorFace.y, - reactorFace.z);
			final TileEntity tileEntity = world.getTileEntity(blockPos);
			if (tileEntity instanceof TileEntityEnanReactorCore) {
				reactorCore = (TileEntityEnanReactorCore) tileEntity;
				weakReactorCore = new WeakReference<>(reactorCore);
				vReactorCore = new Vector3(reactorCore).translate(0.5);
			} else {
				WarpDrive.logger.error(String.format("%s Invalid TileEntityEnanReactorCore: %s",
				                                     this, tileEntity));
			}
		}
		return reactorCore;
	}
	
	@Override
	public void onBlockUpdateDetected() {
		super.onBlockUpdateDetected();
		
		final TileEntityEnanReactorCore reactorCore = getReactorCore();
		if (reactorCore != null) {
			reactorCore.onBlockUpdateDetected();
		}
	}
	
	boolean stabilize(final int energy) {
		if (energy <= 0) {
			return false;
		}
		
		if (laserMedium_direction == null) {
			return false;
		}
		
		energyStabilizationRequest = energy;
		return true;
	}
	
	private void doStabilize(final int energy) {
		if (energy <= 0) {
			return;
		}
		
		if (laserMedium_direction == null) {
			return;
		}
		
		final TileEntityEnanReactorCore reactorCore = getReactorCore();
		if (reactorCore == null) {
			return;
		}
		if (laserMedium_consumeExactly(energy, false)) {
			if (WarpDriveConfig.LOGGING_ENERGY && WarpDriveConfig.LOGGING_LUA) {
				WarpDrive.logger.info(String.format("ReactorLaser on %s side sending %d", reactorFace, energy));
			}
			reactorCore.decreaseInstability(reactorFace, energy);
			PacketHandler.sendBeamPacket(world, vLaser, vReactorCore, 0.1F, 0.2F, 1.0F, 25, 50, 100);
		}
	}
	
	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
		tagCompound = super.writeToNBT(tagCompound);
		tagCompound.setInteger("reactorFace", reactorFace.ordinal());
		tagCompound.setInteger("energyStabilizationRequest", energyStabilizationRequest);
		return tagCompound;
	}
	
	@Override
	public void readFromNBT(final NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		
		reactorFace = EnumReactorFace.get(tagCompound.getInteger("reactorFace"));
		energyStabilizationRequest = tagCompound.getInteger("energyStabilizationRequest");
	}
	
	
	// Common OC/CC methods
	@Override
	public Object[] isAssemblyValid() {
		if (reactorFace == EnumReactorFace.UNKNOWN) {
			return new Object[] { false, "No reactor detected" };
		}
		return super.isAssemblyValid();
	}
	
	@Override
	public Object[] stabilize(final Object[] arguments) {
		if (arguments.length != 1) {
			return new Object[] { false, "Invalid number of arguments" };
		}
		final int energy;
		try {
			energy = Commons.toInt(arguments[0]);
		} catch (final Exception exception) {
			if (WarpDriveConfig.LOGGING_LUA) {
				WarpDrive.logger.error(String.format("%s LUA error on stabilize(): Integer expected for 1st argument %s",
				                                     this, arguments[0]));
			}
			return new Object[] { false, "Invalid integer" };
		}
		return new Object[] { stabilize(energy) };
	}
	
	@Override
	public Object[] side() {
		return new Object[] { reactorFace.indexStability, reactorFace.enumTier.getName(), reactorFace.getName() };
	}
	
	// OpenComputers callback methods
	@Callback
	@Optional.Method(modid = "opencomputers")
	public Object[] stabilize(final Context context, final Arguments arguments) {
		return stabilize(OC_convertArgumentsAndLogCall(context, arguments));
	}
	
	@Callback
	@Optional.Method(modid = "opencomputers")
	public Object[] side(final Context context, final Arguments arguments) {
		return side();
	}
	
	// ComputerCraft IPeripheral methods
	@Override
	@Optional.Method(modid = "computercraft")
	public Object[] callMethod(@Nonnull final IComputerAccess computer, @Nonnull final ILuaContext context, final int method, @Nonnull final Object[] arguments) {
		final String methodName = CC_getMethodNameAndLogCall(method, arguments);
		
		switch (methodName) {
		case "stabilize":
			return stabilize(arguments);
			
		case "side":
			return side();
		}
		
		return super.callMethod(computer, context, method, arguments);
	}
}