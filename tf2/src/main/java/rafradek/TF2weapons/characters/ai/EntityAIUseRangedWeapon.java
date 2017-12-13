package rafradek.TF2weapons.characters.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import rafradek.TF2weapons.TF2Util;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.characters.EntityHeavy;
import rafradek.TF2weapons.characters.EntityTF2Character;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.weapons.ItemMedigun;
import rafradek.TF2weapons.weapons.ItemUsable;
import rafradek.TF2weapons.weapons.ItemWeapon;

public class EntityAIUseRangedWeapon extends EntityAIBase {

	protected final EntityTF2Character entityHost;

	protected EntityLivingBase attackTarget;
	public boolean reloading;

	protected int rangedAttackTime;
	protected float entityMoveSpeed;
	protected int comeCloser;

	private float attackRange;
	protected float attackRangeSquared;

	protected boolean pressed;
	private boolean altpreesed;
	protected boolean dodging;
	public boolean jump;
	public float dodgeSpeed = 1f;
	public int jumprange;
	public float projSpeed;
	public double fireAtFeet;

	private boolean firstTick;

	public float gravity;

	public boolean explosive;

	public EntityAIUseRangedWeapon(EntityTF2Character par1IRangedAttackMob, float par2, float par5) {
		this.rangedAttackTime = -1;
		this.comeCloser = 0;
		this.entityHost = par1IRangedAttackMob;
		this.entityMoveSpeed = par2;
		this.attackRange = par5;
		this.attackRangeSquared = par5 * par5;

		this.setMutexBits(2);
	}

	public void setRange(float range) {
		this.attackRange = range;
		this.attackRangeSquared = range * range;
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
		return this.shouldExecute();
	}

	/**
	 * Resets the task
	 */
	@Override
	public void resetTask() {
		if ((this.entityHost.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemWeapon) &&this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).state != 0) {
			pressed = false;
			((ItemWeapon) this.entityHost.getHeldItem(EnumHand.MAIN_HAND).getItem()).endUse(
					this.entityHost.getHeldItem(EnumHand.MAIN_HAND), this.entityHost, this.entityHost.world,
					this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).state, 0);
			this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).state = 0;
			TF2Util.sendTracking(new TF2Message.ActionMessage(0, entityHost), entityHost);
		}
		if (this.jump)
			this.entityHost.jump = false;
		this.entityHost.getNavigator().clearPathEntity();
		this.attackTarget = null;
		this.comeCloser = 0;
		this.rangedAttackTime = -1;
	}

	@Override
	public void updateTask() {
		if ((this.attackTarget != null && this.attackTarget.deathTime > 0) || this.entityHost.deathTime > 0) {
			this.resetTask();
			return;
		}
		if (this.attackTarget == null)
			return;
		
		ItemStack item = this.entityHost.getHeldItem(EnumHand.MAIN_HAND);
		
		if (!(item.getItem() instanceof ItemWeapon))
			return;
		ItemWeapon weapon = ((ItemWeapon) item.getItem());

		double d0 = this.entityHost.getDistanceSq(this.attackTarget.posX, this.attackTarget.getEntityBoundingBox().minY,
				this.attackTarget.posZ);

		double lookX = this.attackTarget.posX;
		double lookY = this.attackTarget.posY + this.attackTarget.getEyeHeight();
		double lookZ = this.attackTarget.posZ;
		boolean shouldFireProj = true;
		float dist = this.entityHost.getDistanceToEntity(this.attackTarget);
		if (this.projSpeed > 0) {

			
			float ticksToReach = dist / this.projSpeed;

			lookX += (this.attackTarget.posX - this.entityHost.targetPrevPos[1]) * ticksToReach * 0.5;
			lookZ += (this.attackTarget.posZ - this.entityHost.targetPrevPos[5]) * ticksToReach * 0.5;
			lookY = this.attackTarget.posY + this.attackTarget.height / 2;
			if (this.entityHost.world.rayTraceBlocks(new Vec3d(this.entityHost.posX,
					this.entityHost.posY + this.entityHost.getEyeHeight(), this.entityHost.posZ),
					new Vec3d(lookX, lookY, lookZ), false, true, false) != null)
				lookY = this.attackTarget.posY + this.attackTarget.getEyeHeight();
			double yFall = this.attackTarget.motionY < 0 && !this.attackTarget.onGround? -this.attackTarget.motionY : 0;
			for (int i = 1; !this.attackTarget.isInWater() && i <= (ticksToReach); i++) {
				lookY += gravity * i;
				if (!this.attackTarget.onGround && this.attackTarget.motionY < 0)
					yFall += 0.08 * i;
			}

			RayTraceResult mop = this.entityHost.world.rayTraceBlocks(this.attackTarget.getPositionVector(),
					this.attackTarget.getPositionVector().addVector(0, -0.3f - yFall, 0));
			if (mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK)
				yFall = this.attackTarget.posY - mop.hitVec.y;
			shouldFireProj = mop != null || this.attackTarget.motionY <= 0f;
			// System.out.println("perform"+yFall);
			// System.out.println("perform"+yFall);
			// System.out.println("look: "+this.attackTarget.posX+"
			// "+this.entityHost.targetPrevPos[1]);

			// System.out.println("raytracing:"+this.entityHost.world.rayTraceBlocks(new
			// Vec3d(this.entityHost.posX,this.entityHost.posY+this.entityHost.getEyeHeight(),this.entityHost.posZ),
			// new Vec3d(lookX,this.attackTarget.posY,lookZ)));
			lookY -= yFall;
			
			if (this.fireAtFeet > 0 && this.entityHost.world.rayTraceBlocks(
					new Vec3d(this.entityHost.posX, this.entityHost.posY + this.entityHost.getEyeHeight(),
							this.entityHost.posZ),
					new Vec3d(lookX, this.attackTarget.posY, lookZ), false, true, false) == null)
				/*
				 * if(this.fireAtFeet==2&&this.attackTarget.motionY<=0){
				 * RayTraceResult
				 * mop=this.entityHost.world.rayTraceBlocks(this.attackTarget
				 * .getPositionVector(),
				 * this.attackTarget.getPositionVector().addVector(0, yFall*1.2,
				 * 0)); if(mop != null &&
				 * mop.typeOfHit==RayTraceResult.Type.BLOCK){
				 * lookY=mop.hitVec.y; } else{
				 * lookY=this.attackTarget.posY-yFall; } } else{
				 */
				lookY -= (this.attackTarget.height/2)*this.fireAtFeet;
			// }

		}

		boolean stay = this.entityHost.getEntitySenses().canSee(this.attackTarget)
				|| (this.projSpeed > 0 && this.attackTarget.motionY > 0);
		boolean fire = stay && shouldFireProj && TF2Util.lookingAt(this.entityHost,
				(this.explosive && d0 < 16 ? 30 : 0) + weapon.getWeaponSpreadBase(item, this.entityHost) * 200 + 2 + Math.toDegrees(MathHelper.atan2(this.attackTarget.width / 2, dist)),
				lookX, lookY, lookZ);
		if (!this.reloading && (this.entityHost.world.getDifficulty() != EnumDifficulty.HARD || !fire)
				&& item.getItemDamage() == item.getMaxDamage() && weapon.hasClip(item))
			this.reloading = true;
		else if (this.reloading && item.getItemDamage() == 0)
			this.reloading = false;
		
		this.entityHost.getLookHelper().setLookPosition(lookX, lookY, lookZ, this.entityHost.rotation, 90.0F);
		// this.entityHost.getLookHelper().onUpdateLook();
		// if(!(this.entityHost instanceof
		// EntitySoldier&&((EntitySoldier)this.entityHost).rocketJump))

		// this.entityHost.getLookHelper().setLookPositionWithEntity(this.attackTarget,
		// 1.0F, 90.0F);
		if ((!reloading/*
						 * ||(this.entityHost.world.getDifficulty()==
						 * EnumDifficulty.HARD&&d0 <=
						 * (double)this.attackRangeSquared/4)
						 */) && fire && d0 <= this.attackRangeSquared
				&& (((ItemUsable)item.getItem()).isAmmoSufficient(item, entityHost, true) || weapon.getAmmoType(item) == 0)) {
			this.reloading = false;
			if (!pressed) {
				pressed = true;
				weapon.startUse(item, this.entityHost, this.entityHost.world,
						this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).state, 1);
				this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).state = 1;
				TF2Util.sendTracking(new TF2Message.ActionMessage(1, entityHost), entityHost);
				// System.out.println("coœdo");
			}

		} else {
			if (pressed) {
				if (this.jump)
					this.entityHost.jump = false;
				int valuedef = this.entityHost instanceof EntityHeavy ? 2 : 0;
				weapon.endUse(item, this.entityHost, this.entityHost.world,
						this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).state, valuedef);
				this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).state = valuedef;
				TF2Util.sendTracking(new TF2Message.ActionMessage(valuedef, entityHost),
						entityHost);
				// System.out.println("coœz");
			}
			pressed = false;
		}
		// if(){
	}

}
