package rafradek.TF2weapons.upgrade;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2Attribute;

public class TileEntityUpgrades extends TileEntity {

	public static final int UPGRADES_COUNT = 10;
	public HashMap<TF2Attribute, Integer> attributes = new HashMap<>();
	public TF2Attribute[] attributeList = new TF2Attribute[UPGRADES_COUNT];

	public TileEntityUpgrades() {
		super();
	}

	public TileEntityUpgrades(World world) {
		this.world = world;
	}

	/**
	 * Removes up to a specified number of items from an inventory slot and
	 * returns them in a new stack.
	 */
	public void generateUpgrades(Random rand) {
		// System.out.println("Max Size: "+MapList.nameToAttribute.size());
		List<TF2Attribute> passAttributes = TF2Attribute.getAllPassibleAttributesForUpgradeStation();
		for (int i = 0; i < UPGRADES_COUNT; i++)
			while (true) {
				TF2Attribute attr = passAttributes.get(rand.nextInt(passAttributes.size()));
				if (!this.attributes.containsKey(attr)) {
					attributeList[i] = attr;
					this.attributes.put(attr, Math.max(1, attr.numLevels - (i < 4 ? 0 : 1)));
					break;
				}
			}
		// this.world.markAndNotifyBlock(pos,
		// this.world.getChunkFromBlockCoords(getPos()),
		// this.world.getBlockState(getPos()),
		// this.world.getBlockState(pos), 2);
		this.markDirty();
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey("Attributes")) {
			NBTTagCompound attrs = compound.getCompoundTag("Attributes");
			NBTTagList attrList = (NBTTagList) compound.getTag("AttributesList");
			for (String key : attrs.getKeySet())
				this.attributes.put(TF2Attribute.attributes[Integer.parseInt(key)], attrs.getInteger(key));
			for (int i = 0; i < attrList.tagCount(); i++)
				this.attributeList[i] = TF2Attribute.attributes[attrList.getIntAt(i)];
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		NBTTagCompound attrs = new NBTTagCompound();
		NBTTagList attrList = new NBTTagList();
		compound.setTag("Attributes", attrs);
		compound.setTag("AttributesList", attrList);
		for (TF2Attribute attr : this.attributeList)
			if (attr != null) {
				attrList.appendTag(new NBTTagInt(attr.id));
				attrs.setInteger(String.valueOf(attr.id), this.attributes.get(attr));
			}

		return compound;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		// System.out.println("Sending packet");
		return new SPacketUpdateTileEntity(this.pos, 29, this.getUpdateTag());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return this.writeToNBT(new NBTTagCompound());
	}

	@Override
	public void onDataPacket(net.minecraft.network.NetworkManager net,
			net.minecraft.network.play.server.SPacketUpdateTileEntity pkt) {
		// System.out.println("Received: "+pkt.getNbtCompound());
		this.readFromNBT(pkt.getNbtCompound());
	}
}
