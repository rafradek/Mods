package rafradek.TF2weapons.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.arena.GameArena;
import rafradek.TF2weapons.block.BlockOverheadDoor;
import rafradek.TF2weapons.block.BlockRobotDeploy;
import rafradek.TF2weapons.entity.mercenary.EntityTF2Character;
import rafradek.TF2weapons.entity.mercenary.TF2CharacterAdditionalData;
import rafradek.TF2weapons.entity.mercenary.EntityTF2Character.Order;
import rafradek.TF2weapons.item.ItemAmmoPackage;
import rafradek.TF2weapons.item.ItemFromData;
import rafradek.TF2weapons.item.ItemMoney;
import rafradek.TF2weapons.item.ItemRobotPart;
import rafradek.TF2weapons.item.ItemToken;
import rafradek.TF2weapons.item.ItemUsable;
import rafradek.TF2weapons.item.ItemWeapon;
import rafradek.TF2weapons.util.PropertyType;
import rafradek.TF2weapons.util.TF2Util;

public class TileEntityGameConfigure extends TileEntity implements ITickable, IEntityConfigurable {
	
	private static final String[] OUTPUT_NAMES = {};
	private EntityOutputManager outputManager = new EntityOutputManager(this);
	private String name="";
	
	@Override
	public void update() {
		
	}

	public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
		super.writeToNBT(compound);
		compound.setTag("Config", this.getOutputManager().writeConfig(new NBTTagCompound()));
		
		return compound;
    }
	
	public void readFromNBT(NBTTagCompound compound)
    {
		super.readFromNBT(compound);
		this.getOutputManager().readConfig(compound.getCompoundTag("Config"));
		
    }
	
	public boolean receiveClientEvent(int id, int type)
    {
            return super.receiveClientEvent(id, type);
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
    	return super.getCapability(capability, facing);
    }
	
	public void onLoad() {
	}
	
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
		return super.shouldRefresh(world, pos, oldState, newSate);
    }

	protected void setWorldCreate(World worldIn)
    {
        this.setWorld(worldIn);
    }
	
	public void setWorld(World worldIn)
    {
       	super.setWorld(worldIn);
        this.getOutputManager().world = worldIn;
    }
	
	@Override
	public NBTTagCompound writeConfig(NBTTagCompound tag) {
		tag.setString("Arena Name", this.getName());
		GameArena arena = this.world.getCapability(TF2weapons.WORLD_CAP, null).gameArenas.get(this.getName());
		if (arena != null) {
			arena.writeConfig(tag);
		}
		else {
			tag.setIntArray("Min Bounds", new int[3]);
			tag.setIntArray("Max Bounds", new int[3]);
		}
		return tag;
	}

	@Override
	public void readConfig(NBTTagCompound tag) {
		this.name = tag.getString("Arena Name").trim();
		if (!this.name.isEmpty()) {
			GameArena arena = this.world.getCapability(TF2weapons.WORLD_CAP, null).gameArenas.get(this.getName());
			if (arena != null) {
				arena.readConfig(tag);
			}
			else {
				arena = new GameArena(this.getWorld(), this.name, this.getPos());
				arena.readConfig(tag);
				this.world.getCapability(TF2weapons.WORLD_CAP, null).gameArenas.put(this.name, arena);
			}
		}
	}

	@Override
	public EntityOutputManager getOutputManager() {
		return this.outputManager;
	}

	@Override
	public String[] getOutputs() {
		return OUTPUT_NAMES;
	}

	public String getName() {
		return name;
	}

	public void removeGameArena() {
		GameArena arena = this.world.getCapability(TF2weapons.WORLD_CAP, null).gameArenas.get(this.getName());
		if (arena != null && arena.getName().equals(this.name)) {
			arena.markDelete = true;
		}
	}

}
