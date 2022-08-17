package rafradek.TF2weapons.inventory;

import javax.annotation.Nullable;

import com.google.common.collect.Multimap;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import rafradek.TF2weapons.TF2PlayerCapability;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.common.WeaponsCapability;
import rafradek.TF2weapons.item.ItemAmmo;
import rafradek.TF2weapons.item.ItemAmmoBelt;
import rafradek.TF2weapons.item.ItemBackpack;
import rafradek.TF2weapons.item.ItemFromData;
import rafradek.TF2weapons.item.ItemToken;
import rafradek.TF2weapons.item.ItemWearable;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.util.TF2Util;

public class ContainerWearables extends Container {
	private static final EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EntityEquipmentSlot[] {
			EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET };
	/** The crafting matrix inventory. */
	/** Determines if inventory manipulation should be handled. */
	public boolean isLocalWorld;
	private final EntityPlayer player;
	public IInventory wearables;
	public static IItemHandler nullInv=new ItemStackHandler(9);
	public boolean ammoBelt;
	public ContainerWearables(final InventoryPlayer playerInventory, final InventoryWearables wearables,
			boolean localWorld, EntityPlayer player) {
		this.isLocalWorld = localWorld;
		this.player = player;
		this.wearables = wearables;
		for (int i = 0; i < 2; i++) {
			TF2PlayerCapability.get(player).wearablesAttrib[i] = wearables.getStackInSlot(i).getAttributeModifiers(EntityEquipmentSlot.HEAD);
		}
		TF2PlayerCapability.get(player).wearablesAttrib[2] = wearables.getStackInSlot(2).getAttributeModifiers(EntityEquipmentSlot.CHEST);
		TF2PlayerCapability.get(player).wearablesAttrib[2].removeAll(SharedMonsterAttributes.ARMOR);
		TF2PlayerCapability.get(player).wearablesAttrib[2].removeAll(SharedMonsterAttributes.ARMOR_TOUGHNESS);

		for (int k = 0; k < 4; ++k) {
			final EntityEquipmentSlot entityequipmentslot = VALID_EQUIPMENT_SLOTS[k];
			this.addSlotToContainer(new Slot(playerInventory, 36 + (3 - k), 8, 8 + k * 18) {
				/**
				 * Returns the maximum stack size for a given slot (usually the
				 * same as getInventoryStackLimit(), but 1 in the case of armor
				 * slots)
				 */
				@Override
				public int getSlotStackLimit() {
					return 1;
				}

				/**
				 * Check if the stack is a valid item for this slot. Always true
				 * beside for the armor slots.
				 */
				@Override
				public boolean isItemValid(@Nullable ItemStack stack) {
					if (stack.isEmpty())
						return false;
					else
						return stack.getItem().isValidArmor(stack, entityequipmentslot, ContainerWearables.this.player);
				}

				@Override
				@Nullable
				@SideOnly(Side.CLIENT)
				public String getSlotTexture() {
					return ItemArmor.EMPTY_SLOT_NAMES[entityequipmentslot.getIndex()];
				}
			});
		}
		for (int k = 0; k < 2; k++) {
			int l = k;
			this.addSlotToContainer(new Slot(wearables, k, 77, 8 + k * 18) {
				@Override
				public int getSlotStackLimit() {
					return 1;
				}

				@Override
				public void onSlotChanged() {
					super.onSlotChanged();
					if (!ContainerWearables.this.player.world.isRemote) {
						// System.out.println("changed");
						if (TF2PlayerCapability.get(ContainerWearables.this.player).wearablesAttrib[l] != null) {
							ContainerWearables.this.player.getAttributeMap().removeAttributeModifiers(TF2PlayerCapability.get(ContainerWearables.this.player).wearablesAttrib[l]);
							TF2PlayerCapability.get(ContainerWearables.this.player).wearablesAttrib[l] = null;
						}

						if (!this.getStack().isEmpty()) {
							Multimap<String, AttributeModifier> modifiers = this.getStack().getAttributeModifiers(EntityEquipmentSlot.HEAD);
							ContainerWearables.this.player.getAttributeMap().applyAttributeModifiers(modifiers);
							TF2PlayerCapability.get(ContainerWearables.this.player).wearablesAttrib[l] = modifiers;
						}
						TF2Util.sendTracking(
								new TF2Message.WearableChangeMessage(ContainerWearables.this.player, this.getSlotIndex(), this.getStack()),ContainerWearables.this.player);
					}
				}

				/**
				 * Check if the stack is a valid item for this slot. Always true
				 * beside for the armor slots.
				 */
				@Override
				public boolean isItemValid(@Nullable ItemStack stack) {
					if (stack.isEmpty())
						return false;
					else
						return stack.getItem() instanceof ItemWearable;
				}

				@Override
				@Nullable
				@SideOnly(Side.CLIENT)
				public String getSlotTexture() {
					return ItemArmor.EMPTY_SLOT_NAMES[EntityEquipmentSlot.HEAD.getIndex()];
				}
			});
		}

		this.addSlotToContainer(new Slot(wearables, 3, 154, 28) {
			@Override
			public int getSlotStackLimit() {
				return 1;
			}

			/**
			 * Check if the stack is a valid item for this slot. Always true
			 * beside for the armor slots.
			 */
			@Override
			public boolean isItemValid(@Nullable ItemStack stack) {
				if (stack.isEmpty())
					return false;
				else
					return stack.getItem() instanceof ItemAmmoBelt;
			}

			@Override
			public void onSlotChanged() {

				super.onSlotChanged();
				addSlotAmmo();
			}

			@Override
			@Nullable
			@SideOnly(Side.CLIENT)
			public String getSlotTexture() {
				return TF2weapons.MOD_ID + ":items/ammo_belt_empty";
			}
		});
		for (int l = 0; l < 3; ++l)
			for (int j1 = 0; j1 < 9; ++j1)
				this.addSlotToContainer(new Slot(playerInventory, j1 + (l + 1) * 9, 8 + j1 * 18, 84 + l * 18));

		for (int i1 = 0; i1 < 9; ++i1)
			this.addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 142));

		this.addSlotToContainer(new Slot(wearables, 4, 77, 62) {
			/**
			 * Check if the stack is a valid item for this slot. Always true
			 * beside for the armor slots.
			 */
			@Override
			public boolean isItemValid(@Nullable ItemStack stack) {
				return stack.getItem() instanceof ItemToken;
			}

			@Override
			@Nullable
			@SideOnly(Side.CLIENT)
			public String getSlotTexture() {
				return TF2weapons.MOD_ID + ":items/token_empty";
			}
		});
		this.addSlotToContainer(new Slot(wearables, 2, 77, 44) {
			/**
			 * Check if the stack is a valid item for this slot. Always true
			 * beside for the armor slots.
			 */
			@Override
			public boolean isItemValid(@Nullable ItemStack stack) {
				return stack.getItem() instanceof ItemFromData && stack.getItem().isValidArmor(stack, EntityEquipmentSlot.CHEST, player);
			}

			@Override
			@Nullable
			@SideOnly(Side.CLIENT)
			public String getSlotTexture() {
				return ItemArmor.EMPTY_SLOT_NAMES[EntityEquipmentSlot.CHEST.getIndex()];
			}

			@Override
			public void onSlotChanged() {
				super.onSlotChanged();
				if (!ContainerWearables.this.player.world.isRemote) {
					// System.out.println("changed");
					if (TF2PlayerCapability.get(ContainerWearables.this.player).wearablesAttrib[2] != null) {
						ContainerWearables.this.player.getAttributeMap().removeAttributeModifiers(TF2PlayerCapability.get(ContainerWearables.this.player).wearablesAttrib[2]);
						TF2PlayerCapability.get(ContainerWearables.this.player).wearablesAttrib[2] = null;
					}

					if (!this.getStack().isEmpty()) {
						Multimap<String, AttributeModifier> modifiers = this.getStack().getAttributeModifiers(EntityEquipmentSlot.CHEST);
						modifiers.removeAll(SharedMonsterAttributes.ARMOR.getName());
						modifiers.removeAll(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName());
						ContainerWearables.this.player.getAttributeMap().applyAttributeModifiers(modifiers);
						TF2PlayerCapability.get(ContainerWearables.this.player).wearablesAttrib[2] = modifiers;
					}
					TF2Util.sendTracking(
							new TF2Message.WearableChangeMessage(ContainerWearables.this.player, this.getSlotIndex(), this.getStack()),ContainerWearables.this.player);
				}
			}
		});
		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 3; ++j)
				addSlotToContainer(new SlotItemHandler( nullInv,
						j + i * 3, 98 + j * 18, 18 + i * 18) {

					@Override
					public boolean isItemValid(@Nullable ItemStack stack) {
						return false;
					}
				});
		addSlotAmmo();
		//if(wearables.getStackInSlot(3)!=null && wearables.getStackInSlot(3).hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))
		//this.addSlotAmmo();
	}

	/**
	 * Callback for when the crafting matrix is changed.
	 */

	/**
	 * Called when the container is closed.
	 */

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);

		if (!playerIn.world.isRemote && !WeaponsCapability.get(playerIn).forcedClass)
			((ItemToken)TF2weapons.itemToken).updateAttributes(this.wearables.getStackInSlot(4), playerIn);
		/*if (this.wearables.getStackInSlot(3) == null)
			for (int i = 4; i < 13; ++i) {
				ItemStack itemstack = this.wearables.removeStackFromSlot(i);

				if (item!stack.isEmpty())
					playerIn.dropItem(itemstack, false);
			}*/
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	public void addSlotAmmo(){
		IItemHandler handler=nullInv;
		ammoBelt=false;
		if(!this.wearables.getStackInSlot(3).isEmpty()&&this.wearables.getStackInSlot(3).hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)){
			handler=this.wearables.getStackInSlot(3).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			ammoBelt=true;
		}
		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 3; ++j){
				Slot slot=new SlotItemHandler( handler,
						j + i * 3, 98 + j * 18, 18 + i * 18) {

					@Override
					public boolean isItemValid(@Nullable ItemStack stack) {
						return this.getItemHandler()!=nullInv&&!stack.isEmpty() && stack.getItem() instanceof ItemAmmo && super.isItemValid(stack);
					}
				};
				this.inventorySlots.set(this.inventorySlots.size()-9+j + i * 3, slot);
				slot.slotNumber=this.inventorySlots.size()-9+j + i * 3;
			}
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
			EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(itemstack);

			if (index >= 0 && index < 4) {
				if (!this.mergeItemStack(itemstack1, 8, 44, false))
					return ItemStack.EMPTY;
			} else if (index >= 4 && index < 7) {
				if (!this.mergeItemStack(itemstack1, 8, 44, false))
					return ItemStack.EMPTY;
			} else if (itemstack1.getItem() instanceof ItemBackpack && index >= 8) {
				if (!this.mergeItemStack(itemstack1, 6, 7, false))
					return ItemStack.EMPTY;
			} else if (entityequipmentslot.getSlotType() == EntityEquipmentSlot.Type.ARMOR
					&& !this.inventorySlots.get(4 - entityequipmentslot.getSlotIndex()).getHasStack()) {
				int i = 4 - entityequipmentslot.getSlotIndex();

				if (!this.mergeItemStack(itemstack1, i, i + 1, false))
					return ItemStack.EMPTY;
			} else if (itemstack1.getItem() instanceof ItemToken
					&& !this.inventorySlots.get(44).getHasStack()) {
				if (!this.mergeItemStack(itemstack1, 44, 45, false))
					return ItemStack.EMPTY;
			} else if (itemstack1.getItem() instanceof ItemAmmoBelt
					&& !this.inventorySlots.get(7).getHasStack()) {
				if (!this.mergeItemStack(itemstack1, 7, 8, false))
					return ItemStack.EMPTY;
			} else if (itemstack1.getItem() instanceof ItemWearable && index >= 8) {
				if (!this.mergeItemStack(itemstack1, 4, 6, false))
					return ItemStack.EMPTY;
			} else if (itemstack1.getItem() instanceof ItemAmmo && ammoBelt && index < 44) {
				if (!this.mergeItemStack(itemstack1, 45, 54, false))
					return ItemStack.EMPTY;
			} else if (index >= 8 && index < 35) {
				if (!this.mergeItemStack(itemstack1, 35, 44, false))
					return ItemStack.EMPTY;
			} else if (index >= 35 && index < 44) {
				if (!this.mergeItemStack(itemstack1, 8, 35, false))
					return ItemStack.EMPTY;
			} else if (!this.mergeItemStack(itemstack1, 8, 44, false))
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

	/**
	 * Called to determine if the current slot is valid for the stack merging
	 * (double-click) code. The stack passed in is null for the initial slot
	 * that was double-clicked.
	 */
}