package rafradek.rig;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2Achievements;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.weapons.ItemCleaver;

public class ContainerBench extends Container {
	/** The crafting matrix inventory (3x3). */
	// public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	public ItemStackHandler node = new ItemStackHandler(1);
	public ItemStack upgradedItem = ItemStack.EMPTY;
	private final World world;
	/** Position of the workbench */
	private final BlockPos pos;
	public EntityPlayer player;
	public int currentRecipe = -1;
	public int[] transactions = new int[6];
	public int[] transactionsCost = new int[6];

	public ContainerBench(EntityPlayer player, InventoryPlayer playerInventory,
			World worldIn, BlockPos posIn) {
		this.player = player;
		this.world = worldIn;
		this.pos = posIn;
		this.addSlotToContainer(new SlotItemHandler(node, 0, 108, 8) {
			@Override
			public boolean isItemValid(@Nullable ItemStack stack) {
				if (stack.isEmpty())
					return false;
				else
					return stack.getItem() instanceof ItemNode;
			}
		});

		/*
		 * for (int k = 0; k < 3; ++k) { for (int i1 = 0; i1 < 9; ++i1) {
		 * this.addSlotToContainer(new Slot(cabinet, i1 + k * 9 + 9, 8 + i1 *
		 * 18, 91 + k * 18)); } }
		 */

		for (int k = 0; k < 3; ++k)
			for (int i1 = 0; i1 < 9; ++i1)
				this.addSlotToContainer(new Slot(playerInventory, i1 + k * 9 + 9, 36 + i1 * 18, 134 + k * 18));

		for (int l = 0; l < 9; ++l)
			this.addSlotToContainer(new Slot(playerInventory, l, 36 + l * 18, 192));
		
		for (int i = 0; i < 1; ++i)
			this.addSlotToContainer(new SlotItemHandler(player.getCapability(RIG.RIG_ITEM, null), i, -1000, 8) {
				@Override
				public boolean canTakeStack(EntityPlayer player) {
					return false;
				}
			});
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);

		if (!this.world.isRemote) {
			ItemStack itemstack = this.node.extractItem(0, 64, false);

			RIG.equip(playerIn, playerIn.getCapability(RIG.RIG_ITEM, null).getStackInSlot(0));
			if (!itemstack.isEmpty())
				playerIn.dropItem(itemstack, false);
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return this.world.getBlockState(this.pos).getBlock() != RIG.bench ? false
				: playerIn.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D,
						this.pos.getZ() + 0.5D) <= 64.0D;
	}

	public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        if(!upgradedItem.isEmpty())
	        for (int i = 0; i < this.listeners.size(); ++i)
	        {
	            IContainerListener listener = this.listeners.get(i);
	            listener.sendSlotContents(this, 37, this.upgradedItem);
	            /*for(RIGUpgrade upgrade: RIGUpgrade.getUpgrades(upgradedItem)) {
	            	listener.sendWindowProperty(this, 20000+upgrade.id, upgrade.getAttributeValue(upgradedItem));
	            }*/
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

			if (index == 0) {
				if (!this.mergeItemStack(itemstack1, 1, 28, true))
					return ItemStack.EMPTY;

				slot.onSlotChange(itemstack1, itemstack);
			} else if (index >= 1 && index < 28) {
				if (!this.mergeItemStack(itemstack1, 0, 1, false))
					return ItemStack.EMPTY;
			} else if (!this.mergeItemStack(itemstack1, 1, 28, false))
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

	@Override
	public boolean enchantItem(EntityPlayer playerIn, int id) {
		if (id == 64) {
			this.upgradedItem = playerIn.getCapability(RIG.RIG_ITEM, null).getStackInSlot(0);
		}
		else {
			ItemStack stack = this.upgradedItem;
	
			int idEnch = Math.min(7, Math.max(id / 2, 0));
			System.out.println("Selected: "+this.upgradedItem);
			boolean adding = id % 2 == 0;
			if (stack.isEmpty() || id / 2 >= RIGUpgrade.getUpgrades(stack).size())
				return false;
			
			RIGUpgrade attr = RIGUpgrade.getUpgrades(stack).get(id / 2);
			
			int nodetype = getValidNodeType(stack);
			int cost = 100;
	
			NBTTagCompound tag = stack.getTagCompound().getCompoundTag("Upgrades");
			String key = String.valueOf(attr.id);
			int level = tag.getInteger(key);
			
			if (adding && level < 6 && !node.getStackInSlot(0).isEmpty() && node.getStackInSlot(0).getMetadata() == nodetype) {
				
				tag.setInteger(key, level + 1);
				this.transactions[idEnch]++;
				this.transactionsCost[idEnch] += cost;
	
				node.extractItem(0, 1, false);
				/*playerIn.addStat(TF2Achievements.WEAPON_UPGRADE);
				if (attr.numLevels > 1 && attr.calculateCurrLevel(stack) == attr.numLevels)
					playerIn.addStat(TF2Achievements.FULLY_UPGRADED);*/
			} else if (!adding && this.transactions[idEnch] > 0 && node.insertItem(0, new ItemStack(RIG.node, 1, nodetype), true).isEmpty()) {
				cost = this.transactionsCost[idEnch];
				int count = this.transactions[idEnch];
				tag.setInteger(key, level - count);
				node.insertItem(0, new ItemStack(RIG.node, this.transactions[idEnch], nodetype), false);
				
				this.transactions[idEnch]=0;
				this.transactionsCost[idEnch]=0;
			}
		}
		this.detectAndSendChanges();
		return true;
	}

	public static int getValidNodeType(ItemStack stack) {
		if(stack.getItem() instanceof ItemRIG)
			return 1;
		return 0;
	}
	/**
	 * Called to determine if the current slot is valid for the stack merging
	 * (double-click) code. The stack passed in is null for the initial slot
	 * that was double-clicked.
	 */
	/*
	 * public boolean canMergeSlot(ItemStack stack, Slot slotIn) { return
	 * super.canMergeSlot(stack, slotIn); }
	 */
}
