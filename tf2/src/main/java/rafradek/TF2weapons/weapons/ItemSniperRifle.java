package rafradek.TF2weapons.weapons;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2Achievements;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.TF2DamageSource;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.characters.EntitySniper;
import rafradek.TF2weapons.message.TF2Message.PredictionMessage;

public class ItemSniperRifle extends ItemBulletWeapon {
	public static UUID slowdownUUID = UUID.fromString("12843092-A5D6-BBCD-3D4F-A3DD4D8C65A9");
	public static AttributeModifier slowdown = new AttributeModifier(slowdownUUID, "sniper slowdown", -0.73D, 2);

	@Override
	public boolean canAltFire(World worldObj, EntityLivingBase player, ItemStack item) {
		return super.canAltFire(worldObj, player, item)
				&& player.getCapability(TF2weapons.WEAPONS_CAP, null).fire1Cool <= 0;
	}

	@Override
	public boolean use(ItemStack stack, EntityLivingBase living, World world, EnumHand hand,
			PredictionMessage message) {
		if (living instanceof EntityPlayer || stack.getTagCompound().getBoolean("WaitProper")) {
			super.use(stack, living, world, hand, message);
			this.disableZoom(stack, living);
			stack.getTagCompound().setBoolean("WaitProper", false);
			if(message != null &&(message.readData==null || message.readData.isEmpty()))
				living.getCapability(TF2weapons.PLAYER_CAP, null).headshotsRow=0;
			return true;
		} else {
			stack.getTagCompound().setBoolean("WaitProper", true);
			this.altUse(stack, living, world);
			living.getCapability(TF2weapons.WEAPONS_CAP, null).fire1Cool = 2500;
		}
		return false;
	}

	@Override
	public void altUse(ItemStack stack, EntityLivingBase living, World world) {
		WeaponsCapability cap = living.getCapability(TF2weapons.WEAPONS_CAP, null);
		if (!cap.charging) {
			cap.charging = true;
			if (world.isRemote && living == Minecraft.getMinecraft().player)
				Minecraft.getMinecraft().gameSettings.mouseSensitivity *= 0.4f;
			if (living.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(slowdownUUID) == null)
				living.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(slowdown);
		} else
			this.disableZoom(stack, living);
		cap.fire1Cool=400;

	}

	public void disableZoom(ItemStack stack, EntityLivingBase living) {
		WeaponsCapability cap = living.getCapability(TF2weapons.WEAPONS_CAP, null);
		if (living.world.isRemote && living == Minecraft.getMinecraft().player && cap.charging)
			Minecraft.getMinecraft().gameSettings.mouseSensitivity *= 2.5f;
		cap.chargeTicks = 0;
		cap.charging = false;
		living.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(slowdown);
	}

	@Override
	public boolean canHeadshot(EntityLivingBase living, ItemStack stack) {
		// TODO Auto-generated method stub
		return living.getCapability(TF2weapons.WEAPONS_CAP, null).chargeTicks > 4;
	}

	@Override
	public boolean showTracer(ItemStack stack) {
		return TF2Attribute.getModifier("Weapon Mode", stack, 0, null) == 1;
	}
	
	@Override
	public boolean showSpecialTracer(ItemStack stack) {
		return TF2Attribute.getModifier("Weapon Mode", stack, 0, null) == 1;
	}
	
	@Override
	public float getWeaponDamage(ItemStack stack, EntityLivingBase living, Entity target) {
		return super.getWeaponDamage(stack, living, target) * (living != null ? this.getZoomBonus(stack, living) * 
				(living.getCapability(TF2weapons.WEAPONS_CAP, null).chargeTicks >= getChargeTime(stack, living) ? TF2Attribute.getModifier("Damage Charged", stack, 1, living): 1) : 1);
	}

	@Override
	public float getWeaponMaxDamage(ItemStack stack, EntityLivingBase living) {
		return super.getWeaponMaxDamage(stack, living);
	}

	@Override
	public float getWeaponMinDamage(ItemStack stack, EntityLivingBase living) {
		return super.getWeaponMinDamage(stack, living);
	}

	public float getZoomBonus(ItemStack stack, EntityLivingBase living) {
		return 1 + Math.max(0, (living.getCapability(TF2weapons.WEAPONS_CAP, null).chargeTicks - 20)
				/ ((getChargeTime(stack, living) - 20) / 2));
	}

	public static float getChargeTime(ItemStack stack, EntityLivingBase living) {
		return 66 / TF2Attribute.getModifier("Charge", stack, 1, living);
	}

	@Override
	public short getAltFiringSpeed(ItemStack item, EntityLivingBase player) {
		return 400;
	}

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5) {
		super.onUpdate(par1ItemStack, par2World, par3Entity, par4, par5);
		WeaponsCapability cap = par3Entity.getCapability(TF2weapons.WEAPONS_CAP, null);

		if (cap.charging && par5)
			if (cap.chargeTicks < getChargeTime(par1ItemStack, (EntityLivingBase) par3Entity))
				cap.chargeTicks += 1;
		// System.out.println("Charging: "+cap.chargeTicks);

		if (par3Entity instanceof EntitySniper && ((EntitySniper) par3Entity).getAttackTarget() != null
				&& par1ItemStack.getTagCompound().getBoolean("WaitProper"))
			if (((EntitySniper) par3Entity).getHealth() < 8 && cap.fire1Cool > 250)
				par3Entity.getCapability(TF2weapons.WEAPONS_CAP, null).fire1Cool = 250;
	}

	/*
	 * public double getDiff(EntityTF2Character mob){
	 * if(mob.getAttackTarget()!=null){
	 * mob.attack.lookingAt(mob.getAttackTarget(),2) double
	 * mX=mob.getAttackTarget().posX-mob.getAttackTarget().lastTickPosX; double
	 * mY=mob.getAttackTarget().posY-mob.getAttackTarget().lastTickPosY; double
	 * mZ=mob.getAttackTarget().posZ-mob.getAttackTarget().lastTickPosZ; double
	 * totalMotion=Math.sqrt(mX*mX+mY*mY+mZ*mZ);
	 * System.out.println("Odskok: "+totalMotion); return totalMotion; } return
	 * 0; }
	 */
	@Override
	public void holster(WeaponsCapability cap, ItemStack stack, EntityLivingBase living, World world) {
		this.disableZoom(stack, living);
		super.holster(cap, stack, living, world);
	}
	
	@Override
	public boolean canFire(World worldObj, EntityLivingBase player, ItemStack item) {
		if(super.canFire(worldObj, player, item)) {
			if(player instanceof EntityPlayer && TF2Attribute.getModifier("Weapon Mode", item, 0, player) == 1 && !player.getCapability(TF2weapons.WEAPONS_CAP, null).charging) {
				TF2weapons.playSound(player,getSound(item, PropertyType.NO_FIRE_SOUND),0.7f,1);
				return false;
			}
			return true;
		}
		return false;
	}
	
	public void onDealDamage(ItemStack stack, EntityLivingBase attacker, Entity target, DamageSource source, float amount) {
		super.onDealDamage(stack, attacker, target, source, amount);
		/*if(attacker instanceof EntityPlayerMP && target instanceof EntityLivingBase && !target.isEntityAlive() && TF2weapons.isEnemy(attacker, (EntityLivingBase) target)){
			if(!attacker.getCapability(TF2weapons.WEAPONS_CAP, null).charging){
				((EntityPlayerMP) attacker).addStat(TF2Achievements.KILLED_NOSCOPE);
				if(((EntityPlayerMP) attacker).getStatFile().readStat(TF2Achievements.KILLED_NOSCOPE)>=10)
					((EntityPlayerMP) attacker).addStat(TF2Achievements.NO_SCOPE);
			}
			if(((TF2DamageSource)source).getCritical()==2){
				if(++attacker.getCapability(TF2weapons.PLAYER_CAP, null).headshotsRow>=3)
					((EntityPlayerMP) attacker).addStat(TF2Achievements.EFFICIENT_SNIPER);
			}
			else
				attacker.getCapability(TF2weapons.PLAYER_CAP, null).headshotsRow=0;
		}*/
	}
	public void doFireSound(ItemStack stack, EntityLivingBase living, World world, int critical) {
		if (ItemFromData.getData(stack).hasProperty(PropertyType.CHARGED_FIRE_SOUND) 
				&& living.getCapability(TF2weapons.WEAPONS_CAP, null).chargeTicks >= getChargeTime(stack, living)) {
			SoundEvent soundToPlay = SoundEvent.REGISTRY
					.getObject(new ResourceLocation(ItemFromData.getData(stack).getString(PropertyType.CHARGED_FIRE_SOUND)
							+ (critical == 2 ? ".crit" : "")));
			living.playSound(soundToPlay, 4f, 1f);
			if (world.isRemote)
				ClientProxy.removeReloadSound(living);
		}
		else {
			super.doFireSound(stack, living, world, critical);
		}
	}
}
