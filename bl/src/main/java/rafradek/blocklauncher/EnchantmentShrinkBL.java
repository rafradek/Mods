package rafradek.blocklauncher;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class EnchantmentShrinkBL extends Enchantment {

	protected EnchantmentShrinkBL() {
		super(Enchantment.Rarity.RARE, BlockLauncher.enchType, EntityEquipmentSlot.values());
		this.setName("elasticity");
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack p_92089_1_) {
		return p_92089_1_.getItem() instanceof TNTCannon && !BlockLauncher.cannon.isActivator(p_92089_1_);
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public int getMinEnchantability(int p_77321_1_) {
		return 20 + p_77321_1_ * 6;
	}

	@Override
	public int getMaxEnchantability(int p_77321_1_) {
		return this.getMinEnchantability(p_77321_1_) + 6;
	}
}
