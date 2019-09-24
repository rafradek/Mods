package rafradek.TF2weapons.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.common.WeaponsCapability;
import rafradek.TF2weapons.entity.mercenary.EntitySpy;
import rafradek.TF2weapons.item.ItemCloak;
import rafradek.TF2weapons.item.ItemDisguiseKit;
import rafradek.TF2weapons.util.TF2Util;

public class EntityAIAmbush extends EntityAIBase {

	public EntitySpy host;
	public int counter;

	public EntityAIAmbush(EntitySpy entitySpy) {
		this.host = entitySpy;
		this.setMutexBits(3);
	}

	@Override
	public boolean shouldExecute() {
		EntityLivingBase target = this.host.getAttackTarget();
		if (!this.host.hasWatch() || target == null) {
			this.host.isAmbushing = false;
			return false;
		}
		
		double dist = target.getDistanceSq(this.host);
		boolean isAttacked = this.host.getRevengeTarget() != null && this.host.ticksExisted - this.host.getRevengeTimer() < 45;
		boolean enemyLooking = !this.host.getWepCapability().isDisguised() && !this.host.loadout.getStackInSlot(3).getTagCompound().getBoolean("Active") &&
				this.host.getEntitySenses().canSee(target) && TF2Util.lookingAtFast(target, dist > 400 ? 50 : 105, this.host.posX, this.host.posY, this.host.posZ);
		boolean isAtEnemy = dist < 9 && !TF2Util.lookingAtFast(target, 105, this.host.posX, this.host.posY, this.host.posZ);
		return host.isAmbushing = !isAttacked && !enemyLooking && !isAtEnemy;
	}

	@Override
	public void resetTask() {
		// System.out.println("Stop");
		this.host.getNavigator().clearPath();
		if (this.host.loadout.getStackInSlot(3).getTagCompound().getBoolean("Active"))
			((ItemCloak) this.host.loadout.getStackInSlot(3).getItem()).setCloak(
					false, this.host.loadout.getStackInSlot(3), this.host,
					this.host.world);
		this.host.isAmbushing = false;
		/*
		 * if(this.host.cloak.getTagCompound().getBoolean("Active")){
		 * ((ItemCloak)this.host.cloak.getItem()).altUse(this.host.cloak,
		 * this.host, this.host.world); }
		 */
	}

	@Override
	public void updateTask() {
		// System.out.println("Tick");
		EntityLivingBase target = this.host.getAttackTarget();
		if ((target != null && target.deathTime > 0) || host.deathTime > 0) {
			this.resetTask();
			return;
		}
		if (target == null)
			return;
		/*
		 * if(!this.host.cloak.getTagCompound().getBoolean("Active")){
		 * 
		 * ((ItemCloak)this.host.cloak.getItem()).setCloak(true,
		 * this.host.cloak, this.host, this.host.world); }
		 */
		ItemStack cloak = this.host.loadout.getStackInSlot(3);
		System.out.print("mov"+this.host.getWepCapability().isDisguised());
		double dist = target.getDistanceSq(this.host);
		boolean enemyLooking = !cloak.getTagCompound().getBoolean("Active") &&
				this.host.getEntitySenses().canSee(target) && TF2Util.lookingAtFast(target, 105, this.host.posX, this.host.posY, this.host.posZ);
		
		if (cloak.getItemDamage() < cloak.getMaxDamage() - 72 && !cloak.getTagCompound().getBoolean("Active") && !enemyLooking)
			((ItemCloak) cloak.getItem()).setCloak(true, cloak, this.host, this.host.world);
		
		if (!this.host.getWepCapability().isDisguised()) {
			if (this.host.getAttackTarget() != null && this.host.getAttackTarget() instanceof EntityPlayer)
				ItemDisguiseKit.startDisguise(this.host, this.host.world, "M:"+TF2weapons.animals.get(this.host.getRNG().nextInt(TF2weapons.animals.size())));
			else
				ItemDisguiseKit.startDisguise(this.host, this.host.world, "T:Engineer");
		}
		// this.host.getNavigator().tryMoveToEntityLiving(target, 1);
		this.host.getLookHelper().setLookPosition(target.posX, target.posY + target.getEyeHeight(), target.posZ, 90,
				90.0F);
		
		if (dist > 16 || !TF2Util.lookingAtFast(target, 105, this.host.posX, this.host.posY, this.host.posZ)) {
			float x = -MathHelper.sin(target.rotationYaw / 180.0F * (float) Math.PI);
			float z = MathHelper.cos(target.rotationYaw / 180.0F * (float) Math.PI);
			Vec3d pos = new Vec3d(target.posX - x * 2, target.posY, target.posZ - z * 2);
			host.getNavigator().tryMoveToXYZ(target.posX - x * 2, target.posY, target.posZ - z * 2, 1);
		}
		else {
			float x = -MathHelper.sin(target.rotationYaw / 180.0F * (float) Math.PI);
			float z = MathHelper.cos(target.rotationYaw / 180.0F * (float) Math.PI);
			Vec3d pos = new Vec3d(x, 0, z).crossProduct(new Vec3d(0,1,0));
			host.getNavigator().tryMoveToXYZ(target.posX + pos.x * 3 - x * 2, target.posY, target.posZ + pos.z * 3 - z * 2, 1f);
		}
		// this.host.getLookHelper().setLookPosition(target.posX+x*2,target.posY+target.getEyeHeight(),target.posZ+z*2,
		// 90f, 90.0F);

	}
}
