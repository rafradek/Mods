package rafradek.TF2weapons.weapons;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import rafradek.TF2weapons.message.TF2Message.PredictionMessage;

public class ItemCleaver extends ItemProjectileWeapon {

	public ItemCleaver() {
		super();
		this.setMaxStackSize(16);
	}

	@Override
	public boolean doMuzzleFlash(ItemStack stack, EntityLivingBase attacker, EnumHand hand) {
		return false;
	}
	
	@Override
	public boolean use(ItemStack stack, EntityLivingBase living, World world, EnumHand hand,
			PredictionMessage message) {
		if (super.use(stack, living, world, hand, message) && !world.isRemote) {
			stack.shrink(1);
		}
		return true;
	}
}
