package rafradek.blocklauncher;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class EnchantmentFireBL extends Enchantment {

	protected EnchantmentFireBL() {
		super(Enchantment.Rarity.UNCOMMON, BlockLauncher.enchType, EntityEquipmentSlot.values());
		this.setName("hellfire");
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack p_92089_1_) {
		return p_92089_1_.getItem() instanceof TNTCannon && BlockLauncher.cannon.getType(p_92089_1_) == 3;
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
