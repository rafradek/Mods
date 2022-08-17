package rafradek.TF2weapons.entity.projectile;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rafradek.TF2weapons.TF2ConfigVars;
import rafradek.TF2weapons.common.TF2Attribute;
import rafradek.TF2weapons.util.TF2Util;

public class EntityFlame extends EntityProjectileBase {

	public EntityFlame(World world) {
		super(world);
	}

	public void initProjectile(EntityLivingBase shooter, EnumHand hand, ItemStack weapon) {
		super.initProjectile(shooter, hand, weapon);
		this.addVelocity(shooter.motionX, shooter.motionY, shooter.motionZ);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRender3d(double x, double y, double z) {
		return false;
	}

	@Override
	public void onHitGround(int x, int y, int z, RayTraceResult mop) {
		if (!this.world.isRemote && TF2ConfigVars.destTerrain==2
				&& this.world.getBlockState(mop.getBlockPos()).getMaterial().getCanBurn()
				&& this.world.getBlockState(mop.getBlockPos().offset(mop.sideHit)).getMaterial() != Material.FIRE
				&& this.world.getBlockState(mop.getBlockPos().offset(mop.sideHit)).getBlock().isReplaceable(world,
						getPosition().offset(mop.sideHit)))
			this.world.setBlockState(mop.getBlockPos().offset(mop.sideHit), Blocks.FIRE.getDefaultState());
		this.setDead();
	}

	@Override
	public void onHitMob(Entity entityHit, RayTraceResult mop) {
		if (!this.world.isRemote && !this.hitEntities.contains(entityHit)) {
			this.hitEntities.add(entityHit);
			int critical = TF2Util.calculateCritPost(entityHit, shootingEntity, this.getCritical(),
					this.usedWeapon);
			// float distance= (float) new Vec3d(this.shootingEntity.posX,
			// this.shootingEntity.posY,
			// this.shootingEntity.posZ).distanceTo(new Vec3d(mop.hitVec.x,
			// mop.hitVec.y, mop.hitVec.z))+5.028f;
			float dmg = TF2Util.calculateDamage(entityHit, world, this.shootingEntity, usedWeapon, critical,
					 1f + (float)(this.ticksExisted-1) / (this.getMaxTime()-1));
			// System.out.println("damage: "+dmg);
			// dmg*=ItemUsable.getData(this.usedWeapon).get("Min
			// damage").getDouble()+1-(this.ticksExisted/this.getMaxTime())*ItemUsable.getData(this.usedWeapon).get("Min
			// damage").getDouble();

			if (TF2Util.dealDamage(entityHit, this.world, this.shootingEntity, this.usedWeapon, critical, dmg,
					TF2Util.causeBulletDamage(this.usedWeapon, this.shootingEntity, critical, this).setFireDamage())
					&& (entityHit.ticksExisted - entityHit.getEntityData().getInteger("LastHitBurn") > 1
					|| entityHit.getEntityData().getInteger("LastHitBurn") > entityHit.ticksExisted)) {
				entityHit.getEntityData().setInteger("LastHitBurn", entityHit.ticksExisted);
				TF2Util.igniteAndAchievement(entityHit, this.shootingEntity, 1, TF2Attribute.getModifier("Burn Time", this.usedWeapon, 1, shootingEntity));
			}

		}
	}

	@Override
	public void onUpdate() {
		if (this.isInsideOfMaterial(Material.WATER)) {
			this.setDead();
			return;
		}
		super.onUpdate();
	}

	@Override
	public void spawnParticles(double x, double y, double z) {
		// TODO Auto-generated method stub

	}

	/*
	 * public double getMaxDistance(){ return 6.2865; }
	 */

	@Override
	public int getMaxTime() {
		return Math.round(3 + (TF2Attribute.getModifier("Flame Range", this.usedWeapon, 2f, this.shootingEntity)));
	}

	@Override
	protected float getSpeed() {
		return 1.2570f;
	}

	@Override
	public double getGravity() {
		return 0;
	}

	@Override
	public float getCollisionSize() {
		return 0.2f + this.ticksExisted * 0.18f;
	}
	
	public boolean isPushable() {
		return false;
	}
	
	public boolean canPenetrate() {
		return true;
	}
}
