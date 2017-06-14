package rafradek.blocklauncher;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class EnchantmentHeavyBL extends Enchantment {

	protected EnchantmentHeavyBL() {
		super(Enchantment.Rarity.UNCOMMON, BlockLauncher.enchType, EntityEquipmentSlot.values());
		this.setName("heavy");
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack p_92089_1_) {
		return p_92089_1_.getItem() instanceof TNTCannon && !BlockLauncher.cannon.isActivator(p_92089_1_)
				&& BlockLauncher.cannon.getType(p_92089_1_) != 3;
	}

	@Override
	public int getMaxLevel() {
		return 2;
	}

	@Override
	public int getMinEnchantability(int p_77321_1_) {
		return 20 + p_77321_1_ * 12;
	}

	@Override
	public int getMaxEnchantability(int p_77321_1_) {
		return this.getMinEnchantability(p_77321_1_) + 12;
	}
}
