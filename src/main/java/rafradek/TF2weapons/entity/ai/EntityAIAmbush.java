package rafradek.TF2weapons.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.common.WeaponsCapability;
import rafradek.TF2weapons.entity.mercenary.EntitySpy;
import rafradek.TF2weapons.item.ItemDisguiseKit;

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
		/*
		 * if(this.host.getLastAttacker()!=null&&this.host.ticksExisted-this.
		 * host.getLastAttackerTime()<45){ return false; }
		 * System.out.println("Cloak: "+this.host.cloak.getTagCompound().
		 * getFloat("charge")); //--counter;
		 * if(target!=null&&(this.host.cloak.getTagCompound().getFloat("charge")
		 * >0.5f||this.host.getEntityData().getByte("IsCloaked")!=0)){
		 * if(this.host.getDistanceSq(target)>5||TF2weapons.lookingAt(
		 * target, 110, this.host.posX, this.host.posY, this.host.posZ)){
		 * System.out.println("Start"); return true; } }
		 */
		// System.out.println("execute:
		// "+target!=null&&this.host.getEntityData().getByte("IsCloaked")!=0);
		return target != null && WeaponsCapability.get(this.host).isInvisible();
	}

	@Override
	public void resetTask() {
		// System.out.println("Stop");
		this.host.getNavigator().clearPath();
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
		if (this.host.getAttackTarget() != null && this.host.getAttackTarget() instanceof EntityPlayer)
			ItemDisguiseKit.startDisguise(this.host, this.host.world, "M:"+TF2weapons.animals.get(this.host.getRNG().nextInt(TF2weapons.animals.size())));
		else
			ItemDisguiseKit.startDisguise(this.host, this.host.world, "T:Engineer");
		// this.host.getNavigator().tryMoveToEntityLiving(target, 1);
		this.host.getLookHelper().setLookPosition(target.posX, target.posY + target.getEyeHeight(), target.posZ, 90,
				90.0F);
		float x = -MathHelper.sin(target.rotationYaw / 180.0F * (float) Math.PI);
		float z = MathHelper.cos(target.rotationYaw / 180.0F * (float) Math.PI);
		host.getNavigator().tryMoveToXYZ(target.posX - x * 2, target.posY, target.posZ - z * 2, 1);
		// this.host.getLookHelper().setLookPosition(target.posX+x*2,target.posY+target.getEyeHeight(),target.posZ+z*2,
		// 90f, 90.0F);

	}
}
