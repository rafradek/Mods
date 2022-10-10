package rafradek.TF2weapons.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rafradek.TF2weapons.entity.EntityPickup;

public class ItemPickup extends Item {

	public ItemPickup() {
		this.setHasSubtypes(true);
		this.setUnlocalizedName("tf2pickup");
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.tf2pickup." + stack.getMetadata();
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			BlockPos forwardpos = pos.offset(facing);
			EntityPickup pickup = new EntityPickup(worldIn,
					EntityPickup.Type.values()[player.getHeldItem(hand).getMetadata()], true);
			if (!player.isCreative())
				player.getHeldItem(hand).shrink(1);
			pickup.setPosition(forwardpos.getX() + 0.5, forwardpos.getY(), forwardpos.getZ() + 0.5);
			worldIn.spawnEntity(pickup);
		}
		return EnumActionResult.SUCCESS;
	}

	@Override
	public void getSubItems(CreativeTabs par2CreativeTabs, NonNullList<ItemStack> par3List) {
		// System.out.println(this.getCreativeTab());
		if (!this.isInCreativeTab(par2CreativeTabs))
			return;
		for (int i = 0; i < EntityPickup.Type.values().length; i++) {
			if (EntityPickup.Type.values()[i].visible)
				par3List.add(new ItemStack(this, 1, i));
		}
	}
}
