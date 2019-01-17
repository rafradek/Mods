package rafradek.TF2weapons.entity.projectile;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.client.ClientProxy;
import rafradek.TF2weapons.common.TF2Attribute;
import rafradek.TF2weapons.util.TF2Util;

public class EntityStickybomb extends EntityProjectileBase {

	public int stickCooldown;
	public EntityStickybomb(World p_i1756_1_) {
		super(p_i1756_1_);
		this.setSize(0.3f, 0.3f);
	}

	public void initProjectile(EntityLivingBase shooter, EnumHand hand, ItemStack weapon) {
		super.initProjectile(shooter, hand, weapon);
		this.setSize(0.3f, 0.3f);
		this.setType((int) TF2Attribute.getModifier("Weapon Mode", this.usedWeapon, 0, shooter));
	}
	
	@Override
	public float getPitchAddition() {
		return 3;
	}

	public boolean attackEntityFrom(DamageSource source, float damage){
		if (source.isExplosion() && !TF2Util.isOnSameTeam(source.getTrueSource(), this.shootingEntity)) {
			this.addStickCooldown();
		}
		return super.attackEntityFrom(source, damage);
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
	}
	
	public int getArmTime() {
		return Math.round(TF2Attribute.getModifier("Arm Time", this.usedWeapon, 0.8f, this.shootingEntity)*20);
	}
	public boolean canBeCollidedWith()
    {
        return this.isSticked();
    }
	
	@Override
	public void onHitGround(int x, int y, int z, RayTraceResult mop) {

	}

	@Override
	public void onHitMob(Entity entityHit, RayTraceResult mop) {

	}

	public double maxMotion() {
		return Math.max(this.motionX, Math.max(this.motionY, this.motionZ));
	}

	@Override
	public void spawnParticles(double x, double y, double z) {

	}

	public void addStickCooldown() {
		this.stickCooldown = 20;
		this.setSticked(false);
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		if (this.stickCooldown > 0) {
			this.stickCooldown--;
		}
		if (this.shootingEntity == null || !this.shootingEntity.isEntityAlive())
			this.setDead();
	}

	@Override
	public void setDead() {
		super.setDead();
		if (!this.world.isRemote)
			this.shootingEntity.getCapability(TF2weapons.WEAPONS_CAP, null).activeBomb.remove(this);
	}

	@Override
	protected float getSpeed() {
		return 0.7667625f;
	}

	@Override
	public double getGravity() {
		return 0.0381f;
	}

	@Override
	public boolean isSticky() {
		return this.stickCooldown <= 0;
	}

	@Override
	public boolean useCollisionBox() {
		return true;
	}
	
	@Override
	public int getMaxTime() {
		return 72000;
	}

	@Override
	public void onHitBlockX() {
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
	}

	@Override
	public void onHitBlockY(Block block) {
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
	}

	@Override
	public void onHitBlockZ() {
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
	}
	
	public boolean isGlowing()
    {
        return super.isGlowing() || (this.getType() == 1 && this.world.isRemote && this.shootingEntity==ClientProxy.getLocalPlayer() && this.ticksExisted >= this.getArmTime()&& TF2Util.lookingAt(this.shootingEntity, 30, this.posX, this.posY, this.posZ));
    }
}
