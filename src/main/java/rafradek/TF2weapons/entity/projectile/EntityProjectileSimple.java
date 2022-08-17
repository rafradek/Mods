package rafradek.TF2weapons.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.common.TF2Attribute;
import rafradek.TF2weapons.item.ItemFromData;
import rafradek.TF2weapons.item.ItemSniperRifle;
import rafradek.TF2weapons.util.PropertyType;
import rafradek.TF2weapons.util.TF2Util;

public class EntityProjectileSimple extends EntityProjectileBase {

	float damage = -1;
	boolean impact = false;

	public EntityProjectileSimple(World world) {
		super(world);
		this.setSize(0.3F, 0.3F);
	}

	@Override
	public void initProjectile(EntityLivingBase shooter, EnumHand hand, ItemStack weapon) {
		super.initProjectile(shooter, hand, weapon);
		this.setSize(0.3F, 0.3F);
		if (this.usedWeapon.getTagCompound().getBoolean("ArrowLit")) {
			this.usedWeaponOrig.getTagCompound().setBoolean("ArrowLit", false);
			this.setFire(1000);
		}
		if(ItemFromData.getData(this.usedWeapon).getString(PropertyType.PROJECTILE).equals("repairclaw"))
			this.setType(0);
		else if(ItemFromData.getData(this.usedWeapon).getString(PropertyType.PROJECTILE).equals("syringe"))
			this.setType(1);
		else if(ItemFromData.getData(this.usedWeapon).getString(PropertyType.PROJECTILE).equals("cleaver"))
			this.setType(2);
		else if(ItemFromData.getData(this.usedWeapon).getString(PropertyType.PROJECTILE).equals("arrow"))
			this.setType(3);
		else if(ItemFromData.getData(this.usedWeapon).getString(PropertyType.PROJECTILE).equals("hhhaxe"))
			this.setType(8);
	}

	@Override
	public void onHitGround(int x, int y, int z, RayTraceResult mop) {
		if (!this.world.isRemote) {
			this.impact = true;

			if (ItemFromData.getData(this.usedWeapon).hasProperty(PropertyType.HIT_SOUND)) {
				SoundEvent event = ItemFromData.getData(this.usedWeapon).hasProperty(PropertyType.HIT_WORLD_SOUND)
						? ItemFromData.getSound(this.usedWeapon, PropertyType.HIT_WORLD_SOUND)
								: ItemFromData.getSound(this.usedWeapon, PropertyType.HIT_SOUND);
				this.playSound(event, 1.3f, 1f);
			}

			if (TF2Attribute.getModifier("Destroy Block", this.usedWeapon, 0, shootingEntity) > 0) {

				float damage = this.damage;
				if (damage == -1) {
					damage = TF2Util.calculateDamage(TF2weapons.dummyEnt, this.world, this.shootingEntity,
							this.usedWeapon, this.getCritical(),
							(float) this.shootingEntity.getPositionVector().distanceTo(mop.hitVec));
					if (this.usedWeapon.getItem() instanceof ItemSniperRifle)
						damage *= 2.52f;
					damage *= TF2Attribute.getModifier("Destroy Block", this.usedWeapon, 0, this.shootingEntity);
				}
				this.damage = TF2Util.damageBlock(mop.getBlockPos(), this.shootingEntity, this.world,
						this.usedWeapon, this.getCritical(), damage, null, null);
				if (this.damage <= 0)
					this.setDead();
			} else
				this.setDead();
		}
	}

	@Override
	public void onHitMob(Entity entityHit, RayTraceResult mop) {
		attackDirect(entityHit,1, mop.hitInfo instanceof Boolean ? (Boolean)mop.hitInfo : false, mop.hitVec);
	}

	@Override
	public void spawnParticles(double x, double y, double z) {

	}

	@Override
	public boolean isPushable() {
		return this.getType()!=1;
	}
}
