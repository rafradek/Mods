package rafradek.TF2weapons.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.client.ClientProxy;
import rafradek.TF2weapons.common.TF2Attribute;
import rafradek.TF2weapons.common.WeaponsCapability;
import rafradek.TF2weapons.entity.building.EntityBuilding;
import rafradek.TF2weapons.util.TF2Util;

public class ItemDisguiseKit extends Item {

	public ItemDisguiseKit() {
		this.setCreativeTab(TF2weapons.tabutilitytf2);
		this.setMaxStackSize(1);
		this.setMaxDamage(50);
	}

	public static void startDisguise(EntityLivingBase living, World world, String type) {
		WeaponsCapability.get(living).setDisguiseType(type);
		if (living.getCapability(TF2weapons.WEAPONS_CAP, null).disguiseTicks == 0)
			// System.out.println("starting disguise");
			if (!world.isRemote)
			living.getCapability(TF2weapons.WEAPONS_CAP, null).disguiseTicks = 1;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer living, EnumHand hand) {
		if (world.isRemote && ItemToken.allowUse(living, "spy") && TF2Util.getFirstItem(living.inventory, stack -> TF2Attribute.getModifier("No Disguise Kit", stack, 0, living) != 0).isEmpty())
			ClientProxy.showGuiDisguise();
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, living.getHeldItem(hand));
	}

	public static boolean isDisguised(EntityLivingBase living, EntityLivingBase view) {
		if(!living.hasCapability(TF2weapons.WEAPONS_CAP, null) || !WeaponsCapability.get(living).isDisguised() 
				|| (living.getCapability(TF2weapons.WEAPONS_CAP, null).invisTicks != 0 && !(view instanceof EntityBuilding)))
			return false;
		String disguisetype=WeaponsCapability.get(living).getDisguiseType();
		if(disguisetype.startsWith("M:") || disguisetype.startsWith("T:"))
			return true;
		if(disguisetype.startsWith("P:")) {
			return living.world.getScoreboard().getPlayersTeam(disguisetype.substring(2)) == view.getTeam();
		}
		return false;
	}
	
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair)
    {
        return repair.getItem() == TF2weapons.itemTF2 && repair.getMetadata() == 2;
    }
}
