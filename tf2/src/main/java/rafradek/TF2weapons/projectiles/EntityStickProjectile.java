package rafradek.TF2weapons.projectiles;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityStickProjectile extends EntityProjectileSimple {

	public boolean sticked;

	public EntityStickProjectile(World world) {
		super(world);
		this.setSize(0.3F, 0.3F);
		
	}

	public EntityStickProjectile(World world, EntityLivingBase living, EnumHand hand) {
		super(world, living, hand);
		this.setSize(0.3F, 0.3F);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onHitGround(int x, int y, int z, RayTraceResult mop) {
		super.onHitGround(x, y, z, mop);
		this.setPosition(mop.hitVec.x + mop.sideHit.getFrontOffsetX() * 0.1,
				mop.hitVec.y + mop.sideHit.getFrontOffsetY() * 0.1f,
				mop.hitVec.z + mop.sideHit.getFrontOffsetZ() * 0.1);
		this.sticked = true;
		this.stickedBlock = mop.getBlockPos();
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
		if (this.impact)
			this.impact = false;
		else
			super.setDead();
	}

	@Override
	public void spawnParticles(double x, double y, double z) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean moveable() {
		return !this.sticked;
	}
}
