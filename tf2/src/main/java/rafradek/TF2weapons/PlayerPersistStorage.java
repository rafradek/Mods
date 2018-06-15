package rafradek.TF2weapons;

import java.util.HashSet;
import java.util.UUID;

import com.google.common.collect.HashMultimap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import rafradek.TF2weapons.building.EntityBuilding;
import rafradek.TF2weapons.building.EntityDispenser;
import rafradek.TF2weapons.building.EntitySentry;
import rafradek.TF2weapons.building.EntityTeleporter;

public class PlayerPersistStorage implements INBTSerializable<NBTTagCompound> {

	public final UUID uuid;
	public HashSet<BlockPos> lostMercPos = new HashSet<>();
	public HashSet<BlockPos> medicMercPos = new HashSet<>();
	public HashSet<BlockPos> restMercPos = new HashSet<>();
	@SuppressWarnings("unchecked")
	public Tuple<UUID, NBTTagCompound>[] buildings = new Tuple[4];
	public boolean save = false;
	public PlayerPersistStorage(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		
		NBTTagList list = new NBTTagList();
		for(BlockPos pos:lostMercPos) {
			list.appendTag(new NBTTagIntArray(new int[] {pos.getX(), pos.getY(), pos.getZ()}));
		}
		
		NBTTagList list2 = new NBTTagList();
		for(BlockPos pos:medicMercPos) {
			list.appendTag(new NBTTagIntArray(new int[] {pos.getX(), pos.getY(), pos.getZ()}));
		}
		
		NBTTagList list3 = new NBTTagList();
		for(BlockPos pos:restMercPos) {
			list.appendTag(new NBTTagIntArray(new int[] {pos.getX(), pos.getY(), pos.getZ()}));
		}
		
		tag.setTag("MercsLost", list);
		tag.setTag("MercsMedic", list2);
		tag.setTag("MercsRest", list3);
		
		if (buildings[0] != null) {
			tag.setUniqueId("SentryUUID", buildings[0].getFirst());
			tag.setTag("Sentry", buildings[0].getSecond());
		}
		
		if (buildings[1] != null) {
			tag.setUniqueId("DispenserUUID", buildings[1].getFirst());
			tag.setTag("Dispenser", buildings[1].getSecond());
		}
		
		if (buildings[2] != null) {
			tag.setUniqueId("TeleporterAUUID", buildings[2].getFirst());
			tag.setTag("TeleporterA", buildings[2].getSecond());
		}
		
		if (buildings[3] != null) {
			tag.setUniqueId("TeleporterBUUID", buildings[3].getFirst());
			tag.setTag("TeleporterB", buildings[3].getSecond());
		}
		
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.save = true;
		for(String key: nbt.getCompoundTag("MercsLost").getKeySet()){
			NBTTagList list = nbt.getTagList(key, 11);
			for (int i = 0; i < list.tagCount(); i++) {
				int[] pos = list.getIntArrayAt(i);
				lostMercPos.add(new BlockPos(pos[0], pos[1], pos[2]));
			}
		}
		for(String key: nbt.getCompoundTag("MercsMedic").getKeySet()){
			NBTTagList list = nbt.getTagList(key, 11);
			for (int i = 0; i < list.tagCount(); i++) {
				int[] pos = list.getIntArrayAt(i);
				medicMercPos.add(new BlockPos(pos[0], pos[1], pos[2]));
			}
		}
		for(String key: nbt.getCompoundTag("MercsRest").getKeySet()){
			NBTTagList list = nbt.getTagList(key, 11);
			for (int i = 0; i < list.tagCount(); i++) {
				int[] pos = list.getIntArrayAt(i);
				restMercPos.add(new BlockPos(pos[0], pos[1], pos[2]));
			}
		}
		
		if (nbt.hasUniqueId("SentryUUID"))
			this.buildings[0] = new Tuple<>(nbt.getUniqueId("SentryUUID"), nbt.getCompoundTag("Sentry"));
		
		if (nbt.hasUniqueId("DispenserUUID"))
			this.buildings[1] = new Tuple<>(nbt.getUniqueId("DispenserUUID"), nbt.getCompoundTag("Dispenser"));
		
		if (nbt.hasUniqueId("TeleporterAUUID"))
			this.buildings[2] = new Tuple<>(nbt.getUniqueId("TeleporterAUUID"), nbt.getCompoundTag("TeleporterA"));
		
		if (nbt.hasUniqueId("TeleporterBUUID"))
			this.buildings[3] = new Tuple<>(nbt.getUniqueId("TeleporterBUUID"), nbt.getCompoundTag("TeleporterB"));
	}

	public void setSave() {
		this.save = true;
	}
	
	public void setBuilding(EntityBuilding building) {
		NBTTagCompound tag = new NBTTagCompound();
		building.writeEntityToNBT(tag);
		if (building instanceof EntitySentry)
			this.buildings[0] = new Tuple<>(building.getUniqueID(), tag);
		else if (building instanceof EntityDispenser)
			this.buildings[1] = new Tuple<>(building.getUniqueID(), tag);
		else if (building instanceof EntityTeleporter && ((EntityTeleporter)building).isExit())
			this.buildings[3] = new Tuple<>(building.getUniqueID(), tag);
		else
			this.buildings[2] = new Tuple<>(building.getUniqueID(), tag);
		this.setSave();
	}
	
	public boolean hasBuilding(int slot) {
		return this.buildings[slot] != null;
	}

	public boolean allowBuilding(EntityBuilding building) {
		return this.buildings[building.getBuildingID()] == null || this.buildings[building.getBuildingID()].getFirst().equals(building.getUniqueID());
	}
	
	public static PlayerPersistStorage get(EntityPlayer player) {
		return get(player.world, player.getUniqueID());
	}
	
	public static PlayerPersistStorage get(World world, UUID uuid) {
		return world.getCapability(TF2weapons.WORLD_CAP, null).getPlayerStorage(uuid);
	}
}
