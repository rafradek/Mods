package rafradek.TF2weapons.entity.projectile;

import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityStickProjectile extends EntityProjectileSimple {

	public boolean sticked;

	public boolean clientSticked;
	public EntityStickProjectile(World world) {
		super(world);
		this.setSize(0.3F, 0.3F);

	}

	@Override
	public void onHitGround(int x, int y, int z, RayTraceResult mop) {
		super.onHitGround(x, y, z, mop);
		if (!this.world.isAirBlock(mop.getBlockPos())) {
			this.setPosition(mop.hitVec.x + mop.sideHit.getFrontOffsetX() * 0.1,
					mop.hitVec.y + mop.sideHit.getFrontOffsetY() * 0.1f,
					mop.hitVec.z + mop.sideHit.getFrontOffsetZ() * 0.1);
			this.sticked = true;
			this.stickedBlock = mop.getBlockPos();
		}
	}

	@Override
	public void onUpdate() {
		if (this.ticksExisted > this.getMaxTime()
				|| (this.world.isRemote && this.sticked && this.world.isAirBlock(stickedBlock))) {
			this.setDead();
			return;
		} else if (!this.sticked)
			super.onUpdate();
	}

	@Override
	public void setDead() {
		if (this.world.isRemote && this.sticked && this.getThrower() != null && !this.clientSticked) {
			EntityStickProjectile ent = new EntityStickProjectile(this.world);
			ent.setPositionAndRotation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
			ent.sticked = true;
			ent.setType(this.getType());
			ent.setThrower(this.getThrower());
			ent.clientSticked = true;
			ent.stickedBlock = this.stickedBlock;
			this.world.spawnEntity(ent);
		}
		super.setDead();
	}

	@Override
	public void spawnParticles(double x, double y, double z) {}

	@Override
	public boolean moveable() {
		return !this.sticked;
	}
}
