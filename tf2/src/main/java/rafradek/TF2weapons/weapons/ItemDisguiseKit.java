package rafradek.TF2weapons.weapons;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.TF2EventsCommon;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.building.EntityBuilding;

public class ItemDisguiseKit extends Item {

	public ItemDisguiseKit() {
		this.setCreativeTab(TF2weapons.tabutilitytf2);
		this.setMaxStackSize(50);
		this.setMaxDamage(25);
	}

	public static void startDisguise(EntityLivingBase living, World world, String type) {
		living.getDataManager().set(TF2EventsCommon.ENTITY_DISGUISE_TYPE, type);
		if (living.getCapability(TF2weapons.WEAPONS_CAP, null).disguiseTicks == 0)
			// System.out.println("starting disguise");
			if (!world.isRemote)
			living.getCapability(TF2weapons.WEAPONS_CAP, null).disguiseTicks = 1;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer living, EnumHand hand) {
		if (world.isRemote)
			ClientProxy.showGuiDisguise();
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, living.getHeldItem(hand));
	}

	public static boolean isDisguised(EntityLivingBase living, EntityLivingBase view) {
		if(!living.hasCapability(TF2weapons.WEAPONS_CAP, null) || !living.getDataManager().get(TF2EventsCommon.ENTITY_DISGUISED) 
				|| (living.getCapability(TF2weapons.WEAPONS_CAP, null).invisTicks != 0 && !(view instanceof EntityBuilding)))
			return false;
		String disguisetype=living.getDataManager().get(TF2EventsCommon.ENTITY_DISGUISE_TYPE);
		if(disguisetype.startsWith("M:") || disguisetype.startsWith("T:"))
			return true;
		if(disguisetype.startsWith("P:")) {
			return living.world.getScoreboard().getPlayersTeam(disguisetype.substring(2)) == view.getTeam();
		}
		return false;
	}

}
