package rafradek.TF2weapons.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;

public interface IEntityTF2 {

	default boolean hasHead() {
		return false;
	}

	default AxisAlignedBB getHeadBox() {
		return null;
	};

	default boolean hasDamageFalloff() {
		return true;
	}

	default boolean isBuilding() {
		return false;
	}

	default boolean isBackStabbable(EntityLivingBase attacker, ItemStack knife) {
		return true;
	}

	default float getBackstabDamageReduction(EntityLivingBase attacker, ItemStack knife, float mult) {
		return mult;
	}
}
