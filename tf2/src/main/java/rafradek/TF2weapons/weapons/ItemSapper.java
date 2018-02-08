package rafradek.TF2weapons.weapons;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import rafradek.TF2weapons.TF2Sounds;
import rafradek.TF2weapons.TF2Util;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.building.EntityBuilding;

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
			stack.shrink(1);
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
