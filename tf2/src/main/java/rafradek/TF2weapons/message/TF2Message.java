package rafradek.TF2weapons.message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import io.netty.buffer.ByteBuf;
import io.netty.util.internal.SocketUtils;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.pages.Contract;
import rafradek.TF2weapons.pages.Contract.Objective;
import rafradek.TF2weapons.weapons.WeaponsCapability;

public abstract class TF2Message implements IMessage {

	public static class ActionMessage extends TF2Message {
		int value;
		int entity;

		public ActionMessage() {

		}

		public ActionMessage(int value, EntityLivingBase entity) {
			this.value = value;
			this.entity = entity.getEntityId();
		}

		public ActionMessage(int value) {
			this.value = value;
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			this.value = buf.readByte();
			if (buf.readableBytes() > 0)
				this.entity = buf.readInt();
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeByte(this.value);
			if (this.entity != 0)
				buf.writeInt(this.entity);
		}
	}

	public static class DisguiseMessage extends TF2Message {
		String value;

		public DisguiseMessage() {

		}

		public DisguiseMessage(String name) {
			this.value = name;
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			int stringLength = buf.readByte();
			value = buf.toString(buf.readerIndex(), stringLength, StandardCharsets.UTF_8);
			buf.readerIndex(buf.readerIndex() + stringLength);
		}

		@Override
		public void toBytes(ByteBuf buf) {
			byte[] stringValueArray = value.getBytes(StandardCharsets.UTF_8);
			buf.writeByte(stringValueArray.length);
			buf.writeBytes(stringValueArray);
		}
	}

	public static class UseMessage extends TF2Message {
		int value;
		int newAmmo;
		boolean reload;
		EnumHand hand;

		public UseMessage() {
		}

		public UseMessage(int value, boolean reload,int newAmmoValue, EnumHand hand) {
			this.value = value;
			this.newAmmo=newAmmoValue;
			this.reload = reload;
			this.hand = hand;
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			this.value = buf.readShort();
			this.reload = buf.readBoolean();
			this.newAmmo = buf.readShort();
			this.hand = buf.readBoolean() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeShort(value);
			buf.writeBoolean(reload);
			buf.writeShort(newAmmo);
			buf.writeBoolean(hand == EnumHand.MAIN_HAND);
		}

	}

	public static class PredictionMessage extends TF2Message {
		public double x;
		public double y;
		public double z;
		public float pitch;
		public float yaw;
		public long time;
		// public int slot;
		public EnumHand hand;
		public List<RayTraceResult> target;
		public List<Object[]> readData;
		public int state;

		public PredictionMessage() {
		}

		public PredictionMessage(double x, double y, double z, float pitch, float yaw, int state, EnumHand hand) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.pitch = pitch;
			this.yaw = yaw;
			this.hand = hand;
			this.state = state;
		}

		public PredictionMessage(double x, double y, double z, float pitch, float yaw, int state, EnumHand hand,
				List<RayTraceResult> target2) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.pitch = pitch;
			this.yaw = yaw;
			this.hand = hand;
			this.target = target2;
			this.state = state;
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			this.x = buf.readDouble();
			this.y = buf.readDouble();
			this.z = buf.readDouble();
			this.pitch = buf.readFloat();
			this.yaw = buf.readFloat();
			this.hand = buf.readBoolean() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
			this.state = buf.readByte();
			if (buf.readableBytes() > 0) {
				this.readData = new ArrayList<Object[]>();
				while (buf.readableBytes() > 0)
					if (buf.readBoolean()) {
						Object[] obj = new Object[3];
						obj[0] = buf.readInt();
						// obj[1]=buf.readFloat();
						// obj[2]=buf.readFloat();
						// obj[3]=buf.readFloat();
						obj[1] = buf.readBoolean();
						obj[2] = buf.readFloat();
						this.readData.add(obj);
					} else {
						Object[] obj = new Object[6];
						obj[3] = buf.readInt();
						obj[4] = buf.readInt();
						obj[5] = buf.readInt();
						// obj[1]=buf.readFloat();
						// obj[2]=buf.readFloat();
						// obj[3]=buf.readFloat();
						obj[2] = buf.readFloat();
						this.readData.add(obj);
					}
			}
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeDouble(x);
			buf.writeDouble(y);
			buf.writeDouble(z);
			buf.writeFloat(pitch);
			buf.writeFloat(yaw);
			buf.writeBoolean(hand == EnumHand.MAIN_HAND);
			buf.writeByte(state);
			if (target != null)
				for (RayTraceResult mop : target) {
					if (mop.entityHit != null) {
						buf.writeBoolean(true);
						buf.writeInt(mop.entityHit.getEntityId());
						// if(mop.hitVec!=null){
						// buf.writeFloat((float) mop.hitVec.x);
						// buf.writeFloat((float) mop.hitVec.y);
						// buf.writeFloat((float) mop.hitVec.z);
						// }
						// buf.writeInt(mop.entityHit.getEntityId());
						buf.writeBoolean(((float[]) mop.hitInfo)[0] == 1);
					} else {
						buf.writeBoolean(false);
						buf.writeInt(mop.getBlockPos().getX());
						buf.writeInt(mop.getBlockPos().getY());
						buf.writeInt(mop.getBlockPos().getZ());
					}
					buf.writeFloat(((float[]) mop.hitInfo)[1]);

				}
		}

	}

	public static class PropertyMessage extends TF2Message {
		String name;
		int intValue;
		float floatValue;
		short shortValue;
		byte byteValue;
		String stringValue;
		int entityID;
		byte type;

		public PropertyMessage() {
		}

		public PropertyMessage(String name, Number value) {
			this.name = name;
			if (value instanceof Integer) {
				this.type = 0;
				this.intValue = value.intValue();
			} else if (value instanceof Float) {
				this.type = 1;
				this.floatValue = value.floatValue();
			} else if (value instanceof Byte) {
				this.type = 2;
				this.byteValue = value.byteValue();
			}
		}

		public PropertyMessage(String name, Number value, Entity entity) {
			this(name, value);
			this.entityID = entity.getEntityId();
		}

		public PropertyMessage(String name, String value, Entity entity) {
			this.type = 3;
			this.name = name;
			this.stringValue = value;
			this.entityID = entity.getEntityId();
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			entityID = buf.readInt();
			type = buf.readByte();
			if (type == 0)
				intValue = buf.readInt();
			else if (type == 1)
				floatValue = buf.readFloat();
			else if (type == 2)
				byteValue = buf.readByte();
			else if (type == 3) {
				int stringLength = buf.readByte();
				stringValue = buf.toString(buf.readerIndex(), stringLength, StandardCharsets.UTF_8);
				buf.readerIndex(buf.readerIndex() + stringLength);
			}
			// value=buf.readInt();
			name = buf.toString(buf.readerIndex(), buf.readableBytes(), StandardCharsets.UTF_8);
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeInt(entityID);
			buf.writeByte(type);
			if (type == 0)
				buf.writeInt(intValue);
			else if (type == 1)
				buf.writeFloat(floatValue);
			else if (type == 2)
				buf.writeByte(byteValue);
			else if (type == 3) {
				byte[] stringValueArray = stringValue.getBytes(StandardCharsets.UTF_8);
				buf.writeByte(stringValueArray.length);
				buf.writeBytes(stringValueArray);
			}
			byte[] stringNameArray = name.getBytes(StandardCharsets.UTF_8);
			buf.writeBytes(stringNameArray);
		}

	}

	public static class CapabilityMessage extends TF2Message {
		int healTarget;
		int entityID;
		int heads;
		int critTime;
		List < EntityDataManager.DataEntry<? >> entries;
		boolean sendAll;
		
		public CapabilityMessage() {
		}

		public CapabilityMessage(Entity entity,boolean sendAll) {
			WeaponsCapability cap = entity.getCapability(TF2weapons.WEAPONS_CAP, null);
			this.entityID = entity.getEntityId();
			this.healTarget = cap.getHealTarget();
			//this.critTime = cap.critTime;
			//this.heads = cap.collectedHeads;
			if(sendAll) {
				this.entries=cap.dataManager.getAll();
			}
			else {
				this.entries=cap.dataManager.getDirty();
			}
			// new Exception().printStackTrace();
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			entityID = buf.readInt();
			healTarget = buf.readInt();
			try {
				entries = EntityDataManager.readEntries(new PacketBuffer(buf));
				//System.out.println("Entries rec: "+(entries != null ? entries.size() : 0));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*
			 * try { tag=CompressedStreamTools.read(new ByteBufInputStream(buf),
			 * new NBTSizeTracker(2097152L)); } catch (IOException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); }
			 */
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeInt(entityID);
			buf.writeInt(healTarget);
			//System.out.println("Entries: "+entries.size());
			try {
				EntityDataManager.writeEntries(entries,new PacketBuffer(buf));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*
			 * try { CompressedStreamTools.write(tag, new
			 * ByteBufOutputStream(buf)); } catch (IOException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); }
			 */
		}

	}

	public static class BulletMessage extends TF2Message {
		// public int shooter;
		public ArrayList<RayTraceResult> target;
		public ArrayList<Object[]> readData;
		public int slot;
		public EnumHand hand;

		public BulletMessage() {

		}

		public BulletMessage(int slot, ArrayList<RayTraceResult> target, EnumHand hand) {
			// this.shooter=shooter.getEntityId();
			this.slot = slot;
			this.target = target;
			this.hand = hand;
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			slot = buf.readByte();
			this.hand = buf.readBoolean() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
			this.readData = new ArrayList<Object[]>();
			while (buf.readableBytes() > 0) {
				Object[] obj = new Object[3];
				obj[0] = buf.readInt();
				// obj[1]=buf.readFloat();
				// obj[2]=buf.readFloat();
				// obj[3]=buf.readFloat();
				obj[1] = buf.readBoolean();
				obj[2] = buf.readFloat();
				this.readData.add(obj);
			}
		}

		@Override
		public void toBytes(ByteBuf buf) {
			// buf.writeInt(shooter);
			buf.writeByte(this.slot);
			buf.writeBoolean(hand == EnumHand.MAIN_HAND);
			for (RayTraceResult mop : target) {
				buf.writeInt(mop.entityHit.getEntityId());
				// if(mop.hitVec!=null){
				// buf.writeFloat((float) mop.hitVec.x);
				// buf.writeFloat((float) mop.hitVec.y);
				// buf.writeFloat((float) mop.hitVec.z);
				// }
				// buf.writeInt(mop.entityHit.getEntityId());
				buf.writeBoolean(((float[]) mop.hitInfo)[0] == 1);
				buf.writeFloat(((float[]) mop.hitInfo)[1]);
			}

		}

	}

	public static class GuiConfigMessage extends TF2Message {
		// public int shooter;
		int entityid;
		BlockPos pos;
		boolean isTile;
		byte id;
		boolean exit;
		boolean grab;
		int value;
		int targetFlags;

		public GuiConfigMessage() {

		}

		public GuiConfigMessage(int entityID, byte id, int value) {
			// this.shooter=shooter.getEntityId();
			this.isTile = true;
			this.id = id;
			this.value = value;
			this.entityid = entityID;
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			id = buf.readByte();
			entityid = buf.readInt();
			value = buf.readInt();
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeByte(id);
			buf.writeInt(entityid);
			buf.writeInt(value);
		}

	}

	public static class ShowGuiMessage extends TF2Message {
		// public int shooter;

		public int id;

		public ShowGuiMessage() {

		}

		public ShowGuiMessage(int id) {
			// this.shooter=shooter.getEntityId();
			this.id = id;
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			id = buf.readByte();
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeByte(id);

		}

	}

	public static class WeaponDataMessage extends TF2Message {

		byte[] bytes;

		public WeaponDataMessage() {
		}

		public WeaponDataMessage(byte[] bytes) {
			this.bytes = bytes;
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			//System.out.println("Read bytes: "+buf.readableBytes());
			this.bytes=new byte[buf.readableBytes()];
			buf.readBytes(this.bytes);
		}

		@Override
		public void toBytes(ByteBuf buf) {
			//System.out.println("Write bytes: "+bytes.length);
			buf.writeBytes(bytes);
		}

	}

	public static class WearableChangeMessage extends TF2Message {
		// public int shooter;

		public int slot;
		public int entityID;
		public ItemStack stack;

		public WearableChangeMessage() {

		}

		public WearableChangeMessage(EntityPlayer player, int slot, ItemStack stack) {
			// this.shooter=shooter.getEntityId();
			this.slot = slot;
			this.entityID = player.getEntityId();
			this.stack = stack;
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			slot = buf.readByte();
			entityID = buf.readInt();
			try {
				stack = new PacketBuffer(buf).readItemStack();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeByte(slot);
			buf.writeInt(entityID);
			new PacketBuffer(buf).writeItemStack(stack);

		}

	}
	public static class WeaponDroppedMessage extends TF2Message {
		// public int shooter;

		public String name;

		public WeaponDroppedMessage() {

		}

		public WeaponDroppedMessage(String name) {
			// this.shooter=shooter.getEntityId();
			this.name=name;
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			this.name=new PacketBuffer(buf).readString(256);
		}

		@Override
		public void toBytes(ByteBuf buf) {
			new PacketBuffer(buf).writeString(name);

		}

	}
	public static class ContractMessage extends TF2Message {
		// public int shooter;

		public int id;
		public Contract contract;
		
		public ContractMessage() {

		}

		public ContractMessage(int id,Contract contract) {
			// this.shooter=shooter.getEntityId();
			this.id=id;
			this.contract=contract;
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			this.id=buf.readByte();
			String name=new PacketBuffer(buf).readString(256);
			int expireDay=buf.readInt();
			int progress=buf.readShort();
			Objective[] objectives=new Objective[buf.readByte()];
			for(int i=0;i<objectives.length;i++) {
				objectives[i]=Objective.values()[buf.readByte()];
			}
			this.contract=new Contract(name,expireDay,objectives);
			this.contract.progress=progress;
			this.contract.active=buf.readBoolean();
			this.contract.rewards=buf.readByte();
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeByte(id);
			new PacketBuffer(buf).writeString(contract.className);
			buf.writeInt(contract.expireDay);
			buf.writeShort(contract.progress);
			buf.writeByte(contract.objectives.length);
			for(Objective obj:contract.objectives) {
				buf.writeByte(obj.ordinal());
			}
			buf.writeBoolean(contract.active);
			buf.writeByte(contract.rewards);
			
				
		}

	}
	public static class VelocityAddMessage extends TF2Message {
		// public int shooter;

		public float x;
		public float y;
		public float z;
		public boolean airborne;

		public VelocityAddMessage() {

		}

		public VelocityAddMessage(Vec3d vec, boolean airborne) {
			// this.shooter=shooter.getEntityId();
			this.x=(float) vec.x;
			this.y=(float) vec.y;
			this.z=(float) vec.z;
			this.airborne=airborne;
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			this.x=buf.readFloat();
			this.y=buf.readFloat();
			this.z=buf.readFloat();
			this.airborne=buf.readBoolean();
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeFloat(x);
			buf.writeFloat(y);
			buf.writeFloat(z);
			buf.writeBoolean(airborne);
		}

	}
	
	public static class AttackSyncMessage extends TF2Message {
		long time;
		int entity;

		public AttackSyncMessage() {

		}

		public AttackSyncMessage(long value, EntityLivingBase entity) {
			this.time = value;
			this.entity = entity.getEntityId();
		}

		public AttackSyncMessage(long value) {
			this.time = value;
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			this.time=buf.readLong();
			if (buf.readableBytes() > 0)
				this.entity = buf.readInt();
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeLong(this.time);
			if (this.entity != 0)
				buf.writeInt(this.entity);
		}
	}
	
	public static class InitMessage extends TF2Message {
		
		int port;

		int id;
		
		public InitMessage() {

		}

		public InitMessage(int port, int id) {
			this.port = port;
			this.id = id;
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			this.port = buf.readUnsignedShort();
			this.id = buf.readShort();
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeShort(this.port);
			buf.writeShort(id);
		}
	}
	
	public static class InitClientMessage extends TF2Message {
		
		int sentryTargets;
		boolean dispenserPlayer;
		boolean teleporterPlayer;
		boolean teleporterEntity;
		
		public InitClientMessage() {

		}

		public InitClientMessage(Configuration conf) {
			ConfigCategory cat = conf.getCategory("default building targets");
			this.sentryTargets = cat.get("Attack on hurt").getBoolean() ? 1 : 0;
			this.sentryTargets += cat.get("Attack other players").getBoolean() ? 2 : 0;
			this.sentryTargets += cat.get("Attack hostile mobs").getBoolean() ? 4 : 0;
			this.sentryTargets += cat.get("Attack friendly creatures").getBoolean() ? 8 : 0;
			this.dispenserPlayer = cat.get("Dispensers heal neutral players").getBoolean();
			this.teleporterPlayer = cat.get("Neutral players can teleport").getBoolean();
			this.teleporterEntity = cat.get("Entities can teleport").getBoolean();
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			this.sentryTargets = buf.readByte();
			this.dispenserPlayer = buf.readBoolean();
			this.teleporterPlayer = buf.readBoolean();
			this.teleporterEntity = buf.readBoolean();
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeByte(this.sentryTargets);
			buf.writeBoolean(this.dispenserPlayer);
			buf.writeBoolean(this.teleporterPlayer);
			buf.writeBoolean(this.teleporterEntity);
		}
	}
}
