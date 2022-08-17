package rafradek.TF2weapons.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import rafradek.TF2weapons.entity.mercenary.EntityDemoman;

public class EntityAISentryBuster extends EntityAIBase {

	protected EntityDemoman attacker;
	/** An amount of decrementing ticks that allows the entity to attack once the tick reaches 0. */
	protected int attackTick;
	/** The speed with which the mob will approach the target */
	double speedTowardsTarget;
	/** When true, the mob will continue chasing its target, even if it can't find a path to them right now. */
	boolean longMemory;
	/** The PathEntity of our entity. */
	Path path;
	private int delayCounter;
	private double targetX;
	private double targetY;
	private double targetZ;
	protected final int attackInterval = 20;
	private double lastDistance=Double.MAX_VALUE;
	private int distanceIncreased = 0;

	public EntityAISentryBuster(EntityDemoman creature)
	{
		this.attacker = creature;
		this.setMutexBits(3);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	@Override
	public boolean shouldExecute()
	{
		EntityLivingBase entitylivingbase = this.attacker.target;
		BlockPos target = this.attacker.targetpos;
		if (entitylivingbase != null)
			target = entitylivingbase.getPosition();

		if (target != null)
		{
			this.path = this.attacker.getNavigator().getPathToPos(target);

			if (this.path != null)
			{
				return true;
			}
			else
			{
				return this.getAttackReachSqr() >= this.attacker.getDistanceSq(target);
			}
		}
		return false;
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	@Override
	public boolean shouldContinueExecuting()
	{
		EntityLivingBase entitylivingbase = this.attacker.target;
		BlockPos target = this.attacker.targetpos;
		if (entitylivingbase != null)
			target = entitylivingbase.getPosition();

		if (target == null)
		{
			return false;
		}
		else if (!this.attacker.isWithinHomeDistanceFromPosition(new BlockPos(entitylivingbase)))
		{
			return false;
		}
		else
		{
			return !(entitylivingbase instanceof EntityPlayer) || !((EntityPlayer)entitylivingbase).isSpectator() && !((EntityPlayer)entitylivingbase).isCreative();
		}
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	@Override
	public void startExecuting()
	{
		this.attacker.getNavigator().setPath(this.path, 2.4);
		this.delayCounter = 0;
	}

	/**
	 * Reset the task's internal state. Called when this task is interrupted by another one
	 */
	@Override
	public void resetTask()
	{
		EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();

		if (entitylivingbase instanceof EntityPlayer && (((EntityPlayer)entitylivingbase).isSpectator() || ((EntityPlayer)entitylivingbase).isCreative()))
		{
			this.attacker.setAttackTarget((EntityLivingBase)null);
		}

		this.attacker.getNavigator().clearPath();
	}

	/**
	 * Keep ticking a continuous task that has already been started
	 */
	@Override
	public void updateTask()
	{
		EntityLivingBase entitylivingbase = this.attacker.target;
		BlockPos target = this.attacker.targetpos;
		if (entitylivingbase != null)
			target = entitylivingbase.getPosition();
		this.attacker.getLookHelper().setLookPosition(target.getX(), target.getY(),target.getZ(), 30.0F, 30.0F);
		double d0 = this.attacker.getDistanceSq(target);
		--this.delayCounter;

		if (d0 >= this.lastDistance) {
			this.distanceIncreased+=3;
			if (this.distanceIncreased > 300) {
				this.attacker.explode();
			}
		}
		else
			this.distanceIncreased -=1;
		if (this.delayCounter <= 0 && (this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D || entitylivingbase.getDistanceSq(this.targetX, this.targetY, this.targetZ) >= 1.0D || this.attacker.getRNG().nextFloat() < 0.05F))
		{
			this.targetX = target.getX();
			this.targetY = target.getY();
			this.targetZ = target.getZ();
			this.delayCounter = 4 + this.attacker.getRNG().nextInt(7);

			if (d0 > 1024.0D)
			{
				this.delayCounter += 10;
			}
			else if (d0 > 256.0D)
			{
				this.delayCounter += 5;
			}

			if (!this.attacker.getNavigator().tryMoveToXYZ(target.getX(), target.getY(), target.getZ(), 2.4f))
			{
				this.distanceIncreased += 75;
				this.delayCounter += 15;
			}
		}

		this.lastDistance = d0;
		this.attackTick = Math.max(this.attackTick - 1, 0);
		this.checkAndPerformAttack(d0);
	}

	protected void checkAndPerformAttack(double p_190102_2_)
	{
		double d0 = this.getAttackReachSqr();

		if (p_190102_2_ <= d0 && this.attackTick <= 0)
		{
			this.attacker.explode();
		}
	}

	protected double getAttackReachSqr()
	{
		return (double)(this.attacker.width * 2.0F * this.attacker.width * 2.0F);
	}
}
