package rafradek.TF2weapons.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rafradek.TF2weapons.tileentity.TileEntityGameConfigure;
import rafradek.TF2weapons.tileentity.TileEntityResupplyCabinet;

public class BlockGameConfigure extends BlockContainer {

	public BlockGameConfigure() {
		super(Material.IRON);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		TileEntityGameConfigure upgrades = new TileEntityGameConfigure();
		return upgrades;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {

		}
		return false;
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		this.updateState(worldIn, pos, state);
	}

	private void updateState(World worldIn, BlockPos pos, IBlockState state) {}

	@Override
	public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {

	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		this.updateState(worldIn, fromPos, state);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {

		TileEntity ent = worldIn.getTileEntity(pos);
		if (ent instanceof TileEntityGameConfigure) {
			((TileEntityGameConfigure) ent).removeGameArena();
		}
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return super.canPlaceBlockAt(worldIn, pos)
				&& worldIn.getBlockState(pos.up()).getBlock().isReplaceable(worldIn, pos.up());
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
		if (!(worldIn.getTileEntity(pos) instanceof TileEntityResupplyCabinet))
			return 0;
		return ((TileEntityResupplyCabinet) worldIn.getTileEntity(pos)).cooldownUse.size() > 0 ? 15 : 0;
	}
}
