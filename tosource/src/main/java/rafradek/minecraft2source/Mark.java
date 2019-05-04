package rafradek.minecraft2source;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class Mark {
	
	public String name;
	public List<BlockRange> range = new ArrayList<>();
	public MarkType type;
	
	public enum MarkType {
		ENTITY,
		VISIBLE,
		WORLD,
		DETAIL,
		SKYBOX;
		
		public Mark create() {
			switch(this)
			{
			case ENTITY: return new EntityMark();
			default: return new Mark();
			}
		}
	}

	public void read(DataInputStream data) throws IOException {
		
	}

	public void write(DataOutputStream data) throws IOException {
		
	}
}
