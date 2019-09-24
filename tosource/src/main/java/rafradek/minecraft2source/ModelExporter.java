package rafradek.minecraft2source;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.io.Files;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import rafradek.minecraft2source.MapBuilder.Model;
import rafradek.minecraft2source.MapBuilder.ModelTriangle;
import rafradek.minecraft2source.MapBuilder.SpriteCombined;
import rafradek.minecraft2source.ModelExporter.SpriteProperties;

public abstract class ModelExporter {
	
	public boolean niceName;
	public Map<TextureAtlasSprite, ModelExporter.SpriteProperties> sprites;
	
	 public ThreadPoolExecutor threads = 
				new ThreadPoolExecutor(Minecraft2Source.threadCount, Minecraft2Source.threadCount,
				        0L, TimeUnit.MILLISECONDS,
				        new LinkedBlockingQueue<Runnable>());
	public List<Future<?>> futures = new ArrayList<>();
	public int threadCount =Minecraft2Source.threadCount;
	protected float scale = Minecraft2Source.blockSize;

	public void addSprite(TextureAtlasSprite sprite, boolean model, Material material) {
		if (!sprites.containsKey(sprite)) {
			ModelExporter.SpriteProperties properties = new ModelExporter.SpriteProperties(sprites.size(),sprite, this.niceName);
			properties.material = material;
			if (model)
				properties.hasModel = true;
			else
				properties.hasBrush = true;
			sprites.put(sprite, properties);
		}
		else {
			if (model)
				sprites.get(sprite).hasModel = true;
			else
				sprites.get(sprite).hasBrush = true;
		}
    }
	
	public static class SpriteProperties {
		
		public boolean isWater;
		public boolean hasModel;
		public boolean hasBrush;
		public int selfillum;
		public Material material;
		public List<Integer> colors = new ArrayList<>();
		public int id;
		public String name;
		public TextureAtlasSprite detail;
		public SpriteProperties(int id, TextureAtlasSprite sprite, boolean nicename) {
			this.id = id;
			if (sprite instanceof MapBuilder.SpriteCombined) {
				if (nicename) {
					String[] front=((MapBuilder.SpriteCombined)sprite).front.getIconName().split("/");
					String[] back=((MapBuilder.SpriteCombined)sprite).back.getIconName().split("/");
					this.name = front[front.length-1]
							+"_"+back[front.length-1];
				}
				else {
					this.name = /*encodeInt(((SpriteCombined)sprite).front.getOriginX()/16)
							+"_"+encodeInt(((SpriteCombined)sprite).front.getOriginY()/16)
							+"_"+*/MapBuilder.encodeInt(((MapBuilder.SpriteCombined)sprite).back.getOriginX()/16)
							+"_"+MapBuilder.encodeInt(((MapBuilder.SpriteCombined)sprite).back.getOriginY()/16);
				}
			}
			else {
				if (nicename) {
					String[] name = sprite.getIconName().split("/");
					this.name = name[name.length-1];
				}
				else {
					this.name = Integer.toString(sprite.getOriginX()/16, Character.MAX_RADIX)+"-"+Integer.toString(sprite.getOriginY()/16, Character.MAX_RADIX);
				}
			}
			
		}
	}
	
	public abstract Collection<Model> getModels();
	
	public void writeModels() throws IOException {
    	File parent = new File("./models/");
    	parent.mkdirs();
    	new File(Minecraft2Source.gamePathFile, "models/"+getModelOutputPath()).mkdirs();
    	File outputDir = new File(Minecraft2Source.gamePathFile, "custom/minecraft/models/"+getModelOutputPath());
    	outputDir.mkdirs();
    	for (Model model : getModels()) {
    		FileWriter writer = new FileWriter(new File(parent,model.name+".smd"));
    		writer.write("version 1\n");
    		writer.write("nodes\n");
    		writer.write("0 root -1\n");
    		writer.write("end\n");
    		writer.write("skeleton\n");
    		writer.write("time 0\n");
    		writer.write("0 0 0 0 0 0 0\n");
    		writer.write("end\n");
    		writer.write("triangles\n");
    		Set<ModelExporter.SpriteProperties> spriteSet = new HashSet<>();
    		this.writeTriangles(writer, model.quads, spriteSet);
    		writer.write("end\n");
    		writer.flush();
    		writer.close();
    		
    		if (!model.collisionBox.isEmpty()) {
        		writer = new FileWriter(new File(parent,model.name+"-p.smd"));
        		writer.write("version 1\n");
        		writer.write("nodes\n");
        		writer.write("0 root -1\n");
        		writer.write("end\n");
        		writer.write("skeleton\n");
        		writer.write("time 0\n");
        		writer.write("0 0 0 0 0 0 0\n");
        		writer.write("end\n");
        		writer.write("triangles\n");
        		this.writeTriangles(writer, model.collisionBox, null);
        		writer.write("end\n");
        		writer.flush();
        		writer.close();
    		}
    		
    		writer = new FileWriter(new File(parent,model.name+".qc"));
    		writer.write("$modelname \""+getModelOutputPath()+"/"+model.name+".mdl\"\n");
    		writer.write("$body root \""+model.name+".smd\"\n");
    		writer.write("$staticprop\n");
    		writer.write("$surfaceprop combinemetal\n");
    		writer.write("$cdmaterials \"models/"+getModelOutputPath()+"\"\n");
    		writer.write("$sequence idle \""+model.name+".smd\"\n");
    		writer.write("$texturegroup gr \n");
    		writer.write("{\n");
    		writer.write("{ ");
    		for (ModelExporter.SpriteProperties sprite : spriteSet) {
				writer.write(sprite.name);
				writer.write(" ");
			}
    		writer.write("}\n");
    		for (int i = 0; i < model.skinCount; i++) {
    			writer.write("{ ");
    			for (ModelExporter.SpriteProperties sprite : spriteSet) {
    				if (i >= sprite.colors.size())
    					writer.write(sprite.name);
    				else
    					writer.write(sprite.name+"-"+MapBuilder.encodeInt(sprite.colors.get(i)));
    				writer.write(" ");
    			}
    			writer.write("}\n");
    		}
    		writer.write("}\n");
    		if (!model.collisionBox.isEmpty()) {
    			writer.write("$collisionmodel \""+model.name+"-p.smd"+"\"\n");
    			writer.write("{\n");
    			writer.write("$concave\n");
    			writer.write("}\n");
    		}
    		writer.flush();
    		writer.close();
    		threads.execute(()->
    		{
    			try {
    				Process pr = Runtime.getRuntime().exec("\""+new File(Minecraft2Source.enginePathFile, "studiomdl").getAbsolutePath()
							+"\" -game \""+Minecraft2Source.gamePathFile.getAbsolutePath()+"\" \""+new File(parent,model.name+".qc").getAbsolutePath()+"\"");
    				pr.waitFor();
					for(File file : new File(Minecraft2Source.gamePathFile, "models/"+getModelOutputPath()).listFiles()) {
						if (file.getName().startsWith(model.name)) {
							Files.move(file, new File(outputDir, file.getName()));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
    		});
    		
    	}
    }
    
    public void writeTriangles(Writer writer, List<ModelTriangle> tris, Set<ModelExporter.SpriteProperties> spriteSet) throws IOException {
    	
		for (ModelTriangle tri : tris) {
			if (tri.sprite != null) {
				writer.write(sprites.get(tri.sprite).name+"\n");
				if (tri.hasTint)
					spriteSet.add(sprites.get(tri.sprite));
			}
			else
				writer.write("mat\n");
			for (int i =0; i <3;i++) {
    			writer.write("0 ");
    			writer.write(Float.toString(tri.pos[i].x * this.scale));
    			writer.write(" ");
    			writer.write(Float.toString(-tri.pos[i].z * this.scale));
    			writer.write(" ");
    			writer.write(Float.toString(tri.pos[i].y * this.scale));
    			writer.write("  ");
    			writer.write(Float.toString(tri.normal[i].x));
    			writer.write(" ");
    			writer.write(Float.toString(tri.normal[i].z));
    			writer.write(" ");
    			writer.write(Float.toString(tri.normal[i].y));
    			writer.write("  ");
    			writer.write(Float.toString(tri.uv[i].x));
    			writer.write(" ");
    			writer.write(Float.toString(1-tri.uv[i].y));
    			writer.write(" 0\n");
			}
		}
    }
    public void writeTextures() throws IOException {
    	File parent = new File(Minecraft2Source.gamePathFile,"custom/minecraft/materials");
    	parent.mkdirs();
    	new File(parent,getMaterialOutputPath()).mkdirs();
    	new File(parent,"models/"+getMaterialOutputPath()).mkdirs();
    	for (Entry<TextureAtlasSprite, ModelExporter.SpriteProperties> entry : this.sprites.entrySet()) {
    		TextureAtlasSprite sprite = entry.getKey();
    		//BufferedImage image = new BufferedImage(sprite.getIconWidth(),sprite.getIconHeight(),BufferedImage.TYPE_INT_ARGB);
    		//image.setRGB(0, 0, sprite.getIconWidth(), sprite.getIconHeight(), imagedata, 0, sprite.getIconWidth());
    		VTFWriter.write(parent,getMaterialOutputPath()+"/"+entry.getValue().name,sprite, entry.getValue());
    		/*for (int i = 0; i < entry.getValue().colors.size(); i++) {
    			int color = entry.getValue().get(i);
    			float r = ((color >>> 16) & 255) / 255f;
    			float g = ((color >>> 8) & 255) / 255f;
    			float b = (color & 255) / 255f;
    			int[] imagedatac = imagedata.clone();
    			for (int j = 0; j < imagedatac.length; j++) {
    				int ap = imagedatac[j] >>> 24;
    				int rp = (int) (((imagedatac[j] >>> 16) & 255) * r);
        			int gp = (int) (((imagedatac[j] >>> 8) & 255) * g);
        			int bp = (int) ((imagedatac[j] & 255) * b);
        			imagedatac[j] = (ap << 24) | (rp << 16) | (gp << 8) | bp;
    			}
        		BufferedImage imagec = new BufferedImage(sprite.getIconWidth(),sprite.getIconHeight(),BufferedImage.TYPE_INT_ARGB);
        		imagec.setRGB(0, 0, sprite.getIconWidth(), sprite.getIconHeight(), imagedatac, 0, sprite.getIconWidth());
        		File filec = ;
        		ImageIO.write(imagec, "png", filec);
        		VTFWriter.write(parent,spriteID.get(sprite)+"-"+i,sprite.getIconWidth(), sprite.getIconHeight(),1,imagedatac);
    		}*/
    	}
    }
    
    public String getModelOutputPath() {
		return "minecraft";
	}
	
	public String getMaterialOutputPath() {
		return "minecraft";
	}
}
