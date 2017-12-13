package rafradek.TF2weapons.building;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemDye;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.TF2Achievements;
import rafradek.TF2weapons.TF2Sounds;
import rafradek.TF2weapons.TF2Util;
import rafradek.TF2weapons.weapons.WeaponsCapability;

public class EntityTeleporter extends EntityBuilding {
	// public static ArrayList<BlockPosDimension> teleportersData=new
	// ArrayList<BlockPosDimension>();
	public static final int TP_PER_PLAYER = 128;

	public static int tpCount = 0;
	public static HashMap<UUID, TeleporterData[]> teleporters = new HashMap<UUID, TeleporterData[]>();

	public int tpID = -1;
	public int ticksToTeleport;

	public EntityTeleporter linkedTp;

	public float spin;
	public float spinRender;

	public long timestamp;
	
	private static final DataParameter<Integer> TELEPORTS = EntityDataManager.createKey(EntityTeleporter.class,
			DataSerializers.VARINT);
	private static final DataParameter<Integer> TPPROGRESS = EntityDataManager.createKey(EntityTeleporter.class,
			DataSerializers.VARINT);
	private static final DataParameter<Byte> CHANNEL = EntityDataManager.createKey(EntityTeleporter.class,
			DataSerializers.BYTE);
	private static final DataParameter<Boolean> EXIT = EntityDataManager.createKey(EntityTeleporter.class,
			DataSerializers.BOOLEAN);
	private static final DataParameter<Byte> COLOR = EntityDataManager.createKey(EntityTeleporter.class,
			DataSerializers.BYTE);

	public EntityTeleporter(World worldIn) {
		super(worldIn);
		this.setSize(1f, 0.2f);
	}

	public EntityTeleporter(World worldIn, EntityLivingBase living) {
		super(worldIn, living);
		this.setSize(1f, 0.2f);
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity entityIn) {
		if (!this.world.isRemote && !this.isExit() && this.getTPprogress() <= 0 && entityIn != null
				&& entityIn instanceof EntityLivingBase && !(entityIn instanceof EntityBuilding)
				&& ((this.getOwner() != null && ((WeaponsCapability.get(this.getOwner()).teleporterEntity && !(entityIn instanceof EntityPlayer)) || 
						(WeaponsCapability.get(this.getOwner()).teleporterPlayer && entityIn instanceof EntityPlayer && entityIn.getTeam() == null)))
						|| TF2Util.isOnSameTeam(EntityTeleporter.this, entityIn))
				&& entityIn.getEntityBoundingBox()
						.intersects(this.getEntityBoundingBox().grow(0, 0.5, 0).offset(0, 0.5D, 0)))
			if (ticksToTeleport <= 0)
				if (ticksToTeleport < 0)
					ticksToTeleport = 10;
				else {
					TeleporterData exit = this.getTeleportExit();
					if (exit != null) {
						if (exit.dimension != this.dimension) {
							if(entityIn instanceof EntityPlayerMP && net.minecraftforge.common.ForgeHooks.onTravelToDimension(this, exit.dimension)) {
								this.world.getMinecraftServer().getPlayerList().transferPlayerToDimension((EntityPlayerMP) entityIn, 
										exit.dimension, new TeleporterDim((WorldServer) this.world,exit));
								
							}
							else {
								World destworld = this.world.getMinecraftServer().getWorld(exit.dimension);
								Entity newent = EntityList.newEntity(entityIn.getClass(), destworld);
								if(newent != null) {
									NBTTagCompound data = entityIn.writeToNBT(new NBTTagCompound());
									data.removeTag("Dimension");
									newent.readFromNBT(data);
									entityIn.setDead();
									newent.forceSpawn = true;
									entityIn.moveToBlockPosAndAngles(exit, entityIn.rotationYaw, entityIn.rotationPitch);
									destworld.spawnEntity(newent);
									entityIn = newent;
								}
							}
						}
						entityIn.setPositionAndUpdate(exit.getX() + 0.5, exit.getY() + 0.23, exit.getZ() + 0.5);
						this.setTeleports(this.getTeleports() + 1);
						this.setTPprogress(this.getLevel() == 1 ? 200 : (this.getLevel() == 2 ? 100 : 60));
						this.playSound(TF2Sounds.MOB_TELEPORTER_SEND, 1.5f, 1f);
						entityIn.playSound(TF2Sounds.MOB_TELEPORTER_RECEIVE, 0.75f, 1f);
						if(this.getOwner() instanceof EntityPlayerMP){
							((EntityPlayer) this.getOwner()).addStat(TF2Achievements.TELEPORTED);
							/*if(((EntityPlayerMP) this.getOwner()).getStatFile().readStat(TF2Achievements.TELEPORTED)>=100)
								((EntityPlayer) this.getOwner()).addStat(TF2Achievements.TELEPORTS);*/
						}
					}
				}
		return super.getCollisionBox(entityIn);
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (!this.world.isRemote) {
			if (this.tpID == -1) {
				tpCount++;
				this.tpID = tpCount;
			}
			if (!this.isExit()) {
				ticksToTeleport--;
				if (this.getTPprogress() > 0)
					this.setTPprogress(this.getTPprogress() - 1);
				if (this.getSoundState() == 1 && (this.getTPprogress() > 0 || this.getTeleportExit() == null)) {
					this.setSoundState(0);
					if (this.linkedTp != null)
						this.linkedTp.setSoundState(0);

				}
				if (this.getSoundState() == 0 && this.getTPprogress() <= 0 && this.getTeleportExit() != null) {
					this.setSoundState(1);
					if (this.linkedTp != null)
						this.linkedTp.setSoundState(1);
				}
				if (this.linkedTp != null && this.getColor() != this.linkedTp.getColor()) {
					this.getDataManager().set(COLOR, (byte) this.linkedTp.getColor());
				}
				/*
				 * if(ticksToTeleport<=0){ List<EntityLivingBase>
				 * targetList=this.world.getEntitiesWithinAABB(
				 * EntityLivingBase.class, this.getEntityBoundingBox().grow(0,
				 * 0.5, 0).offset(0, 0.5D, 0), new
				 * Predicate<EntityLivingBase>(){
				 * 
				 * @Override public boolean apply(EntityLivingBase input) {
				 * 
				 * return !(input instanceof
				 * EntityBuilding)&&EntityTeleporter.this!=input&&((TF2weapons.
				 * dispenserHeal&&input instanceof EntityPlayer &&
				 * getTeam()==null && input.getTeam() ==
				 * null)||TF2weapons.isOnSameTeam(EntityTeleporter.this,input));
				 * }
				 * 
				 * });
				 * 
				 * if(!targetList.isEmpty()){ if(ticksToTeleport<0){
				 * ticksToTeleport=10; } else{ BlockPosDimension
				 * exit=this.getTeleportExit(); if(exit !=null){
				 * if(exit.dimension!=this.dimension)
				 * targetList.get(0).travelToDimension(exit.dimension);
				 * targetList.get(0).setPositionAndUpdate(exit.getX()+0.5,
				 * exit.getY()+0.23, exit.getZ()+0.5); } } } }
				 */
			} else if (teleporters.get(this.getOwnerId())[this.getID()] == null
					|| teleporters.get(this.getOwnerId())[this.getID()].id != this.tpID) {
				this.setExit(false);
				this.updateTeleportersData(true);
			} else if (teleporters.get(this.getOwnerId())[this.getID()].id == this.tpID
					&& !this.getPosition().equals(teleporters.get(this.getOwnerId())[this.getID()]))
				this.updateTeleportersData(false);
		} else if (this.getSoundState() == 1)
			this.spin += (float) Math.PI * (this.getLevel() == 1 ? 0.25f : (this.getLevel() == 2 ? 0.325f : 0.4f));
		else
			this.spin = 0;
	}

	public TeleporterData getTeleportExit() {
		UUID uuid = this.getOwnerId();
		if (this.getOwnerId() != null && teleporters.get(uuid) != null) {
			final TeleporterData data = teleporters.get(uuid)[this.getID()];
			List<EntityTeleporter> list = world.getEntities(EntityTeleporter.class,
					new Predicate<EntityTeleporter>() {

						@Override
						public boolean apply(EntityTeleporter input) {
							// TODO Auto-generated method stub
							return data != null && data.id == input.tpID;
						}

					});
			if (!list.isEmpty())
				this.linkedTp = list.get(0);
			// System.out.println("linkedtpset");
			return data;
		}
		return null;
	}

	@Override
	public SoundEvent getSoundNameForState(int state) {
		switch (state) {
		case 0:
			return null;
		case 1:
			return this.getLevel() == 1 ? TF2Sounds.MOB_TELEPORTER_SPIN_1
					: (this.getLevel() == 2 ? TF2Sounds.MOB_TELEPORTER_SPIN_2 : TF2Sounds.MOB_TELEPORTER_SPIN_3);
		default:
			return super.getSoundNameForState(state);
		}
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		if (player == this.getOwner() && player.getHeldItem(hand).getItem() instanceof ItemDye) {
			if(!world.isRemote) {
				this.setColor(player.getHeldItem(hand).getMetadata());
			}
			return true;
		}
		if (this.world.isRemote && player == this.getOwner() && hand == EnumHand.MAIN_HAND) {
			ClientProxy.showGuiTeleporter(this);
			return true;
		}
		return false;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(TELEPORTS, 0);
		this.dataManager.register(TPPROGRESS, 0);
		this.dataManager.register(EXIT, false);
		this.dataManager.register(CHANNEL, (byte) 0);
		this.dataManager.register(COLOR, (byte) -1);
	}

	public boolean isExit() {
		return this.dataManager.get(EXIT);
	}

	public void setExit(boolean exit) {
		// if(this.getOwner()!=null&&exit&&teleporters.get(UUID.fromString(this.getOwnerId()))[this.getID()]!=null)
		// return;
		this.dataManager.set(EXIT, exit);
		if (this.getOwnerId() != null)
			this.updateTeleportersData(false);
	}

	public int getID() {
		return this.dataManager.get(CHANNEL);
	}

	public void setID(int id) {
		if ((id >= TP_PER_PLAYER || id < 0)/*
											 * ||teleporters.get(UUID.fromString(
											 * this.getOwnerId()))[id]!=null
											 */)
			return;
		if (this.getOwnerId() != null)
			this.updateTeleportersData(true);
		this.dataManager.set(CHANNEL, (byte) id);
		;
		if (this.getOwnerId() != null)
			this.updateTeleportersData(false);
	}

	public int getTPprogress() {
		return this.isDisabled() ? 20 : this.dataManager.get(TPPROGRESS);
	}

	public void setTPprogress(int progress) {
		this.dataManager.set(TPPROGRESS, progress);
	}

	public void setTeleports(int amount) {
		// TODO Auto-generated method stub
		this.dataManager.set(TELEPORTS, amount);
	}

	public int getTeleports() {
		// TODO Auto-generated method stub
		return this.dataManager.get(TELEPORTS);
	}

	public void setColor(int color) {
		this.dataManager.set(COLOR, (byte)color);
		if(this.linkedTp != null && !this.isExit()) {
			this.linkedTp.setColor(color);
		}
	}

	public int getColor() {
		return this.dataManager.get(COLOR);
	}
	
	/*
	 * public void setOwner(EntityLivingBase owner) { super.setOwner(owner);
	 * if(owner instanceof EntityPlayer){ this.dataManager.set(key, value);14,
	 * owner.getUniqueID().toString()); } }
	 */
	@Override
	public void upgrade() {
		super.upgrade();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return null;
	}

	@Override
	public void setDead() {
		// Chunk
		// chunk=this.world.getChunkFromBlockCoords(this.getPosition());
		// if(chunk.isLoaded()){
		if (!this.world.isRemote)
			this.updateTeleportersData(true);
		// }
		// System.out.println("teleporter removed: "+chunk.isLoaded()+"
		// "+chunk.isEmpty()+" "+chunk.isPopulated());
		super.setDead();
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		super.notifyDataManagerChange(key);
		if (!this.world.isRemote)
			if (key == OWNER_UUID) {
				UUID id = this.getOwnerId();
				if (!teleporters.containsKey(id))
					teleporters.put(id, new TeleporterData[TP_PER_PLAYER]);
			}
	}

	public void updateTeleportersData(boolean forceremove) {
		if (this.world.isRemote)
			return;

		UUID id = this.getOwnerId();
		if (!forceremove
				&& this.isExit()/* &&teleporters.get(id)[this.getID()]==null */)
			teleporters.get(id)[this.getID()] = new TeleporterData(this.getPosition(), this.tpID, this.dimension);
		else if (teleporters.get(id)[this.getID()] != null && teleporters.get(id)[this.getID()].id==this.tpID) {
			//new Exception().printStackTrace();
			teleporters.get(id)[this.getID()] = null;
		}
	}

	@Override
	protected SoundEvent getDeathSound() {
		return TF2Sounds.MOB_TELEPORTER_DEATH;
	}

	@Override
	public float getCollHeight() {
		return 0.2f;
	}

	@Override
	public float getCollWidth() {
		return 1f;
	}

	public int getIronDrop() {
		return 1 + this.getLevel()/2;
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeEntityToNBT(par1NBTTagCompound);

		par1NBTTagCompound.setByte("TeleExitID", (byte) this.getID());
		par1NBTTagCompound.setBoolean("TeleExit", this.isExit());
		par1NBTTagCompound.setInteger("TeleID", this.tpID);
		par1NBTTagCompound.setShort("Teleports", (short) this.getTeleports());
		par1NBTTagCompound.setByte("Color", (byte) this.getColor());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readEntityFromNBT(par1NBTTagCompound);

		this.tpID = par1NBTTagCompound.getInteger("TeleID");
		this.setTeleports(par1NBTTagCompound.getShort("Teleports"));
		this.getDataManager().set(CHANNEL, par1NBTTagCompound.getByte("TeleExitID"));
		if(teleporters.get(this.getOwnerId())[this.getID()] == null || teleporters.get(this.getOwnerId())[this.getID()].id == this.tpID)
			//this.setID(par1NBTTagCompound.getByte("TeleExitID"));
			this.setExit(par1NBTTagCompound.getBoolean("TeleExit"));
		this.getDataManager().set(COLOR, par1NBTTagCompound.getByte("Color"));
	}

	public static class TeleporterData extends BlockPos {

		public final int dimension;
		public final int id;
		public TeleporterData(BlockPos blockPos, int id, int dimension) {
			super(blockPos);
			this.id = id;
			this.dimension = dimension;
		}
	}

}
