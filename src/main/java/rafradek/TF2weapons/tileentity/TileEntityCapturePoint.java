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

public class TileEntityCapturePoint extends TileEntity implements ITickable, IEntityConfigurable {
	
	private static final String[] OUTPUT_NAMES = {"On RED started capture", "On BLU started capture", "On RED capture", "On BLU capture", "On RED capture break", "On BLU capture break", "Capture progress"};
	public Team team;
	public float captureProgress;
	public Team contestingTeam;
	public int enemyCount;
	public boolean blocked;
	public boolean enabled=true;
	public int updateTicks = 0;
	public Team defaultTeam;
	public int pointId=0;
	public int nextPointId=-1;
	private EntityOutputManager outputManager = new EntityOutputManager(this);
	public void setEnabled(boolean enable) {
       
		this.world.addBlockEvent(this.pos, this.getBlockType(), 0, enabled ? 1:0);
		if (this.enabled != enable) {
			this.enabled = enable;
			this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);
		}
		
	}
	
	@Override
	public void update() {
		if (!this.world.isRemote) {
			float prevProgress = captureProgress;
			if (this.enabled) {
				boolean blocked = false;
				Team enemyteam = null;
				int enemynumber = 0;
				for (EntityLivingBase living : this.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(this.pos).grow(2), entityf ->{
					return entityf.isEntityAlive()  && entityf.getTeam() != null && entityf.hasCapability(TF2weapons.WEAPONS_CAP, null);
				})) {
					if (living.getTeam() == team || (contestingTeam != null && living.getTeam() != contestingTeam))
						blocked = true;
					else if (living.getTeam() == contestingTeam || contestingTeam == null) {
						enemyteam = living.getTeam();
						enemynumber +=1;
					}
					
				}
				if (enemyteam != null && contestingTeam == null) {
					contestingTeam = enemyteam;
					if (enemyteam.getName().equals("RED"))
						this.activateOutput("On RED started capture");
					else if (enemyteam.getName().equals("BLU"))
						this.activateOutput("On BLU started capture");
				}
				this.blocked = blocked;
				this.enemyCount = enemynumber;
				System.out.println("blocked: "+blocked+" "+this.enemyCount+" "+this.captureProgress+" "+this.team+" "+this.contestingTeam);
			}
			else {
				captureProgress = 0;
				this.enemyCount = 0;
			}
			if (contestingTeam != null) {
				float delta=0;
				if (!blocked && enemyCount > 0) {
					delta = 0.01f * enemyCount;
				}
				if (enemyCount == 0) {
					delta = -0.01f;
				}
				if (++this.updateTicks%5 == 0) {
					this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);
					this.activateOutput("Capture progress",this.captureProgress,5);
				}
				captureProgress +=delta;
				if (captureProgress >=1) {
					team = contestingTeam;
					contestingTeam = null;
					captureProgress = 0;
					if (team.getName().equals("RED"))
						this.activateOutput("On RED capture");
					else if (team.getName().equals("BLU"))
						this.activateOutput("On BLU capture");
				}
				else if (this.enemyCount == 0 && captureProgress <=0) {
					contestingTeam = null;
					if (team.getName().equals("RED"))
						this.activateOutput("On RED capture break");
					else if (team.getName().equals("BLU"))
						this.activateOutput("On BLU capture break");
				}
			}
		}
	}

	public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
		super.writeToNBT(compound);
		compound.setTag("Config", this.getOutputManager().writeConfig(new NBTTagCompound()));
		compound.setBoolean("Enabled", this.enabled);
		if (this.team != null)
			compound.setString("Team", this.team.getName());
		
		return compound;
    }
	
	public void readFromNBT(NBTTagCompound compound)
    {
		super.readFromNBT(compound);
		this.getOutputManager().readConfig(compound.getCompoundTag("Config"));
		this.enabled = compound.getBoolean("Enabled");
		if (this.hasWorld() && compound.hasKey("Team"))
			this.team = this.world.getScoreboard().getTeam(compound.getString("Team"));
    }
	
	public boolean receiveClientEvent(int id, int type)
    {
        if (id == 1)
        {
            this.enabled= type != 0;
            return true;
        }
        else
        {
            return super.receiveClientEvent(id, type);
        }
    }
	
	/*@Nullable
    public SPacketUpdateTileEntity getUpdatePacket()
    {
		NBTTagCompound tag = new NBTTagCompound();
		if (this.maxprogress > 0) {
			tag.setByte("P", (byte) ((float)this.progress/(float)this.maxprogress*7f));
			if (this.progress > 0)
				tag.setByte("C", (byte) ItemToken.getClassID(TF2Util.getWeaponUsedByClass(this.weapon.extractItem(hasWeapon,64,true))));
		}
        return new SPacketUpdateTileEntity(this.pos, 9999, tag);
    }
	
	public void onDataPacket(net.minecraft.network.NetworkManager net, net.minecraft.network.play.server.SPacketUpdateTileEntity pkt)
    {
		this.progressClient = pkt.getNbtCompound().getByte("P");
		this.classType = pkt.getNbtCompound().getByte("C");
    }*/
	
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
    	/*if (facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
    		if (facing.getAxis() == Axis.Y)
    			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.weapon);
    		else if (facing.getAxis() == Axis.X)
    			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.parts);
    		else 
    			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.money);
    	}
    	else*/
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
		tag.setInteger("Control Point ID", this.pointId);
		tag.setInteger("Next Control Point ID", this.nextPointId);
		if (this.defaultTeam != null)
			tag.setString("T:Default Team", this.defaultTeam.getName());
		else
			tag.setString("T:Default Team", "");
		return tag;
	}

	@Override
	public void readConfig(NBTTagCompound tag) {
		this.pointId = tag.getInteger("Control Point ID");
		this.nextPointId = tag.getInteger("Next Control Point ID");
		if (this.hasWorld() && tag.hasKey("T:Default Team"))
			this.defaultTeam = this.world.getScoreboard().getTeam(tag.getString("T:Default Team"));
	}

	@Override
	public EntityOutputManager getOutputManager() {
		return this.outputManager;
	}

	@Override
	public String[] getOutputs() {
		return OUTPUT_NAMES;
	}

}
