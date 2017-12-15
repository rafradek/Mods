package rafradek.TF2weapons.characters.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import rafradek.TF2weapons.TF2Util;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.characters.EntityDemoman;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.weapons.ItemWeapon;
import rafradek.TF2weapons.weapons.ItemStickyLauncher;
import rafradek.TF2weapons.weapons.ItemUsable;

public class EntityAIStickybomb extends EntityAIBase {

	/** The entity the AI instance has been applied to */
	protected final EntityDemoman entityHost;

	/**
	 * The entity (as a RangedAttackMob) the AI instance has been applied to.
	 */

	protected float entityMoveSpeed;
	protected int field_75318_f;

	/**
	 * The maximum time the AI has to wait before peforming another ranged
	 * attack.
	 */
	private float attackRange;
	protected float attackRangeSquared;

	protected boolean pressed;
	private boolean altpreesed;
	protected boolean dodging;
	protected boolean dodge;
	public boolean jump;
	public float dodgeSpeed = 1f;
	public int jumprange;
	public int searchTimer;
	private boolean firstTick;
	public Vec3d attackTarget;

	public float gravity;

	public boolean explosive;

	public EntityAIStickybomb(EntityDemoman par1IRangedAttackMob, float par2, float par5) {
		this.field_75318_f = 0;
		this.entityHost = par1IRangedAttackMob;
		this.entityMoveSpeed = par2;
		this.attackRange = par5;
		this.attackRangeSquared = par5 * par5;

		this.setMutexBits(3);
	}

	public void setRange(float range) {
		this.attackRange = range;
		this.attackRangeSquared = range * range;
	}

	@Override
	public boolean shouldExecute() {
		return this.entityHost.loadout.getStackInSlot(1)!=null && !(this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).activeBomb.size()>7)
				&&this.entityHost.loadout.getStackInSlot(1).getItem() instanceof ItemStickyLauncher && this.entityHost.getAttackTarget()==null;
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
		if (this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).state != 0) {
			pressed = false;
			if (this.entityHost.getHeldItemMainhand().getItem() instanceof ItemWeapon)
				((ItemWeapon) this.entityHost.getHeldItem(EnumHand.MAIN_HAND).getItem()).endUse(
						this.entityHost.getHeldItem(EnumHand.MAIN_HAND), this.entityHost, this.entityHost.world,
						this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).state, 0);
			this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).state = 0;
			TF2Util.sendTracking(new TF2Message.ActionMessage(0, entityHost), entityHost);
		}
		this.entityHost.getNavigator().clearPathEntity();
		this.attackTarget=null;
		this.field_75318_f = 0;
		this.entityHost.switchSlot(0);
	}

	/**
	 * Updates the task
	 */
	public double lookingAtMax() {
		if (this.attackTarget == null)
			return 0;
		double d0 = this.attackTarget.x - this.entityHost.posX;
		double d1 = (this.attackTarget.y)
				- (this.entityHost.posY + this.entityHost.getEyeHeight());
		double d2 = this.attackTarget.z - this.entityHost.posZ;
		double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
		float f = (float) (Math.atan2(d2, d0) * 180.0D / Math.PI) - 90.0F;
		float f1 = (float) (-(Math.atan2(d1, d3) * 180.0D / Math.PI));
		float compareyaw = Math
				.abs(180 - Math.abs(Math.abs(f - MathHelper.wrapDegrees(this.entityHost.rotationYawHead)) - 180));
		float comparepitch = Math.abs(180 - Math.abs(Math.abs(f1 - this.entityHost.rotationPitch) - 180));
		// System.out.println("Angled: "+compareyaw+" "+comparepitch);
		return Math.max(compareyaw, comparepitch);
	}

	@Override
	public void updateTask() {
		if(this.attackTarget==null)
			this.attackTarget=RandomPositionGenerator.findRandomTarget(this.entityHost, 10, 4);
		if(this.attackTarget==null)
			return;
		this.entityHost.switchSlot(1);
		double d0 = this.entityHost.getDistanceSq(this.attackTarget.x, this.attackTarget.y,
				this.attackTarget.z);

		double lookX = this.attackTarget.x;
		double lookY = this.attackTarget.y;
		double lookZ = this.attackTarget.z;

		if (d0 <= this.attackRangeSquared) {
			if (!this.dodging) {
				this.entityHost.getNavigator().clearPathEntity();
				this.dodging = true;
			}
		} else {
			this.dodging = false;

			this.entityHost.getNavigator().tryMoveToXYZ(this.attackTarget.x,this.attackTarget.y,this.attackTarget.z, this.entityMoveSpeed);
		}
		// if(!(this.entityHost instanceof
		// EntitySoldier&&((EntitySoldier)this.entityHost).rocketJump))
		this.entityHost.getLookHelper().setLookPosition(lookX, lookY, lookZ, this.entityHost.rotation, 90.0F);
		// this.entityHost.getLookHelper().setLookPositionWithEntity(this.attackTarget,
		// 1.0F, 90.0F);
		if (d0 <= this.attackRangeSquared/* && this.entityHost.getAmmo() > 0 */) {
			if (!pressed) {
				pressed = true;
				((ItemUsable) this.entityHost.getHeldItem(EnumHand.MAIN_HAND).getItem()).startUse(
						this.entityHost.getHeldItem(EnumHand.MAIN_HAND), this.entityHost, this.entityHost.world,
						this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).state, 1);
				this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).state = 1;
				TF2Util.sendTracking(new TF2Message.ActionMessage(1, entityHost), entityHost);
				// System.out.println("coœdo");
			}

		} else {
			if (pressed) {
				((ItemUsable) this.entityHost.getHeldItem(EnumHand.MAIN_HAND).getItem()).endUse(
						this.entityHost.getHeldItem(EnumHand.MAIN_HAND), this.entityHost, this.entityHost.world,
						this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).state, 0);
				this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).state = 0;
				TF2Util.sendTracking(new TF2Message.ActionMessage(0, entityHost), entityHost);
				// System.out.println("coœz");
			}
			pressed = false;
		}
		// if(){
		// }
	}

}
