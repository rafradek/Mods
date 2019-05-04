package rafradek.minecraft2source;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.Set;
import java.util.function.BiConsumer;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.ExecutorServices;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.google.common.io.Files;

import akka.actor.FSM.State;
import codechicken.lib.math.MathHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.IClientCommand;
import net.minecraftforge.common.util.EnumHelper;
import rafradek.minecraft2source.Mark.MarkType;

public class MapBuilder
{
    private static final Logger LOGGER = LogManager.getLogger();

    
    
    private static WorldVertexBufferUploader vboInstance;
    
    private static VertexReader vertexReader = new VertexReader();
    
    public static final TextureAtlasSprite CLIP = new SpriteTool("TOOLS/TOOLSCLIP");
    public static final TextureAtlasSprite PLAYER_CLIP = new SpriteTool("TOOLS/TOOLSPLAYERCLIP");
    public static final TextureAtlasSprite SKYBOX = new SpriteTool("TOOLS/TOOLSSKYBOX");
    public Vector3f offset = new Vector3f(0f,0f,0f); 
    
    public List<Cube>[][][] cubes;
    public boolean[][][] occluders;
    public Map<BakedQuad, Vector3f[]> vecCache = new HashMap<>();
    public Map<BakedQuad, Vector2f[]> uvCache = new HashMap<>();
    public Map<IBlockState, Model> models = new HashMap<>();
    public Map<IBlockState, List<CubeDef>> brushes = new HashMap<>();
    public static Map<Material, String> materialNames = new HashMap<>();
    public List<ModelInstance> modelList;
    public List<MapEntity> entityList;
    public List<BiConsumer<IBlockState,List<CubeDef>>> fixes;
    public Map<TextureAtlasSprite, SpriteProperties> sprites;
    public int entityid;
    public int solidid;
    public int sideid;
    public short[][][] heightCache;
    public short[][][] widthCache;
    public short[][][] depthCache;
    public byte[][][] lights;
    
   	public TextureAtlasSprite lavaStill = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/lava_still");
   	public TextureAtlasSprite lavaFlow = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/lava_flow");
   	public TextureAtlasSprite waterStill = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/water_still");
   	public TextureAtlasSprite waterFlow = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/water_flow");
    public ThreadPoolExecutor threads = 
					new ThreadPoolExecutor(Minecraft2Source.threadCount, Minecraft2Source.threadCount,
					        0L, TimeUnit.MILLISECONDS,
					        new LinkedBlockingQueue<Runnable>());
    public List<Future<?>> futures = new ArrayList<>();
    public int threadCount =Minecraft2Source.threadCount;
    public List<EntityMark> markParts[][];
    public static Field fieldLightmap;
    public Map<EntityMark, Integer> marks;
    public Set<Integer> marksUsed;
    
    public boolean niceName;
    public boolean modelsOnly;
    
    public int brushcount;
    
    public int xmin;
    public int ymin;
    public int zmin;
    
    public int xsize;
    public int ysize;
    public int zsize;
    
    public boolean isManifest;
    
	@SuppressWarnings("unchecked")
	public MapBuilder(int xmin, int ymin, int zmin, int xsize, int ysize, int zsize) {
		super();
		this.xmin = xmin;
		this.ymin = ymin;
		this.zmin = zmin;
		this.xsize = xsize;
		this.ysize = ysize;
		this.zsize = zsize;
		cubes = new List[xsize][ysize][zsize];
    	occluders = new boolean[xsize][ysize][zsize];
    	lights = new byte[xsize][ysize][zsize];
		vecCache = new HashMap<>();
    	brushes = new HashMap<>();
    	sprites = new HashMap<>();
    	models = new HashMap<>();
    	modelList = new ArrayList<>();
    	widthCache= new short[xsize][ysize][zsize];
    	heightCache= new short[xsize][ysize][zsize];
    	depthCache= new short[xsize][ysize][zsize];
	}

	public void initialize(World world) {
    	entityList = new ArrayList<>();
    	MapEntity worldent = new MapEntity("worldspawn", true);
    	entityList.add(worldent);
    	MapEntity detail = new MapEntity("func_detail", true);
    	entityList.add(detail);
    	MapEntity nonsolid = new MapEntity("func_brush", true);
    	nonsolid.properties.put("Solidity","1");
    	nonsolid.properties.put("vrad_brush_cast_shadows","0");
    	nonsolid.properties.put("InputFilter", "32");
    	entityList.add(nonsolid);
    	MapEntity noshadow= new MapEntity("func_brush", true);
    	noshadow.properties.put("Solidity","1");
    	noshadow.properties.put("vrad_brush_cast_shadows","1");
    	noshadow.properties.put("InputFilter", "32");
    	entityList.add(noshadow);
    	/*MapEntity selflight= new MapEntity("func_brush", true);
    	selflight.properties.put("Solidity", "2");
    	selflight.properties.put("disablereceiveshadows", "1");
    	selflight.properties.put("disableshadows","1");
    	selflight.properties.put("_minlight",Float.toString(3.75f));
    	entityList.add(selflight);*/
    	DynamicTexture lightmap = (DynamicTexture) Minecraft.getMinecraft().getTextureManager().getTexture(new ResourceLocation("dynamic/lightmap_1"));
    	int[] data = lightmap.getTextureData();
    	for (int i = 0; i< 256; i+=16) {
    		System.out.println("color: "+((data[i]>>>16)&255)+" "+((data[i]>>>8)&255)+" "+((data[i])&255));
    	}
    	for (int i = 0; i< 16; i+=1) {
    		System.out.println("color2: "+((data[i]>>>16)&255)+" "+((data[i]>>>8)&255)+" "+((data[i])&255));
    	}
    	
    	MapEntity lightenv = new MapEntity("light_environment", true);
    	lightenv.properties.put("angles", "30 0 0");
    	lightenv.properties.put("pitch", "-70");
    	
    	StringBuilder builder = new StringBuilder();
    	int i = 240-world.getSkylightSubtracted()*16;
    	builder.append(((data[i]>>>16)&255));
    	builder.append(' ');
    	builder.append(((data[i]>>>8)&255));
    	builder.append(' ');
    	builder.append((data[i]&255));
    	builder.append(' ');
    	builder.append((((data[i]>>>8)&255) + ((data[i]>>>8)&255) + (data[i]&255)) * Minecraft2Source.sunBrightness/765);
    	lightenv.properties.put("_light", builder.toString());
    	
    	builder = new StringBuilder();
    	i = 240-(world.getSkylightSubtracted()+2)*16;
    	builder.append(((data[i]>>>16)&255));
    	builder.append(' ');
    	builder.append(((data[i]>>>8)&255));
    	builder.append(' ');
    	builder.append((data[i]&255));
    	builder.append(' ');
    	builder.append((((data[i]>>>8)&255) + ((data[i]>>>8)&255) + (data[i]&255)) * (int)(Minecraft2Source.sunBrightness*0.8f)/765);
    	lightenv.properties.put("_ambient", builder.toString());
    	
    	entityList.add(lightenv);
    	addSprite(lavaStill,false, Material.WATER);
    	addSprite(lavaFlow,false, Material.WATER);
    	addSprite(waterStill,false, Material.WATER);
    	addSprite(waterFlow,false, Material.WATER);
    	this.sprites.get(lavaStill).isWater = true;
    	this.sprites.get(lavaFlow).isWater = true;
    	this.sprites.get(waterStill).isWater = true;
    	this.sprites.get(waterFlow).isWater = true;
    }
    
    public int getFirstFullCubeId(List<Cube> cubes) {
    	for (int i = 0; i < cubes.size(); i++) {
    		if (cubes.get(i).entity != 3 && cubes.get(i).def.isFullCube)
    			return i;
    	}
    	return -1;
    }
    
    public Cube getFirstFullCube(List<Cube> cubes) {
    	for (int i = 0; i < cubes.size(); i++) {
    		if (cubes.get(i).entity != 3 && cubes.get(i).def.isFullCube)
    			return cubes.get(i);
    	}
    	return null;
    }
    
    @SuppressWarnings("unchecked")
	public void initEntityMarks() {
    	int xmsize=MathHelper.ceil(this.xsize/16f);
    	int zmsize=MathHelper.ceil(this.zsize/16f);
    	markParts = new List[xmsize][zmsize];
    	marks = new HashMap<>();
    	for (EntityMark mark : Minecraft2Source.entities) {
    		boolean added= false;
    		if (mark.type != MarkType.ENTITY)
    			continue;
    		for (BlockRange range : mark.range) {
    			int xmax = Math.min(xmsize,MathHelper.ceil((range.maxX - this.xmin + this.xsize)/16f));
    			int zmax = Math.min(zmsize,MathHelper.ceil((range.maxZ - this.zmin + this.zsize)/16f));
    			for(int x = Math.max(0,(range.minX - this.xmin)/16); x < xmax; x++) {
    				for(int z = Math.max(0,(range.minZ - this.zmin)/16); z < zmax; z++) {
    					List<EntityMark> part = markParts[x][z];
    					if (part == null)
    						markParts[x][z] = part = new ArrayList<>();
        				part.add(mark);
        				added = true;
        			}
    			}
    		}
    		if (added) {
    			MapEntity ent = new MapEntity(((EntityMark)mark).classname, false);
    			ent.properties.put("targetname", mark.name);
    			ent.properties.putAll(((EntityMark)mark).keyValues);
    			marks.put((EntityMark) mark, entityList.size());
    			entityList.add(ent);
    		}
    	}
    }
    
    public boolean buildModelCache(IBlockState state, IBakedModel model, World world, long rand, BlockPos pos, float twidth, float theight,EnumMap<EnumFacing, List<BakedQuad>> quads) {
    	
    	vecCache.clear();
    	uvCache.clear();
		boolean diagonal = false;
		
    	//vecCache.clear();
    	for (EnumFacing facing: EnumFacing.VALUES) {
    		List<BakedQuad> quadsList=model.getQuads(state, facing, rand);
    		quads.put(facing, new ArrayList<>(quadsList));
    		//Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Face: "+facing));
        	for(int i =0; i < quadsList.size(); i++) {
        		Vector3f[] cache = new Vector3f[4];
        		Vector2f[] uvcache = new Vector2f[4];
        		uvcache[0] = new Vector2f(1,1);
        		uvcache[1] = new Vector2f();
        		BakedQuad quad = quadsList.get(i);
        		TextureAtlasSprite sprite = quad.getSprite();
        		vecCache.put(quad, cache);
        		uvCache.put(quad, uvcache);
        		int[] vertexData=quad.getVertexData();
        		/*if (cubeList.size() <= i)
        			cubeList.add(new Cube());
        		Cube cube = cubeList.get(i);
        		cube.sidesEnabled.set(facing.ordinal(),shouldRender);
        		cube.sprites.put(facing, quads.get(i).getSprite());*/
        		float usize = sprite.getMaxU()-sprite.getMinU();
        		float vsize = sprite.getMaxV()-sprite.getMinV();
        		for (int j=0; j<28;j+=7) {
	        		float x1 = Math.round(Float.intBitsToFloat(vertexData[0+j])*256f)/256f;
	        		float y1 = Math.round(Float.intBitsToFloat(vertexData[1+j])*256f)/256f;
	        		float z1 = Math.round(Float.intBitsToFloat(vertexData[2+j])*256f)/256f;
	        		float u = Math.round(Float.intBitsToFloat(vertexData[4+j])*twidth)/twidth;
	        		float v = Math.round(Float.intBitsToFloat(vertexData[5+j])*theight)/theight;
	    			u -= sprite.getMinU();
	    			v -= sprite.getMinV();
	    			
	    			u /= usize;
	    			v /= vsize;
	        		cache[j/7]=new Vector3f(x1,y1,z1);
	        		if (j > 0 && !diagonal) {
	        			Vector3f prev = cache[(j-1)/7];
	        			if ((prev.x != x1 && prev.y != y1) || (prev.x != x1 && prev.z != z1) || (prev.y != y1 && prev.z != z1))
	        				diagonal = true;
	        		}
	        		uvcache[j/7]= new Vector2f(u, v);
	        		//Minecraft.getMinecraft().player.sendMessage(new TextComponentString(x1+" "+y1+" "+z1+" "+u+" "+v+" "+quadsList.get(i).getSprite().getMinV()+" "+vertexData.length));
        		}
        		
        	}
        		
    	}
    	if (!model.getQuads(state, null, rand).isEmpty()) {
			
    		//Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Face: null"));
    		for (int j=0;j<model.getQuads(state, null, rand).size();j++) {
    			
    			BakedQuad quad = model.getQuads(state, null, rand).get(j);
    			TextureAtlasSprite sprite = quad.getSprite();
	    		int[] vertexData=quad.getVertexData();
	    		Vector3f[] cache = new Vector3f[4];
        		Vector2f[] uvcache = new Vector2f[4];
        		uvCache.put(quad, uvcache);
        		vecCache.put(quad, cache);
        		
        		float usize = sprite.getMaxU()-sprite.getMinU();
        		float vsize = sprite.getMaxV()-sprite.getMinV();
		    	for (int i=0; i<28;i+=7) {
	        		float x1 = Math.round(Float.intBitsToFloat(vertexData[0+i])*256f)/256f;
	        		float y1 = Math.round(Float.intBitsToFloat(vertexData[1+i])*256f)/256f;
	        		float z1 = Math.round(Float.intBitsToFloat(vertexData[2+i])*256f)/256f;
	        		float u = Math.round(Float.intBitsToFloat(vertexData[4+i])*twidth)/twidth;
	        		float v = Math.round(Float.intBitsToFloat(vertexData[5+i])*theight)/theight;
	        		u -= sprite.getMinU();
	    			v -= sprite.getMinV();
	    			
	    			u /= usize;
	    			v /= vsize;
	        		cache[i/7]=new Vector3f(x1,y1,z1);
	        		if (i > 0 && !diagonal) {
	        			Vector3f prev = cache[(i-1)/7];
	        			if ((prev.x != x1 && prev.y != y1) || (prev.x != x1 && prev.z != z1) || (prev.y != y1 && prev.z != z1))
	        				diagonal = true;
	        		}
	        		uvcache[i/7]= new Vector2f(u, v);
	        		//Minecraft.getMinecraft().player.sendMessage(new TextComponentString( x1+" "+y1+" "+z1+" "+u+" "+v+" "+quad.getSprite().getMaxV()+" "+vertexData.length));
	    		}
	    		quads.get(model.getQuads(state, null, rand).get(j).getFace()).add(model.getQuads(state, null, rand).get(j));
    		}
		}
    	return diagonal;
    }
    
    public void loadTileEntityModel(World world, IBlockState state, int x, int y, int z) {
    	
    }
    
    @SuppressWarnings("unchecked")
	public void build(World world, String filename, boolean saveWorld, boolean saveEntity) throws IOException {
    	
    	
		
    	entityid = 0;
    	solidid = 0;
    	sideid = 0;
    	
    	BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
    	
    	
    	brushcount = 0;
    	initialize(world);
    	this.initEntityMarks();
		
    	long start = System.nanoTime();
		TextureAtlasSprite sprtest = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:items/brick");
		int twidth=(int) (sprtest.getIconWidth()/(sprtest.getMaxU()-sprtest.getMinU()));
		int theight=(int) (sprtest.getIconHeight()/(sprtest.getMaxV()-sprtest.getMinV()));
		float twidthsp=twidth;
		float theightsp=theight;
		byte[][][] empty = new byte[xsize][ysize][zsize];
		ChunkCache ccache = new ChunkCache(world, new BlockPos(this.xmin,this.ymin, this.zmin),
				new BlockPos(this.xmin+this.xsize, this.ymin + this.ysize, this.zmin+this.zsize), 2);
		System.out.println("init took "+(System.nanoTime()-start));
		for (int x= 0; x < xsize; x++) {
			byte[][] emptyx = empty[x];
			for (int y= 0; y < ysize; y++) {
				byte[] emptyxy = emptyx[y];
				for (int z= 0; z < zsize; z++) {
					pos.setPos(x+xmin, y+ymin, z+zmin);
					IBlockState state = ccache.getBlockState(pos).getActualState(ccache, pos);
					AxisAlignedBB cbox = state.getCollisionBoundingBox(ccache, pos);
					if (cbox == null)
						emptyxy[z] = 0;
					else
						emptyxy[z] = (byte)(cbox.maxY*16);
				}
			}
		}
		try {
			try {
				vboInstance = (WorldVertexBufferUploader) Minecraft2Source.vboField.get(Tessellator.getInstance());
				EnumHelper.setFailsafeFieldValue(Minecraft2Source.vboField, Tessellator.getInstance(), vertexReader);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (int x= 0; x < xsize; x++) {
				for (int y= 0; y < ysize; y++) {
	        		for (int z= 0; z < zsize; z++) {	
	        			pos.setPos(x+xmin, y+ymin, z+zmin);
	    	        	IBlockState state = ccache.getBlockState(pos).getActualState(ccache, pos);
	    	        	
	    	        	long rand = world.rand.nextLong();
	    	        	if(!brushes.containsKey(state) && !models.containsKey(state)) {
	    	        		List<AxisAlignedBB> cboxo= new ArrayList<>();
	    	                List<AxisAlignedBB> cbox= new ArrayList<>();
	    	                
	    	            	state.addCollisionBoxToList(world, pos, new AxisAlignedBB(pos), cboxo, null, true);
	    	            	TileEntity tent = ccache.getTileEntity(pos);
	    	            	if (tent != null) {
	    	            		TileEntityRendererDispatcher.instance.render(tent, 0, 0);
	    	            	}
	    	            	for (AxisAlignedBB bbox : cboxo) {
	    	            		cbox.add(bbox.offset(-pos.getX(), -pos.getY(), -pos.getZ()));
	    	            	}
	    	        		if (state.getRenderType() == EnumBlockRenderType.MODEL) {
	    	        			
	    	        			IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(state);
	    	        			List<BakedQuad> noFaceQuad=model.getQuads(state, null, rand);
	    	        			EnumMap<EnumFacing, List<BakedQuad>> quads = new EnumMap<>(EnumFacing.class);
	    	        			boolean diagonal = this.buildModelCache(state, model, world, rand, pos, twidthsp, theightsp, quads);
		        	        	if (diagonal || this.modelsOnly) {
		        	        		addModel(state, readModel(state, cbox, offset));
		        	        	}
		        	        	else {
		        	        		List<CubeDef> cubes= readCubes(quads, state, model, rand, noFaceQuad, cbox);
		        	        		if (cubes != null)
		        	        			brushes.put(state, cubes);
		        	        		else
		        	        			addModel(state, readModel(state, cbox, offset));
		        	        	}
	    	        		}
	    	        		else if (state.getRenderType() == EnumBlockRenderType.LIQUID) {
	    	        			int level = ((Integer)state.getValue(BlockLiquid.LEVEL)).intValue();
	    	        			int total = 0;
	    	        			float height = 0;
	    	        			if (level >= 8 || level == 0)
	    	                    {
	    	                        height += BlockLiquid.getLiquidHeightPercent(level) * 10.0F;
	    	                        total += 10;
	    	                    }
	
	    	                    height += BlockLiquid.getLiquidHeightPercent(level);
	    	                    ++total;
	    	                    height = 1.0f - (height/total);
	    	                    
	    	                    CubeDef cube = new CubeDef();
	    	                    cube.xMin=0;
	    	                    cube.xMax=1;
	    	                    cube.yMin=0;
	    	                    cube.yMax=height;
	    	                    cube.zMin=0;
	    	                    cube.zMax=1;
	    	                    if (state.getMaterial() == Material.WATER) {
	    	                    	cube.sprites.put(EnumFacing.UP,this.waterStill);
	    	                    	for (EnumFacing facing : EnumFacing.HORIZONTALS)
	    	                    	cube.sprites.put(facing,this.waterFlow);
	    	                    }
	    	                    else {
	    	                    	cube.sprites.put(EnumFacing.UP,this.lavaStill);
	    	                    	for (EnumFacing facing : EnumFacing.HORIZONTALS)
	    	                    	cube.sprites.put(facing,this.lavaFlow);
	    	                    }
	    	                    Vector2f[] uv = new Vector2f[2];
	    	                    uv[0] = new Vector2f(0,0);
	    	                    uv[1] = new Vector2f(1,1);
	    	                    cube.uv.put(EnumFacing.UP, uv);
	    	                    
	    	                    cube.isWater = true;
	    	                    for (EnumFacing facing : EnumFacing.HORIZONTALS) {
	    	                    	cube.cullFace.add(facing);
	    	                    	cube.uv.put(facing, uv);
	    	                    }
	    	                    brushes.put(state,Lists.newArrayList(cube));
	    	        		}
	    	        	}
	    	        	
	        		}
				}
			}
		this.waitForFutures();
    	}
		finally {
        	try {
				EnumHelper.setFailsafeFieldValue(Minecraft2Source.vboField, Tessellator.getInstance(), vboInstance);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		
		System.out.println("model discovery took "+(System.nanoTime()-start) + " brushes: "+brushes.size()+ " models: "+models.size());
		for (int x= 0; x < ysize; x++) {
			for (int y= 0; y < zsize; y++) {
        		for (int z= 0; z < xsize; z++) {
        			
        		}
			}
		}
		int xthreads = Math.min(this.threadCount,xsize);
		EntityMark visiblemark = Minecraft2Source.entitiesMap.get(MarkType.VISIBLE.toString());
		for (int l = 0; l < xthreads; l++) {
			int l1 = l;
			futures.add(threads.submit(()->
			{
				EnumSet<EnumFacing> visiblefaces = EnumSet.allOf(EnumFacing.class);
			for (int x= l1; x < xsize; x+=xthreads) {
				List<Cube>cubesx[][] = cubes[x];
		    	for (int y= 0; y < ysize; y++) {
		    		List<Cube>cubesxy[] = cubesx[y];
					for (int z= 0; z < zsize; z++) {
	    				pos.setPos(x+xmin, y+ymin, z+zmin);
	    	        	IBlockState state = world.getBlockState(pos).getActualState(ccache, pos);
	    	        	
	    	        	Vec3d offset = state.getOffset(ccache, pos);
	    	        	
	    	        	int entityid = this.getEntityForBlock(pos);
	    	        	
	    	        	boolean hasBrush = false;
	    	        	if (brushes.containsKey(state) && !brushes.get(state).isEmpty()) {
	    	        		
	        	        	List<Cube> cubeList = cloneCubeList(brushes.get(state),empty,x,y,z);
	        	        	for(int j = 0; j < cubeList.size(); j++) {
	        	        		Cube cube = cubeList.get(j);
	        	        		if (cube.def.autoEntity != 2)
	        	        			hasBrush = true;
	        	        		if (cube.def.isWater && cube.yMax < 1 && ccache.getBlockState(pos.up()).getMaterial() == state.getMaterial())
	        	        			cube.yMax = 1;
	        	        		for (int i = 0; i < 6; i++) {
	        	        			if (cube.def.tint[i] != -1) {
	        	        				int color = Minecraft.getMinecraft().getBlockColors().colorMultiplier(state, ccache, pos, cube.def.tint[i]);
	        	        				cube.color[i] = color;
	        	        				List<Integer> colors=sprites.get(cube.def.sprites.get(EnumFacing.VALUES[i])).colors;
	        	        				boolean hascolor = false;
	        	        				for (int k = 0; k< colors.size(); k++)
	        	        					if (colors.get(k) == color) {
	        	        						hascolor = true;
	        	        						break;
	        	        					}
	        	        				
	        	        				if (!hascolor) {
	        	        					colors.add(color);
	        	        				}
	        	        			}
	        	        		}
	        	        			
	        	        		cube.xMin = (float) ((cube.xMin +(offset.x+x)));
	        	        		cube.yMin = (float) ((cube.yMin +(offset.y+y)));
	        	        		cube.zMin = (float) ((cube.zMin +(offset.z+z)));
	        	        		cube.xMax = (float) ((cube.xMax +(offset.x+x)));
	        	        		cube.yMax = (float) ((cube.yMax +(offset.y+y)));
	        	        		cube.zMax = (float) ((cube.zMax +(offset.z+z)));
	        	        		cube.entity = entityid;
	        	        		if (cube.entity == 0)
	        	        			cube.entity = cube.def.autoEntity;
	        	        		if (cube.entity == 0 && Minecraft2Source.occludersEnabled && Minecraft2Source.volumeSearch && !cube.def.isWater)
	        	        			cube.entity = 1;
	        	        	}
	        	        	for(int j = 0; j < cubeList.size(); j++) {
	        	        		Cube cube = cubeList.get(j);
	        	        		
	        	        		if (visiblemark != null) {
	        	        			visiblefaces.clear();
	        	        			for (BlockRange range : visiblemark.range) {
	        	        				if (range.maxX > x+xmin) {
	        	        					visiblefaces.add(EnumFacing.EAST);
	        	        				}
	        	        				if (range.minX+1 < x+xmin) {
	        	        					visiblefaces.add(EnumFacing.WEST);
	        	        				}
	        	        				if (range.maxZ > z+zmin) {
	        	        					visiblefaces.add(EnumFacing.SOUTH);
	        	        				}
	        	        				if (range.minZ+1 < z+zmin) {
	        	        					visiblefaces.add(EnumFacing.NORTH);
	        	        				}
	        	        				if (range.maxY > y+ymin) {
	        	        					visiblefaces.add(EnumFacing.UP);
	        	        				}
	        	        				if (range.minY+1 < y+ymin) {
	        	        					visiblefaces.add(EnumFacing.DOWN);
	        	        				}
	        	        			}
	        	        		}
	        	        		for(EnumFacing facing : EnumFacing.VALUES) {
	        	        			if (visiblefaces.contains(facing) &&
	        	        					(!cube.def.cullFace.contains(facing) || (entityid != this.getEntityForBlock(pos.offset(facing))) || state.shouldSideBeRendered(ccache, pos, facing))) {
	        	        				cube.sidesEnabled.add(facing);
	        	        				cube.isVisible = true;
	        	        			}
	        	        		}
	        	        		/*if (!Minecraft2Source.volumeSearch) {
	        	        			Cube joined = joinAllCubes(cubeList, cube, x, y, z, lastCubeListX); 
	        	        			if (joined != null) {
	        	        				cubeList.set(j, joined);
	        	    					brushcount-=1;
	        	    					entityList.get(cube.entity).removeCube(cube);
	        	    					j--;
	        	        			}
	        	        		}*/
	        	        	}
	        	        	
	        	        	for (Cube cube: cubeList) {
	        	        		if (!Minecraft2Source.volumeSearch) {
		        	        		brushcount+=1;
		        	        		entityList.get(cube.entity).addCube(cube);
	        	        		}
	        	        	}
	        	        	cubesxy[z]=cubeList;
	        	        	
	    	        	}
	    	        	int light = state.getLightValue(ccache, pos);
	    	        	if (light > 0) {
	    	        		lights[x][y][z] = (byte)( light + (hasBrush ? 16 : 0));
	    	        	}
	    	        	if (models.containsKey(state)) {
	    	        		
	    	        		Model modeldef = models.get(state);
	    	        		
	    	        		int skinid = 0;
	    	        		if (modeldef.hasTint) {
	        	        		int color = Minecraft.getMinecraft().getBlockColors().colorMultiplier(state, ccache, pos, 0);
	        	        		label:
	        	        		for (ModelTriangle tri : modeldef.quads) {
	        	        			if (tri.hasTint) {
		        	        			List<Integer> colors=sprites.get(tri.sprite).colors;
			        	        		boolean hascolor = false;
			        	        		for (int k = 0; k< colors.size(); k++)
				        					if (colors.get(k) == color) {
				        						skinid = Math.min(31,k+1);
				        						hascolor = true;
				        						break label;
				        					}
				        				
				        				if (!hascolor) {
				        					skinid=Math.min(31,colors.size()+1);
				        					colors.add(color);
				        					break label;
				        				}
	        	        			}
	        	        		}
	        	        		if (skinid > modeldef.skinCount) {
		        					modeldef.skinCount = skinid;
		        				}
	    	        		}
	    	        		
	    	        		MapEntity modelinst = new MapEntity("prop_static",true);
	    	        		
	    	        		modelinst.setPosition(x+(float)offset.x, y+(float)offset.y, z+(float)offset.z);
	    	        		
	    	        		modelinst.properties.put("disableshadows", "1");
	    	        		modelinst.properties.put("model", "models/minecraft/"+modeldef.name+".mdl");
	    	        		modelinst.properties.put("skin", Integer.toString(skinid));
	    	        		modelinst.properties.put("ignorenormals", "1");

	    	        		if (modeldef.collisionBox.isEmpty())
	    	        			modelinst.properties.put("solid", "0");
	    	        		entityList.add(modelinst);
	    	        	}
	    			}
					
	    		}
	    	}
			}));
		}
		this.waitForFutures();
    	System.out.println("adding cubes took "+(System.nanoTime()-start));
    	
    	if (Minecraft2Source.occludersEnabled)
			this.calculateOccluders(world);
    	System.out.println("occluders took "+(System.nanoTime()-start));
    	
    	if (Minecraft2Source.volumeSearch) {
    		int lastVolume=Integer.MAX_VALUE;
    		int[] maxpos = new int[7];
    		int[][] maxposcache = new int[threads.getCorePoolSize()][7];
    		this.initVolumeCache(false);
    		System.out.println("volume start took "+(System.nanoTime()-start));
    		int i = 0;
    		int errors = 0;
        	while(true) {
        		getHighestVolume(lastVolume,maxpos,maxposcache);
        		if (maxpos[6] < 4)
        			break;
        		if (maxpos[6] > lastVolume)
        			errors +=1;
        		lastVolume = maxpos[6];
        		Cube cube = this.getFirstFullCube(cubes[maxpos[3]][maxpos[4]][maxpos[5]]);
        		i++;
        		if (i == 100)
        			System.out.println("volume 100 took "+(System.nanoTime()-start));
        		if (i == 1000)
        			System.out.println("volume 1000 took "+(System.nanoTime()-start));
        		this.updateVolumeCache(maxpos);
        		int xthreads1 = Math.min(this.threadCount,maxpos[3]-maxpos[0]+1);
    			for (int l = 0; l < xthreads1; l++) {
    				int l1 = l;
    				
    				futures.add(threads.submit(()->
    				{
    					for(int x = maxpos[0]+l1; x <= maxpos[3]; x+=xthreads1) {
	        			boolean minX= x==maxpos[0];
	        			boolean maxX= x==maxpos[3];
	        			for(int y = maxpos[1]; y <= maxpos[4]; y++) {
	        				boolean minY= y==maxpos[1];
	            			boolean maxY= y==maxpos[4];
	        				for(int z = maxpos[2]; z <= maxpos[5]; z++) {
	        					boolean minZ= z==maxpos[2];
	                			boolean maxZ= z==maxpos[5];
	                			Cube cube2=cubes[x][y][z].get(0);
	                			if (minX && cube2.sidesEnabled.contains(EnumFacing.WEST))
	                				cube.sidesEnabled.add(EnumFacing.WEST);
	                			if (maxX && cube2.sidesEnabled.contains(EnumFacing.EAST))
	                				cube.sidesEnabled.add(EnumFacing.EAST);
	                			if (minY && cube2.sidesEnabled.contains(EnumFacing.DOWN))
	                				cube.sidesEnabled.add(EnumFacing.DOWN);
	                			if (maxY && cube2.sidesEnabled.contains(EnumFacing.UP))
	                				cube.sidesEnabled.add(EnumFacing.UP);
	                			if (minZ && cube2.sidesEnabled.contains(EnumFacing.NORTH))
	                				cube.sidesEnabled.add(EnumFacing.NORTH);
	                			if (maxZ && cube2.sidesEnabled.contains(EnumFacing.SOUTH))
	                				cube.sidesEnabled.add(EnumFacing.SOUTH);
	                			if( cubes[x][y][z].size() > 1)
	                				cubes[x][y][z].remove(this.getFirstFullCubeId(cubes[x][y][z]));
	                			else
	        						cubes[x][y][z]= null;
	                			if (cube.entity == 1 && cube.def.autoEntity == 0 && occluders[x][y][z])
	                				cube.entity = 0;
	                		}
	            		}
    				}
    				}));
        		}
    			this.waitForFutures();
    			cube.xMin=maxpos[0];
        		cube.yMin=maxpos[1];
        		cube.zMin=maxpos[2];
        		cube.xMax=maxpos[3]+1;
        		cube.yMax=maxpos[4]+1;
        		cube.zMax=maxpos[5]+1;
        		cube.extendedX=maxpos[3]-maxpos[0]+1;
        		cube.extendedY=maxpos[4]-maxpos[1]+1;
        		cube.extendedZ=maxpos[5]-maxpos[2]+1;
        		brushcount++;
        		entityList.get(cube.entity).addCube(cube);
        	}
        	System.out.println("volume "+i+" took "+(System.nanoTime()-start)+" errors: "+errors);
        	
    	}
    	
    	for (int y= 0; y < ysize; y++) {
			for (int z= 0; z < zsize; z++) {
				List<Cube> lastCubeListX=null;
        		for (int x= 0; x < xsize; x++) {
        			pos.setPos(x+xmin, y+ymin, z+zmin);
        			List<Cube> listCubes=cubes[x][y][z];
        			if (listCubes != null) {
        				
        				for (int j = 0; j < listCubes.size(); j++) {
            				Cube cube = listCubes.get(j);
            				if (cube.entity == 1 && cube.def.autoEntity == 0 && occluders[x][y][z])
                				cube.entity = 0;
            				
	            			Cube joined = joinAllCubes(listCubes, cube, x, y, z, lastCubeListX); 
    	        			if (joined != null) {
    	        				listCubes.set(j, joined);
    	    					brushcount-=1;
    	    					entityList.get(cube.entity).removeCube(cube);
    	    					j--;
    	    					
    	        			}
        				}
        				for (Cube cube: listCubes) {
        	        		brushcount+=1;
        	        		entityList.get(cube.entity).addCube(cube);
        	        	}
        			}
        			lastCubeListX=listCubes;
        		}
			}
    	}
    	
    	System.out.println("volume post took "+(System.nanoTime()-start)+" "+brushcount);
    	if (!Minecraft2Source.occludersEnabled){
        	Iterator<Cube> it = entityList.get(0).cubes.iterator();
    		while(it.hasNext()) {
    			Cube cube = it.next();
    			if (cube.extendedX * cube.extendedY * cube.extendedZ < 4) {
	    			it.remove();
	    			entityList.get(1).addCube(cube);
    			}
    		}
    	}
    	
    	this.removeInvisibleBrushes(this.entityList.get(2));
    	
    	if (brushcount > 8000) {
    		for (int i = 0; i < 3;i++) {
    			this.removeInvisibleBrushes(this.entityList.get(i));
    		}
    	}
    	
    	if (Minecraft2Source.boundaries == 0) {
    		addSkybox();
    	}
    	
    	this.surroundLightBlocks(world);
    	this.addLights(ccache);
    	
    	System.out.println("misc took "+(System.nanoTime()-start));
    	
    	if (saveWorld)
    		this.writeMap(xsize, ysize, zsize, true, new File(Minecraft2Source.mapOutput,filename+"_w.vmf"));
    	if (saveEntity)
    		this.writeMap(xsize, ysize, zsize, false, new File(Minecraft2Source.mapOutput,filename+"_e.vmf"));
    	
    	writeTextures();
    	writeModels();
    	
    	System.out.println("write took "+(System.nanoTime()-start));
    	Minecraft.getMinecraft().player.sendMessage(new TextComponentString("commands.buildmap.start"));
        
    }
    
    public void removeInvisibleBrushes(MapEntity entity) {
		Set<Cube> set = entity.cubes;
		if (set != null) {
    		Iterator<Cube> it = set.iterator();
    		label:
    		while(it.hasNext()) {
    			Cube cube = it.next();
    			for (EnumFacing facing : EnumFacing.VALUES) {
        			if (cube.def.sprites.containsKey(facing) && cube.sidesEnabled.contains(facing)) {
    		    		continue label;
    		    	}
    			}
    			brushcount--;
    			it.remove();
    		}
		}
    }
    public void addLights(IBlockAccess ccache) {
    	BlockPos.MutableBlockPos pos = new MutableBlockPos();
    	for (int x= 0; x < xsize; x++) {
    		byte[][] lightsx = lights[x];
	    	for (int y= 0; y < ysize; y++) {
	    		byte[] lightsxy = lightsx[y];
				for (int z= 0; z < zsize; z++) {
					int light = lightsxy[z];
        			
        			if ((light & 16) == 16) {
        				light = light & 15;
        				lightsxy[z] = 0;
        				for (EnumFacing facing : EnumFacing.VALUES) {
        					if (!this.isOutOfBounds(x+facing.getFrontOffsetX(),y+facing.getFrontOffsetY(),z+facing.getFrontOffsetZ())) {
    	        			IBlockState state2 = ccache.getBlockState(pos.setPos(x+this.xmin, y+this.ymin, z+this.zmin).offset(facing));
    	        			if (!state2.isFullCube() && state2.getLightValue(ccache, pos) < light)
    	        				lights[x+facing.getFrontOffsetX()][y+facing.getFrontOffsetY()][z+facing.getFrontOffsetZ()] = (byte) (light-1);
        					}
        				}
        			}
        		}
			}
    	}
    	for (int x= 0; x < xsize; x++) {
    		byte[][] lightsx = lights[x];
	    	for (int y= 0; y < ysize; y++) {
	    		byte[] lightsxy = lightsx[y];
				for (int z= 0; z < zsize; z++) {
					int light = lightsxy[z];
        			
        			if (light> 0) {
        				boolean foundLight = false;
            			for (int y1= 0; y1 < 2; y1++) {
        	    			for (int z1= 0; z1 < 2; z1++) {
        	            		for (int x1= 0; x1 < 2; x1++) {
        	            			
        	            			if (!(x1==0 && y1== 0 && z1==0)&& !this.isOutOfBounds(x-x1,y-y1,z-z1) && lights[x-x1][y-y1][z-z1] >= light) {
        	            				foundLight = true;
        	            				break;
        	            			}
        	            		}
        	    			}
            			}
            			if (!foundLight) {
	        				MapEntity ent = new MapEntity("light", true);
	        				ent.setPosition(x+0.5f, y+0.5f, z+0.5f);
	        				ent.properties.put("_light", "207 195 169 "+light*Minecraft2Source.blockBrightness);
	        				ent.properties.put("_fifty_percent_distance", Float.toString(Minecraft2Source.blockSize*light*0.85f));
	        				ent.properties.put("_zero_percent_distance", Float.toString(Minecraft2Source.blockSize*light));
	        				ent.properties.put("_distance", Float.toString(Minecraft2Source.blockSize*light*1.1f));
	        				ent.properties.put("_hardfalloff", Float.toString(Minecraft2Source.blockSize*light*1.1f));
	        				entityList.add(ent);
            			}
            			else
            				lightsxy[z] = 0;
        			}
        		}
			}
    	}
    }
    
    public void addSkybox() {
    	CubeDef skybox = new CubeDef();
		skybox.autoEntity=0;
		for (EnumFacing facing : EnumFacing.VALUES)
			skybox.sprites.put(facing, SKYBOX);
		Cube cube = new Cube();
		cube.def = skybox;
		cube.xMin=0;
		cube.yMin=0;
		cube.zMin=-1;
		cube.xMax=xsize;
		cube.yMax=ysize;
		cube.zMax=0;
		this.entityList.get(0).addCube(cube);
		
		cube = new Cube();
		cube.def = skybox;
		cube.xMin=0;
		cube.yMin=-1;
		cube.zMin=0;
		cube.xMax=xsize;
		cube.yMax=0;
		cube.zMax=zsize;
		this.entityList.get(0).addCube(cube);
		
		cube = new Cube();
		cube.def = skybox;
		cube.xMin=-1;
		cube.yMin=0;
		cube.zMin=0;
		cube.xMax=0;
		cube.yMax=ysize;
		cube.zMax=zsize;
		this.entityList.get(0).addCube(cube);
		
		cube = new Cube();
		cube.def = skybox;
		cube.xMin=0;
		cube.yMin=0;
		cube.zMin=zsize;
		cube.xMax=xsize;
		cube.yMax=ysize;
		cube.zMax=zsize+1;
		this.entityList.get(0).addCube(cube);
		
		cube = new Cube();
		cube.def = skybox;
		cube.xMin=0;
		cube.yMin=ysize;
		cube.zMin=0;
		cube.xMax=xsize;
		cube.yMax=ysize+1;
		cube.zMax=zsize;
		this.entityList.get(0).addCube(cube);
		
		cube = new Cube();
		cube.def = skybox;
		cube.xMin=xsize;
		cube.yMin=0;
		cube.zMin=0;
		cube.xMax=xsize+1;
		cube.yMax=ysize;
		cube.zMax=zsize;
		this.entityList.get(0).addCube(cube);
    }
    public void surroundLightBlocks(World world) {
    	BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(0, 0, 0);
		MapEntity entity = entityList.get(1);
		if (entity.cubes != null) {
			Iterator<Cube> it = entity.cubes.iterator();
			label1:
			while(it.hasNext()) {
				Cube cube = it.next();
				if (!cube.def.isFullCube) {
					continue;
				}
				boolean defined = false;
				for(int x=(int)cube.xMin; x < (int)cube.xMin+cube.extendedX; x++) {
					for(int y=(int)cube.yMin; y < (int)cube.yMin+cube.extendedY; y++) {
						for(int z=(int)cube.zMin; z < (int)cube.zMin+cube.extendedZ; z++) {
							int light = this.lights[x][y][z];
		    	        	if (!defined && (light & 16) == 16) {
		    	        		it.remove();
		    	        		MapEntity newent = new MapEntity("func_brush", true);
    							newent.addCube(cube);
    							newent.properties.put("Solidity", "2");
    							newent.properties.put("disablereceiveshadows", "1");
    							newent.properties.put("disableshadows","1");
    							newent.properties.put("_minlight",Float.toString((light & 15)/4f));
    							cube.entity=entityList.size();
		    	        		//cube.entity=4;
		    	        		//entityList.get(4).addCube(cube);
    							entityList.add(newent);
    							defined =true;
		    	        	}
		    	        	if (defined) {
		    	        		this.lights[x][y][z] = (byte) (light & 15);
		    	        	}
	    				}
    				}
				}
				
			}
    	}
    }
    
    public static void writeMapInit(KeyValueWriter keywriter) throws IOException {
    	keywriter.startGroup("versioninfo");
    	keywriter.keyValue("editorversion", 400);
    	keywriter.keyValue("editorbuild", 7937);
    	keywriter.keyValue("mapversion", 1);
    	keywriter.keyValue("formatversion", 100);
    	keywriter.keyValue("prefab", 0);
    	keywriter.endGroup();
    	keywriter.startGroup("visgroups");
    	keywriter.endGroup();
    	keywriter.startGroup("viewsettings");
    	keywriter.keyValue("bSnapToGrid", 1);
    	keywriter.keyValue("bShowGrid", 1);
    	keywriter.keyValue("nGridSpacing", (int)Minecraft2Source.blockSize);
    	keywriter.keyValue("bShow3DGrid", 0);
    	keywriter.endGroup();
    	keywriter.startGroup("world");
    	keywriter.keyValue("id", 1);
    	keywriter.keyValue("mapversion", 1);
    	keywriter.keyValue("classname", "worldspawn");
    	keywriter.keyValue("skyname", "sky_tf2_04");
    	keywriter.keyValue("maxpropscreenwidth", "-1");
    	keywriter.keyValue("detailvbsp", "detail_2fort.vbsp");
    	keywriter.keyValue("detailmaterial", "detail/detailsprites_2fort");
    }
    
    public void writeMap(int xsize, int ysize, int zsize, boolean generated, File file) throws IOException {
    	file.getParentFile().mkdirs();
    	FileWriter writer = new FileWriter(file);
    	KeyValueWriter keywriter = new KeyValueWriter(writer);
    	
    	writeMapInit(keywriter);
    	entityid+=1;
    	if (generated && entityList.get(0).cubes != null)
    		writeCubes(keywriter, entityList.get(0).cubes);
    	
    	keywriter.endGroup();
    	
    	
    	for(int i = 1; i < entityList.size(); i++) {
    		MapEntity entity = entityList.get(i);
    		if (((entity.generated && generated) || (!entity.generated && !generated)) && (generated || entity.cubes != null)) {
	    		keywriter.startGroup("entity");
	    		entityid+=1;
	    		keywriter.keyValue("id", entityid);
	    		for(Entry<String, String> entry : entity.properties.entrySet()) {
	    			keywriter.keyValue(entry.getKey(), entry.getValue());
	    		}
	    		if (entity.hasOrigin) {
	        		StringBuilder builder = new StringBuilder();
	        		builder.append(translateZ(entity.z));
	        		builder.append(' ');
	        		builder.append(translateX(entity.x));
	        		builder.append(' ');
	        		builder.append(translateY(entity.y));
	        		keywriter.keyValue("origin", builder.toString());
	    		}
	    		if (entity.cubes != null)
	    			writeCubes(keywriter,entity.cubes);
	    		keywriter.endGroup();
    		}
    	}
    	keywriter.startGroup("cameras");
    	keywriter.keyValue("activecamera", -1);
    	keywriter.endGroup();
    	if (this.isManifest) {
    	keywriter.startGroup("cordon");
    	keywriter.keyValue("mins", "(0 0 0)");
    	keywriter.keyValue("maxs", "("+zsize*Minecraft2Source.blockSize+" "+xsize*Minecraft2Source.blockSize+" "+ysize*Minecraft2Source.blockSize+")");
    	keywriter.keyValue("active", this.isManifest && Minecraft2Source.boundaries == 1 ? 1 : 0);
    	}
    	keywriter.endGroup();
    	writer.flush();
    	writer.close();
    	
    	
    }
    /*private void getHighestVolume2D(byte[][] comparray, int[] out) {
    	int xsize = comparray.length;
    	int zsize = comparray[0].length;
    	int[][] sizearray = new int[xsize][zsize];
    		for (int j = 0; j < zsize; j++) {
    			int sum = 0;
    			for (int k = 0; k < xsize; k++) {
    				sum++;
    				sizearray[k][j] = sum;
    				boolean reset = true;
    				if (k > 0 && cubes[k][y][j] != null && cubes[k-1][i][j] != null) {
	    				Cube cube1 = cubes[k-1][i][j].get(0);
	    				Cube cube2 = cubes[k][i][j].get(0);
						if (cube.isJoinable(cube2, EnumFacing.EAST)) {
							reset = false;
						}
    				}
    				if (reset)
    					sum = 0;
    			}
    		}
    }*/
    
    public Vector3f translatePos(Vector3f in) {
    	in.x= in.z * Minecraft2Source.blockSize;
    	in.y= in.x * Minecraft2Source.blockSize;
    	in.z= in.y * Minecraft2Source.blockSize;
    	return in;
    }
    
    public float translateX(float x) {
    	return x * Minecraft2Source.blockSize;
    }
    
    public float translateY(float y) {
    	return y * Minecraft2Source.blockSize;
    }
    
    public float translateZ(float z) {
    	return z * Minecraft2Source.blockSize;
    }
    
    public Cube joinAllCubes(List<Cube> cubeList, Cube cube,int x, int y, int z, List<Cube> lastCubeListX) {
    	if (lastCubeListX != null) {
			for (Cube lastcube : lastCubeListX) {
				if(lastcube != cube) {
    				if(this.joinCube(cube, lastcube, EnumFacing.EAST)) {
    					return lastcube;
    				}
    				else {
    					
    				}
				}
			}
		}
		
		if (z > 0 && cubes[x][y][z-1] != null) {
			for (Cube lastcube : cubes[x][y][z-1]) {
				if(lastcube != cube) {
					if (this.joinCube(cube, lastcube, EnumFacing.SOUTH)) {
    					return lastcube;
    				}
					else {
						
					}
				}
			}
		}
		int xoff = x;
		int zoff = z;
		if (cube.def.helperDir != null) {
			xoff+=cube.def.helperDir.getFrontOffsetX();
			zoff+=cube.def.helperDir.getFrontOffsetZ();
		}
		if (y > 0 && cubes[xoff][y-1][zoff] != null) {
			
			for (Cube lastcube : cubes[xoff][y-1][zoff]) {
				if(lastcube != cube && this.joinCube(cube, lastcube, EnumFacing.UP)) {
					return lastcube;
				}
			}
		}
		return null;
    }
    private void initVolumeCache(boolean occluder) {
    	for(int l = 0; l < this.threadCount; l++) {
    		int l1=l;
	    	futures.add(threads.submit(()->
	    	{
	    	for (int i = l1; i < ysize; i+=this.threadCount) {
	    		for (int j = 0; j < zsize; j++) {
	    			int sum = 0;
	    			CubeDef firstvis = null;
	    			for (int k = 0; k < xsize; k++) {
	    				boolean reset = true;
	    				
	    				if (k > 0 && cubes[k][i][j] != null && cubes[k-1][i][j] != null) {
		    				Cube cube1 = this.getFirstFullCube(cubes[k-1][i][j]);
		    				Cube cube2 = this.getFirstFullCube(cubes[k][i][j]);
							if (cube1 != null && cube2 != null && ((occluder && cube1.def.autoEntity == 0 && cube2.def.autoEntity == 0) || 
									(!occluder && cube1.isJoinable(cube2, EnumFacing.EAST,firstvis)))) {
								reset = false;
								sum++;
							}
	    				}
	    				
	    				if (cubes[k][i][j] != null) {
	    					Cube cube = this.getFirstFullCube(cubes[k][i][j]);
	    					if (cube != null && (k == 0 || cube.isVisible))
	    						firstvis = cube.def;
	    				}
	    				
	    				if (reset)
	    					sum = 1;
	    				heightCache[k][i][j] = (short) sum;
	    			}
	    		}
	    	}
	    	}));
	    }
    	for(int l = 0; l < this.threadCount; l++) {
    		int l1=l;
	    	futures.add(threads.submit(()->
	    	{
			for (int i = l1; i < ysize; i+=this.threadCount) {
				for (int k = 0; k < xsize; k++) {
					int width = 0;
					CubeDef firstvis = null;
					for (int j = 0; j < zsize; j++) {
						
						boolean reset = true;
						if (j > 0 && cubes[k][i][j] != null && cubes[k][i][j-1] != null) {
		    				Cube cube1 = this.getFirstFullCube(cubes[k][i][j-1]);
		    				Cube cube2 = this.getFirstFullCube(cubes[k][i][j]);
							if (cube1 != null && cube2 != null && ((occluder && cube1.def.autoEntity == 0 && cube2.def.autoEntity == 0) || 
									(!occluder && cube1.isJoinable(cube2, EnumFacing.SOUTH, firstvis)))) {
								reset = false;
								width++;
							}
	    				}
						
						if (cubes[k][i][j] != null) {
	    					Cube cube = this.getFirstFullCube(cubes[k][i][j]);
	    					if (cube != null && (j == 0 || cube.isVisible))
	    						firstvis = cube.def;
	    				}
						if (reset)
							width = 1;
						widthCache[k][i][j] = (short) width;
					}
				}
			}
	    	}));
    	}
    	for(int l = 0; l < this.threadCount; l++) {
    		int l1=l;
	    	futures.add(threads.submit(()->
	    	{
			for (int k = l1; k < xsize; k+=this.threadCount) {
				for (int j = 0; j < zsize; j++) {
					int sum = 0;
					CubeDef firstvis = null;
					for (int i = 0; i < ysize; i++) {
						
						boolean reset = true;
						
						if (i > 0 && cubes[k][i][j] != null && cubes[k][i-1][j] != null) {
		    				Cube cube1 = this.getFirstFullCube(cubes[k][i-1][j]);
		    				Cube cube2 = this.getFirstFullCube(cubes[k][i][j]);
							if (cube1 != null && cube2 != null && ((occluder && cube1.def.autoEntity == 0 && cube2.def.autoEntity == 0) || 
									(!occluder && cube1.isJoinable(cube2, EnumFacing.UP, firstvis)))) {
								reset = false;
								sum++;
							}
	    				}
						
						if (cubes[k][i][j] != null) {
	    					Cube cube = this.getFirstFullCube(cubes[k][i][j]);
	    					if (cube != null && (i == 0 || cube.isVisible))
	    						firstvis = cube.def;
	    				}
						if (reset)
							sum = 1;
						depthCache[k][i][j] = (short) sum;
					}
				}
			}
	    	}));
    	}
    	this.waitForFutures();
    }
    private void updateVolumeCache(int[] maxpos) {
    	futures.add(threads.submit(()->
    	{
		for(int z = maxpos[2]; z <= maxpos[5]; z++) {
			for(int y = maxpos[1]; y <= maxpos[4]; y++) {
				for(int x = maxpos[0]; x < xsize; x++) {
    				if (heightCache[x][y][z] > x-maxpos[3])
    					heightCache[x][y][z] = (short) Math.max(1,x-maxpos[3]);
					else
						break;
				}
    		}
		}
    	}));
    	futures.add(threads.submit(()->
    	{
    		for(int x = maxpos[0]; x <= maxpos[3]; x++) {
    			for(int y = maxpos[1]; y <= maxpos[4]; y++) {
					for(int z = maxpos[2]; z < zsize; z++) {
	    				if (widthCache[x][y][z] > z-maxpos[5])
	    					widthCache[x][y][z] = (short) Math.max(1,z-maxpos[5]);
						else
							break;
					}
	    		}
			}
    	}));
    	futures.add(threads.submit(()->
    	{
    		for(int z = maxpos[2]; z <= maxpos[5]; z++) {
    			for(int x = maxpos[0]; x <= maxpos[3]; x++) {
					for(int y = maxpos[1]; y < ysize; y++) {
	    				if (depthCache[x][y][z] > y-maxpos[4])
	    					depthCache[x][y][z] = (short) (y-maxpos[4]);
						else
							break;
					}
	    		}
			}
    	}));
		this.waitForFutures();
    }
    private int[] getHighestVolume(int threshold, int[] out, int[][] threadcache) {
    	
		int maxVol=0;
		out[6] = maxVol;
		
		out[0]=0;
		out[1]=0;
		out[2]=0;
		
		out[3]=0;
		out[4]=0;
		out[5]=0;
		//ThreadPoolExecutor threads = (ThreadPoolExecutor) Executors.newFixedThreadPool(8);
		for (int t = 0; t<this.threadCount;t++) {
			int t1 = t;
			futures.add(threads.submit(()->
	    	{
	    	int[] out1=threadcache[t1];
			out1[6] = 1;
			
			out1[0]=0;
			out1[1]=0;
			out1[2]=0;
			
			out1[3]=0;
			out1[4]=0;
			out1[5]=0;
			for (int k = xsize-1-t1; k >=0; k-=this.threadCount) {
				short[][] widthCacheL=widthCache[k];
				short[][] heightCacheL=heightCache[k];
				short[][] depthCacheL=depthCache[k];
				for (int i = ysize-1; i >= 0; i--) {
					short[] widthCacheL1=widthCacheL[i];
					short[] heightCacheL1=heightCacheL[i];
					short[] depthCacheL1=depthCacheL[i];
					for (int j = zsize-1; j >=0; j--) {
					
						
						int width = widthCacheL1[j];
						
						if (width < 0) {
							//System.out.println("nextis"+widthCache[k][i][j+width-1]+" "+(j+width));
							j+= width;
							continue;
						}
						
						int depth = depthCacheL1[j];
						int tvol = depth * width * heightCacheL1[j];
						if (tvol > out1[6]) {
							int minwidth= zsize;
							int minheight = xsize;
							for (int y1 = 0; y1 < depth; y1++) {
								int ym = i-y1;
								short[] ymCacheL2=heightCacheL[ym];
								minwidth = Math.min(widthCacheL[ym][j], minwidth);
								for (int z1 = 0; z1 < minwidth; z1++) {
									int zm = j-z1;
									minheight = Math.min(ymCacheL2[zm], minheight);
									int vol = (y1+1) * minheight * (z1+1);
									int xm = k-minheight+1;
									if (vol > out1[6]) {
										out1[6] = vol;
										
										out1[0]=xm;
										out1[1]=ym;
										out1[2]=zm;
										
										out1[3]=k;
										out1[4]=i;
										out1[5]=j;
									}
								}
							}
							if (out1[6] >= threshold)
								return;
						}
						else if (tvol == 1 && (k == xsize-1 || heightCache[k+1][i][j] < 2) && (i == ysize-1 || depthCacheL[i+1][j] < 2) && (j == zsize-1 || widthCacheL1[j+1] < 2)) {
							short value = j == zsize-1 ? 0 : (short)(widthCacheL1[j+1]-1);
							widthCacheL1[j]=value;
							widthCacheL1[j-value]=value;
						}
					}
				}
			}
	    	}));
		}
		this.waitForFutures();
		for (int i=0;i<this.threadCount;i++) {
			if (threadcache[i][6] > out[6]) {
				out[6] = threadcache[i][6];
				
				out[0]=threadcache[i][0];
				out[1]=threadcache[i][1];
				out[2]=threadcache[i][2];
				
				out[3]=threadcache[i][3];
				out[4]=threadcache[i][4];
				out[5]=threadcache[i][5];
			}
		}
		return out;
	}

	public int getEntityForBlock(BlockPos pos) {
		if (pos.getX() >= this.xmin+this.xsize || pos.getX() < this.xmin || pos.getZ() >= this.zmin+this.zsize || pos.getX() < this.xmin)
			return 0;
		int xpos = (pos.getX()-this.xmin)/16;
		int zpos = (pos.getZ()-this.zmin)/16;
		if (markParts[xpos][zpos] != null) {
			for (EntityMark mark : markParts[xpos][zpos]) {
				for (BlockRange range : mark.range) {
					if (range.minX <= pos.getX() && range.maxX >= pos.getX() && range.minY <= pos.getY() && range.maxY >= pos.getY() && range.minZ <= pos.getZ() && range.maxZ >= pos.getZ())
						return marks.get(mark);
				}
			}
		}
		return 0;
    }
    
    public void addModel(IBlockState state, Model model) {
    	model.id = models.size();
    	this.models.put(state, model);
    }
    
    public void writeModels() throws IOException {
    	File parent = new File("./models/");
    	parent.mkdirs();
    	new File(Minecraft2Source.gamePathFile, "models/minecraft/").mkdirs();
    	File outputDir = new File(Minecraft2Source.gamePathFile, "custom/minecraft/models/minecraft/");
    	outputDir.mkdirs();
    	for (Entry<IBlockState, Model> entry : this.models.entrySet()) {
    		Model model = entry.getValue();
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
    		Set<SpriteProperties> spriteSet = new HashSet<>();
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
    		writer.write("$modelname \"minecraft/"+model.name+".mdl\"\n");
    		writer.write("$body root \""+model.name+".smd\"\n");
    		writer.write("$staticprop\n");
    		writer.write("$surfaceprop combinemetal\n");
    		writer.write("$cdmaterials \"models/minecraft"+"\"\n");
    		writer.write("$sequence idle \""+model.name+".smd\"\n");
    		writer.write("$texturegroup gr \n");
    		writer.write("{\n");
    		writer.write("{ ");
    		for (SpriteProperties sprite : spriteSet) {
				writer.write(sprite.name);
				writer.write(" ");
			}
    		writer.write("}\n");
    		for (int i = 0; i < model.skinCount; i++) {
    			writer.write("{ ");
    			for (SpriteProperties sprite : spriteSet) {
    				if (i >= sprite.colors.size())
    					writer.write(sprite.name);
    				else
    					writer.write(sprite.name+"-"+encodeInt(sprite.colors.get(i)));
    				writer.write(" ");
    			}
    			writer.write("}\n");
    		}
    		writer.write("}");
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
					for(File file : new File(Minecraft2Source.gamePathFile, "models/minecraft/").listFiles()) {
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
    
    public void writeTriangles(Writer writer, List<ModelTriangle> tris, Set<SpriteProperties> spriteSet) throws IOException {
    	
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
    			writer.write(Float.toString(tri.pos[i].x * Minecraft2Source.blockSize));
    			writer.write(" ");
    			writer.write(Float.toString(-tri.pos[i].z * Minecraft2Source.blockSize));
    			writer.write(" ");
    			writer.write(Float.toString(tri.pos[i].y * Minecraft2Source.blockSize));
    			writer.write("  ");
    			writer.write(Float.toString(tri.normal.x));
    			writer.write(" ");
    			writer.write(Float.toString(tri.normal.z));
    			writer.write(" ");
    			writer.write(Float.toString(tri.normal.y));
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
    	new File(parent,"minecraft").mkdirs();
    	new File(parent,"models/minecraft").mkdirs();
    	for (Entry<TextureAtlasSprite, SpriteProperties> entry : this.sprites.entrySet()) {
    		TextureAtlasSprite sprite = entry.getKey();
    		//BufferedImage image = new BufferedImage(sprite.getIconWidth(),sprite.getIconHeight(),BufferedImage.TYPE_INT_ARGB);
    		//image.setRGB(0, 0, sprite.getIconWidth(), sprite.getIconHeight(), imagedata, 0, sprite.getIconWidth());
    		VTFWriter.write(parent,"minecraft/"+entry.getValue().name,sprite, entry.getValue());
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
    public void calculateOccluders(World world) {
    	
    	int lastVolume=Integer.MAX_VALUE;
		int[] maxpos = new int[7];
		int[][] maxposcache = new int[this.threadCount][7];
		this.initVolumeCache(true);
    	while(true) {
    		getHighestVolume(lastVolume,maxpos,maxposcache);
    		if (maxpos[6] <= Minecraft2Source.minOccluderVolume)
    			break;
    		if ((maxpos[5]-maxpos[2] != 0 && maxpos[4]-maxpos[3] != 0) || (maxpos[4]-maxpos[1] != 0 && maxpos[3]-maxpos[0] != 0) || (maxpos[5]-maxpos[2] != 0 && maxpos[3]-maxpos[0] != 0)) {
	    		lastVolume = maxpos[6];
	    		for (int l = 0; l < this.threadCount; l++) {
	    			int l1 = l;
	    			futures.add(threads.submit(()->
	    			{
			    		for(int x = maxpos[0]+l1; x <= maxpos[3]; x+=threadCount) {
			    			for(int y = maxpos[1]; y <= maxpos[4]; y++) {
			    				for(int z = maxpos[2]; z <= maxpos[5]; z++) {
			    					occluders[x][y][z] = true;
			    				}
			    			}
			    		}
	    			}));
	    		}
	    		this.waitForFutures();
    		}
    		this.updateVolumeCache( maxpos);
    	}
    }
    
    public boolean canJoinCube(Cube first, Cube last, EnumFacing facing) {
    	if (first.def != last.def) {
	    	for (EnumFacing facingw : EnumFacing.VALUES) {
	    		if (facingw != facing && facingw != facing.getOpposite()) {
	    		if (first.def.sprites.get(facingw) != last.def.sprites.get(facingw))
	    			return false;
	    		if ((!last.def.fulluv || !first.def.fulluv) && first.def.sprites.get(facingw) != null) {
		    		Vector2f[] uvfirst = first.def.uv.get(facingw);
		    		Vector2f[] uvlast = last.def.uv.get(facingw);
		    		if (uvfirst[0].x != uvlast[0].x || uvfirst[0].y != uvlast[0].y || uvfirst[1].x != uvlast[1].x || uvfirst[1].y != uvlast[1].y)
		    			return false;
		    		}
	    		}
	    	}
	    	if (first.def.helperDir != last.def.helperDir)
	    		return false;
    	}
    	if ((first.entity > 1 || last.entity > 1) && first.entity != last.entity)
    		return false;
    	
    	switch (facing) {
    	case DOWN: return first.yMax >= last.yMin && first.yMin < last.yMin && first.zMin == last.zMin && first.xMin == last.xMin && first.zMax == last.zMax && first.xMax == last.xMax;

		case EAST: return first.xMin <= last.xMax && first.xMax > last.xMax && first.yMin == last.yMin && first.zMin == last.zMin && first.yMax == last.yMax && first.zMax == last.zMax;

		case NORTH: return first.zMax >= last.zMin && first.zMin < last.zMin && first.yMin == last.yMin && first.xMin == last.xMin && first.yMax == last.yMax && first.xMax == last.xMax;

		case SOUTH: return first.zMin <= last.zMax && first.zMax > last.zMax && first.yMin == last.yMin && first.xMin == last.xMin && first.yMax == last.yMax && first.xMax == last.xMax;

		case UP: 
			if (last.def.helperDir == null)
				return first.yMin <= last.yMax && first.yMax > last.yMax && first.zMin == last.zMin && first.xMin == last.xMin && first.zMax == last.zMax && first.xMax == last.xMax;
			else
				switch (last.def.helperDir) {
				case WEST: return first.yMin <= last.yMax && first.yMax > last.yMax && first.zMin == last.zMin && first.xMin == last.xMax && first.zMax == last.zMax && first.xMax > last.xMax;

				case EAST: return first.yMin <= last.yMax && first.yMax > last.yMax && first.zMin == last.zMin && first.xMax == last.xMin && first.zMax == last.zMax && first.xMin < last.xMin;

				case NORTH: return first.yMin <= last.yMax && first.yMax > last.yMax && first.xMin == last.xMin && first.zMin == last.zMax && first.xMax == last.xMax && first.zMax > last.zMax;

				case SOUTH: return first.yMin <= last.yMax && first.yMax > last.yMax && first.xMin == last.xMin && first.zMax == last.zMin && first.xMax == last.xMax && first.zMin < last.zMin;
				}
		
		break;

		case WEST: return first.xMax >= last.xMin && first.xMin < last.xMin && first.yMin == last.yMin && first.zMin == last.zMin && first.yMax == last.yMax && first.zMax == last.zMax;

		default: break;
    	}
    	
    	return false;
    }
    
    public void joinCubeDef(Cube first, Cube last, EnumFacing facing) {
    	if (first.def != last.def && (first.def.sprites.get(facing) != last.def.sprites.get(facing) || first.def.sprites.get(facing.getOpposite()) != last.def.sprites.get(facing.getOpposite()))){
    		CubeDef newdef = new CubeDef();
    		newdef.autoEntity=last.def.autoEntity;
    		newdef.cullFace=last.def.cullFace.clone();
    		newdef.sprites=last.def.sprites.clone();
    		newdef.tint=last.def.tint.clone();
    		newdef.uv=last.def.uv.clone();
    		newdef.xMax=last.def.xMax;
    		newdef.yMax=last.def.yMax;
    		newdef.zMax=last.def.zMax;
    		newdef.xMin=last.def.xMin;
    		newdef.yMin=last.def.yMin;
    		newdef.zMin=last.def.zMin;
    		newdef.fulluv = last.def.fulluv;
    		if (first.def.sprites.containsKey(facing))
    			newdef.sprites.put(facing, first.def.sprites.get(facing));
    		if (first.def.cullFace.contains(facing))
    			newdef.cullFace.add(facing);
    		else
    			newdef.cullFace.remove(facing);
    		last.def = newdef;
    	}
    }
    public boolean joinCube(Cube first, Cube last, EnumFacing facing) {
    	
    	
    	if (!canJoinCube(first,last,facing))
    		return false;
    	else {
    		switch (facing) {
    		case DOWN:
    			last.yMin=first.yMin;
    			last.extendedY+=1;
    			break;
    		case EAST: 
    			last.xMax=first.xMax;
    			last.extendedX+=1;
    			break;
    		case NORTH: 
    			last.zMin=first.zMin;
    			last.extendedZ+=1;
    			break;
    		case SOUTH: 
    			last.zMax=first.zMax;
    			last.extendedZ+=1;
    			break;
    		case UP:
    			last.yMax=first.yMax;
    			last.extendedY+=1;
    			if (last.def.helperDir != null) {
    				switch (last.def.helperDir) {
    				case WEST: last.xMax = first.xMax;
    				break;
    				case EAST: last.xMin = first.xMin;
    				break;
    				case NORTH: last.zMax = first.zMax;
    				break;
    				case SOUTH: last.zMin = first.zMin;
    				break;
    				}
    			}
    			break;
    		case WEST: 
    			last.xMin=first.xMin;
    			last.extendedX+=1;
    			break;
    		default: break;
        	}
    		last.sidesEnabled.addAll(first.sidesEnabled);
    		this.joinCubeDef(first, last, facing);
    		return true;
    	}
    }
    /*public boolean canExtend(Cube last, EnumFacing facing, int x, int y, int z) {
    	float volume = last.extendedX * last.extendedY * last.extendedZ;
    	for (int zi = z; zi >= 0; zi--) {
    		if ()
    	}
    	return true;
    }*/
    public boolean joinCube(CubeDef first, CubeDef last, EnumFacing facing) {
    	for (EnumFacing facingw : EnumFacing.VALUES) {
    		if (facingw != facing && facingw != facing.getOpposite() && first.sprites.get(facingw) != last.sprites.get(facingw)) {
    			return false;
    		}
    	}
    	
    	if (first.helperDir != last.helperDir)
    		return false;
    	
    	if (first.autoEntity != last.autoEntity)
    		return false;
    	boolean canjoin = false;
    	switch (facing) {
		case DOWN: canjoin = first.yMax >= last.yMin && first.yMin < last.yMin && first.zMin == last.zMin && first.xMin == last.xMin && first.zMax == last.zMax && first.xMax == last.xMax;
			break;
		case EAST: canjoin = first.xMin <= last.xMax && first.xMax > last.xMax && first.yMin == last.yMin && first.zMin == last.zMin && first.yMax == last.yMax && first.zMax == last.zMax;
		break;
		case NORTH: canjoin = first.zMax >= last.zMin && first.zMin < last.zMin && first.yMin == last.yMin && first.xMin == last.xMin && first.yMax == last.yMax && first.xMax == last.xMax;
		break;
		case SOUTH: canjoin = first.zMin <= last.zMax && first.zMax > last.zMax && first.yMin == last.yMin && first.xMin == last.xMin && first.yMax == last.yMax && first.xMax == last.xMax;
		break;
		case UP: 
			if (last.helperDir == null)
				canjoin = first.yMin <= last.yMax && first.yMax > last.yMax && first.zMin == last.zMin && first.xMin == last.xMin && first.zMax == last.zMax && first.xMax == last.xMax;
			else
				switch (last.helperDir) {
				case WEST: canjoin = first.yMin <= last.yMax && first.yMax > last.yMax && first.zMin == last.zMin && first.xMin == last.xMax && first.zMax == last.zMax && first.xMax > last.xMax;
				break;
				case EAST: canjoin = first.yMin <= last.yMax && first.yMax > last.yMax && first.zMin == last.zMin && first.xMax == last.xMin && first.zMax == last.zMax && first.xMin < last.xMin;
				break;
				case NORTH: canjoin = first.yMin <= last.yMax && first.yMax > last.yMax && first.xMin == last.xMin && first.zMin == last.zMax && first.xMax == last.xMax && first.zMax > last.zMax;
				break;
				case SOUTH: canjoin = first.yMin <= last.yMax && first.yMax > last.yMax && first.xMin == last.xMin && first.zMax == last.zMin && first.xMax == last.xMax && first.zMin < last.zMin;
				break;
				}
			break;
		case WEST: canjoin = first.xMax >= last.xMin && first.xMin < last.xMin && first.yMin == last.yMin && first.zMin == last.zMin && first.yMax == last.yMax && first.zMax == last.zMax;
		break;
		default: break;
    	}

    	if (!canjoin)
    		return false;
    	else {
    		switch (facing) {
    		case DOWN:
    			last.yMin=first.yMin;
    			if (last.uv.containsKey(EnumFacing.EAST))
    				last.uv.get(EnumFacing.EAST)[1].y=first.uv.get(EnumFacing.EAST)[1].y;
    			if (last.uv.containsKey(EnumFacing.WEST))
    				last.uv.get(EnumFacing.WEST)[1].y=first.uv.get(EnumFacing.WEST)[1].y;
    			if (last.uv.containsKey(EnumFacing.SOUTH))
    				last.uv.get(EnumFacing.SOUTH)[1].y=first.uv.get(EnumFacing.SOUTH)[1].y;
    			if (last.uv.containsKey(EnumFacing.NORTH))
    				last.uv.get(EnumFacing.NORTH)[1].y=first.uv.get(EnumFacing.NORTH)[1].y;
    			break;
    		case EAST: 
    			last.xMax=first.xMax;
    			if (last.uv.containsKey(EnumFacing.UP))
    			last.uv.get(EnumFacing.UP)[1].x=first.uv.get(EnumFacing.UP)[1].x;
    			if (last.uv.containsKey(EnumFacing.WEST))
    			last.uv.get(EnumFacing.DOWN)[1].x=first.uv.get(EnumFacing.DOWN)[1].x;
    			if (last.uv.containsKey(EnumFacing.SOUTH))
    			last.uv.get(EnumFacing.SOUTH)[1].x=first.uv.get(EnumFacing.SOUTH)[1].x;
    			if (last.uv.containsKey(EnumFacing.NORTH))
    			last.uv.get(EnumFacing.NORTH)[1].x=first.uv.get(EnumFacing.NORTH)[1].x;
    			break;
    		case NORTH: 
    			last.zMin=first.zMin;
    			if (last.uv.containsKey(EnumFacing.UP))
    			last.uv.get(EnumFacing.UP)[0].y=first.uv.get(EnumFacing.UP)[0].y;
    			if (last.uv.containsKey(EnumFacing.DOWN))
    			last.uv.get(EnumFacing.DOWN)[0].y=first.uv.get(EnumFacing.DOWN)[0].y;
    			if (last.uv.containsKey(EnumFacing.EAST))
    			last.uv.get(EnumFacing.EAST)[1].x=first.uv.get(EnumFacing.EAST)[1].x;
    			if (last.uv.containsKey(EnumFacing.WEST))
    			last.uv.get(EnumFacing.WEST)[1].x=first.uv.get(EnumFacing.WEST)[1].x;
    			break;
    		case SOUTH: 
    			last.zMax=first.zMax;
    			if (last.uv.containsKey(EnumFacing.UP))
    			last.uv.get(EnumFacing.UP)[1].y=first.uv.get(EnumFacing.UP)[1].y;
    			if (last.uv.containsKey(EnumFacing.DOWN))
    			last.uv.get(EnumFacing.DOWN)[1].y=first.uv.get(EnumFacing.DOWN)[1].y;
    			if (last.uv.containsKey(EnumFacing.EAST))
    			last.uv.get(EnumFacing.EAST)[0].x=first.uv.get(EnumFacing.EAST)[0].x;
    			if (last.uv.containsKey(EnumFacing.WEST))
    			last.uv.get(EnumFacing.WEST)[0].x=first.uv.get(EnumFacing.WEST)[0].x;
    			break;
    		case UP:
    			last.yMax=first.yMax;
    			if (last.helperDir != null) {
    				switch (last.helperDir) {
    				case WEST: last.xMax = first.xMax;
    				break;
    				case EAST: last.xMin = first.xMin;
    				break;
    				case NORTH: last.zMax = first.zMax;
    				break;
    				case SOUTH: last.zMin = first.zMin;
    				break;
    				}
    			}
    			if (last.uv.containsKey(EnumFacing.EAST))
    			last.uv.get(EnumFacing.EAST)[0].y=first.uv.get(EnumFacing.EAST)[0].y;
    			if (last.uv.containsKey(EnumFacing.WEST))
    			last.uv.get(EnumFacing.WEST)[0].y=first.uv.get(EnumFacing.WEST)[0].y;
    			if (last.uv.containsKey(EnumFacing.SOUTH))
    			last.uv.get(EnumFacing.SOUTH)[0].y=first.uv.get(EnumFacing.SOUTH)[0].y;
    			if (last.uv.containsKey(EnumFacing.NORTH))
    			last.uv.get(EnumFacing.NORTH)[0].y=first.uv.get(EnumFacing.NORTH)[0].y;
    			break;
    		case WEST: 
    			last.xMin=first.xMin;
    			if (last.uv.containsKey(EnumFacing.UP))
    			last.uv.get(EnumFacing.UP)[0].x=first.uv.get(EnumFacing.UP)[0].x;
    			if (last.uv.containsKey(EnumFacing.DOWN))
    			last.uv.get(EnumFacing.DOWN)[0].x=first.uv.get(EnumFacing.DOWN)[0].x;
    			if (last.uv.containsKey(EnumFacing.SOUTH))
    			last.uv.get(EnumFacing.SOUTH)[0].x=first.uv.get(EnumFacing.SOUTH)[0].x;
    			if (last.uv.containsKey(EnumFacing.NORTH))
    			last.uv.get(EnumFacing.NORTH)[0].x=first.uv.get(EnumFacing.NORTH)[0].x;
    			break;
    		default: break;
        	}
			last.sprites.put(facing, first.sprites.get(facing));
			last.uv.put(facing, first.uv.get(facing));
			if (first.cullFace.contains(facing))
				last.cullFace.add(facing);
    		return true;
    	}
    }
    
    public boolean canJoin(Vector3f[] first, Vector3f[] second, EnumFacing facingfirst, EnumFacing facingsecond) {
    	boolean opposite = facingfirst == facingsecond.getOpposite();
    	
    	if (opposite) {
    		float fminx = Math.min(first[0].x,first[2].x);
    		float fminy = Math.min(first[0].y,first[2].y);
    		float fminz = Math.min(first[0].z,first[2].z);
    		float sminx = Math.min(second[0].x,second[2].x);
    		float sminy = Math.min(second[0].y,second[2].y);
    		float sminz = Math.min(second[0].z,second[2].z);
    		float fmaxx = Math.max(first[0].x,first[2].x);
    		float fmaxy = Math.max(first[0].y,first[2].y);
    		float fmaxz = Math.max(first[0].z,first[2].z);
    		float smaxx = Math.max(second[0].x,second[2].x);
    		float smaxy = Math.max(second[0].y,second[2].y);
    		float smaxz = Math.max(second[0].z,second[2].z);
    		if (!Minecraft2Source.allowFlatBrush) {
    			if (facingfirst == EnumFacing.UP && facingsecond == EnumFacing.DOWN && fmaxy <= smaxy)
    				return false;
    			else if (facingfirst == EnumFacing.DOWN && facingsecond == EnumFacing.UP && fminy >= sminy)
    				return false;
    			else if (facingfirst == EnumFacing.EAST && facingsecond == EnumFacing.WEST && fmaxx <= smaxx)
    				return false;
    			else if (facingfirst == EnumFacing.WEST && facingsecond == EnumFacing.EAST && fminx >= sminx)
    				return false;
    			else if (facingfirst == EnumFacing.SOUTH && facingsecond == EnumFacing.NORTH && fmaxz <= smaxz)
    				return false;
    			else if (facingfirst == EnumFacing.NORTH && facingsecond == EnumFacing.SOUTH && fminz >= sminz)
    				return false;
    		}
    			return (fminx == sminx && fminy == sminy && fmaxx == smaxx && fmaxy == smaxy)
	    	    		|| (fminx == sminx && fminz == sminz && fmaxx == smaxx && fmaxz == smaxz)
	    	    		|| (fminz == sminz && fminy == sminy && fmaxz == smaxz && fmaxy == smaxy);
    	}
    	else {
    		int eq=0;
    		for (int i =0; i < 4; i ++) {
    			for (int j =0; j < 4; j ++) {
        			if (second[i].equals(first[j])) {
        				eq+=1;
        				if (eq == 2)
        					return true;
        			}
        		}
    		}
    		return false;
    	}
    }
    
    public void writeCubes(KeyValueWriter keywriter, Set<Cube> cubes) throws IOException {
	    for (Cube cube : cubes) {
	    	keywriter.startGroup("solid");
	    	solidid+=1;
	    	keywriter.keyValue("id", solidid);
	    	if ((cube.entity == 2 || cube.entity == 3) && !cube.isVisible) {
	    		keywriter.endGroup();
	    		continue;
	    	}
		    for (EnumFacing facing: EnumFacing.VALUES) {
		    	if (cube.def.helperDir != null && Minecraft2Source.rampTriangle && facing == EnumFacing.UP)
		    		continue;
		    	keywriter.startGroup("side");
		    	sideid+=1;
		    	keywriter.keyValue("id", sideid);
		    	Vector3f[] vectors = new Vector3f[3];
		    	boolean rampdir = facing == cube.def.helperDir && Minecraft2Source.rampTriangle;
		    	
		    	switch(facing) {
				case DOWN:vectors[0] = new Vector3f(cube.zMin, cube.xMin, cube.yMin);
			    	vectors[1] = new Vector3f(cube.zMax, cube.xMin, cube.yMin);
			    	vectors[2] = new Vector3f(cube.zMax, cube.xMax, cube.yMin); break;
				case SOUTH:vectors[0] = new Vector3f(cube.zMax, cube.xMax, cube.yMin);
			    	vectors[1] = new Vector3f(cube.zMax, cube.xMin, cube.yMin);
			    	vectors[2] = new Vector3f(rampdir ? cube.zMin : cube.zMax, cube.xMin, cube.yMax); break;
				case WEST:vectors[0] = new Vector3f(cube.zMax, cube.xMin, cube.yMin);
			    	vectors[1] = new Vector3f(cube.zMin, cube.xMin, cube.yMin);
			    	vectors[2] = new Vector3f(cube.zMin, rampdir ? cube.xMax : cube.xMin, cube.yMax); break;
				case EAST:vectors[0] = new Vector3f(cube.zMax, rampdir ? cube.xMin : cube.xMax, cube.yMax);
			    	vectors[1] = new Vector3f(cube.zMin, rampdir ? cube.xMin : cube.xMax, cube.yMax);
			    	vectors[2] = new Vector3f(cube.zMin, cube.xMax, cube.yMin); break;
				case UP:vectors[0] = new Vector3f(cube.zMin, cube.xMax, cube.yMax);
			    	vectors[1] = new Vector3f(cube.zMax, cube.xMax, cube.yMax);
			    	vectors[2] = new Vector3f(cube.zMax, cube.xMin, cube.yMax); break;
				case NORTH:vectors[0] = new Vector3f(rampdir ? cube.zMax : cube.zMin, cube.xMax, cube.yMax);
			    	vectors[1] = new Vector3f(rampdir ? cube.zMax : cube.zMin, cube.xMin, cube.yMax);
			    	vectors[2] = new Vector3f(cube.zMin, cube.xMin, cube.yMin); break;
				default:
					break;
		    	}
		    	
		    	vectors[0].scale(Minecraft2Source.blockSize);
		    	vectors[1].scale(Minecraft2Source.blockSize);
		    	vectors[2].scale(Minecraft2Source.blockSize);
		    	StringBuilder builder = new StringBuilder();
		    	builder.append('(');
		    	builder.append(vectors[0].x);builder.append(' ');builder.append(vectors[0].y);builder.append(' ');builder.append(vectors[0].z);builder.append(") (");
		    	builder.append(vectors[1].x);builder.append(' ');builder.append(vectors[1].y);builder.append(' ');builder.append(vectors[1].z);builder.append(") (");
		    	builder.append(vectors[2].x);builder.append(' ');builder.append(vectors[2].y);builder.append(' ');builder.append(vectors[2].z);builder.append(')');
		    	keywriter.keyValue("plane", builder.toString());
		    	Vector3f uScale = new Vector3f();
		    	Vector3f vScale = new Vector3f();
		    	float scalex = 0.25f;
		    	float scaley = 0.25f;
		    	float uScalef = 1f;
		    	float vScalef = 1f;
		    	float dispx=0;
		    	float dispy=0;
		    	if (cube.def.sprites.containsKey(facing) && cube.def.sprites.get(facing) instanceof SpriteTool) {
		    		TextureAtlasSprite sprite = cube.def.sprites.get(facing);
		    		keywriter.keyValue("material", sprite.getIconName());
		    	}
		    	else if (cube.def.sprites.get(facing) != null && cube.sidesEnabled.contains(facing)) {
		    		TextureAtlasSprite sprite = cube.def.sprites.get(facing);
		    		SpriteProperties properties = sprites.get(sprite);
		    		if (cube.color[facing.ordinal()] == -1)
		    			keywriter.keyValue("material", "MINECRAFT/"+properties.name);
		    		else
		    			keywriter.keyValue("material", "MINECRAFT/"+properties.name+"-"+encodeInt(cube.color[facing.ordinal()]));
		    		scalex = Minecraft2Source.blockSize/sprite.getIconWidth();
		    		scaley = Minecraft2Source.blockSize/sprite.getIconHeight();
		    		Vector2f[] uv = cube.def.uv.get(facing);
		    		uScalef = uv[1].x;
		    		vScalef = uv[1].y;
		    		dispx=uv[0].x;
		    		dispy=uv[0].y;
		    	}
		    	else {
		    		keywriter.keyValue("material", "TOOLS/TOOLSNODRAW");
		    	}
		    	
		    	switch(facing) {
				case DOWN: uScale.set(0, uScalef, 0); vScale.set(-vScalef, 0, 0); break;
				case UP: uScale.set(0, uScalef, 0); vScale.set(vScalef, 0, 0); break;
				case SOUTH: uScale.set(0, uScalef, 0); vScale.set(0, 0, -vScalef); break;
				case NORTH: uScale.set(0, -uScalef, 0); vScale.set(0, 0, -vScalef); break;
				case WEST: uScale.set(uScalef, 0, 0); vScale.set(0, 0, -vScalef); break;
				case EAST: uScale.set(-uScalef, 0, 0); vScale.set(0, 0, -vScalef); break;
				default:
					break;
		    	}
		    	keywriter.keyValue("uaxis", "["+uScale.x+" "+uScale.y+" "+uScale.z+" "+dispx+"] "+scalex);
		    	keywriter.keyValue("vaxis", "["+vScale.x+" "+vScale.y+" "+vScale.z+" "+dispy+"] "+scaley);
		    	keywriter.keyValue("rotation", 0);
		    	keywriter.keyValue("lightmapscale", Minecraft2Source.lightmapScale);
		    	keywriter.keyValue("smoothing_groups", 0);
		    	keywriter.endGroup();
	    	}
		    keywriter.startGroup("editor");
		    keywriter.keyValue("color", "0 156 181");
		    keywriter.keyValue("visgroupshown", 1);
		    keywriter.keyValue("visgroupautoshown", 1);
		    keywriter.endGroup();
		    keywriter.endGroup();
    	}
    }
    
    public static String encodeInt(int value) {
    	return Integer.toString(value,Character.MAX_RADIX);
    }
    public List<CubeDef> readCubes(Map<EnumFacing, List<BakedQuad>> quads, IBlockState state, IBakedModel model, long rand, List<BakedQuad> noFace, List<AxisAlignedBB> cbox) {
    	boolean fullcube = state.isFullCube() && state.isOpaqueCube();
    	List<CubeDef> cubes = new ArrayList<>();
    	int nonjoinable = 0;
    	while (true) {
    		boolean cubeadded = false;
    		CubeDef cube = null;
    		for (Entry<EnumFacing, List<BakedQuad>> entry: quads.entrySet()) {
    			EnumFacing facing1 = entry.getKey();
    			for (int i = 0; i < entry.getValue().size(); i++) {
    				BakedQuad quad1 = entry.getValue().get(i);
	    			for (Entry<EnumFacing, List<BakedQuad>> entry2: quads.entrySet()) {
	        			EnumFacing facing2 = entry2.getKey();
		        		if (facing2 != facing1) {
		        			for (int j = 0; j < entry2.getValue().size(); j++) {
		        				BakedQuad quad2 = entry2.getValue().get(j);
		        				Vector3f[] first = vecCache.get(quad1);
		        				Vector3f[] second = vecCache.get(quad2);
		        				if (canJoin(first, second, facing1, facing2)) {
		        					boolean created = false;
		        					if (cube == null) {
		        						cube = new CubeDef();
		        						created = true;
		        					}
		        					
		        		    		float fminx = Math.min(Math.min(first[0].x,first[2].x),Math.min(second[0].x,second[2].x));
		        		    		float fminy = Math.min(Math.min(first[0].y,first[2].y),Math.min(second[0].y,second[2].y));
		        		    		float fminz = Math.min(Math.min(first[0].z,first[2].z),Math.min(second[0].z,second[2].z));
		        		    		float fmaxx = Math.max(Math.max(first[0].x,first[2].x),Math.max(second[0].x,second[2].x));
		        		    		float fmaxy = Math.max(Math.max(first[0].y,first[2].y),Math.max(second[0].y,second[2].y));
		        		    		float fmaxz = Math.max(Math.max(first[0].z,first[2].z),Math.max(second[0].z,second[2].z));
		        		    		
		        		    		if (fmaxx - fminx == 0)
		        		    			fmaxx +=0.00390625f;
		        		    		if (fmaxy - fminy == 0)
		        		    			fmaxy +=0.00390625f;
		        		    		if (fmaxz - fminz == 0)
		        		    			fmaxz +=0.00390625f;
		        		    		
		        		    		cube.xMin= Math.min(fminx, cube.xMin);
		        		    		cube.yMin= Math.min(fminy, cube.yMin);
		        		    		cube.zMin= Math.min(fminz, cube.zMin);
		        		    		cube.xMax= Math.max(fmaxx, cube.xMax);
		        		    		cube.yMax= Math.max(fmaxy, cube.yMax);
		        		    		cube.zMax= Math.max(fmaxz, cube.zMax);
		        		    		
		        		    		cube.addFace(facing2, quad2, !noFace.contains(quad2), state, this);
		        		    		
		        		    		if (created) {
		        						cube.addFace(facing1, quad1, !noFace.contains(quad1), state, this);
		        					}
		        		    		cubeadded=true;
		        		    		entry2.getValue().remove(j);
		        		    		j--;
		        		    		break;
		        				}
		        			}
	        			}
	        		}
	    			if (cubeadded) {
	    				entry.getValue().remove(i);
	    				break;
	    			}
	    			else {
	    				return null;
	    			}
    			}
    			if (cubeadded) {
    				cubes.add(cube);
    				break;
    			}
    		}
    		if (!cubeadded)
    			break;
    	}
    	ArrayList<CubeDef> ramps = new ArrayList<>();
    	label:
    	for (int i = 0; i < cubes.size(); i++) {
			CubeDef first = cubes.get(i);
			if (first.yMax - first.yMin >= 18f/Minecraft2Source.blockSize && first.yMax - first.yMin <= Minecraft2Source.maxHeightRamp && !cbox.isEmpty() && first.autoEntity != 3) {
				boolean ground = first.yMin == 0;
				for (EnumFacing facing : EnumFacing.HORIZONTALS) {
					boolean below = false;
					for (int j = 0; j < cubes.size(); j++) {
						CubeDef last = cubes.get(j);
						if (j > i && first.xMin == last.xMin && first.xMax == last.xMax && first.yMin == last.yMin && first.yMax == last.yMax && first.zMin == last.zMin && first.zMax == last.zMax) {
			    			continue label;
			    		}
					}
					if (!ground) {
						for (int j = 0; j < cubes.size(); j++) {
							if (j != i) {
								CubeDef last = cubes.get(j);
								if (first.yMin == last.yMax) {
									switch(facing) {
									case WEST: below = first.xMin > last.xMin; break;
									case EAST: below = first.xMax < last.xMax; break;
									case NORTH: below = first.zMin > last.zMin; break;
									case SOUTH: below = first.zMax < last.zMax; break;
									default:
									}
									if (below)
										break;
								}
							}
						}
					}
					if (ground || below) {
						CubeDef ramp = new CubeDef();
						for (EnumFacing facing2: EnumFacing.VALUES) {
		    				ramp.sprites.put(facing2, PLAYER_CLIP);
		    			}
						ramp.helperDir=facing;
						
						
						if (Minecraft2Source.rampTriangle) {
							ramp.yMin=first.yMin;
							ramp.yMax=first.yMax;
							
							switch (facing) {
							case WEST: ramp.zMin=first.zMin; ramp.zMax=first.zMax; ramp.xMax = first.xMin; ramp.xMin = first.xMin - (ramp.yMax - ramp.yMin); break;
							case EAST: ramp.zMin=first.zMin; ramp.zMax=first.zMax; ramp.xMin = first.xMax; ramp.xMax = first.xMax + (ramp.yMax - ramp.yMin); break;
							case NORTH: ramp.xMin=first.xMin; ramp.xMax=first.xMax; ramp.zMax = first.zMin; ramp.zMin = first.zMin - (ramp.yMax - ramp.yMin); break;
							case SOUTH: ramp.xMin=first.xMin; ramp.xMax=first.xMax; ramp.zMin = first.zMax; ramp.zMax = first.zMax + (ramp.yMax - ramp.yMin); break;
							default:
							}
						}
						else {
							for (int j =0; j < (first.yMax-first.yMin) * Minecraft2Source.blockSize / 18f; j++) {
								ramp.yMin=first.yMin;
								ramp.yMax=first.yMin+(18f/Minecraft2Source.blockSize)*(j+1);
								
								switch (facing) {
								case WEST: ramp.zMin=first.zMin; ramp.zMax=first.zMax; ramp.xMax = first.xMin; ramp.xMin = first.xMin - 0.03125f * j; break;
								case EAST: ramp.zMin=first.zMin; ramp.zMax=first.zMax; ramp.xMin = first.xMax; ramp.xMax = first.xMax + 0.03125f * j; break;
								case NORTH: ramp.xMin=first.xMin; ramp.xMax=first.xMax; ramp.zMax = first.zMin; ramp.zMin = first.zMin - 0.03125f * j; break;
								case SOUTH: ramp.xMin=first.xMin; ramp.xMax=first.xMax; ramp.zMin = first.zMax; ramp.zMax = first.zMax + 0.03125f * j; break;
								default:
								}
							}
						}
						
						ramps.add(ramp);
					}
				}
			}
    	}
    	cubes.addAll(ramps);
    	
    	for (AxisAlignedBB box : cbox) {
    		boolean contained = false;
    		for (CubeDef cube : cubes) {
	    		if (box.minX >= cube.xMin && box.maxX <= cube.xMax && box.minY >= cube.yMin && box.maxY <= cube.yMax && box.minZ >= cube.zMin && box.maxZ <= cube.zMax) {
	    			contained = true;
	    			break;
	    		}
	    		
    		}
    		if (!contained) {
    			CubeDef cube = new CubeDef();
    			for (EnumFacing facing: EnumFacing.VALUES) {
    				cube.sprites.put(facing, PLAYER_CLIP);
    			}
    			cube.xMin=(float) box.minX;
    			cube.yMin=(float) box.minY;
    			cube.zMin=(float) box.minZ;
    			cube.xMax=(float) box.maxX;
    			cube.yMax=(float) box.maxY;
    			cube.zMax=(float) box.maxZ;
    			cubes.add(cube);
    		}
    	}
    	
    	label2:
    	for (int i = 0; i < cubes.size(); i++) {
			CubeDef first = cubes.get(i);
			
			if (first.autoEntity == 0 && !(first.sprites.get(EnumFacing.NORTH) instanceof SpriteTool))
				first.autoEntity = cbox.isEmpty() ? 2 : (fullcube ? 0 : 1);
			if ((first.xMin != 0 && first.xMax != 1 && first.yMin != 0 && first.yMax != 1 && first.zMin != 0 && first.zMax != 1)) {
				nonjoinable++;
			}
			for (int j = 0; j < cubes.size(); j++) {
				if (j != i) {
					CubeDef last = cubes.get(j);
					if (j > i && first.xMin == last.xMin && first.xMax == last.xMax && first.yMin == last.yMin && first.yMax == last.yMax && first.zMin == last.zMin && first.zMax == last.zMax) {
		    			last.autoEntity =3;
		    		}
			    	for (EnumFacing facingw : EnumFacing.VALUES) {
			    		if (this.joinCube(first, last, facingw)) {
			    			cubes.remove(i);
			    			i--;
			    			continue label2;
			    		}
			    	}
				}
    		}
			if (first.xMin == 0 && first.xMax == 1 && first.yMin == 0 && first.yMax == 1 && first.zMin == 0 && first.zMax == 1) {
				first.isFullCube = true;
			}
    	}
    	if (nonjoinable > 1)
    		return null;
    	for (int i = 0; i < cubes.size(); i++) {
    		CubeDef first = cubes.get(i);
    		for (int j = i+1; j < cubes.size(); j++) {
    			CubeDef last = cubes.get(j);
    			if (first.autoEntity == 2 && last.autoEntity == 2 && first.intersects(last))
    				return null;
    			for (EnumFacing facingw : EnumFacing.VALUES) {
    				TextureAtlasSprite front = last.sprites.get(facingw);
    				TextureAtlasSprite back = first.sprites.get(facingw);
	    			if (front != null && back != null && !(front instanceof SpriteTool) && !(back instanceof SpriteTool) && last.isSamePlaneSize(first, facingw)) {
	    				TextureAtlasSprite joined =joinTexture(front,back);
	    				this.addSprite(joined, false, state.getMaterial());
	    				first.sprites.put(facingw, joined);
	    				last.sprites.remove(facingw);
	    				first.tint[facingw.ordinal()]=last.tint[facingw.ordinal()];
	    				last.tint[facingw.ordinal()]=-1;
	    				if (last.sprites.size() == 0) {
	    					cubes.remove(j);
	    					j--;
	    				}
	    				
	    			}
		    	}
    		}
    	}
    	return cubes;
    }
    
    public Model readModel(IBlockState state, List<AxisAlignedBB> cbox, Vector3f offset){
    	Model model = new Model();
    	for (BakedQuad quad : vecCache.keySet()) {
    		Vector3f[] vec = vecCache.get(quad);
    		
    		Vector3f.add(vec[0], offset, vec[0]);
    		Vector3f.add(vec[1], offset, vec[1]);
    		Vector3f.add(vec[2], offset, vec[2]);
    		Vector3f.add(vec[3], offset, vec[3]);
    		
    		Vector2f[] uv = uvCache.get(quad);
    		Vector3f sub1 = new Vector3f();
    		Vector3f sub2 = new Vector3f();
    		Vector3f normal = new Vector3f();
    		Vector3f.sub(vec[0], vec[1], sub1);
    		Vector3f.sub(vec[1], vec[2], sub2);
    		Vector3f.cross(sub1, sub2, normal);
    		TextureAtlasSprite sprite = quad.getSprite();
    		ModelTriangle tri1 = new ModelTriangle();
    		tri1.normal = normal;
    		tri1.pos = new Vector3f[] {vec[1],vec[2],vec[0]};
    		tri1.uv = new Vector2f[] {uv[1],uv[2],uv[0]};
    		tri1.sprite = sprite;
    		tri1.hasTint = quad.hasTintIndex();
    		
    		ModelTriangle tri2 = new ModelTriangle();
    		tri2.normal = normal;
    		tri2.pos = new Vector3f[] {vec[2],vec[3],vec[0]};
    		tri2.uv = new Vector2f[] {uv[2],uv[3],uv[0]};
    		tri2.sprite = quad.getSprite();
    		tri2.hasTint = quad.hasTintIndex();
    		model.quads.add(tri1);
    		model.quads.add(tri2);
    		if (quad.hasTintIndex())
    			model.hasTint = true;
    		this.addSprite(quad.getSprite(), true,state.getMaterial());
    	}
    	for (AxisAlignedBB box : cbox) {
    		
    		Vector3f mmm = new Vector3f((float)box.minX, (float)box.minY, (float)box.minZ);
    		Vector3f xmm = new Vector3f((float)box.maxX, (float)box.minY, (float)box.minZ);
    		Vector3f mxm = new Vector3f((float)box.minX, (float)box.maxY, (float)box.minZ);
    		Vector3f mmx = new Vector3f((float)box.minX, (float)box.minY, (float)box.maxZ);
    		Vector3f xxm = new Vector3f((float)box.maxX, (float)box.maxY, (float)box.minZ);
    		Vector3f mxx = new Vector3f((float)box.minX, (float)box.maxY, (float)box.maxZ);
    		Vector3f xmx = new Vector3f((float)box.maxX, (float)box.minY, (float)box.maxZ);
    		Vector3f xxx = new Vector3f((float)box.maxX, (float)box.maxY, (float)box.maxZ);
    		
    		ModelTriangle tri1 = new ModelTriangle();
    		tri1.normal = new Vector3f(0,1,0);
    		tri1.pos = new Vector3f[] {xxm,xxx,mxm};
    		model.collisionBox.add(tri1);
    		
    		tri1 = new ModelTriangle();
    		tri1.normal = new Vector3f(0,1,0);
    		tri1.pos = new Vector3f[] {xxx,mxx,mxm};
    		model.collisionBox.add(tri1);
    		
    		tri1 = new ModelTriangle();
    		tri1.normal = new Vector3f(0,-1,0);
    		tri1.pos = new Vector3f[] {mmm,xmx,xmm};
    		model.collisionBox.add(tri1);
    		
    		tri1 = new ModelTriangle();
    		tri1.normal = new Vector3f(0,-1,0);
    		tri1.pos = new Vector3f[] {mmm,mmx,xmx};
    		model.collisionBox.add(tri1);
    		
    		tri1 = new ModelTriangle();
    		tri1.normal = new Vector3f(1,0,0);
    		tri1.pos = new Vector3f[] {xxm,xxx,xmm};
    		model.collisionBox.add(tri1);
    		
    		tri1 = new ModelTriangle();
    		tri1.normal = new Vector3f(1,0,0);
    		tri1.pos = new Vector3f[] {xxx,xmx,xmm};
    		model.collisionBox.add(tri1);
    		
    		tri1 = new ModelTriangle();
    		tri1.normal = new Vector3f(-1,0,0);
    		tri1.pos = new Vector3f[] {mmm,mxx,mxm};
    		model.collisionBox.add(tri1);
    		
    		tri1 = new ModelTriangle();
    		tri1.normal = new Vector3f(-1,0,0);
    		tri1.pos = new Vector3f[] {mmm,mmx,mxx};
    		model.collisionBox.add(tri1);
    		
    		tri1 = new ModelTriangle();
    		tri1.normal = new Vector3f(0,0,1);
    		tri1.pos = new Vector3f[] {xmx,xxx,mmx};
    		model.collisionBox.add(tri1);
    		
    		tri1 = new ModelTriangle();
    		tri1.normal = new Vector3f(0,0,1);
    		tri1.pos = new Vector3f[] {xxx,mxx,mmx};
    		model.collisionBox.add(tri1);
    		
    		tri1 = new ModelTriangle();
    		tri1.normal = new Vector3f(0,0,-1);
    		tri1.pos = new Vector3f[] {mmm,xxm,xmm};
    		model.collisionBox.add(tri1);
    		
    		tri1 = new ModelTriangle();
    		tri1.normal = new Vector3f(0,0,-1);
    		tri1.pos = new Vector3f[] {mmm,mxm,xxm};
    		model.collisionBox.add(tri1);
    	}
    	
    	Vector2f[] uvdef = new Vector2f[] {new Vector2f(1,0),new Vector2f(1,1),new Vector2f(0,0)};
    	for (ModelTriangle tri: model.collisionBox) {
    		tri.uv = uvdef;
    	}
    	
    	StringBuilder modelname = new StringBuilder();
    	if (this.niceName)
    		modelname.append(state.getBlock().getRegistryName().getResourcePath());
    	else
    		modelname.append(encodeInt(Block.getIdFromBlock(state.getBlock())));
    	for (IProperty<?> prop : state.getPropertyKeys()) {
    		modelname.append("_");
    		int i = 0;
    		for (Object value : prop.getAllowedValues()) {
    			if (state.getValue(prop).equals(value)) {
    				modelname.append(Integer.toString(i, Character.MAX_RADIX));
    				break;
    			}
    			i++;
    		}
    	}
    	model.name=modelname.toString();
    	return model;
    }
    private TextureAtlasSprite joinTexture(TextureAtlasSprite front, TextureAtlasSprite back) {
		return new SpriteCombined(front,back);
	}

	/**
     * Clone cube list
	 * @param z 
	 * @param y 
	 * @param x 
	 * @param empty 
     */
    
    public List<Cube> cloneCubeList(List<CubeDef> cubes, byte[][][] empty, int x, int y, int z){
    	List<Cube> list = new ArrayList<>(cubes.size());
    	for (CubeDef cubeold : cubes) {
    		if (cubeold.helperDir != null) {
    			int xfront = x+cubeold.helperDir.getFrontOffsetX();
    			int zfront = z+cubeold.helperDir.getFrontOffsetZ();
    			int ylevel = (int)(cubeold.yMax*16f);
    			
    			if (isOutOfBounds(x, y+1, z) || isOutOfBounds(xfront, y, zfront) || isOutOfBounds(xfront, y-1, zfront) || empty[x][y+1][z] != 0)
    				continue;
    			if (empty[xfront][y][zfront] != 0 && empty[xfront][y][zfront] >= ylevel - 1)
    				continue;
    			if (empty[xfront][y][zfront] == 0 && empty[xfront][y-1][zfront] < 14)
    				continue;
    		}
    		Cube cube = new Cube();
    		cube.xMin = cubeold.xMin;
    		cube.yMin = cubeold.yMin;
    		cube.zMin = cubeold.zMin;
    		cube.xMax = cubeold.xMax;
    		cube.yMax = cubeold.yMax;
    		cube.zMax = cubeold.zMax;
    		cube.def = cubeold;
    		cube.color = new int[] {-1,-1,-1,-1,-1,-1};
    		
    		list.add(cube);
    	}
    	return list;
    }
    
    public boolean isOutOfBounds(int x, int y, int z) {
    	return x >= xsize || y >= ysize || z >= zsize || x < 0 || y < 0 || z < 0;
    }
    public void addSprite(TextureAtlasSprite sprite, boolean model, Material material) {
		if (!sprites.containsKey(sprite)) {
			SpriteProperties properties = new SpriteProperties(sprites.size(),sprite, this.niceName);
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
    
    public void waitForFutures() {
    	for (Future<?> future: futures) {
    		try {
				future.get();
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	futures.clear();
    }
    public static class CubeBase {
    	float xMin=2;
    	float yMin=2;
    	float zMin=2;
    	float xMax=-1;
    	float yMax=-1;
    	float zMax=-1;
    	
    	public Vector2f[] getPlaneBounds(EnumFacing facing) {
        	switch (facing) {
    		case DOWN: case UP: return new Vector2f[] {new Vector2f(this.xMin, this.zMin),new Vector2f(this.xMax, this.zMax)};

    		case EAST: case WEST: return new Vector2f[] {new Vector2f(this.zMin, this.yMin),new Vector2f(this.zMax, this.yMax)};

    		case NORTH: case SOUTH: return new Vector2f[] {new Vector2f(this.xMin, this.yMin),new Vector2f(this.xMax, this.yMax)};
        	}
        	
        	return null;
        }
    	
    	public boolean isSamePlaneSizeReverse(CubeBase first,EnumFacing facing) {
        	switch (facing) {
    		case DOWN: return first.yMax == this.yMin && first.zMin == this.zMin && first.xMin == this.xMin && first.zMax == this.zMax && first.xMax == this.xMax;

    		case EAST: return first.xMin == this.xMax && first.yMin == this.yMin && first.zMin == this.zMin && first.yMax == this.yMax && first.zMax == this.zMax;

    		case NORTH: return  first.zMax == this.zMin && first.yMin == this.yMin && first.xMin == this.xMin && first.yMax == this.yMax && first.xMax == this.xMax;

    		case SOUTH: return first.zMin == this.zMax && first.yMin == this.yMin && first.xMin == this.xMin && first.yMax == this.yMax && first.xMax == this.xMax;

    		case UP: return  first.yMin == this.yMax && first.zMin == this.zMin && first.xMin == this.xMin && first.zMax == this.zMax && first.xMax == this.xMax;
    		
    		case WEST:  return first.xMax == this.xMin && first.yMin == this.yMin && first.zMin == this.zMin && first.yMax == this.yMax && first.zMax == this.zMax;
        	}
        	
        	return false;
        }
        
        public boolean isSamePlaneSize(CubeBase first,EnumFacing facing) {
        	switch (facing) {
    		case DOWN: return first.yMin == this.yMin && first.zMin == this.zMin && first.xMin == this.xMin && first.zMax == this.zMax && first.xMax == this.xMax;

    		case EAST: return first.xMax == this.xMax && first.yMin == this.yMin && first.zMin == this.zMin && first.yMax == this.yMax && first.zMax == this.zMax;

    		case NORTH: return  first.zMin == this.zMin && first.yMin == this.yMin && first.xMin == this.xMin && first.yMax == this.yMax && first.xMax == this.xMax;

    		case SOUTH: return first.zMax == this.zMax && first.yMin == this.yMin && first.xMin == this.xMin && first.yMax == this.yMax && first.xMax == this.xMax;

    		case UP: return  first.yMax == this.yMax && first.zMin == this.zMin && first.xMin == this.xMin && first.zMax == this.zMax && first.xMax == this.xMax;
    		
    		case WEST:  return first.xMin == this.xMin && first.yMin == this.yMin && first.zMin == this.zMin && first.yMax == this.yMax && first.zMax == this.zMax;
        	}
        	
        	return first.xMin == this.xMin && first.xMax == this.xMax && first.yMin == this.yMin && first.zMin == this.zMin && first.yMax == this.yMax && first.zMax == this.zMax;
        }
        
        
        public boolean intersects(CubeBase range)
        {
            return this.xMin <= range.xMax && this.xMax >= range.xMin && this.yMin <= range.yMax && this.yMax >= range.yMin && this.zMin <= range.zMax && this.zMax >= range.zMin;
        }
    	
    	public boolean contains(CubeBase range)
        {
            return this.xMin <= range.xMin && this.xMax >= range.xMax && this.yMin <= range.yMin && this.yMax >= range.yMax && this.zMin <= range.zMin && this.zMax >= range.zMax;
        }
    }
    
    public static class ModelInstance {
    	public Model model;
    	public int skinid;
    	public Vec3d pos;
    }
    
    public static class Model {
    	public int id;
    	public String name;
    	public boolean hasTint;
    	public int skinCount;
    	public List<ModelTriangle> collisionBox = new ArrayList<>();
    	public List<ModelTriangle> quads = new ArrayList<>();
    }
    
    public static class ModelTriangle {
    	public Vector3f normal;
		public TextureAtlasSprite sprite;
		public boolean hasTint;
    	public Vector2f[] uv;
    	public Vector3f[] pos;
    }
    
    public static class Cube extends CubeBase{
    	CubeDef def;
    	int entity = 0;
    	int extendedX=1;
    	int extendedY=1;
    	int extendedZ=1;
    	int[] color;
    	EnumSet<EnumFacing> sidesEnabled = EnumSet.noneOf(EnumFacing.class);
    	boolean isVisible;
    	
        public boolean isJoinable(Cube first, EnumFacing facing, CubeDef firstvis) {
        	
        	if (first.entity != this.entity)
        		return false;
        	
        	
        	if (first.def != this.def) {
        		
        		if (!(!first.isVisible && !this.isVisible)) {
	    	    	for (EnumFacing facingw : EnumFacing.VALUES) {
	    	    		if (first.def.sprites.get(facingw) != this.def.sprites.get(facingw)) {
	    	    			return false;
	    	    		}
	    	    	}
        		}
        	}
        	
        	if (this.def.isFullCube && first.def.isFullCube)
        		return true;
        	
        	switch (facing) {
    		case DOWN: return this.yMin==0 && first.yMax==1 && first.zMin == this.zMin && first.xMin == this.xMin && first.zMax == this.zMax && first.xMax == this.xMax;

    		case EAST: return this.xMax==1 && first.xMin==0 && first.yMin == this.yMin && first.zMin == this.zMin && first.yMax == this.yMax && first.zMax == this.zMax;

    		case NORTH: return  this.zMin==0 && first.zMax==1 && first.yMin == this.yMin && first.xMin == this.xMin && first.yMax == this.yMax && first.xMax == this.xMax;

    		case SOUTH: return this.zMax==1 && first.zMin==0 && first.yMin == this.yMin && first.xMin == this.xMin && first.yMax == this.yMax && first.xMax == this.xMax;

    		case UP: return this.yMax==1 && first.yMin==0 && first.zMin == this.zMin && first.xMin == this.xMin && first.zMax == this.zMax && first.xMax == this.xMax;
    		
    		case WEST:  return this.xMin==0 && first.xMax==1 && first.yMin == this.yMin && first.zMin == this.zMin && first.yMax == this.yMax && first.zMax == this.zMax;
        	}
        	return false;
        }
    }

    public static class CubeDef extends CubeBase{
    	int autoEntity=0;
    	boolean isFullCube=false;
    	EnumMap<EnumFacing, TextureAtlasSprite> sprites=new EnumMap<>(EnumFacing.class);
    	EnumSet<EnumFacing> cullFace = EnumSet.noneOf(EnumFacing.class);
    	int[] tint = new int[] {-1,-1,-1,-1,-1,-1};
    	EnumMap<EnumFacing, Vector2f[]> uv=new EnumMap<>(EnumFacing.class);
    	boolean fulluv = true;
    	boolean isWater = false;
    	EnumFacing helperDir;
    	
    	public void addFace(EnumFacing facing, BakedQuad quad, boolean cullface, IBlockState state, MapBuilder builder) {
    		this.sprites.put(facing, quad.getSprite());
			this.tint[facing.ordinal()] = quad.getTintIndex();
    		if (cullface)
    			this.cullFace.add(facing);
    		Vector2f[] uv = builder.uvCache.get(quad);
    		Vector2f[] uvnew = new Vector2f[2];
    		builder.addSprite(quad.getSprite(), false, state.getMaterial());
    		
    		SpriteProperties prop = builder.sprites.get(quad.getSprite());
    		float minx = 0;
    		float miny = 0;
    		float sizex = 0;
    		float sizey = 0;
    		switch (facing) {
    		case SOUTH: case NORTH: minx = this.xMin; sizex = this.xMax - this.xMin; miny = 1-this.yMax; sizey = this.yMax - yMin; break;
    		case EAST: case WEST: minx = this.zMin; sizex = this.zMax - this.zMin; miny = 1-this.yMax; sizey = this.yMax - yMin; break;
    		case UP: case DOWN: minx = this.xMin; sizex = this.xMax - this.xMin; miny = this.zMin; sizey = this.zMax - zMin; break;
    		}
    		float scalex = (uv[2].x-uv[0].x) / sizex;
    		float scaley = (uv[2].y-uv[0].y) / sizey;
    		Vector2f uvvert = new Vector2f(uv[0]);
    		uvvert.x -=minx * scalex;
			uvvert.y -=miny * scaley;
			uvvert.x /=sizex;
			uvvert.y /=sizey;
    		uvnew[0] = uvvert;
    		uvnew[1] = new Vector2f(scalex, scaley);
    		if (uvnew[0].x != 0 || uvnew[0].y != 0 || uvnew[1].x != 1 || uvnew[1].y != 1)
    			this.fulluv = false;
    		this.uv.put(facing, uvnew);
    	}
    }
    
    public static class SpriteTool extends TextureAtlasSprite {

		public SpriteTool(String spriteName) {
			super(spriteName);
		}
    	
    }
    
    public static class SpriteCombined extends TextureAtlasSprite {

    	public int color;
    	public TextureAtlasSprite front;
    	public TextureAtlasSprite back;
		public SpriteCombined(TextureAtlasSprite front, TextureAtlasSprite back) {
			super(front.getIconName()+"-"+back.getIconName());
			this.front = front;
			this.back = back;
		}
    	
		public boolean equals(Object object) {
			if (this == object)
				return true;
			else if (object instanceof SpriteCombined) {
				return this.front == ((SpriteCombined)object).front && this.back == ((SpriteCombined)object).back;
			}
			return false;
		}
		
		public int hashCode() {
			return (front.hashCode() & 0x55555555) | (back.hashCode() & 0xAAAAAAAA);
		}
		
		public int[][] getFrameTextureData(int index)
	    {
	        int[] datafront = front.getFrameTextureData(index)[0];
	        int[] databack = back.getFrameTextureData(index)[0];
	        int[] out = new int[datafront.length];
	        
	        for (int i = 0; i < out.length; i++) {
	        	if ((datafront[i] >>> 24) < 255) {
	        		out[i] = databack[i];
	        	}
	        	else
	        		out[i] = 0xFF000000;
	        }
	        
	        return new int[][] {out};
	    }
		
		public int getFrameCount()
	    {
	        return this.back.getFrameCount();
	    }
		
		public int getIconWidth()
	    {
	        return this.front.getIconWidth();
	    }
		
		public int getIconHeight()
	    {
	        return this.front.getIconHeight();
	    }
    }
    
    public static class MapEntity {
    	int id;
    	Map<String, String> properties = new HashMap<>();
    	List<BlockPos> min = new ArrayList<BlockPos>();
    	List<BlockPos> max = new ArrayList<BlockPos>();
    	float x;
    	float y;
    	float z;
    	boolean hasOrigin;
    	boolean generated;
    	Set<Cube> cubes;
    	
    	public MapEntity(String classname, boolean generated) {
    		properties.put("classname", classname);
    		this.generated=generated;
    	}
    	public void addCube(Cube cube) {
    		if (cubes == null)
    			cubes = new HashSet<>();
    		cubes.add(cube);
    	}
    	
    	public void removeCube(Cube cube) {
    		if (cubes != null)
    			cubes.remove(cube);
    	}
    	
    	public void setPosition(float x, float y, float z) {
    		this.x = x;
    		this.y = y;
    		this.z = z;
    		this.hasOrigin = true;
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
			if (sprite instanceof SpriteCombined) {
				if (nicename) {
					String[] front=((SpriteCombined)sprite).front.getIconName().split("/");
					String[] back=((SpriteCombined)sprite).back.getIconName().split("/");
					this.name = front[front.length-1]
							+"_"+back[front.length-1];
				}
				else {
					this.name = /*encodeInt(((SpriteCombined)sprite).front.getOriginX()/16)
							+"_"+encodeInt(((SpriteCombined)sprite).front.getOriginY()/16)
							+"_"+*/encodeInt(((SpriteCombined)sprite).back.getOriginX()/16)
							+"_"+encodeInt(((SpriteCombined)sprite).back.getOriginY()/16);
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
    
    public static class SpriteFragment {
    	TextureAtlasSprite sprite;
    	int minX;
    	int maxX;
    	int minY;
    	int maxY;
    	
    	int id;
    	public SpriteFragment(TextureAtlasSprite sprite, Vector2f[] uv, Vector3f[] vecCache, EnumFacing facing) {
    		
    	}
    }
    
    static {

    	materialNames.put(Material.ANVIL, "metal");
    	materialNames.put(Material.CACTUS, "foliage");
    	materialNames.put(Material.CAKE, "foliage");
    	materialNames.put(Material.CARPET, "carpet");
    	materialNames.put(Material.CLAY, "gravel");
    	materialNames.put(Material.CLOTH, "flesh");
    	materialNames.put(Material.CRAFTED_SNOW, "snow");
    	materialNames.put(Material.GLASS, "glass");
    	materialNames.put(Material.GRASS, "grass");
    	materialNames.put(Material.GROUND, "dirt");
    	materialNames.put(Material.ICE, "ice");
    	materialNames.put(Material.IRON, "metal");
    	materialNames.put(Material.LAVA, "water");
    	materialNames.put(Material.LEAVES, "foliage");
    	materialNames.put(Material.PACKED_ICE, "ice");
    	materialNames.put(Material.ROCK, "rock");
    	materialNames.put(Material.SAND, "sand");
    	materialNames.put(Material.SNOW, "snow");
    	materialNames.put(Material.VINE, "foliage");
    	materialNames.put(Material.WATER, "water");
    	materialNames.put(Material.WOOD, "wood");
    	
    	
    }
}