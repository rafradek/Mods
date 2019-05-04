package rafradek.minecraft2source;

import java.io.IOException;
import java.io.Writer;

public class KeyValueWriter {

	private Writer writer;
	private int indent;
	private boolean endedGroup=true;
	public KeyValueWriter(Writer writer) {
		this.writer = writer;
	}
	
	public void writeNewLine() throws IOException {
		writer.write("\n");
		for (int i = 0; i < indent; i++)
			writer.write("\t");
	}
	
	public void startGroup(String name) throws IOException {
		if (!endedGroup) {
			writeNewLine();
		}
		writer.write(name);
		writeNewLine();
		writer.write('{');
		indent+=1;
		this.endedGroup = false;
	}
	
	public void keyValue(String key, String value) throws IOException {
		writeNewLine();
		writer.write('"');
		writer.write(key);
		writer.write("\" \"");
		writer.write(value);
		writer.write('"');
	}
	
	public void keyValue(String key, int value) throws IOException {
		this.keyValue(key, Integer.toString(value));
	}
	
	public void endGroup() throws IOException {
		this.endedGroup =true;
		indent-=1;
		writeNewLine();
		writer.write('}');
		writeNewLine();
	}
}
