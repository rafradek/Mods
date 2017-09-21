package rafradek.TF2weapons.weapons;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2Attribute;
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
			if(!(living instanceof EntityPlayer && ((EntityPlayer)living).capabilities.isCreativeMode))
			stack.shrink(1);
			if(living instanceof EntityPlayer)
				((EntityPlayer)living).getCooldownTracker().setCooldown(this, this.getFiringSpeed(stack, living)/50);
		}
		return true;
	}
	@Override
	public boolean canFire(World world, EntityLivingBase living, ItemStack stack) {
		return super.canFire(world, living, stack) && !(living instanceof EntityPlayer && ((EntityPlayer)living).getCooldownTracker().hasCooldown(this));
	}
}
