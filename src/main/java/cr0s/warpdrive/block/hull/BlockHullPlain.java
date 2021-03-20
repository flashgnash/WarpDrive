package cr0s.warpdrive.block.hull;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IDamageReceiver;
import cr0s.warpdrive.block.BlockAbstractBase;
import cr0s.warpdrive.config.WarpDriveConfig;
import cr0s.warpdrive.data.EnumHullPlainType;
import cr0s.warpdrive.data.EnumTier;
import cr0s.warpdrive.data.Vector3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.PushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.common.ToolType;

public class BlockHullPlain extends BlockAbstractBase implements IDamageReceiver {
	
	final int               indexColor;
	final EnumHullPlainType enumHullPlainType;
	
	public BlockHullPlain(@Nonnull final String registryName, @Nonnull final EnumTier enumTier, @Nonnull final DyeColor enumDyeColor, @Nonnull final EnumHullPlainType enumHullPlainType) {
		super(getDefaultProperties(Material.ROCK, enumDyeColor.getMapColor())
				      .hardnessAndResistance(WarpDriveConfig.HULL_HARDNESS[enumTier.getIndex()], WarpDriveConfig.HULL_BLAST_RESISTANCE[enumTier.getIndex()])
				      .harvestTool(ToolType.PICKAXE)
				      .harvestLevel(WarpDriveConfig.HULL_HARVEST_LEVEL[enumTier.getIndex()])
				      .sound(SoundType.GLASS)
				      .lightValue(10),
		      registryName, enumTier);
		
		this.indexColor = enumDyeColor.getId();
		this.enumHullPlainType = enumHullPlainType;
	}
	
	@Nullable
	@Override
	public BlockItem createItemBlock() {
		return new ItemBlockHull(this);
	}
	
	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public PushReaction getPushReaction(@Nonnull final BlockState blockState) {
		return PushReaction.BLOCK;
	}
	
	@Override
	public float getBlockHardness(@Nonnull final BlockState blockState, @Nonnull final World world, @Nonnull final BlockPos blockPos,
	                              @Nonnull final DamageSource damageSource, final int damageParameter, @Nonnull final Vector3 damageDirection, final int damageLevel) {
		// TODO: adjust hardness to damage type/color
		return WarpDriveConfig.HULL_HARDNESS[enumTier.getIndex()];
	}
	
	@Override
	public int applyDamage(@Nonnull final BlockState blockState, @Nonnull final World world, @Nonnull final BlockPos blockPos,
	                       @Nonnull final DamageSource damageSource, final int damageParameter, @Nonnull final Vector3 damageDirection, final int damageLevel) {
		if (damageLevel <= 0) {
			return 0;
		}
		if (enumTier == EnumTier.BASIC) {
			world.removeBlock(blockPos, false);
		} else {
			world.setBlockState(blockPos, WarpDrive.blockHulls_plain[enumTier.getIndex() - 1][enumHullPlainType.ordinal()][indexColor]
			                              .getDefaultState(), 2);
		}
		return 0;
	}
}
