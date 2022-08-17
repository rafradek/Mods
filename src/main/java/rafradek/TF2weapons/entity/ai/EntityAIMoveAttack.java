package rafradek.TF2weapons.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import rafradek.TF2weapons.entity.building.EntityBuilding;
import rafradek.TF2weapons.entity.mercenary.EntitySniper;
import rafradek.TF2weapons.entity.mercenary.EntityTF2Character;
import rafradek.TF2weapons.item.ItemMedigun;
import rafradek.TF2weapons.item.ItemUsable;
import rafradek.TF2weapons.item.ItemWeapon;

public class EntityAIMoveAttack extends EntityAIBase {

	/** The entity the AI instance has been applied to */
	protected final EntityTF2Character entityHost;

	/**
	 * The entity (as a RangedAttackMob) the AI instance has been applied to.
	 */
	protected EntityLivingBase attackTarget;

	/**
	 * A decrementing tick that spawns a ranged attack once this value reaches
	 * 0. It is then set back to the maxRangedAttackTime.
	 */
	protected int rangedAttackTime;
	protected float entityMoveSpeed;
	protected int comeCloser;

	/**
	 * The maximum time the AI has to wait before peforming another ranged
	 * attack.
	 */
	private float attackRange;
	protected float attackRangeSquared;

	protected boolean inRange;
	protected boolean dodge;
	public boolean jump;
	public float dodgeSpeed = 1f;
	public int jumprange;
	public float projSpeed;
	public double fireAtFeet;

	public float gravity;

	public boolean explosive;

	public boolean dodgeHeadFor;

	private float attackRangeSSquared;

	public EntityAIMoveAttack(EntityTF2Character par1IRangedAttackMob, float par2, float par5) {
		this.rangedAttackTime = -1;
		this.comeCloser = 0;
		this.entityHost = par1IRangedAttackMob;
		this.entityMoveSpeed = par2;
		this.attackRange = par5;
		this.attackRangeSquared = par5 * par5;

		this.setMutexBits(1);
	}

	public void setRange(float range) {
		this.attackRange = range;
		this.attackRangeSquared = range * range;
		this.attackRangeSSquared = (range+5) * (range+5);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	@Override
	public boolean shouldExecute() {
		EntityLivingBase EntityLivingBase = this.entityHost.getAttackTarget();

		if (EntityLivingBase == null)
			return false;
		else {
			this.attackTarget = EntityLivingBase;
			return !this.entityHost.getHeldItemMainhand().isEmpty()
					&& (this.entityHost.getHeldItemMainhand().getItem() instanceof ItemWeapon || this.entityHost.getHeldItemMainhand().getItem() instanceof ItemMedigun);
		}
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	@Override
	public boolean shouldContinueExecuting() {
		return this.shouldExecute() || !this.entityHost.getNavigator().noPath();
	}

	/**
	 * Resets the task
	 */
	@Override
	public void resetTask() {
		if (this.jump)
			this.entityHost.jump = false;
		this.entityHost.getNavigator().clearPath();
		this.attackTarget = null;
		this.comeCloser = 0;
		this.rangedAttackTime = -1;
	}

	/**
	 * Updates the task
	 */


	@Override
	public void updateTask() {
		if ((this.attackTarget != null && this.attackTarget.deathTime > 0) || this.entityHost.deathTime > 0) {
			this.resetTask();
			return;
		}
		if (this.attackTarget == null)
			return;
		
		ItemStack item = this.entityHost.getHeldItem(EnumHand.MAIN_HAND);
		
		if (!(item.getItem() instanceof ItemUsable))
			return;
		
		double d0 = this.entityHost.getDistanceSq(this.attackTarget.posX, this.attackTarget.getEntityBoundingBox().minY,
				this.attackTarget.posZ);

		boolean stay = this.entityHost.getEntitySenses().canSee(this.attackTarget)
				|| (this.projSpeed > 0 && this.attackTarget.motionY > 0);
		//this.entityHost.setJumping(true);
		
		float range = this.attackTarget instanceof EntityBuilding ? this.attackRangeSSquared : this.attackRangeSquared;
		if (stay) {
			++this.comeCloser;
			if (d0 <= (double) this.attackRangeSquared / (this.entityHost instanceof EntitySniper ? 2f : 4f))
				this.comeCloser = 20;
		} else
			this.comeCloser = 0;
		
		if ((d0 <= range && this.comeCloser >= 20) || this.entityHost.getWepCapability().isExpJump()) {
			if (!this.inRange) {
				this.entityHost.getNavigator().clearPath();
				this.inRange = true;
			}
		} else {
			this.inRange = false;
			/*
			 * if(this.entityHost.onGround&&this.entityHost instanceof
			 * EntitySoldier&&this.entityHost.getHeldItem(EnumHand.MAIN_HAND).
			 * getItemDamage()<this.entityHost.getHeldItem(EnumHand.MAIN_HAND).
			 * getMaxDamage()-1){
			 * ((EntitySoldier)this.entityHost).rocketJump=true; }
			 */
			this.entityHost.getNavigator().tryMoveToEntityLiving(this.attackTarget, this.entityMoveSpeed);
		}
		//this.entityHost.getLookHelper().setLookPosition(lookX, lookY, lookZ, this.entityHost.rotation, 90.0F);
		// this.entityHost.getLookHelper().onUpdateLook();
		// if(!(this.entityHost instanceof
		// EntitySoldier&&((EntitySoldier)this.entityHost).rocketJump))

		// this.entityHost.getLookHelper().setLookPositionWithEntity(this.attackTarget,
		// 1.0F, 90.0F);
		// if(){
		if (this.jump && d0 < this.jumprange)
			this.entityHost.jump = true;
		else if (this.jump)
			this.entityHost.jump = false;
		if (this.dodge && (this.entityHost.getNavigator().noPath() || (this.entityHost.ticksExisted % 20) == 0)) {
			Vec3d Vec3d = RandomPositionGenerator.findRandomTarget(this.entityHost, 4, 2);

			if (Vec3d != null) {
				double offsetX = this.dodgeHeadFor ? this.attackTarget.posX - this.entityHost.posX : 0;
				double offsetY = this.dodgeHeadFor ? this.attackTarget.posY - this.entityHost.posY : 0;
				double offsetZ = this.dodgeHeadFor ? this.attackTarget.posZ - this.entityHost.posZ : 0;
				this.entityHost.getNavigator().tryMoveToXYZ(Vec3d.x + offsetX, Vec3d.y + offsetY,
						Vec3d.z + offsetZ, this.entityMoveSpeed * this.dodgeSpeed);
			}
		}
		// }
	}

	public void setDodge(boolean i, boolean headFor) {
		this.dodge = i;
		this.dodgeHeadFor = headFor;
	}

}
