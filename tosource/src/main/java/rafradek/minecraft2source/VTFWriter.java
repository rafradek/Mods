package rafradek.minecraft2source;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import rafradek.minecraft2source.MapBuilder.SpriteCombined;
import rafradek.minecraft2source.ModelExporter.SpriteProperties;

public class VTFWriter {

	private static final byte[] HEADER = {86,84,70,0,7,0,0,0,1,0,0,0,64,0,0,0,0,0,0,0,1,3,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,1,13,0,0,0,0,0,1};
	public static void write(File file, String name, TextureAtlasSprite sprite, ModelExporter.SpriteProperties property) throws IOException {
		AnimationMetadataSection animation = null;
		try {
			if (sprite instanceof SpriteCombined) {
				animation = (AnimationMetadataSection) Minecraft2Source.animationField.get( ((SpriteCombined) sprite).back);
			}
			else {
				animation = (AnimationMetadataSection) Minecraft2Source.animationField.get( sprite);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int translucent = writeTexture(file ,name, sprite);
		
		if (sprite instanceof SpriteCombined) {
			String[] namespr = ((SpriteCombined)sprite).front.getIconName().split("/");
			writeTexture(file,name+"_"+namespr[namespr.length-1], ((SpriteCombined) sprite).front);
		}
		
		float frameRate = 0;
		if (animation != null && animation.getFrameCount() > 0)
			frameRate = 20f/animation.getFrameTimeSingle(0);
		else if (sprite.getFrameCount() > 1)
			frameRate = 20;
		else
			frameRate = 0;
		
		String material = MapBuilder.materialNames.get(property.material);
		if (property.isWater) {
			writeMaterial(new File(file,name+".vmt"),name,false, 0xFFFFFF , frameRate, translucent,true, false,material, sprite);
			writeMaterial(new File(file,name+"-u.vmt"),name,false, 0xFFFFFF , frameRate, translucent,true, true,material, sprite);
		}
		else {
			if (property.hasBrush)
				writeMaterial(new File(file,name+".vmt"),name,false, 0xFFFFFF , frameRate, translucent,false, false,material, sprite);
			if (property.hasModel)
				writeMaterial(new File(file,"models/"+name+".vmt"),name,true, 0xFFFFFF , frameRate, translucent,false, false,material, sprite);
			for (int i= 0; i < property.colors.size(); i++) {
				if (property.hasBrush)
					writeMaterial(new File(file,name+"-"+Integer.toString(property.colors.get(i),Character.MAX_RADIX)+".vmt"),name,false,
							property.colors.get(i) , frameRate, translucent,false, false,material, sprite);
				if (property.hasModel)
					writeMaterial(new File(file,"models/"+name+"-"+Integer.toString(property.colors.get(i),Character.MAX_RADIX)+".vmt"),name,true, property.colors.get(i),
							frameRate, translucent,false, false,material, sprite);
			}
		}
	}
	
	public static int writeTexture(File file, String name, TextureAtlasSprite sprite) throws IOException {
		AnimationMetadataSection animation = null;
		try {
			animation = (AnimationMetadataSection) Minecraft2Source.animationField.get(sprite);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		FileOutputStream stream = new FileOutputStream(new File(file,name+".vtf"));
		byte[] header = HEADER.clone();
		int translucent = 0;
		int[] testdata = sprite.getFrameTextureData(0)[0];
		for(int i =0; i < testdata.length; i++) {
			int alpha = testdata[i] >>> 24;
			if (translucent == 0 && alpha < 255) {
				if (translucent == 0 && alpha == 0)
					translucent = 1;
				else if (alpha > 0){
					translucent = 2;
				}
			}
		}
		int width = sprite.getIconWidth();
		int height = sprite.getIconHeight();
		header[16] = (byte)(width & 255);
		header[17] = (byte)((width >>> 8) & 255); 
		header[18] = (byte)(height & 255);
		header[19] = (byte)((height >>> 8) & 255);
		header[24] = (byte)(animation != null && animation.getFrameCount() > 0 ? animation.getFrameCount() : sprite.getFrameCount());
		if (translucent > 0) {
			header[21] += 32;
			header[52] = 0;
		}
		
		stream.write(header);
		if (animation != null && animation.getFrameCount() > 0)
			for (int j = 0; j < animation.getFrameCount(); j++) {
				writeFrame(stream,translucent,sprite,animation.getFrameIndex(j));
			}
		else {
			for (int j = 0; j < sprite.getFrameCount(); j++) {
				writeFrame(stream,translucent,sprite,j);
			}
		}
		stream.close();
		return translucent;
	}
	
	public static void writeFrame(FileOutputStream stream, int translucent, TextureAtlasSprite sprite, int frame) throws IOException {
		int[] data = sprite.getFrameTextureData(frame)[0];
		for(int i =0; i < data.length; i++) {
			int val = data[i];
			stream.write((val >>> 16) & 255);
			stream.write((val >>> 8) & 255);
			stream.write(val & 255);
			if (translucent > 0)
				stream.write(val >>> 24);
		}
	}
	
	public static void writeMaterial(File file, String texturename, boolean isModel,int color, float frameRate, int translucent, boolean water, boolean waterunder,
			String material, TextureAtlasSprite sprite) throws IOException {
		
		boolean detail = sprite instanceof SpriteCombined;
		
		FileWriter wmtwriter = new FileWriter(file);	
		KeyValueWriter keywriter = new KeyValueWriter(wmtwriter);
		if (isModel)
			keywriter.startGroup("VertexLitGeneric");
		else
			keywriter.startGroup("LightmappedGeneric");
		if (water) {
			keywriter.keyValue("%compilewater", 1);
			keywriter.keyValue("$abovewater", waterunder ? 0 : 1);
			keywriter.keyValue("$surfaceprop", "water");
			keywriter.keyValue("$fogenable", 1);
			keywriter.keyValue("$fogstart", 0);
			keywriter.keyValue("$fogend", 512);
			keywriter.keyValue("$fogcolor", "{0 0 25}");
			if (!waterunder)
				keywriter.keyValue("$bottommaterial", texturename+"-u");
		}
		keywriter.keyValue("$basetexture", texturename);
		if (translucent == 1 ) {
			keywriter.keyValue("$alphatest", 1);
			keywriter.keyValue("$alphatestreference", ".5");
		}
		else if (translucent > 0) {
			keywriter.keyValue("$translucent", 1);
		}
		if (material != null)
			keywriter.keyValue("$surfaceprop", material);
		if ((color & 0xFFFFFF) != 0xFFFFFF) {
			StringBuilder cbuilder = new StringBuilder();
			cbuilder.append('{');
			cbuilder.append((color>>>16) & 255);
			cbuilder.append(' ');
			cbuilder.append((color>>>8) & 255);
			cbuilder.append(' ');
			cbuilder.append(color & 255);
			cbuilder.append('}');
			if (detail) {
				keywriter.keyValue("$detailtint", cbuilder.toString());
			}
			else {
				if (isModel)
					keywriter.keyValue("$color2", cbuilder.toString());
				else
					keywriter.keyValue("$color", cbuilder.toString());
			}
		}
		if (detail) {
			String[] namespr = ((SpriteCombined)sprite).front.getIconName().split("/");
			keywriter.keyValue("$detailblendmode", "1");
			keywriter.keyValue("$detailscale", "1");
			keywriter.keyValue("$detailblendfactor", "1");
			keywriter.keyValue("$detail", texturename+"_"+namespr[namespr.length-1]);
		}
		if (frameRate > 0) {
			keywriter.startGroup("Proxies");
			keywriter.startGroup("AnimatedTexture");
			keywriter.keyValue("animatedTextureVar", "$basetexture");
			keywriter.keyValue("animatedTextureFrameNumVar", "$frame");
			keywriter.keyValue("animatedTextureFrameRate", Float.toString(frameRate));
			keywriter.endGroup();
			keywriter.endGroup();
		}
		keywriter.endGroup();
		wmtwriter.close();
	}
}
