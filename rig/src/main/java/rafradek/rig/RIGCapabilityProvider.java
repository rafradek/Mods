package rafradek.rig;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.ItemStackHandler;
import rafradek.TF2weapons.TF2weapons;

public class RIGCapabilityProvider implements ICapabilityProvider, INBTSerializable<NBTTagCompound> {

	public ItemStackHandler item=new ItemStackHandler();
	
	public RIGCapabilityProvider() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		// TODO Auto-generated method stub
		return RIG.RIG_ITEM != null && capability == RIG.RIG_ITEM;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (RIG.RIG_ITEM != null && capability == RIG.RIG_ITEM)
			return RIG.RIG_ITEM.cast(item);
		return null;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		// TODO Auto-generated method stub
		NBTTagCompound tag=new NBTTagCompound();
		tag.setTag("Item", item.serializeNBT());
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		item.deserializeNBT(nbt.getCompoundTag("Item"));
	}

}
