package rafradek.TF2weapons.weapons;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.message.TF2Message.PredictionMessage;

public class ItemJetpackTrigger extends ItemUsable {

	public ItemJetpackTrigger() {
		super();
		this.setCreativeTab(TF2weapons.tabutilitytf2);
	}

	@Override
	public boolean use(ItemStack stack, EntityLivingBase living, World world, EnumHand hand, PredictionMessage message) {
		ItemStack jetpack = living.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		if(!world.isRemote && jetpack.getItem() instanceof ItemJetpack && ((ItemJetpack)jetpack.getItem()).canActivate(jetpack, living)) {
			((ItemJetpack)jetpack.getItem()).activateJetpack(jetpack, living, false);
			
		}
		return false;
	}
	
	@Override
	public void altUse(ItemStack stack, EntityLivingBase living, World world) {
		this.use(stack, living, world, EnumHand.MAIN_HAND, null);
	}
	
	@Override
	public boolean fireTick(ItemStack stack, EntityLivingBase living, World world) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean altFireTick(ItemStack stack, EntityLivingBase living, World world) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public short getAltFiringSpeed(ItemStack item, EntityLivingBase player) {
		return (short) this.getFiringSpeed(item, player);
	}
}
