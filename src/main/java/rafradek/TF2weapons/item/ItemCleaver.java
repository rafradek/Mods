package rafradek.TF2weapons.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2ConfigVars;
import rafradek.TF2weapons.entity.EntityDummy;
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
			if(living instanceof EntityPlayer)
				((EntityPlayer)living).getCooldownTracker().setCooldown(this, TF2ConfigVars.fastItemCooldown?this.getFiringSpeed(stack, living) / 50 : 120);
			if(living instanceof EntityPlayer && !((EntityPlayer)living).capabilities.isCreativeMode && !TF2ConfigVars.freeUseItems)
				stack.shrink(1);
			
		}
		return true;
	}
	@Override
	public boolean canFire(World world, EntityLivingBase living, ItemStack stack) {
		return super.canFire(world, living, stack) && !(living instanceof EntityPlayer && ((EntityPlayer)living).getCooldownTracker().hasCooldown(this));
	}
	
	public boolean isProjectileInfinite(EntityLivingBase living, ItemStack stack) {
		return !(living instanceof EntityDummy) 
				&& (TF2ConfigVars.freeUseItems || !(living instanceof EntityPlayer) || ((EntityPlayer)living).capabilities.isCreativeMode);
	}
}
