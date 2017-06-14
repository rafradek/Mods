package rafradek.crit;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class EnchantmentCrit extends Enchantment {
	protected EnchantmentCrit() {
		super(Enchantment.Rarity.UNCOMMON, EnumEnchantmentType.WEAPON, EntityEquipmentSlot.values());
		this.setName("crit");
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {
		return stack.getItem() instanceof ItemSword;
	}
	
	public boolean canApply(ItemStack stack)
    {
        return stack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).containsKey(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
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
