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
import rafradek.TF2weapons.util.TF2Util;

public class ItemEventMaker extends Item {

	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer living, EnumHand hand) {
		ItemStack stack = living.getHeldItem(hand);
		if (!world.isRemote) {
			if (TF2Util.getTeam(living) == null) {
				living.sendMessage(new TextComponentString("You must be assigned to a team"));
				return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
			}
			if (!world.getCapability(TF2weapons.WORLD_CAP, null).startInvasion(living)) {
				living.sendMessage(new TextComponentString("There is a mission in progress"));
				return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
			}
			else {
				stack.shrink(1);
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		
	}
}
