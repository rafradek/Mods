package rafradek.TF2weapons.upgrade;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2Achievements;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.TF2weapons;

public class ContainerUpgrades extends Container {
	/** The crafting matrix inventory (3x3). */
	// public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	public IInventory upgradedItem = new InventoryBasic("", false, 1);
	private final World world;
	/** Position of the workbench */
	private final BlockPos pos;
	public EntityPlayer player;
	public int currentRecipe = -1;
	public TileEntityUpgrades station;
	public boolean[] applicable = new boolean[TileEntityUpgrades.UPGRADES_COUNT];
	public int[] transactions = new int[TileEntityUpgrades.UPGRADES_COUNT];
	public int[] transactionsCost = new int[TileEntityUpgrades.UPGRADES_COUNT];

	public ContainerUpgrades(EntityPlayer player, InventoryPlayer playerInventory, TileEntityUpgrades station,
			World worldIn, BlockPos posIn) {
		this.station = station;
		this.player = player;
		this.world = worldIn;
		this.pos = posIn;
		this.addSlotToContainer(new Slot(upgradedItem, 0, 108, 8) {
			@Override
			public void onSlotChanged() {
				super.onSlotChanged();
				transactions = new int[TileEntityUpgrades.UPGRADES_COUNT];
				transactionsCost = new int[TileEntityUpgrades.UPGRADES_COUNT];
				refreshData();
			}

			@Override
			public boolean isItemValid(@Nullable ItemStack stack) {
				if (stack.isEmpty())
					return false;
				else
					return stack.getItem() instanceof ItemFromData && stack.getMaxStackSize() == 1;
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
	}

	public void refreshData() {

		for (int i = 0; i < TileEntityUpgrades.UPGRADES_COUNT; i++)
			this.applicable[i] = this.upgradedItem.getStackInSlot(0) != null
					&& station.attributeList[i].canApply(this.upgradedItem.getStackInSlot(0));
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);

		if (!this.world.isRemote) {
			ItemStack itemstack = this.upgradedItem.removeStackFromSlot(0);

			if (!itemstack.isEmpty())
				playerIn.dropItem(itemstack, false);
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return this.world.getBlockState(this.pos).getBlock() != TF2weapons.blockUpgradeStation ? false
				: playerIn.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D,
						this.pos.getZ() + 0.5D) <= 64.0D;
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
		ItemStack stack = this.upgradedItem.getStackInSlot(0);

		int idEnch = Math.min(7, Math.max(id / 2, 0));
		// System.out.println("Selected: "+idEnch+" "+id);
		boolean adding = id % 2 == 0;
		TF2Attribute attr = this.station.attributeList[idEnch];
		if (stack.isEmpty() || !attr.canApply(stack))
			return false;

		int cost = attr.getUpgradeCost(stack);

		int expPoints = TF2weapons.getExperiencePoints(playerIn);
		if (adding && applicable[idEnch] && attr.calculateCurrLevel(stack) < this.station.attributes.get(attr)
				&& expPoints >= cost) {
			NBTTagCompound tag = stack.getTagCompound().getCompoundTag("Attributes");
			String key = String.valueOf(attr.id);

			if (!tag.hasKey(key))
				tag.setFloat(key, attr.defaultValue);
			tag.setFloat(key, tag.getFloat(key) + attr.perLevel);
			stack.getTagCompound().setInteger("TotalCost", stack.getTagCompound().getInteger("TotalCost") + attr.cost);
			TF2weapons.setExperiencePoints(playerIn, expPoints - cost);
			this.transactions[idEnch]++;
			this.transactionsCost[idEnch] += cost;

			playerIn.addStat(TF2Achievements.WEAPON_UPGRADE);
			if (attr.numLevels > 1 && attr.calculateCurrLevel(stack) == attr.numLevels)
				playerIn.addStat(TF2Achievements.FULLY_UPGRADED);
		} else if (!adding && this.transactions[idEnch] > 0) {
			cost = this.transactionsCost[idEnch];
			int count = this.transactions[idEnch];
			NBTTagCompound tag = stack.getTagCompound().getCompoundTag("Attributes");
			String key = String.valueOf(attr.id);

			if (!tag.hasKey(key))
				return false;
			tag.setFloat(key, tag.getFloat(key) - attr.perLevel * count);
			stack.getTagCompound().setInteger("TotalCost", stack.getTagCompound().getInteger("TotalCost") - attr.cost * count);
			TF2weapons.setExperiencePoints(playerIn, expPoints + cost);
			this.transactions[idEnch]=0;
			this.transactionsCost[idEnch]=0;
		}
		return true;
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
