package rafradek.TF2weapons.weapons;

import java.util.UUID;

import com.google.common.collect.Multimap;

import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.ISpecialArmor.ArmorProperties;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.WeaponData.PropertyType;

public class ItemParachute extends ItemFromData implements ISpecialArmor {

	public ItemParachute() {
		this.setMaxDamage(1000);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
		return new ArmorProperties(0,0,Integer.MAX_VALUE);
	}

	
	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);

		if (slot == EntityEquipmentSlot.CHEST) {
			multimap.put(SharedMonsterAttributes.ARMOR.getName(),
					new AttributeModifier(UUID.fromString("D8499B04-0E66-4726-AB29-64469D234E0D"), "Armor modifier", getData(stack).getFloat(PropertyType.ARMOR), 0));
			multimap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(),
					new AttributeModifier(UUID.fromString("D8499B04-0E66-4726-AB29-64469D234AB7"),
							"Armor toughness modifier", getData(stack).getFloat(PropertyType.ARMOR_TOUGHNESS), 0));
		}

		return multimap;
	}
	
	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
		stack.damageItem((int) (damage * (stack.getTagCompound().getBoolean("Deployed")?8:1)), entity);
	}

	@Override
	public void onArmorTick(World world, final EntityPlayer player, ItemStack itemStack) {
		if (itemStack.getTagCompound().getBoolean("Deployed")) {
			player.motionY=Math.max(-0.1f, player.motionY);
			player.fallDistance=0f;
			/*if (player.ticksExisted % 30 == 0) {
				itemStack.damageItem(1, player);
			}*/
			if (player.onGround || player.isInsideOfMaterial(Material.WATER))
				itemStack.getTagCompound().setBoolean("Deployed", false);
		}
	}
	@Override
	public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity) {
		return armorType == EntityEquipmentSlot.CHEST;
	}
	
	public boolean canApplyAtEnchantingTable(ItemStack stack, net.minecraft.enchantment.Enchantment enchantment)
    {
        return super.canApplyAtEnchantingTable(stack, enchantment) 
        		|| enchantment.type == EnumEnchantmentType.ARMOR_CHEST || enchantment.type == EnumEnchantmentType.ARMOR || enchantment.type == EnumEnchantmentType.WEARABLE;
    }
}
