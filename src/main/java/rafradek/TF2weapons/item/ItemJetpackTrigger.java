package rafradek.TF2weapons.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.message.TF2Message.PredictionMessage;

public class ItemJetpackTrigger extends ItemUsable {

	public ItemJetpackTrigger() {
		super();
		this.setCreativeTab(TF2weapons.tabutilitytf2);
	}

	@Override
	public boolean use(ItemStack stack, EntityLivingBase living, World world, EnumHand hand, PredictionMessage message) {
		ItemStack jetpack = ItemBackpack.getBackpack(living);
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
		return false;
	}

	@Override
	public boolean altFireTick(ItemStack stack, EntityLivingBase living, World world) {
		return false;
	}

	@Override
	public short getAltFiringSpeed(ItemStack item, EntityLivingBase player) {
		return (short) this.getFiringSpeed(item, player);
	}

	@Override
	public boolean showInfoBox(ItemStack stack, EntityPlayer player) {
		return true;
	}

	@Override
	public String[] getInfoBoxLines(ItemStack stack, EntityPlayer player){
		ItemStack backpack = ItemBackpack.getBackpack(player);
		if (backpack.getItem() instanceof ItemJetpack) {
			String charge = "";
			int progress = 20 - (int)((float)backpack.getTagCompound().getShort("Charge")/(float)((ItemJetpack) backpack.getItem()).getCooldown(backpack, player)*20f);
			for(int i=0;i<20;i++){
				if(i<progress)
					charge=charge+"|";
				else
					charge=charge+".";
			}
			return new String[]{"CHARGES: "+backpack.getTagCompound().getByte("Charges"),charge};
		}
		return new String[]{"CHARGES: 0",""};
	}
}
