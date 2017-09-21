package rafradek.TF2weapons.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.TF2weapons;

public class EntityRocket extends EntityProjectileBase {

	public EntityRocket(World p_i1756_1_) {
		super(p_i1756_1_);
		if (p_i1756_1_.isRemote)
			ClientProxy.spawnRocketParticle(this.world, this);
		if (TF2weapons.dynamicLights)
			this.makeLit();
	}

	public EntityRocket(World p_i1756_1_, EntityLivingBase p_i1756_2_, EnumHand hand) {
		super(p_i1756_1_, p_i1756_2_, hand);

	}

	@Override
	public void onHitGround(int x, int y, int z, RayTraceResult mop) {
		this.explode(mop.hitVec.xCoord + mop.sideHit.getFrontOffsetX() * 0.05,
				mop.hitVec.yCoord + mop.sideHit.getFrontOffsetY() * 0.05,
				mop.hitVec.zCoord + mop.sideHit.getFrontOffsetZ() * 0.05, null, 1f);
	}

	@Override
	public void onHitMob(Entity entityHit, RayTraceResult mop) {
		this.explode(mop.hitVec.xCoord, mop.hitVec.yCoord, mop.hitVec.zCoord, mop.entityHit, 1f);
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
