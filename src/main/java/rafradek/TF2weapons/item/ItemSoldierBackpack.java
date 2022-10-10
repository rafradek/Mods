package rafradek.TF2weapons.item;

import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.collect.Multimap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rafradek.TF2weapons.TF2ConfigVars;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.common.TF2Attribute;
import rafradek.TF2weapons.common.WeaponsCapability;
import rafradek.TF2weapons.common.WeaponsCapability.RageType;
import rafradek.TF2weapons.entity.mercenary.EntityTF2Character;
import rafradek.TF2weapons.util.PropertyType;
import rafradek.TF2weapons.util.TF2Util;

public class ItemSoldierBackpack extends ItemBackpack {

	public static ItemStack horn = ItemStack.EMPTY;
	
	public ItemSoldierBackpack() {
		super();
		this.addPropertyOverride(new ResourceLocation("active"),new IItemPropertyGetter() {
			@Override
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				if (entityIn != null && WeaponsCapability.get(entityIn) != null && WeaponsCapability.get(entityIn).isRageActive(RageType.BANNER))
					return 1;
				return 0;
			}
		});
		
	}

	public Potion getBuff(ItemStack stack) {
		return Potion.getPotionFromResourceLocation(getData(stack).getString(PropertyType.EFFECT_TYPE));
	}

	public RageType getRageType(ItemStack stack, EntityLivingBase living) {
		return RageType.BANNER;
	}
	
	public float getMaxRage(ItemStack stack, EntityLivingBase living) {
		return 1f;
	}
	
	public ItemStack getBackpackItemToUse(ItemStack stack, EntityLivingBase player) {
		if (horn.isEmpty())
			horn = new ItemStack(TF2weapons.itemHorn);
    	return horn;
    }
	
	public void addRage(ItemStack stack, float damage, EntityLivingBase target, EntityLivingBase attacker) {
		if (target instanceof EntityTF2Character && !(attacker instanceof EntityPlayer))
			damage *= 0.5f;
		else if (!(target instanceof EntityPlayer))
			damage *= 0.35f;
		this.addRage(stack, attacker, damage / getData(stack).getFloat(PropertyType.DAMAGE));
	}
	
	public void onArmorTickAny(World world, final EntityLivingBase player, ItemStack itemStack) {
		super.onArmorTickAny(world, player, itemStack);
		if (!world.isRemote) {
			if (player.ticksExisted % 5 == 0 && WeaponsCapability.get(player).isRageActive(RageType.BANNER)) {
				/*);
				itemStack.getTagCompound().setFloat("Rage",
						Math.max(0f, itemStack.getTagCompound().getFloat("Rage") - 1f / duration));
				if (itemStack.getTagCompound().getFloat("Rage") <= 0)
					itemStack.getTagCompound().setBoolean("Active", false);*/
				for (EntityLivingBase living : world.getEntitiesWithinAABB(EntityLivingBase.class,
						player.getEntityBoundingBox().grow(10, 10, 10), new Predicate<EntityLivingBase>() {

							@Override
							public boolean apply(EntityLivingBase input) {
								// TODO Auto-generated method stub
								return TF2Util.isOnSameTeam(player, input);
							}

						}))
					TF2Util.addAndSendEffect(living,new PotionEffect(this.getBuff(itemStack), 25));
			}
		}
	}
	
	public void setActive(EntityLivingBase player, ItemStack stack) {
		float duration = TF2Attribute.getModifier("Effect Duration", stack,
				getData(stack).getInt(PropertyType.DURATION), player)*(TF2ConfigVars.longDurationBanner? 2 : 5);
		WeaponsCapability.get(player).setRageActive(RageType.BANNER, true, (1f/duration) * 20f);
	}
}
