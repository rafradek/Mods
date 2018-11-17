package rafradek.TF2weapons.common;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class InventoryPlayerBackpack extends InventoryPlayer {
	
	public InventoryPlayer wrapper;
	public InventoryPlayerBackpack(EntityPlayer playerIn, InventoryPlayer wrapper) {
		super(playerIn);
		this.wrapper = wrapper;
		// TODO Auto-generated constructor stub
	}

	@Override
	public ItemStack getCurrentItem() {
		// TODO Auto-generated method stub
		return this.wrapper.getCurrentItem();
	}

	@Override
	public int getFirstEmptyStack() {
		// TODO Auto-generated method stub
		return this.wrapper.getFirstEmptyStack();
	}

	@Override
	public void setPickedItemStack(ItemStack stack) {
		// TODO Auto-generated method stub
		this.wrapper.setPickedItemStack(stack);
	}

	@Override
	public void pickItem(int index) {
		// TODO Auto-generated method stub
		this.wrapper.pickItem(index);
	}

	@Override
	public int getSlotFor(ItemStack stack) {
		// TODO Auto-generated method stub
		return this.wrapper.getSlotFor(stack);
	}

	@Override
	public int findSlotMatchingUnusedItem(ItemStack p_194014_1_) {
		// TODO Auto-generated method stub
		return this.wrapper.findSlotMatchingUnusedItem(p_194014_1_);
	}

	@Override
	public int getBestHotbarSlot() {
		// TODO Auto-generated method stub
		return this.wrapper.getBestHotbarSlot();
	}

	@Override
	public void changeCurrentItem(int direction) {
		// TODO Auto-generated method stub
		this.wrapper.changeCurrentItem(direction);
	}

	@Override
	public int clearMatchingItems(Item itemIn, int metadataIn, int removeCount, NBTTagCompound itemNBT) {
		// TODO Auto-generated method stub
		return this.wrapper.clearMatchingItems(itemIn, metadataIn, removeCount, itemNBT);
	}

	@Override
	public int storeItemStack(ItemStack itemStackIn) {
		// TODO Auto-generated method stub
		return this.wrapper.storeItemStack(itemStackIn);
	}

	@Override
	public void decrementAnimations() {
		// TODO Auto-generated method stub
		this.wrapper.decrementAnimations();
	}

	@Override
	public boolean addItemStackToInventory(ItemStack itemStackIn) {
		// TODO Auto-generated method stub
		return this.wrapper.addItemStackToInventory(itemStackIn);
	}

	@Override
	public boolean add(int p_191971_1_, ItemStack p_191971_2_) {
		// TODO Auto-generated method stub
		return this.wrapper.add(p_191971_1_, p_191971_2_);
	}

	@Override
	public void placeItemBackInInventory(World p_191975_1_, ItemStack p_191975_2_) {
		// TODO Auto-generated method stub
		this.wrapper.placeItemBackInInventory(p_191975_1_, p_191975_2_);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		// TODO Auto-generated method stub
		return this.wrapper.decrStackSize(index, count);
	}

	@Override
	public void deleteStack(ItemStack stack) {
		// TODO Auto-generated method stub
		this.wrapper.deleteStack(stack);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		// TODO Auto-generated method stub
		return this.wrapper.removeStackFromSlot(index);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		// TODO Auto-generated method stub
		this.wrapper.setInventorySlotContents(index, stack);
	}

	@Override
	public float getStrVsBlock(IBlockState state) {
		// TODO Auto-generated method stub
		return this.wrapper.getStrVsBlock(state);
	}

	@Override
	public NBTTagList writeToNBT(NBTTagList nbtTagListIn) {
		// TODO Auto-generated method stub
		return this.wrapper.writeToNBT(nbtTagListIn);
	}

	@Override
	public void readFromNBT(NBTTagList nbtTagListIn) {
		// TODO Auto-generated method stub
		this.wrapper.readFromNBT(nbtTagListIn);
	}

	@Override
	public int getSizeInventory() {
		// TODO Auto-generated method stub
		return this.wrapper.getSizeInventory();
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return this.wrapper.isEmpty();
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		// TODO Auto-generated method stub
		return this.wrapper.getStackInSlot(index);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.wrapper.getName();
	}

	@Override
	public boolean hasCustomName() {
		// TODO Auto-generated method stub
		return this.wrapper.hasCustomName();
	}

	@Override
	public ITextComponent getDisplayName() {
		// TODO Auto-generated method stub
		return this.wrapper.getDisplayName();
	}

	@Override
	public int getInventoryStackLimit() {
		// TODO Auto-generated method stub
		return this.wrapper.getInventoryStackLimit();
	}

	@Override
	public boolean canHarvestBlock(IBlockState state) {
		// TODO Auto-generated method stub
		return this.wrapper.canHarvestBlock(state);
	}

	@Override
	public ItemStack armorItemInSlot(int slotIn) {
		// TODO Auto-generated method stub
		return this.wrapper.armorItemInSlot(slotIn);
	}

	@Override
	public void damageArmor(float damage) {
		// TODO Auto-generated method stub
		this.wrapper.damageArmor(damage);
	}

	@Override
	public void dropAllItems() {
		// TODO Auto-generated method stub
		this.wrapper.dropAllItems();
	}

	@Override
	public void markDirty() {
		// TODO Auto-generated method stub
		this.wrapper.markDirty();
	}

	@Override
	public int getTimesChanged() {
		// TODO Auto-generated method stub
		return this.wrapper.getTimesChanged();
	}

	@Override
	public void setItemStack(ItemStack itemStackIn) {
		// TODO Auto-generated method stub
		this.wrapper.setItemStack(itemStackIn);
	}

	@Override
	public ItemStack getItemStack() {
		// TODO Auto-generated method stub
		return this.wrapper.getItemStack();
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		// TODO Auto-generated method stub
		return this.wrapper.isUsableByPlayer(player);
	}

	@Override
	public boolean hasItemStack(ItemStack itemStackIn) {
		// TODO Auto-generated method stub
		return this.wrapper.hasItemStack(itemStackIn);
	}

	@Override
	public void openInventory(EntityPlayer player) {
		// TODO Auto-generated method stub
		this.wrapper.openInventory(player);
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		// TODO Auto-generated method stub
		this.wrapper.closeInventory(player);
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		// TODO Auto-generated method stub
		return this.wrapper.isItemValidForSlot(index, stack);
	}

	@Override
	public void copyInventory(InventoryPlayer playerInventory) {
		// TODO Auto-generated method stub
		this.wrapper.copyInventory(playerInventory);
	}

	@Override
	public int getField(int id) {
		// TODO Auto-generated method stub
		return this.wrapper.getField(id);
	}

	@Override
	public void setField(int id, int value) {
		// TODO Auto-generated method stub
		this.wrapper.setField(id, value);
	}

	@Override
	public int getFieldCount() {
		// TODO Auto-generated method stub
		return this.wrapper.getFieldCount();
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		this.wrapper.clear();
	}

	@Override
	public void fillStackedContents(RecipeItemHelper helper, boolean p_194016_2_) {
		// TODO Auto-generated method stub
		this.wrapper.fillStackedContents(helper, p_194016_2_);
	}

}
