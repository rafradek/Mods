package rafradek.TF2weapons.weapons;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerAmmoBelt extends Container {
	private final IInventory ammoBeltInventory;

	public ContainerAmmoBelt(IInventory playerInventory, IInventory ammoBeltInventory) {
		this.ammoBeltInventory = ammoBeltInventory;

		for (int i = 0; i < 9; ++i)
			this.addSlotToContainer(new Slot(ammoBeltInventory, i, 62 + i * 18, 17));

		for (int k = 0; k < 3; ++k)
			for (int i1 = 0; i1 < 9; ++i1)
				this.addSlotToContainer(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));

		for (int l = 0; l < 9; ++l)
			this.addSlotToContainer(new Slot(playerInventory, l, 8 + l * 18, 142));
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return this.ammoBeltInventory.isUsableByPlayer(playerIn);
	}

	/**
	 * Take a stack from the specified inventory slot.
	 */
	@Override
	@Nullable
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (index < 9) {
				if (!this.mergeItemStack(itemstack1, 9, 45, true))
					return ItemStack.EMPTY;
			} else if (!this.mergeItemStack(itemstack1, 0, 9, false))
				return ItemStack.EMPTY;

			if (itemstack1.isEmpty())
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();

			if (itemstack1.getCount() ==itemstack.getCount())
				return ItemStack.EMPTY;

			slot.onTake(playerIn, itemstack1);
		}

		return itemstack;
	}
}