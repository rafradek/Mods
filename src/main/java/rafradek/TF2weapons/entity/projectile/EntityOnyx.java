package rafradek.TF2weapons.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityOnyx extends EntityProjectileBase {

	public EntityOnyx(World p_i1756_1_) {
		super(p_i1756_1_);
	}

	@Override
	public int getMaxTime() {
		return 8;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (this.ticksExisted >= this.getMaxTime()) {
			this.explode(this.posX, this.posY, this.posZ, null, 1f);
		}
	}

	@Override
	public void spawnParticles(double x, double y, double z) {
		this.world.spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, 0.38, 0.08, 0.7);
	}

	@Override
	protected float getSpeed() {
		return 2.5f;
	}

	@Override
	public float getExplosionSize() {
		return 1.7f;
	}

	@Override
	public double getGravity() {
		return 0;
	}

	@Override
	public void onHitGround(int x, int y, int z, RayTraceResult mop) {
		this.explode(mop.hitVec.x + mop.sideHit.getFrontOffsetX() * 0.05,
				mop.hitVec.y + mop.sideHit.getFrontOffsetY() * 0.05,
				mop.hitVec.z + mop.sideHit.getFrontOffsetZ() * 0.05, null, 1f);
	}

	@Override
	public void onHitMob(Entity entityHit, RayTraceResult mop) {
		this.explode(mop.hitVec.x, mop.hitVec.y, mop.hitVec.z, mop.entityHit, 1f);
	}
}
