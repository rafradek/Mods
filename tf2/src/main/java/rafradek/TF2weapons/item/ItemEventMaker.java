package rafradek.TF2weapons.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.entity.mercenary.InvasionEvent;
import rafradek.TF2weapons.util.TF2Util;

public class ItemEventMaker extends Item {

	public ItemEventMaker() {
		this.setHasSubtypes(true);
		this.setUnlocalizedName("eventmaker");
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.eventmaker."+stack.getMetadata();
	}
	
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer living, EnumHand hand) {
		ItemStack stack = living.getHeldItem(hand);
		if (!world.isRemote) {
			if (TF2Util.getTeam(living) == null) {
				living.sendMessage(new TextComponentTranslation("item.eventmaker.noteam"));
				return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
			}
			if (!world.getCapability(TF2weapons.WORLD_CAP, null).startInvasion(living, stack.getMetadata() % InvasionEvent.DIFFICULTY.length)) {
				living.sendMessage(new TextComponentTranslation("item.eventmaker.fail"));
				return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
			}
			else {
				stack.shrink(1);
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		
	}
	
	@Override
	public void getSubItems(CreativeTabs par2CreativeTabs, NonNullList<ItemStack> par3List) {
		// System.out.println(this.getCreativeTab());
		if(!this.isInCreativeTab(par2CreativeTabs))
			return;
		for (int i = 0; i < InvasionEvent.DIFFICULTY.length; i++)
			par3List.add(new ItemStack(this,1,i));
	}
}
