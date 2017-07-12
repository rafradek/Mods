package rafradek.TF2weapons.weapons;

import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import rafradek.TF2weapons.TF2weapons;

public class ItemFireAmmo extends ItemAmmo {

	int uses;
	int type;

	public ItemFireAmmo(int type, int uses) {
		this.type = type;
		this.uses = uses;
		this.setHasSubtypes(false);
	}

	@Override
	public int getTypeInt(ItemStack stack) {
		return type;
	}

	@Override
	public void getSubItems(CreativeTabs par2CreativeTabs, NonNullList<ItemStack> par3List) {
		if(!this.isInCreativeTab(par2CreativeTabs))
			return;
		par3List.add(new ItemStack(this));
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return 1;
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return uses;
	}

	@Override
	public void consumeAmmo(EntityLivingBase living, ItemStack stack, int amount) {
		if (stack == STACK_FILL)
			return;
		if (amount > 0) {
			stack.damageItem(amount, living);

			/*if (stack.getCount() <= 0 && living instanceof EntityPlayer) {
				if (!living.getCapability(TF2weapons.INVENTORY_CAP, null).getStackInSlot(3).isEmpty()){
					IItemHandlerModifiable invAmmo = (IItemHandlerModifiable) living.getCapability(TF2weapons.INVENTORY_CAP, null).getStackInSlot(3)
							.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
					for (int i = 0; i < invAmmo.getSlots(); i++) {
						ItemStack stackInv = invAmmo.getStackInSlot(i);
						if (stack == stackInv) {
							invAmmo.setStackInSlot(i, ItemStack.EMPTY);
							return;
						}
					}
				}
				((EntityPlayer) living).inventory.deleteStack(stack);

			}*/
		}
	}
}
