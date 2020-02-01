package rafradek.minecraft2source;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rafradek.minecraft2source.MapBuilder.Model;
import rafradek.minecraft2source.MapBuilder.ModelTriangle;

public class ModelReader {
	
	Vector3f mmmn = new Vector3f(-0.577349f, -0.577349f, -0.577349f);
	Vector3f xmmn = new Vector3f(0.577349f, -0.577349f, -0.577349f);
	Vector3f mxmn = new Vector3f(-0.577349f, 0.577349f, 0.577349f);
	Vector3f mmxn = new Vector3f(-0.577349f, -0.577349f, 0.577349f);
	Vector3f xxmn = new Vector3f(0.577349f, 0.577349f, -0.577349f);
	Vector3f mxxn = new Vector3f(-0.577349f, 0.577349f, 0.577349f);
	Vector3f xmxn = new Vector3f(0.577349f, -0.577349f, 0.577349f);
	Vector3f xxxn = new Vector3f(0.577349f, 0.577349f, 0.577349f);
	
	public Map<BakedQuad, Vector3f[]> vecCache = new HashMap<>();
    public Map<BakedQuad, Vector2f[]> uvCache = new HashMap<>();
    
    public boolean fixItemUV = false;
	public boolean buildModelCache(IBlockState state, IBakedModel model, World world, long rand, float twidth, float theight,EnumMap<EnumFacing, List<BakedQuad>> quads) {
    	
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
        		
        		float minu = 1;
        		float maxu = 0;
        		
        		float minv = 1;
        		float maxv = 0;
        		
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
	        		if (fixItemUV) {
		        		if (u < minu)
		        			minu = u;
		        		if (u > maxu)
		        			maxu = u;
		        		if (v < minv)
		        			minv = v;
		        		if (v > maxv)
		        			maxv = v;
	        		}
	        		uvcache[i/7]= new Vector2f(u, v);

	        		
	        		//Minecraft.getMinecraft().player.sendMessage(new TextComponentString( x1+" "+y1+" "+z1+" "+u+" "+v+" "+quad.getSprite().getMaxV()+" "+vertexData.length));
	    		}
		    	if (fixItemUV) {
		    		/*for (Vector2f vec : uvcache) {
		    			if (vec.x == minu) {
		    				vec.x += 0.00390625f;
		    			}
		    			if (vec.x == maxu) {
		    				vec.x -= 0.00390625f;
		    			}
		    			if (vec.y == minv) {
		    				vec.y += 0.00390625f;
		    			}
		    			if (vec.y == maxv) {
		    				vec.y -= 0.00390625f;
		    			}
		    		}*/
		    		if (minu == maxu) {
		    			if (cache[0].z < cache[2].z) {
			    			uvcache[2].x-=0.001f;
			    			uvcache[3].x-=0.001f;
		    			}
		    			else {
		    				uvcache[2].x+=0.001f;
			    			uvcache[3].x+=0.001f;
		    			}
		    		}
		    		if (minv == maxv) {
		    			if (cache[0].z < cache[2].z) {
			    			uvcache[2].y-=0.001f;
			    			uvcache[3].y-=0.001f;
		    			}
		    			else {
		    				uvcache[2].y+=0.001f;
			    			uvcache[3].y+=0.001f;
		    			}
		    		}
		    	}
	    		quads.get(quad.getFace()).add(quad);
    		}
		}
    	return diagonal;
    }
	
	public Model readModel(Material material, Vector3f offset, ModelExporter spriteReg, List<AxisAlignedBB> cbox, boolean generate){
		Model model = new Model();
    	
		boolean findBounds = false;
		
		if (cbox == null && generate) {
    		findBounds = true;
    		cbox = new ArrayList<>();
    		
    	}
		
		float minx = Float.MAX_VALUE;
		float miny = Float.MAX_VALUE;
		float minz = Float.MAX_VALUE;
		float maxx = Float.MIN_VALUE;
		float maxy = Float.MIN_VALUE;
		float maxz = Float.MIN_VALUE;
    	for (BakedQuad quad : vecCache.keySet()) {
    		Vector3f[] vec = vecCache.get(quad);
    		
    		if (findBounds) {
    			for (Vector3f v : vec) {
    				if (v.x < minx) {
    					minx = v.x;
    				}
    				if (v.x > maxx) {
    					maxx = v.x;
    				}
    				if (v.y < miny) {
    					miny = v.y;
    				}
    				if (v.y > maxy) {
    					maxy = v.y;
    				}
    				if (v.z < minz) {
    					minz = v.z;
    				}
    				if (v.z > maxz) {
    					maxz = v.z;
    				}
    			}
    		}
    		
    		Vector3f.add(vec[0], offset, vec[0]);
    		Vector3f.add(vec[1], offset, vec[1]);
    		Vector3f.add(vec[2], offset, vec[2]);
    		Vector3f.add(vec[3], offset, vec[3]);
    		
    		Vector2f[] uv = uvCache.get(quad);
    		//System.out.println(uv[0]+" "+uv[1]+" "+uv[2]+" "+uv[3]);
    		Vector3f sub1 = new Vector3f();
    		Vector3f sub2 = new Vector3f();
    		Vector3f normal = new Vector3f();
    		Vector3f.sub(vec[0], vec[1], sub1);
    		Vector3f.sub(vec[1], vec[2], sub2);
    		Vector3f.cross(sub1, sub2, normal);
    		TextureAtlasSprite sprite = quad.getSprite();
    		ModelTriangle tri1 = new ModelTriangle();
    		tri1.normal = new Vector3f[] {normal,normal,normal};
    		tri1.pos = new Vector3f[] {vec[1],vec[2],vec[0]};
    		tri1.uv = new Vector2f[] {uv[1],uv[2],uv[0]};
    		tri1.sprite = sprite;
    		tri1.hasTint = quad.hasTintIndex();
    		
    		ModelTriangle tri2 = new ModelTriangle();
    		tri2.normal = new Vector3f[] {normal,normal,normal};
    		tri2.pos = new Vector3f[] {vec[2],vec[3],vec[0]};
    		tri2.uv = new Vector2f[] {uv[2],uv[3],uv[0]};
    		tri2.sprite = quad.getSprite();
    		tri2.hasTint = quad.hasTintIndex();
    		model.quads.add(tri1);
    		model.quads.add(tri2);
    		if (quad.hasTintIndex())
    			model.hasTint = true;
    		spriteReg.addSprite(quad.getSprite(), true,material);
    		
    		
    	}
    	
    	if (findBounds) {
    		cbox.add(new AxisAlignedBB(minx,miny,minz,maxx,maxy,maxz));
    	}
    	
    	for (AxisAlignedBB box : cbox) {
    		box = box.offset(offset.x, offset.y, offset.z);
    		Vector3f mmm = new Vector3f((float)box.minX, (float)box.minY, (float)box.minZ);
    		Vector3f xmm = new Vector3f((float)box.maxX, (float)box.minY, (float)box.minZ);
    		Vector3f mxm = new Vector3f((float)box.minX, (float)box.maxY, (float)box.minZ);
    		Vector3f mmx = new Vector3f((float)box.minX, (float)box.minY, (float)box.maxZ);
    		Vector3f xxm = new Vector3f((float)box.maxX, (float)box.maxY, (float)box.minZ);
    		Vector3f mxx = new Vector3f((float)box.minX, (float)box.maxY, (float)box.maxZ);
    		Vector3f xmx = new Vector3f((float)box.maxX, (float)box.minY, (float)box.maxZ);
    		Vector3f xxx = new Vector3f((float)box.maxX, (float)box.maxY, (float)box.maxZ);
    		
    		ModelTriangle tri1 = new ModelTriangle();
    		tri1.normal = new Vector3f[] {xxmn,xxxn,mxmn};
    		tri1.pos = new Vector3f[] {xxm,xxx,mxm};
    		model.collisionBox.add(tri1);
    		
    		tri1 = new ModelTriangle();
    		tri1.normal = new Vector3f[] {xxxn,mxxn,mxmn};
    		tri1.pos = new Vector3f[] {xxx,mxx,mxm};
    		model.collisionBox.add(tri1);
    		
    		tri1 = new ModelTriangle();
    		tri1.normal = new Vector3f[] {mmmn,xmxn,xmmn};
    		tri1.pos = new Vector3f[] {mmm,xmx,xmm};
    		model.collisionBox.add(tri1);
    		
    		tri1 = new ModelTriangle();
    		tri1.normal = new Vector3f[] {mmmn,mmxn,xmxn};
    		tri1.pos = new Vector3f[] {mmm,mmx,xmx};
    		model.collisionBox.add(tri1);
    		
    		tri1 = new ModelTriangle();
    		tri1.normal = new Vector3f[] {xxmn,xxxn,xmmn};
    		tri1.pos = new Vector3f[] {xxm,xxx,xmm};
    		model.collisionBox.add(tri1);
    		
    		tri1 = new ModelTriangle();
    		tri1.normal = new Vector3f[] {xxxn,xmxn,xmmn};
    		tri1.pos = new Vector3f[] {xxx,xmx,xmm};
    		model.collisionBox.add(tri1);
    		
    		tri1 = new ModelTriangle();
    		tri1.normal = new Vector3f[] {mmmn,mxxn,mxmn};
    		tri1.pos = new Vector3f[] {mmm,mxx,mxm};
    		model.collisionBox.add(tri1);
    		
    		tri1 = new ModelTriangle();
    		tri1.normal = new Vector3f[] {mmmn,mmxn,mxxn};
    		tri1.pos = new Vector3f[] {mmm,mmx,mxx};
    		model.collisionBox.add(tri1);
    		
    		tri1 = new ModelTriangle();
    		tri1.normal = new Vector3f[] {xmxn,xxxn,mmxn};
    		tri1.pos = new Vector3f[] {xmx,xxx,mmx};
    		model.collisionBox.add(tri1);
    		
    		tri1 = new ModelTriangle();
    		tri1.normal = new Vector3f[] {xxxn,mxxn,mmxn};
    		tri1.pos = new Vector3f[] {xxx,mxx,mmx};
    		model.collisionBox.add(tri1);
    		
    		tri1 = new ModelTriangle();
    		tri1.normal = new Vector3f[] {mmmn,xxmn,xmmn};
    		tri1.pos = new Vector3f[] {mmm,xxm,xmm};
    		model.collisionBox.add(tri1);
    		
    		tri1 = new ModelTriangle();
    		tri1.normal = new Vector3f[] {mmmn,mxmn,xxmn};
    		tri1.pos = new Vector3f[] {mmm,mxm,xxm};
    		model.collisionBox.add(tri1);
    		
    		
    	}
    	Vector2f[] uvdef = new Vector2f[] {new Vector2f(1,0),new Vector2f(1,1),new Vector2f(0,0)};
    	for (ModelTriangle tri: model.collisionBox) {
    		tri.uv = uvdef;
    	}
    	return model;
	}
	
	public Model readModel(IBlockState state, List<AxisAlignedBB> cbox, Vector3f offset, ModelExporter spriteReg){
    	
    	Model model = readModel(state.getMaterial(), offset, spriteReg, cbox, false);
    	
    	
    	
    	
    	StringBuilder modelname = new StringBuilder();
    	if (spriteReg.niceName)
    		modelname.append(state.getBlock().getRegistryName().getResourcePath());
    	else
    		modelname.append(MapBuilder.encodeInt(Block.getIdFromBlock(state.getBlock())));
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
}
