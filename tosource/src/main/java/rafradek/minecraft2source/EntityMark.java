package rafradek.minecraft2source;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class EntityMark extends Mark{
	
	public String classname;
	public Map<String, String> keyValues = new HashMap<>();
	public EntityMark() {
		super();
		this.type = MarkType.ENTITY;
	}
	
	public EntityMark(String name, String classname) {
		super();
		this.name = name;
		this.classname = classname;
		this.type = MarkType.ENTITY;
	}
	
	public void read(DataInputStream data, int version) throws IOException {
		this.name = data.readUTF();
		this.classname = data.readUTF();
		this.type = MarkType.values()[data.readByte()];
		this.range = new ArrayList<>();
		int rangeamount = data.readUnsignedShort();
		for (int i =0; i < rangeamount; i++) {
			this.range.add(new BlockRange(data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readInt()));
		}
		int propamount = data.readUnsignedByte();
		for (int i =0; i < propamount; i++) {
			this.keyValues.put(data.readUTF(), data.readUTF());
		}
	}
	
	public void write(DataOutputStream data) throws IOException {
		
		data.writeUTF(this.name);
		data.writeUTF(this.classname);
		data.writeByte(this.type.ordinal());
		data.writeShort(this.range.size());
		for (int i =0; i < this.range.size(); i++) {
			BlockRange range = this.range.get(i);
			data.writeInt(range.minX);
			data.writeInt(range.minY);
			data.writeInt(range.minZ);
			data.writeInt(range.maxX);
			data.writeInt(range.maxY);
			data.writeInt(range.maxZ);
		}
		data.writeByte(this.keyValues.size());
		for (Entry<String, String> entry : this.keyValues.entrySet()) {
			data.writeUTF(entry.getKey());
			data.writeUTF(entry.getValue());
		}
	}
}
