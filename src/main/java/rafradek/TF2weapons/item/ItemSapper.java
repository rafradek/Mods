package rafradek.TF2weapons.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import rafradek.TF2weapons.TF2ConfigVars;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.client.audio.TF2Sounds;
import rafradek.TF2weapons.common.TF2Attribute;
import rafradek.TF2weapons.entity.building.EntityBuilding;
import rafradek.TF2weapons.entity.mercenary.EntityTF2Character;
import rafradek.TF2weapons.util.TF2Util;

public class ItemSapper extends ItemBulletWeapon {

	public ItemSapper() {
		super();
		this.setMaxStackSize(64);
	}

	@Override
	public boolean onHit(ItemStack stack, EntityLivingBase attacker, Entity target, float damage, int critical, boolean simulate) {
		// System.out.println("Can hit: " + TF2weapons.canHit(attacker,
		// target));
		if (target instanceof EntityBuilding && !((EntityBuilding) target).isSapped()
				&& TF2Util.canHit(attacker, target)) {
			((EntityBuilding) target).setSapped(attacker, stack);
			if(attacker instanceof EntityPlayer){
			attacker.getCapability(TF2weapons.PLAYER_CAP, null).sapperTime=100;
			attacker.getCapability(TF2weapons.PLAYER_CAP, null).buildingOwnerKill=((EntityBuilding)target).getOwner();
			}
			((EntityBuilding) target).playSound(TF2Sounds.MOB_SAPPER_PLANT, 1.3f, 1);
			if (((EntityBuilding) target).getOwner() != null)
				((EntityBuilding) target).getOwner().setRevengeTarget(attacker);
			if (!TF2ConfigVars.freeUseItems)
				stack.shrink(1);
			if (stack.getCount() <= 0 && attacker instanceof EntityPlayer)
				((EntityPlayer) attacker).inventory.deleteStack(stack);
		}
		else if (target instanceof EntityTF2Character && !TF2Util.isOnSameTeam(attacker, target) && ((EntityTF2Character) target).isRobot() 
				&& ((EntityTF2Character) target).getActivePotionEffect(TF2weapons.sapped) == null) {
			
			if (attacker instanceof EntityPlayer && ((EntityPlayer)attacker).getCooldownTracker().hasCooldown(this))
				return false;
			((EntityTF2Character)target).addPotionEffect(new PotionEffect(TF2weapons.stun,140,1));
			((EntityTF2Character)target).addPotionEffect(new PotionEffect(TF2weapons.sapped,140,0));
			float range = TF2Attribute.getModifier("Sapper Strength", stack, 0f, attacker) * 3 + 2;
			for (EntityTF2Character entity : attacker.world.getEntitiesWithinAABB(EntityTF2Character.class, target.getEntityBoundingBox().grow(range), entityl -> {
				return !TF2Util.isOnSameTeam(attacker, entityl) && ((EntityTF2Character) entityl).isRobot() && entityl.getDistanceSq(target) < range * range;
			})){
				((EntityTF2Character)entity).addPotionEffect(new PotionEffect(TF2weapons.stun,140,1));
				((EntityTF2Character)entity).addPotionEffect(new PotionEffect(TF2weapons.sapped,140,0));
			}
			if (!TF2ConfigVars.freeUseItems)
				stack.shrink(1);
			if (attacker instanceof EntityPlayer)
				((EntityPlayer) attacker).getCooldownTracker().setCooldown(this, 300);
			if (stack.getCount() <= 0 && attacker instanceof EntityPlayer)
				((EntityPlayer) attacker).inventory.deleteStack(stack);
		}
		return false;
	}

	@Override
	public float getMaxRange(ItemStack stack) {
		return 2.4f;
	}

	public float getBulletSize() {
		return 0.35f;
	}

	@Override
	public boolean showTracer(ItemStack stack) {
		return false;
	}

	@Override
	public boolean doMuzzleFlash(ItemStack stack, EntityLivingBase attacker, EnumHand hand) {

		return false;
	}
}
