package rafradek.TF2weapons.weapons;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import rafradek.TF2weapons.EntityDummy;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.MapList;
import rafradek.TF2weapons.TF2Sounds;
import rafradek.TF2weapons.TF2Util;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.building.EntityBuilding;
import rafradek.TF2weapons.characters.EntityScout;
import rafradek.TF2weapons.projectiles.EntityBall;
import rafradek.TF2weapons.projectiles.EntityProjectileBase;

public class ItemProjectileWeapon extends ItemWeapon {

	@Override
	public void shoot(ItemStack stack, EntityLivingBase living, World world, int thisCritical, EnumHand hand) {
		if (!world.isRemote) {
			// System.out.println("Tick: "+living.ticksExisted);
			EntityProjectileBase proj;
			/*
			 * double oldX=living.posX; double oldY=living.posY; double
			 * oldZ=living.posZ; float oldPitch=living.rotationPitch; float
			 * oldYaw=living.rotationYawHead; if(this.usePrediction()&&living
			 * instanceof EntityPlayer){ PredictionMessage
			 * message=TF2ProjectileHandler.nextShotPos.get(living);
			 * living.posX=message.x; living.posY=message.y;
			 * living.posZ=message.z; living.rotationYawHead=message.yaw;
			 * living.rotationPitch=message.pitch; }
			 */
			try {
				proj = MapList.projectileClasses.get(ItemFromData.getData(stack).getString(PropertyType.PROJECTILE))
						.getConstructor(World.class, EntityLivingBase.class, EnumHand.class)
						.newInstance(world, living, hand);
				// proj.setIsCritical(thisCritical);
				world.spawnEntity(proj);
				proj.setCritical(thisCritical);
				proj.infinite = this.isProjectileInfinite(living, stack);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
		// living.posX=oldX;
		// living.posY=oldY;
		// living.posZ=oldZ;
		// living.rotationPitch=oldPitch;
		// living.rotationYawHead=oldYaw;
	}

	@Override
	public void onDealDamage(ItemStack stack, EntityLivingBase attacker, Entity target, DamageSource source, float amount) {
		super.onDealDamage(stack, attacker, target, source, amount);
		if (target instanceof EntityLivingBase && !(target instanceof EntityBuilding)
				&& getData(stack).getName().equals("sandmanball")) {
			EntityBall ball = (EntityBall) source.getImmediateSource();
			double reduce=Math.max(0.5, (25-((EntityLivingBase) target).getEntityAttribute(SharedMonsterAttributes.ARMOR).getAttributeValue())/25D);
			if (!ball.canBePickedUp && ball.throwPos.squareDistanceTo(target.getPositionVector()) > 1100) {
				TF2Util.stun((EntityLivingBase) target, 
						(int) (160 * reduce), true);
				target.playSound(TF2Sounds.WEAPON_STUN_MAX, 4f, 1f);
			} else if (!ball.canBePickedUp && ball.throwPos.squareDistanceTo(target.getPositionVector()) > 12) {
				TF2Util.stun((EntityLivingBase) target,
						(int) (ball.throwPos.distanceTo(target.getPositionVector()) * 8 * reduce), false);
				target.playSound(TF2Sounds.WEAPON_STUN, 1.6f, 1f);
			}
		}
	}

	public boolean isProjectileInfinite(EntityLivingBase living, ItemStack stack) {
		return !(living instanceof EntityDummy) && this.searchForAmmo(living, stack) == ItemAmmo.STACK_FILL;
	}

	@Override
	public boolean canFire(World world, EntityLivingBase living, ItemStack stack) {
		return /*
				 * (((!(living instanceof EntityPlayer) || ) ||
				 * TF2ProjectileHandler.nextShotPos.containsKey(living))||world.
				 * isRemote
				 */super.canFire(world, living, stack) && !(living instanceof EntityScout && ((EntityScout)living).usedSlot == 2 && ((EntityScout)living).ballCooldown > 0);
	}
}
