package rafradek.rig;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import rafradek.TF2weapons.TF2weapons;

public class BlockRIGEnchant extends Block {

	public BlockRIGEnchant() {
		super(Material.IRON);
		this.setSoundType(SoundType.METAL);
		this.setCreativeTab(RIG.tabRig);
	}
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote)
			FMLNetworkHandler.openGui(playerIn, RIG.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
			
	}
	
}
