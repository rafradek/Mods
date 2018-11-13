package rafradek.TF2weapons.decoration;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import rafradek.TF2weapons.TF2Util;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.characters.ItemToken;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.weapons.WeaponsCapability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

public class InventoryWearables extends InventoryBasic implements ICapabilityProvider, INBTSerializable<NBTTagList> {

	public EntityLivingBase owner;
	public static final int USED_SLOTS = 5;
	private final NonNullList<ItemStack> inventoryContentsOld;
	public InventoryWearables(EntityLivingBase ply) {
		super("Wearables", false, 13);
		this.inventoryContentsOld = NonNullList.withSize(13, ItemStack.EMPTY);
		owner=ply;
		// TODO Auto-generated constructor stub
	}

	public boolean isEmpty() {
		for (int i = 0; i < USED_SLOTS; i++)
			if (!this.getStackInSlot(i).isEmpty())
				return false;
		return true;
	}

	public void updateSlots() {
		for (int i = 0; i < USED_SLOTS; i++) {
			ItemStack stack = this.getStackInSlot(i);
			ItemStack old = inventoryContentsOld.get(i);
			if (!ItemStack.areItemStacksEqual(stack, old)) {
				inventoryContentsOld.set(i, old);
				TF2Util.sendTracking(new TF2Message.WearableChangeMessage(owner, i, stack),owner);
			}
		}
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		// TODO Auto-generated method stub
		return TF2weapons.INVENTORY_CAP != null && capability == TF2weapons.INVENTORY_CAP;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (TF2weapons.INVENTORY_CAP != null && capability == TF2weapons.INVENTORY_CAP)
			return TF2weapons.INVENTORY_CAP.cast(this);
		return null;
	}

	@Override
	public NBTTagList serializeNBT() {
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < this.getSizeInventory(); i++) {
			ItemStack itemstack = this.getStackInSlot(i);

			if (!itemstack.isEmpty()) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte) i);
				itemstack.writeToNBT(nbttagcompound);
				list.appendTag(nbttagcompound);
			}
		}
		// System.out.println("Saving ");
		return list;
	}
	@Override
	public void deserializeNBT(NBTTagList nbt) {

		for (int i = 0; i < nbt.tagCount(); ++i) {
			NBTTagCompound nbttagcompound = nbt.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot");
			this.setInventorySlotContents(j, new ItemStack(nbttagcompound));
		}
		if(!WeaponsCapability.get(owner).forcedClass) {
			ItemStack token = this.getStackInSlot(4);
			((ItemToken)TF2weapons.itemToken).updateAttributes(token, owner);
		}
		// System.out.println("Reading ");
	}

}
