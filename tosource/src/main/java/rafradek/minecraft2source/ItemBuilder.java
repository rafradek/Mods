package rafradek.minecraft2source;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.lwjgl.util.vector.Vector3f;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import rafradek.minecraft2source.MapBuilder.Model;

public class ItemBuilder extends ModelExporter {

	private static final Vector3f VECTOR_ZERO = new Vector3f(0,0,0);
	
	public ModelReader modelReader = new ModelReader();
	
	public List<Model> models = new ArrayList<>();
	
	public void buildItem(ItemStack stack, World world, EntityLivingBase entity, Vector3f offset, float scale) {
		this.sprites = new HashMap<>();
		this.niceName = true;
		this.models.clear();
		this.sprites.clear();
		this.scale = scale;
		
		TextureAtlasSprite sprtest = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:items/brick");
		int twidth=(int) (sprtest.getIconWidth()/(sprtest.getMaxU()-sprtest.getMinU()));
		int theight=(int) (sprtest.getIconHeight()/(sprtest.getMaxV()-sprtest.getMinV()));
		float twidthsp=twidth;
		float theightsp=theight;
		
		IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, world, null);
		
		EnumMap<EnumFacing, List<BakedQuad>> quads = new EnumMap<>(EnumFacing.class);
		modelReader.buildModelCache(null, model, world, world.rand.nextLong(), twidth, theight, quads);
		Model modelread = modelReader.readModel(Material.AIR, offset, this, null);
		modelread.name = stack.getItem().getRegistryName().getResourcePath()+"_"+MapBuilder.encodeInt(stack.getItemDamage());
		
		if (stack.hasTagCompound()) {
			modelread.name +="_"+stack.getTagCompound().toString().hashCode();
		}
		
		models.add(modelread);
		Minecraft2Source.LOGGER.info("Written %1", modelread.name);
		try {
			this.writeModels();
			this.writeTextures();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public Collection<Model> getModels() {
		// TODO Auto-generated method stub
		return models;
	}
	
	public String getModelOutputPath() {
		return "minecraft/item";
	}
	
	public String getMaterialOutputPath() {
		return "minecraft/item";
	}
}
