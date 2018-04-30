package rafradek.TF2weapons.weapons;

import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.collect.Multimap;

import net.minecraft.enchantment.EnumEnchantmentType;
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

public class ItemBackpack extends ItemFromData {
	
	private UUID ARMOR_MOD = UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E");
	private UUID MAX_HEALTH_MOD = UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D");
	private UUID ARMOR_TOUGHNESS_MOD = UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D");
	public ItemBackpack() {
		this.addPropertyOverride(new ResourceLocation("bodyModel"), new IItemPropertyGetter() {
			@Override
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				if (ItemWearable.usedModel == 1)
					return 1;
				return 0;
			}
		});
		this.addPropertyOverride(new ResourceLocation("headModel"), new IItemPropertyGetter() {
			@Override
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				if (ItemWearable.usedModel == 2)
					return 1;
				return 0;
			}
		});
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);

		if (slot == EntityEquipmentSlot.CHEST) {
			multimap.put(SharedMonsterAttributes.ARMOR.getName(),
					new AttributeModifier(ARMOR_MOD, "Armor modifier",
							TF2Attribute.getModifier("Armor", stack, getData(stack).getFloat(PropertyType.ARMOR), null), 0));
			multimap.put(SharedMonsterAttributes.MAX_HEALTH.getName(),
					new AttributeModifier(MAX_HEALTH_MOD, "Health modifier",
							TF2Attribute.getModifier("Health", stack, 0, null), 0));
			multimap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(),
					new AttributeModifier(ARMOR_TOUGHNESS_MOD,
							"Armor toughness modifier", TF2Attribute.getModifier("Armor", stack, 0, null) * 0.5f + getData(stack).getFloat(PropertyType.ARMOR_TOUGHNESS), 0));
		}

		return multimap;
	}

	@Override
	public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity) {
		return armorType == EntityEquipmentSlot.CHEST;
	}

	@Override
	public void onArmorTick(World world, final EntityPlayer player, ItemStack itemStack) {
		this.onArmorTickAny(world, player, itemStack);
	}
	
    public EntityEquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EntityEquipmentSlot.CHEST;
    }
	
	public void onArmorTickAny(World world, final EntityLivingBase player, ItemStack itemStack) {
		if (!world.isRemote) {
			if (player.ticksExisted % 20 == 0) {
				float heal = TF2Attribute.getModifier("Health Regen", itemStack, 0, player);
				if(heal > 0) {
					int lastHitTime = player.ticksExisted - player.getEntityData().getInteger("lasthit");
					if (lastHitTime >= 120)
						player.heal(heal);
					else if(lastHitTime >= 60)
						player.heal(TF2Util.lerp(heal, heal/4f, (lastHitTime-60)/60f));
					else
						player.heal(heal/4f);
				}
			}
			
		}
	}
	
	public boolean canApplyAtEnchantingTable(ItemStack stack, net.minecraft.enchantment.Enchantment enchantment)
    {
        return super.canApplyAtEnchantingTable(stack, enchantment) 
        		|| enchantment.type == EnumEnchantmentType.ARMOR_CHEST || enchantment.type == EnumEnchantmentType.ARMOR || enchantment.type == EnumEnchantmentType.WEARABLE;
    }
}
