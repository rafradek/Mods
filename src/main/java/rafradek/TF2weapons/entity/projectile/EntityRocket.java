package rafradek.TF2weapons.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2ConfigVars;
import rafradek.TF2weapons.client.ClientProxy;

public class EntityRocket extends EntityProjectileBase {

	public EntityRocket(World p_i1756_1_) {
		super(p_i1756_1_);
		if (p_i1756_1_.isRemote)
			ClientProxy.spawnRocketParticle(this.world, this);
		if (TF2ConfigVars.dynamicLights)
			this.makeLit();
	}

	@Override
	public void onHitGround(int x, int y, int z, RayTraceResult mop) {
		this.explode(mop.hitVec.x + mop.sideHit.getFrontOffsetX() * 0.02,
				mop.hitVec.y + mop.sideHit.getFrontOffsetY() * 0.02,
				mop.hitVec.z + mop.sideHit.getFrontOffsetZ() * 0.02, null, 1f);
	}

	@Override
	public void onHitMob(Entity entityHit, RayTraceResult mop) {
		this.explode(mop.hitVec.x, mop.hitVec.y, mop.hitVec.z, mop.entityHit, 1f);
	}

	public double maxMotion() {
		return Math.max(this.motionX, Math.max(this.motionY, this.motionZ));
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

	}

	@Override
	public void spawnParticles(double x, double y, double z) {
		if (this.isInWater())
			this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, x, y, z, this.motionX, this.motionY,
					this.motionZ);
		else
			this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0, 0, 0);
	}

	@Override
	protected float getSpeed() {
		return 1.04f;
	}

	@Override
	public double getGravity() {
		return 0;
	}

}
