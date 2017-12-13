package rafradek.TF2weapons;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockOverheadDoor extends BlockContainer {

	public static final PropertyInteger SLIDE = PropertyInteger.create("slide", 0, 3);
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	
	public BlockOverheadDoor() {
		super(Material.IRON, MapColor.IRON);
		this.setDefaultState(this.blockState.getBaseState().withProperty(SLIDE, 0).withProperty(FACING, EnumFacing.NORTH));
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
    }
	
	@SuppressWarnings("deprecation")
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		IBlockState above = world.getBlockState(pos.up());
		IBlockState bottom = world.getBlockState(pos.down());
		boolean isTop = !(above.getBlock() instanceof BlockOverheadDoor);
		boolean isDown = !(bottom.getBlock() instanceof BlockOverheadDoor);
		int slide = state.getValue(SLIDE);
		//System.out.println("State: "+state.getValue(SLIDE)+" is down: "+isDown);
		if(isDown) {
			if(!world.getEntitiesWithinAABB(EntityLivingBase.class, this.getBoundingBox(state, world, pos).offset(pos).grow(2)).isEmpty()) {
				if(slide != 3)
					world.setBlockState(pos, state.withProperty(SLIDE, slide + 1));
				else if (!isTop){
					world.scheduleUpdate(pos.up(), this, this.tickRate(world));
					world.setBlockToAir(pos);
				}
			}
			else {
				if(slide != 0)
					world.setBlockState(pos, state.withProperty(SLIDE, slide - 1));
				else if (bottom.getBlock().isAir(state, world, pos.down())) {
					world.setBlockState(pos.down(), state.withProperty(SLIDE, 3));
					System.out.println("Bottom is air");
				}
			}
		}
		if(isDown)
			world.scheduleUpdate(pos, this, this.tickRate(world));
	}
	
	public int tickRate(World world) {
		return 2;
	}
	
	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ,
			int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
	}
	
	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(2+ (meta & 3))).withProperty(SLIDE, meta >> 2);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex()-2 + (state.getValue(SLIDE) << 2);
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { FACING, SLIDE });
	}
}
