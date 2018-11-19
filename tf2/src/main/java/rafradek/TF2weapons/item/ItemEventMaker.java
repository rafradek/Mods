package rafradek.TF2weapons.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2weapons;

public class ItemEventMaker extends Item {

	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer living, EnumHand hand) {
		ItemStack stack = living.getHeldItem(hand);
		if (!world.isRemote) {
			if (!world.getCapability(TF2weapons.WORLD_CAP, null).startInvasion(living)) {
				stack.shrink(1);
				living.sendMessage(new TextComponentString("There is a mission in progress"));
				return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		
	}
}
