package rafradek.TF2weapons.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.util.TF2Util;

public class ItemFireAmmo extends ItemAmmo {

	public int uses;
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

	public int restoreAmmo(ItemStack stack, int amount) {
		if (amount > 0) {
			if (stack.getCount() == 1) {
				int itemDamage = stack.getItemDamage();
				stack.setItemDamage(Math.max(0, itemDamage - amount));
				return Math.min(itemDamage, amount);
			}
		}
		return 0;
	}

	@Override
	public int consumeAmmo(EntityLivingBase living, ItemStack stack, int amount) {
		if (stack == STACK_FILL)
			return 0;
		if (amount > 0) {
			int left = Math.max(0, amount - this.getAmount(stack));
			if (stack.getCount() > 1) {
				ItemStack remain = stack.splitStack(1);
				remain.damageItem(amount, living);
				if (living instanceof EntityPlayer)
					remain = TF2Util.pickAmmo(remain, (EntityPlayer) living, true);
				if (!remain.isEmpty())
					living.entityDropItem(remain,0).setPickupDelay(0);
			}
			else {
				stack.setItemDamage(stack.getItemDamage() + amount);
				if (stack.getItemDamage() > stack.getMaxDamage())
					stack.shrink(1);
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

	@Override
	public int getAmount(ItemStack stack) {
		return (uses-stack.getItemDamage()) * stack.getCount();
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt)
	{
		if(this.getTypeInt(stack) == 10 && TF2weapons.refinedFuel != null) {
			Provider cap = new Provider(stack, 3, TF2weapons.refinedFuel);
			return cap;
		}
		return null;
	}

	public static class Provider implements ICapabilityProvider, IFluidHandlerItem {

		FluidTankProperties prop;
		ItemStack stack;
		int mult;
		Fluid fluid;
		public Provider(ItemStack stack, int mult, Fluid fluid) {
			this.stack = stack;
			this.mult = mult;
			this.fluid = fluid;
		}
		@Override
		public IFluidTankProperties[] getTankProperties() {
			// TODO Auto-generated method stub
			if (stack.isEmpty())
				return new IFluidTankProperties[0];
			return new IFluidTankProperties[] {new FluidTankProperties(new FluidStack(fluid, ((ItemFireAmmo)stack.getItem()).getAmount(stack)*mult), ((ItemFireAmmo)stack.getItem()).uses*mult)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill) {
			// TODO Auto-generated method stub
			if (stack.isEmpty())
				return 0;
			if (resource.getFluid() != fluid)
				return resource.amount;
			int resourceUsed = Math.max(((ItemFireAmmo)stack.getItem()).uses - ((ItemFireAmmo)stack.getItem()).getAmount(stack),(resource.amount/mult))*mult;
			if (doFill) {
				stack.setItemDamage(stack.getItemDamage() - resourceUsed/mult);
			}
			return resourceUsed;
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain) {
			if (stack.isEmpty())
				return null;
			if (resource.getFluid() != fluid)
				return null;
			int resourceUsed = Math.min(((ItemFireAmmo)stack.getItem()).getAmount(stack),(resource.amount/mult))*mult;
			if (doDrain) {
				stack.setItemDamage(stack.getItemDamage() + resourceUsed/mult);
				if (stack.getItemDamage() > stack.getMaxDamage())
					stack.shrink(1);

			}
			return new FluidStack(fluid, resourceUsed);
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain) {
			return drain(new FluidStack(fluid,maxDrain), doDrain);
		}

		@Override
		public ItemStack getContainer() {
			// TODO Auto-generated method stub
			return this.stack;
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			// TODO Auto-generated method stub
			return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			// TODO Auto-generated method stub
			return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY ? (T) this : null;
		}

	}
}
