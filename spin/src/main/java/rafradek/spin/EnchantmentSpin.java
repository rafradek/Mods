package rafradek.spin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class EnchantmentSpin extends Enchantment {
	protected EnchantmentSpin() {
		super(Enchantment.Rarity.UNCOMMON, EnumEnchantmentType.WEAPON, EntityEquipmentSlot.values());
		this.setName("spin");
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {
		return stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemAxe;
	}

	@Override
	public int getMaxLevel() {
		return 4;
	}

	@Override
	public int getMinEnchantability(int p_77321_1_) {
		return 3 + p_77321_1_ * 8;
	}

	@Override
	public int getMaxEnchantability(int p_77321_1_) {
		return this.getMinEnchantability(p_77321_1_) + 8;
	}
}
