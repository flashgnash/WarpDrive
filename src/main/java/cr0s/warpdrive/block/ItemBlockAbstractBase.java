package cr0s.warpdrive.block;

import cr0s.warpdrive.Commons;
import cr0s.warpdrive.api.IBlockBase;
import cr0s.warpdrive.api.IItemBase;
import cr0s.warpdrive.client.ClientProxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockAbstractBase extends ItemBlock implements IItemBase {
	
	// warning: ItemBlock is created during registration, while block is still being constructed.
	// As such, we can't use block properties from constructor
	public ItemBlockAbstractBase(final Block block) {
		super(block);
		setUnlocalizedName(block.getUnlocalizedName());
	}
	
	@Override
	public int getMetadata(final int damage) {
		return damage;
	}
	
	@Nonnull
	@Override
	public String getUnlocalizedName(final ItemStack itemStack) {
		if ( itemStack == null 
		  || !(block instanceof BlockAbstractContainer)
		  || !((BlockAbstractContainer) block).hasSubBlocks ) {
			return getUnlocalizedName();
		}
		return getUnlocalizedName() + itemStack.getItemDamage();
	}
	
	@Nonnull
	@Override
	public EnumRarity getRarity(@Nonnull final ItemStack itemStack) {
		if ( !(block instanceof IBlockBase) ) {
			return super.getRarity(itemStack);
		}
		return ((IBlockBase) block).getRarity(itemStack, super.getRarity(itemStack));
	}
	
	public ITextComponent getStatus(final World world, final NBTTagCompound tagCompound, final IBlockState blockState) {
		final TileEntity tileEntity = block.createTileEntity(world, blockState);
		if (tileEntity instanceof TileEntityAbstractBase) {
			if (tagCompound != null) {
				tileEntity.readFromNBT(tagCompound);
			}
			return ((TileEntityAbstractBase) tileEntity).getStatus();
			
		} else {
			return new TextComponentString("");
		}
	}
	
	@Override
	public void onEntityExpireEvent(EntityItem entityItem, ItemStack itemStack) {
	}
	
	@Nonnull
	@Override
	public ModelResourceLocation getModelResourceLocation(final ItemStack itemStack) {
		return ClientProxy.getModelResourceLocation(itemStack);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(@Nonnull final ItemStack itemStack, @Nullable World world,
	                           @Nonnull final List<String> list, @Nullable final ITooltipFlag advancedItemTooltips) {
		super.addInformation(itemStack, world, list, advancedItemTooltips);
		
		final String tooltipName1 = getUnlocalizedName(itemStack) + ".tooltip";
		if (I18n.hasKey(tooltipName1)) {
			Commons.addTooltip(list, new TextComponentTranslation(tooltipName1).getFormattedText());
		}
		
		final String tooltipName2 = getUnlocalizedName() + ".tooltip";
		if ((!tooltipName1.equals(tooltipName2)) && I18n.hasKey(tooltipName2)) {
			Commons.addTooltip(list, new TextComponentTranslation(tooltipName2).getFormattedText());
		}
		
		final IBlockState blockState;
		if (world != null) {
			blockState = block.getStateForPlacement(world, new BlockPos(0, -1, 0),
			                                        EnumFacing.DOWN, 0.0F, 0.0F, 0.0F,
			                                        itemStack.getMetadata(), Minecraft.getMinecraft().player, EnumHand.MAIN_HAND);
		} else {
			blockState = block.getStateFromMeta(itemStack.getMetadata());
		}
		
		Commons.addTooltip(list, getStatus(world, itemStack.getTagCompound(), blockState).getFormattedText());
	}
	
	@Override
	public String toString() {
		return String.format("%s@%s {%s} %s",
		                     getClass().getSimpleName(),
		                     Integer.toHexString(hashCode()),
		                     REGISTRY.getNameForObject(this),
		                     getUnlocalizedName());
	}
}
