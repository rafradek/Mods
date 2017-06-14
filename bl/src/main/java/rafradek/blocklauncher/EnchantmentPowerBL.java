package rafradek.blocklauncher;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class EnchantmentPowerBL extends Enchantment {

	protected EnchantmentPowerBL() {
		super(Enchantment.Rarity.COMMON, BlockLauncher.enchType, EntityEquipmentSlot.values());
		this.setName("arrowDamage");
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack p_92089_1_) {
		return p_92089_1_.getItem() instanceof TNTCannon;
	}

	@Override
	public int getMaxLevel() {
		return 4;
	}

	@Override
	public int getMinEnchantability(int p_77321_1_) {
		return 1 + (p_77321_1_ - 1) * 15;
	}

	@Override
	public int getMaxEnchantability(int p_77321_1_) {
		return this.getMinEnchantability(p_77321_1_) + 15;
	}
}
