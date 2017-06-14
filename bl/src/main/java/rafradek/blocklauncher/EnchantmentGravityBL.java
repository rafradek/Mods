package rafradek.blocklauncher;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class EnchantmentGravityBL extends Enchantment {

	protected EnchantmentGravityBL() {
		super(Enchantment.Rarity.RARE, BlockLauncher.enchType, EntityEquipmentSlot.values());
		this.setName("gravity");
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack p_92089_1_) {
		return p_92089_1_.getItem() instanceof TNTCannon;
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public int getMinEnchantability(int p_77321_1_) {
		return 30;
	}

	@Override
	public int getMaxEnchantability(int p_77321_1_) {
		return 50;
	}

}
