package rafradek.TF2weapons.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemFireAmmo extends ItemAmmo {

	int uses;
	int type;

	public ItemFireAmmo(int type, int uses, int maxStack) {
		this.type = type;
		this.uses = uses;
		this.setMaxStackSize(maxStack);
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
	public int getMaxDamage(ItemStack stack) {
		return uses-1;
	}

	@SuppressWarnings("deprecation")
	@Override
	public int getItemStackLimit(ItemStack stack) {
		return this.getItemStackLimit();
	}
	
	@Override
	public int consumeAmmo(EntityLivingBase living, ItemStack stack, int amount) {
		if (stack == STACK_FILL)
			return 0;
		if (amount > 0) {
			int left = Math.max(0, amount - (uses-stack.getItemDamage()));
			if (stack.getCount() > 1) {
				ItemStack remain = stack.splitStack(1);
				remain.damageItem(amount, living);
				if (!remain.isEmpty())
				living.entityDropItem(remain,0).setPickupDelay(0);
			}
			else {
				stack.damageItem(amount, living);
			}

			return left;
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
		return 0;
	}
	
	public int getAmount(ItemStack stack) {
		return (uses-stack.getItemDamage()) * stack.getCount();
	}
}
