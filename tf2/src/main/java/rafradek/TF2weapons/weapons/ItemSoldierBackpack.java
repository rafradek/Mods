package rafradek.TF2weapons.weapons;

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
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.TF2Util;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.characters.EntityTF2Character;
import rafradek.TF2weapons.decoration.ItemWearable;

public class ItemSoldierBackpack extends ItemBackpack {


	public Potion getBuff(ItemStack stack) {
		return Potion.getPotionFromResourceLocation(getData(stack).getString(PropertyType.EFFECT_TYPE));
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return stack.getTagCompound().getFloat("Rage") != 1;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return 1 - stack.getTagCompound().getFloat("Rage");
	}

	public void addRage(ItemStack stack, float damage, EntityLivingBase target) {
		if (target instanceof EntityTF2Character)
			damage *= 0.5f;
		else if (!(target instanceof EntityPlayer))
			damage *= 0.35f;
		stack.getTagCompound().setFloat("Rage", Math.min(1,
				stack.getTagCompound().getFloat("Rage") + damage / getData(stack).getFloat(PropertyType.DAMAGE)));
	}
	
	public void onArmorTickAny(World world, final EntityLivingBase player, ItemStack itemStack) {
		super.onArmorTickAny(world, player, itemStack);
		if (!world.isRemote) {
			if (player.ticksExisted % 5 == 0 && itemStack.getTagCompound().getBoolean("Active")) {
				itemStack.getTagCompound().setFloat("Rage",
						Math.max(0,
								itemStack.getTagCompound().getFloat("Rage")
										- 1 / (TF2Attribute.getModifier("Effect Duration", itemStack,
												getData(itemStack).getInt(PropertyType.DURATION), player) - 20)));
				if (itemStack.getTagCompound().getFloat("Rage") <= 0)
					itemStack.getTagCompound().setBoolean("Active", false);
				for (EntityLivingBase living : world.getEntitiesWithinAABB(EntityLivingBase.class,
						player.getEntityBoundingBox().grow(10, 10, 10), new Predicate<EntityLivingBase>() {

							@Override
							public boolean apply(EntityLivingBase input) {
								// TODO Auto-generated method stub
								return TF2Util.isOnSameTeam(player, input);
							}

						}))
					living.addPotionEffect(new PotionEffect(this.getBuff(itemStack), 25));

			}
			if (player instanceof EntityPlayer && ((EntityPlayer) player).isCreative())
				itemStack.getTagCompound().setFloat("Rage", 1);
			
		}
	}
}
