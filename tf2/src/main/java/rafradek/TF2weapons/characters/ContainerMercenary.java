package rafradek.TF2weapons.characters;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.EntityEquipmentSlot.Type;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.oredict.OreDictionary;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2Util;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.characters.EntityTF2Character.Order;
import rafradek.TF2weapons.decoration.ContainerWearables;
import rafradek.TF2weapons.decoration.ItemWearable;
import rafradek.TF2weapons.weapons.ItemAmmo;
import rafradek.TF2weapons.weapons.ItemUsable;
import rafradek.TF2weapons.weapons.ItemWeapon;

public class ContainerMercenary extends ContainerMerchant {
	
	private static final EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EntityEquipmentSlot[] {
			EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET };
	
	public EntityTF2Character mercenary;
	int primaryAmmo;
	int secondaryAmmo;
	public ContainerMercenary(EntityPlayer player, EntityTF2Character merc, World worldIn) {
		super(player.inventory, merc, worldIn);
		this.mercenary = merc;
		for(int i=0;i<3;i++) {
			if(!this.mercenary.loadoutHeld.getStackInSlot(i).isEmpty()) {
				ItemStack buf= this.mercenary.loadout.getStackInSlot(i);
        		this.mercenary.loadout.setStackInSlot(i, this.mercenary.loadoutHeld.getStackInSlot(0));
        		this.mercenary.loadoutHeld.setStackInSlot(i, buf);
        	}
        	
        }
        if(this.mercenary.loadoutHeld.getStackInSlot(3).isEmpty() && this.mercenary.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof ItemWearable) {
        	this.mercenary.loadoutHeld.setStackInSlot(3, this.mercenary.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
        	this.mercenary.setItemStackToSlot(EntityEquipmentSlot.HEAD, ItemStack.EMPTY);
        }
		for (int k = 0; k < 4; ++k) {
			final EntityEquipmentSlot entityequipmentslot = VALID_EQUIPMENT_SLOTS[k];
			this.addSlotToContainer(new SlotItemHandler(merc.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH), 3-k, 184, 8 + k * 18) {
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
			    @Nonnull
			    public ItemStack getStack()
			    {
					if(entityequipmentslot == EntityEquipmentSlot.HEAD && this.getItemHandler().getStackInSlot(this.getSlotIndex()).getItem() instanceof ItemWearable)
						return ItemStack.EMPTY;
						
			        return this.getItemHandler().getStackInSlot(this.getSlotIndex());
			    }
				
				/*@Override
			    public boolean canTakeStack(EntityPlayer playerIn)
			    {
			        return merc.getDropChance();
			    }*/
				
				@Override
				public boolean isItemValid(@Nullable ItemStack stack) {
					if (stack.isEmpty() || merc.getOwner() != player)
						return false;
					else
						return stack.getItem().isValidArmor(stack, entityequipmentslot, merc);
				}

				@Override
				@Nullable
				@SideOnly(Side.CLIENT)
				public String getSlotTexture() {
					return ItemArmor.EMPTY_SLOT_NAMES[entityequipmentslot.getIndex()];
				}
			});
		}
		for (int i = 0; i < 3; i++) {
			final int index=i;
			this.addSlotToContainer(new SlotItemHandler(merc.loadoutHeld, i, 206, 8 + i * 18) {

				@Override
				public int getSlotStackLimit() {
					return 64;
				}

				@Override
			    public boolean canTakeStack(EntityPlayer playerIn)
			    {
			        return super.canTakeStack(playerIn);
			    }
				
				@Override
				public boolean isItemValid(@Nullable ItemStack stack) {
					if (stack.isEmpty() || merc.getOwner() != player)
						return false;
					else {
						return ItemFromData.getData(stack).getInt(PropertyType.SLOT)==this.getSlotIndex()
						&& ItemFromData.getData(stack).getString(PropertyType.MOB_TYPE).contains(merc.getClass().getSimpleName().substring(6).toLowerCase());
					}
				}

				@Override
				@Nullable
				@SideOnly(Side.CLIENT)
				public String getSlotTexture() {
					return TF2weapons.MOD_ID + ":items/weapon_empty_"+index;
				}
			});
		}
		
		this.addSlotToContainer(new SlotItemHandler(merc.refill, 0, 206, 93) {
			@Override
			public boolean isItemValid(@Nullable ItemStack stack) {
				if (stack.isEmpty() || !TF2Util.isOnSameTeam(merc.getOwner(), player))
					return false;
				if (TF2Util.isOre("ingotLead", stack))
					return true;
				if (stack.getItem() instanceof ItemAmmo) {
					int type = ((ItemAmmo)stack.getItem()).getTypeInt(stack);
					return type == ItemFromData.getData(merc.loadout.getStackInSlot(0)).getInt(PropertyType.AMMO_TYPE) ||
							type == ItemFromData.getData(merc.loadout.getStackInSlot(1)).getInt(PropertyType.AMMO_TYPE);
				}
				return false;
			}
			
			@Override
			@Nullable
			@SideOnly(Side.CLIENT)
			public String getSlotTexture() {
				return TF2weapons.MOD_ID + ":items/refill_empty";
			}
		});
		// TODO Auto-generated constructor stub
	}
	
	public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for(IContainerListener listener : this.listeners) {
        	ItemStack weapon = this.mercenary.loadout.getStackInSlot(0);
        	int loaded = weapon.getItem() instanceof ItemWeapon ? weapon.getMaxDamage() - weapon.getItemDamage() : 0;
	        if(this.primaryAmmo != this.mercenary.getAmmo(0)) {
	        	this.primaryAmmo = this.mercenary.getAmmo(0);
	        	//System.out.println("ammo in:");
	        	listener.sendWindowProperty(this, 0, this.primaryAmmo);
	        }
	        weapon = this.mercenary.loadout.getStackInSlot(0);
        	loaded = weapon.getItem() instanceof ItemWeapon ? weapon.getMaxDamage() - weapon.getItemDamage() : 0;
	        if(this.secondaryAmmo != this.mercenary.getAmmo(1) + loaded) {
	        	this.secondaryAmmo = this.mercenary.getAmmo(1) + loaded;
	        	listener.sendWindowProperty(this, 1, this.secondaryAmmo);
	        }
        }
    }
	@SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        if(id == 0)
        	this.primaryAmmo = data;
        else if(id == 1)
        	this.secondaryAmmo = data;
    }
	
	@Override
	public boolean enchantItem(EntityPlayer playerIn, int id) {
		if(id == 256) {
			if(mercenary.getOwner() == playerIn) {
				playerIn.inventory.addItemStackToInventory(new ItemStack(TF2weapons.itemTF2, 1, 2));
			}
			else if(mercenary.getOwnerId() == null && playerIn.inventory.hasItemStack(new ItemStack(TF2weapons.itemTF2, 1, 2))) {
				playerIn.inventory.clearMatchingItems(TF2weapons.itemTF2, 2, 1, null);
				this.mercenary.setOwner(playerIn);
			}
		}
		else if(id >= 260 && mercenary.getOwner() == playerIn) {
			int index = (id - 260) % EntityTF2Character.Order.values().length;
			this.mercenary.setOrder(Order.values()[index]);
		}
		return true;
	}
	public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);
        if(!this.mercenary.world.isRemote) {
	        for(int i=0;i<3;i++) {
	        	if(!this.mercenary.loadoutHeld.getStackInSlot(i).isEmpty()) {
	        		ItemStack buf = this.mercenary.loadout.getStackInSlot(i);
	        		this.mercenary.loadout.setStackInSlot(i, this.mercenary.loadoutHeld.getStackInSlot(0));
	        		this.mercenary.loadoutHeld.setStackInSlot(i, buf);
	        	}
	        }
	        this.mercenary.switchSlot(this.mercenary.usedSlot);
	        
	        for(EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
	        	if(slot.getSlotType() == Type.ARMOR) {
	        		//System.out.println("Not empt:" + slot);
	        		this.mercenary.setDropChance(slot, !this.mercenary.getItemStackFromSlot(slot).isEmpty() ? 2.0f : 0.25f);
	        	}
	        }
	        
	        if(this.mercenary.getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty() && this.mercenary.loadoutHeld.getStackInSlot(3).getItem() instanceof ItemWearable) {
	        	this.mercenary.setItemStackToSlot(EntityEquipmentSlot.HEAD, this.mercenary.loadoutHeld.getStackInSlot(3));
	        	this.mercenary.loadoutHeld.setStackInSlot(3, ItemStack.EMPTY);
	        }
        }
    }
}
