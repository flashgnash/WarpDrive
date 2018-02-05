package cr0s.warpdrive.block.atomic;

import cr0s.warpdrive.api.IStarMapRegistryTileEntity;
import cr0s.warpdrive.block.TileEntityAbstractEnergy;
import cr0s.warpdrive.data.StarMapRegistryItem.EnumStarMapEntryType;
import cr0s.warpdrive.data.VectorI;

import java.util.UUID;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;

public class TileEntityAcceleratorController extends TileEntityAbstractEnergy implements IStarMapRegistryTileEntity {
	
	public TileEntityAcceleratorController() {
		super();
	}
	
	@Override
	public void update() {
		super.update();
	}
	
	// IStarMapRegistryTileEntity overrides
	@Override
	public String getStarMapType() {
		return EnumStarMapEntryType.ACCELERATOR.getName();
	}
	
	@Override
	public UUID getUUID() {
		return null;
	}
	
	@Override
	public AxisAlignedBB getStarMapArea() {
		return null;
	}
	
	@Override
	public int getMass() {
		return 0;
	}
	
	@Override
	public double getIsolationRate() {
		return 0.0;
	}
	
	@Override
	public String getStarMapName() {
		return null;
	}
	
	@Override
	public void onBlockUpdatedInArea(final VectorI vector, final IBlockState blockState) {
		
	}
	
	@Override
	public String toString() {
		return String.format("%s @ \'%s\' (%d %d %d)",
		                     getClass().getSimpleName(),
		                     worldObj == null ? "~NULL~" : worldObj.provider.getSaveFolder(),
		                     pos.getX(), pos.getY(), pos.getZ());
	}
}
