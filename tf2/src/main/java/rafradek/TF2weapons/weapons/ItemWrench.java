package rafradek.TF2weapons.weapons;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.TF2EventsCommon;
import rafradek.TF2weapons.TF2Util;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.building.EntityBuilding;
import rafradek.TF2weapons.building.EntitySentry;
import rafradek.TF2weapons.building.EntityTeleporter;

public class ItemWrench extends ItemMeleeWeapon {

	/*@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List<String> par2List,
			boolean par4) {
		super.addInformation(par1ItemStack, par2EntityPlayer, par2List, par4);
		par2List.add("Metal: " + Integer.toString(200 - par1ItemStack.getItemDamage()) + "/200");
	}*/

	@Override
	public boolean onHit(ItemStack stack, EntityLivingBase attacker, Entity target, float damage, int critical) {
		// attacker.swingArm(EnumHand.MAIN_HAND);

		if (target instanceof EntityBuilding && TF2Util.isOnSameTeam(target, attacker)) {
			EntityBuilding building = (EntityBuilding) target;

			if (building.isSapped())
				building.removeSapper();
			else if(building.isConstructing()) {
				building.wrenchBonusTime=25;
				building.wrenchBonusMult=TF2Attribute.getModifier("Construction Rate", stack, 1, attacker);
				building.playSound(ItemFromData.getSound(stack, PropertyType.BUILD_HIT_SUCCESS_SOUND), 1.7f, 1f);
			}
			else {
				boolean useIgnot = false;
				int metalLeft = attacker.getCapability(TF2weapons.WEAPONS_CAP, null).getMetal();
				float metalMult = TF2Attribute.getModifier("Metal Used", stack, 1f, attacker);
				ItemStack ingot = new ItemStack(Items.IRON_INGOT);
				if (metalLeft == 0 && attacker instanceof EntityPlayer
						&& ((EntityPlayer) attacker).inventory.hasItemStack(ingot)) {
					metalLeft = 50;
					attacker.getCapability(TF2weapons.WEAPONS_CAP, null).setMetal(50);
					useIgnot = true;
				}
				int metalUse = 0;

				if (building.getHealth() < building.getMaxHealth()) {
					metalUse = (int) Math.min(
							(Math.min((building.getMaxHealth() - building.getHealth()) * 3.333333f, 33 * metalMult) + 1),
							metalLeft);
					building.heal(metalUse * 0.3f);
					metalLeft -= metalUse;
				}

				if (building instanceof EntitySentry) {
					metalUse = Math.min(
							Math.min(((EntitySentry) building).getMaxAmmo() - ((EntitySentry) building).getAmmo(), (int) (40 * metalMult)),
							metalLeft);
					((EntitySentry) building).setAmmo(Math.min(((EntitySentry) building).getMaxAmmo(),
							((EntitySentry) building).getAmmo() + metalUse));
					metalLeft -= metalUse;
					if (building.getLevel() == 3) {
						metalUse = Math.min(Math.min(20 - ((EntitySentry) building).getRocketAmmo(), (int) (8 * metalMult)), metalLeft / 2);
						((EntitySentry) building)
								.setRocketAmmo(Math.min(20, ((EntitySentry) building).getRocketAmmo() + metalUse));
						metalLeft -= metalUse * 2;
					}
				}
				if (building.getLevel() < 3) {
					metalUse = Math.min(Math.min(200 - building.getProgress(), (int)TF2Attribute.getModifier("Upgrade Rate", stack, 25 * metalMult, attacker)), metalLeft);
					float teleUpgradeRate = building instanceof EntityTeleporter ? TF2Attribute.getModifier("Teleporter Cost", stack, 1f, attacker) : 1;
					building.setProgress(Math.min(building.getProgress() + (int)(metalUse * teleUpgradeRate), 200));
					metalLeft -= metalUse;
					if (building.getProgress() >= 200)
						building.upgrade();
				}

				building.playSound(ItemFromData.getSound(stack, metalLeft != attacker.getCapability(TF2weapons.WEAPONS_CAP, null).getMetal()
						? PropertyType.BUILD_HIT_SUCCESS_SOUND : PropertyType.BUILD_HIT_FAIL_SOUND), 1.7f, 1f);
				// System.out.println("metal: "+TF2weapons.getMetal(attacker)+"
				// used: "+metalLeft);
				if (useIgnot && metalLeft != 50)
					((EntityPlayer) attacker).inventory.clearMatchingItems(Items.IRON_INGOT, 0, 1, null);

				if (!(attacker instanceof EntityPlayer && ((EntityPlayer) attacker).capabilities.isCreativeMode))
					attacker.getCapability(TF2weapons.WEAPONS_CAP, null).setMetal(metalLeft);
			}

			return false;
		}
		return true;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer living, EnumHand hand) {
		ItemStack stack=living.getHeldItem(hand);
		if(living.getCapability(TF2weapons.WEAPONS_CAP, null).getMetal()>=20 && TF2Attribute.getModifier("Weapon Mode", stack, 0, living) != 0) {
			living.setActiveHand(hand);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		}
		return super.onItemRightClick(world, living, hand);
	}
	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 800;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BOW;
	}
	/*
	 * public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack
	 * newStack, boolean slotChanged) { return
	 * super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged); }
	 */
	public boolean showInfoBox(ItemStack stack, EntityPlayer player){
		return true;
	}
	public String[] getInfoBoxLines(ItemStack stack, EntityPlayer player){
		return new String[]{"METAL",Integer.toString(player.getCapability(TF2weapons.WEAPONS_CAP, null).getMetal())};
	}
}
