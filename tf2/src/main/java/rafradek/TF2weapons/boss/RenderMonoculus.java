package rafradek.TF2weapons.boss;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.characters.EntityHeavy;
import rafradek.TF2weapons.characters.EntityTF2Character;

public class RenderMonoculus extends RenderLivingBase<EntityMonoculus> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/monoculus.png");
	private static final ResourceLocation TEXTURE_ANGRY = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/monoculusangry.png");
	public RenderMonoculus(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelMonoculus(), 0f);
		// TODO Auto-generated constructor stub
		
	}
	protected boolean canRenderName(EntityMonoculus entity)
    {
		return false;
    }
	protected void preRenderCallback(EntityMonoculus entity, float partialTickTime)
    {
		float f = 2F;
		if(entity.begin>0){
			GlStateManager.translate(0, entity.begin*0.133333f, 0);
		}
        /*GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTickTime, 0.0F,
				0.0F, 1.0F);*/
        GlStateManager.scale(f, f, f);
    }
	@Override
	protected ResourceLocation getEntityTexture(EntityMonoculus par1EntityLiving) {
		return par1EntityLiving.isAngry()?TEXTURE_ANGRY:TEXTURE;
	}
	
}
