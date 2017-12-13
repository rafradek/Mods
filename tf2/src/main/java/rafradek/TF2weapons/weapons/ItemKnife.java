package rafradek.TF2weapons.weapons;

import java.util.HashMap;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rafradek.TF2weapons.TF2Achievements;
import rafradek.TF2weapons.TF2DamageSource;
import rafradek.TF2weapons.TF2Util;
import rafradek.TF2weapons.building.EntityBuilding;

public class ItemKnife extends ItemMeleeWeapon {

	public ItemKnife() {
		super();
		this.addPropertyOverride(new ResourceLocation("backstab"), new IItemPropertyGetter() {
			@Override
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				if (entityIn == Minecraft.getMinecraft().player && Minecraft.getMinecraft().objectMouseOver != null
						&& Minecraft.getMinecraft().objectMouseOver.entityHit != null
						&& Minecraft.getMinecraft().objectMouseOver.entityHit
								.getDistanceSqToEntity(entityIn) <= getMaxRange(stack) * getMaxRange(stack)
						&& isBackstab(entityIn, Minecraft.getMinecraft().objectMouseOver.entityHit))
					return 1;
				return 0;
			}
		});
	}

	public void handleShoot(EntityLivingBase living, ItemStack stack, World world, HashMap<Entity, float[]> map,
			int critical, int flags) {
		for(Entity target: map.keySet()) {
			if(this.isBackstab(living, target)) {
				flags+=TF2DamageSource.BACKSTAB;
				break;
			}
		}
		super.handleShoot(living, stack, world, map, critical, flags);
	}
	public boolean isBackstab(EntityLivingBase living, Entity target) {
		if (target != null && target instanceof EntityLivingBase && !(target instanceof EntityBuilding)) {
			float ourAngle = 180 + MathHelper.wrapDegrees(living.rotationYawHead);
			float angle2 = (float) (MathHelper.atan2(living.posX - target.posX, living.posZ - target.posZ) * 180.0D
					/ Math.PI);
			// System.out.println(angle2);
			if (angle2 >= 0)
				angle2 = 180 - angle2;
			else
				angle2 = -180 - angle2;
			angle2 += 180;
			float enemyAngle = 180 + MathHelper.wrapDegrees(target.getRotationYawHead());
			float difference = 180 - Math.abs(Math.abs(ourAngle - enemyAngle) - 180);
			float difference2 = 180 - Math.abs(Math.abs(angle2 - enemyAngle) - 180);
			// System.out.println(angle2+" "+difference2+" "+difference);
			if (difference < 90 && difference2 < 90)
				return true;
		}
		return false;
	}

	@Override
	public float getWeaponDamage(ItemStack stack, EntityLivingBase living, Entity target) {
		return super.getWeaponDamage(stack, living, target) * (this.isBackstab(living, target) ? 10 : 1);
	}

	@Override
	public int setCritical(ItemStack stack, EntityLivingBase shooter, Entity target, int old) {
		return super.setCritical(stack, shooter, target, this.isBackstab(shooter, target) ? 2 : old);
	}

	/*@SideOnly(Side.CLIENT)
	public ModelResourceLocation getModel(ItemStack stack, EntityPlayer player, int useRemaining) {
		if (
			 * Minecraft.getMinecraft().player!=null&&Minecraft.getMinecraft(
			 * ).player.getHeldItem(EnumHand.MAIN_HAND)==stack&&
			 * stack.getItem() instanceof ItemKnife&&
			 /Minecraft.getMinecraft().objectMouseOver.entityHit != null
				&& this.isBackstab(player, Minecraft.getMinecraft().objectMouseOver.entityHit))
			return ClientProxy.nameToModel.get(stack.getTagCompound().getString("Type") + "/b");
		return null;
	}*/
	public void onDealDamage(ItemStack stack, EntityLivingBase attacker, Entity target, DamageSource source, float amount) {
		super.onDealDamage(stack, attacker, target, source, amount);
		if(attacker instanceof EntityPlayerMP&& isBackstab(attacker,target)&& target instanceof EntityLivingBase 
				&& !target.isEntityAlive() && TF2Util.isEnemy(attacker, (EntityLivingBase) target)){
			((EntityPlayerMP) attacker).addStat(TF2Achievements.KILLED_BACKSTAB);
			/*if(((EntityPlayerMP) attacker).getStatFile().readStat(TF2Achievements.KILLED_BACKSTAB)>=400)
				((EntityPlayerMP) attacker).addStat(TF2Achievements.SPYMASTER);
			if(attacker.getCapability(TF2weapons.PLAYER_CAP, null).sapperTime>0 && attacker.getCapability(TF2weapons.PLAYER_CAP, null).buildingOwnerKill==target)
				((EntityPlayerMP) attacker).addStat(TF2Achievements.SAP_STAB);*/
		}
	}
}
