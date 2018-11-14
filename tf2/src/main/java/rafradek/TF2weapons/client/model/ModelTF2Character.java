package rafradek.TF2weapons.client.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import rafradek.TF2weapons.entity.mercenary.EntityTF2Character;

public class ModelTF2Character extends ModelBiped {

	public ModelTF2Character() {
		super(0.0f, 0.0f, 64, 64);
	}
	
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        GlStateManager.pushMatrix();

        if (this.isChild)
        {
            float f = 2.0F;
            GlStateManager.scale(0.75F, 0.75F, 0.75F);
            GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
            this.bipedHead.render(scale);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
            this.bipedBody.render(scale);
            this.bipedRightArm.render(scale);
            this.bipedLeftArm.render(scale);
            this.bipedRightLeg.render(scale);
            this.bipedLeftLeg.render(scale);
            this.bipedHeadwear.render(scale);
        }
        else
        {
            if (entityIn.isSneaking())
            {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }
            if (((EntityTF2Character) entityIn).isGiant()) {
            	GlStateManager.scale(0.75F, 0.75F, 0.75F);
                GlStateManager.translate(0.0F, 0, 0.0F);
                this.bipedHead.render(scale);
            	this.bipedHeadwear.render(scale);
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
            	
            	
            }
            else {
            	this.bipedHead.render(scale);
            	this.bipedHeadwear.render(scale);
            }
            this.bipedBody.render(scale);
            this.bipedRightArm.render(scale);
            this.bipedLeftArm.render(scale);
            this.bipedRightLeg.render(scale);
            this.bipedLeftLeg.render(scale);
            
        }

        GlStateManager.popMatrix();
    }
	
}
