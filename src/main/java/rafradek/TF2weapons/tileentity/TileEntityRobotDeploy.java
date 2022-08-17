package rafradek.TF2weapons.tileentity;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.block.BlockRobotDeploy;
import rafradek.TF2weapons.entity.mercenary.EntityTF2Character;
import rafradek.TF2weapons.entity.mercenary.EntityTF2Character.Order;
import rafradek.TF2weapons.entity.mercenary.TF2CharacterAdditionalData;
import rafradek.TF2weapons.item.ItemFromData;
import rafradek.TF2weapons.item.ItemRobotPart;
import rafradek.TF2weapons.util.TF2Util;

public class TileEntityRobotDeploy extends TileEntity implements ITickable {

	public static final int[] NORMAL_REQUIRE= {2,2,1};
	public static final int[] GIANT_REQUIRE= {4,5,3};
	boolean filled;
	boolean joined;
	UUID owner;
	public ItemStackHandler weapon = new ItemStackHandler(9) {
		@Override
		@Nonnull
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
		{
			String name = TF2Util.getWeaponUsedByClass(stack);
			if (name == null || ItemFromData.getSlotForClass(ItemFromData.getData(stack), name)<4)
				return stack;
			else {
				return super.insertItem(slot, stack, simulate);
			}

		}

		@Override
		protected void onContentsChanged(int slot)
		{
			calculateHasInput();
		}
	};
	public ItemStackHandler parts = new ItemStackHandler(ItemRobotPart.LEVEL.length) {
		@Override
		@Nonnull
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
		{
			if (!(stack.getItem() instanceof ItemRobotPart) || stack.getItemDamage() != slot)
				return stack;
			else
				return super.insertItem(slot, stack, simulate);
		}

		@Override
		protected void onContentsChanged(int slot)
		{
			calculateHasInput();
		}
	};
	private String ownerName;
	public int progress;
	public int maxprogress;
	int hasWeapon;
	String weaponName;

	public int getRequirement(int level) {
		if (joined)
			return GIANT_REQUIRE[level];
		else
			return NORMAL_REQUIRE[level];
	}

	public boolean produceGiant() {
		return joined;
	}

	public void calculateHasInput() {
		hasWeapon = -1;
		weaponName = null;
		for (int i = 0;i < this.weapon.getSlots(); i++) {
			ItemStack stack = this.weapon.getStackInSlot(i);

			if (!stack.isEmpty()) {
				String name = TF2Util.getWeaponUsedByClass(stack);
				if (name != null) {
					hasWeapon = i;
					weaponName = name;
					break;
				}
			}
		}
		filled = true;
		for (int i = 0;i < this.parts.getSlots(); i++) {
			ItemStack stack = this.parts.getStackInSlot(i);
			if (!stack.isEmpty() && stack.getItem() instanceof ItemRobotPart && stack.getItemDamage() == i && stack.getCount() >= getRequirement(ItemRobotPart.LEVEL[i])) {
				//filled[i] = stack.getCount() == (ItemRobotPart.LEVEL[i] == 2 ? 1:2);
			}
			else {
				filled = false;
				break;
			}
		}
	}

	@Override
	public void update() {
		if (!this.world.isRemote) {
			if (hasWeapon>=0 && filled) {
				if (progress == 0) {
					this.maxprogress = 500;
				}
				if (++progress >= this.maxprogress) {
					ItemStack stack = this.weapon.extractItem(hasWeapon,64,false);
					String weaponName = TF2Util.getWeaponUsedByClass(stack);
					EnumFacing facing = this.getWorld().getBlockState(pos).getValue(BlockRobotDeploy.FACING);
					EntityTF2Character entity =
							(EntityTF2Character) EntityList.createEntityByIDFromName(new ResourceLocation(TF2weapons.MOD_ID, weaponName), this.world);
					BlockPos frontpos = this.pos.offset(facing);
					entity.setPosition(frontpos.getX(), frontpos.getY(), frontpos.getZ());
					TF2CharacterAdditionalData data = new TF2CharacterAdditionalData();
					data.team = 2;
					data.isGiant = false;

					entity.setOwnerID(this.ownerName,this.owner);
					entity.onInitialSpawn(this.world.getDifficultyForLocation(frontpos), data);
					entity.playLivingSound();
					for (int i = 0; i < entity.loadout.getSlots(); i++) {
						entity.loadout.setStackInSlot(i, ItemStack.EMPTY);
					}
					entity.loadout.setStackInSlot(ItemFromData.getSlotForClass(ItemFromData.getData(stack), entity),stack);
					entity.switchSlot(ItemFromData.getSlotForClass(ItemFromData.getData(stack), entity));
					entity.setOrder(Order.HOLD);
					this.getWorld().spawnEntity(entity);
					for (int i = 0;i < this.parts.getSlots(); i++) {
						this.parts.extractItem(i, ItemRobotPart.LEVEL[i] == 2 ? 1:2, false);
					}
					this.progress = 0;
				}
			}
			else
				this.progress = 0;
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		if (owner != null) {
			compound.setUniqueId("Owner", owner);
			compound.setString("OwnerName", ownerName);
		}
		compound.setTag("Weapons", this.weapon.serializeNBT());
		compound.setTag("Parts", this.parts.serializeNBT());
		compound.setShort("Progress", (short) this.progress);
		compound.setShort("MaxProgress", (short) this.maxprogress);
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);

		if (compound.hasUniqueId("Owner")) {
			this.owner = compound.getUniqueId("Owner");
			this.ownerName = compound.getString("OwnerName");
		}
		else {
			this.owner = null;
		}
		this.weapon.deserializeNBT(compound.getCompoundTag("Weapons"));
		this.parts.deserializeNBT(compound.getCompoundTag("Parts"));
		this.progress = compound.getShort("Progress");
		this.maxprogress = compound.getShort("MaxProgress");
		this.calculateHasInput();
	}

	@Override
	public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, @Nullable net.minecraft.util.EnumFacing facing)
	{
		if (facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	@Nullable
	public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.util.EnumFacing facing)
	{
		if (facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (facing == EnumFacing.UP)
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.weapon);
			else
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.parts);
		}
		else
			return super.getCapability(capability, facing);
	}

	public void setOwner(String name, UUID uniqueID) {
		this.owner = uniqueID;
		this.ownerName = name;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
	{
		this.joined = newSate.getValue(BlockRobotDeploy.JOINED);
		return oldState.getBlock() != newSate.getBlock() || !newSate.getValue(BlockRobotDeploy.HOLDER);
	}
}
