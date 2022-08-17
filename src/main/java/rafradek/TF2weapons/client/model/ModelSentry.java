package rafradek.TF2weapons.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import rafradek.TF2weapons.entity.building.EntitySentry;

/**
 * ModelSentry - Radafrek + Wolfkann
 * Created using Tabula 6.0.0
 */
public class ModelSentry extends ModelBase {
    public ModelRenderer head;
    public ModelRenderer leg1;
    public ModelRenderer leg2;
    public ModelRenderer foot3;
    public ModelRenderer foot2;
    public ModelRenderer legbase;
    public ModelRenderer backleg1;
    public ModelRenderer backleg2;
    public ModelRenderer main;
    public ModelRenderer leg3;
    public ModelRenderer leg4;
    public ModelRenderer foot1;
    public ModelRenderer foot4;
    public ModelRenderer backleg2_1;
    public ModelRenderer legbase_1;
    public ModelRenderer headChild;
    public ModelRenderer headChild_1;
    public ModelRenderer headChild_2;
    public ModelRenderer Bottem;
    public ModelRenderer Line;
    public ModelRenderer Line_1;
    public ModelRenderer LineSquare;
    public ModelRenderer minihead;
    public ModelRenderer minilight;

    public ModelSentry() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.legbase_1 = new ModelRenderer(this, 13, 0);
        this.legbase_1.setRotationPoint(-1.5F, 16.7F, -2.4F);
        this.legbase_1.addBox(0.0F, 0.0F, 0.0F, 3, 1, 3, 0.0F);
        this.setRotateAngle(legbase_1, -0.18360863730980345F, 0.0F, 0.0F);
        this.Bottem = new ModelRenderer(this, 0, 0);
        this.Bottem.setRotationPoint(-0.5F, 0.5F, 4.6F);
        this.Bottem.addBox(0.0F, 0.0F, 0.0F, 1, 2, 2, 0.0F);
        this.setRotateAngle(Bottem, -0.6850417314077744F, 0.0F, 0.0F);
        this.Line = new ModelRenderer(this, 0, 0);
        this.Line.setRotationPoint(0.0F, 2.0F, 4.1F);
        this.Line.addBox(-0.5F, 0.0F, 0.0F, 1, 3, 1, 0.0F);
        this.setRotateAngle(Line, -0.7277663629049491F, 0.0F, 0.0F);
        this.headChild_2 = new ModelRenderer(this, 0, 17);
        this.headChild_2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.headChild_2.addBox(0.0F, 0.0F, -4.0F, 1, 1, 1, 0.0F);
        this.foot4 = new ModelRenderer(this, 22, 6);
        this.foot4.setRotationPoint(-3.0F, 23.0F, 4.3F);
        this.foot4.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(foot4, 0.0F, -0.08726646259971647F, 0.0F);
        this.leg1 = new ModelRenderer(this, 0, 0);
        this.leg1.setRotationPoint(0.0F, 20.0F, -1.5F);
        this.leg1.addBox(0.0F, -0.5F, -0.30000001192092896F, 1, 1, 7, 0.0F);
        this.setRotateAngle(leg1, -0.6981316804885863F, -2.5307273864746094F, 0.0F);
        this.leg4 = new ModelRenderer(this, 0, 0);
        this.leg4.setRotationPoint(1.7F, 20.3F, 3.5F);
        this.leg4.addBox(-0.4F, 0.0F, 0.0F, 1, 1, 7, 0.0F);
        this.setRotateAngle(leg4, -0.951378975262109F, 0.08726646259971647F, 0.0F);
        this.Line_1 = new ModelRenderer(this, 0, 0);
        this.Line_1.setRotationPoint(0.0F, 3.6F, 2.9F);
        this.Line_1.addBox(-0.5F, 0.0F, 0.0F, 1, 6, 1, 0.0F);
        this.setRotateAngle(Line_1, -2.2979004931757343F, 0.0F, 0.0F);
        this.headChild_1 = new ModelRenderer(this, 32, 10);
        this.headChild_1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.headChild_1.addBox(-7.0F, -2.0F, -3.5F, 5, 4, 7, 0.0F);
        this.setRotateAngle(headChild_1, 0.0F, 1.570796012878418F, 0.0F);
        this.foot3 = new ModelRenderer(this, 22, 6);
        this.foot3.setRotationPoint(0.9F, 23.0F, 4.3F);
        this.foot3.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(foot3, 0.0F, 0.08726646259971647F, 0.0F);
        this.leg2 = new ModelRenderer(this, 0, 0);
        this.leg2.setRotationPoint(0.0F, 20.0F, -1.5F);
        this.leg2.addBox(-1.0F, -0.5F, -0.30000001192092896F, 1, 1, 7, 0.0F);
        this.setRotateAngle(leg2, -0.6981316804885863F, 2.5307273864746094F, 0.04555309191346169F);
        this.backleg1 = new ModelRenderer(this, 15, 9);
        this.backleg1.setRotationPoint(0.5F, 19.2F, -0.1F);
        this.backleg1.addBox(0.0F, 0.0F, 0.0F, 1, 1, 4, 0.0F);
        this.setRotateAngle(backleg1, -0.4363323129985824F, 0.08726646259971647F, 0.0F);
        this.foot2 = new ModelRenderer(this, 22, 6);
        this.foot2.setRotationPoint(-4.4F, 23.0F, -5.2F);
        this.foot2.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(foot2, 0.0F, 0.6108652381980153F, 0.0F);
        this.headChild = new ModelRenderer(this, 32, 10);
        this.headChild.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.headChild.addBox(-2.5F, -2.0F, 1.0F, 5, 4, 7, 0.0F);
        this.LineSquare = new ModelRenderer(this, 0, 0);
        this.LineSquare.setRotationPoint(0.0F, 3.3F, 3.9F);
        this.LineSquare.addBox(-0.5F, 0.0F, 0.0F, 1, 2, 2, 0.0F);
        this.setRotateAngle(LineSquare, -1.8484782107871942F, 0.0F, 0.0F);
        this.backleg2 = new ModelRenderer(this, 15, 9);
        this.backleg2.setRotationPoint(-1.1F, 19.2F, -0.1F);
        this.backleg2.addBox(0.0F, 0.0F, 0.0F, 2, 1, 2, 0.0F);
        this.setRotateAngle(backleg2, -0.4363323129985824F, 0.0F, 0.0F);
        this.main = new ModelRenderer(this, 0, 0);
        this.main.setRotationPoint(0.0F, 18.0F, -1.1F);
        this.main.addBox(-0.5F, -3.5F, -0.5F, 1, 8, 1, 0.0F);
        this.setRotateAngle(main, -0.18369277626926808F, 0.0F, 0.0F);
        this.head = new ModelRenderer(this, 0, 15);
        this.head.setRotationPoint(0.0F, 13.0F, -0.2F);
        this.head.addBox(-2.0F, -1.5F, -3.0F, 4, 3, 4, 0.25F);
        this.legbase = new ModelRenderer(this, 13, 0);
        this.legbase.setRotationPoint(-1.5F, 18.6F, -2.8F);
        this.legbase.addBox(0.0F, 0.0F, 0.0F, 3, 1, 3, 0.0F);
        this.setRotateAngle(legbase, -0.18360863730980345F, 0.0F, 0.0F);
        this.foot1 = new ModelRenderer(this, 22, 6);
        this.foot1.setRotationPoint(2.6F, 22.9F, -6.2F);
        this.foot1.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
        this.setRotateAngle(foot1, -0.0F, -0.6108652381980153F, 0.0F);
        this.leg3 = new ModelRenderer(this, 0, 0);
        this.leg3.setRotationPoint(-2.0F, 20.3F, 3.5F);
        this.leg3.addBox(-0.4F, 0.0F, 0.0F, 1, 1, 7, 0.0F);
        this.setRotateAngle(leg3, -0.9339256827421656F, -0.08726646259971647F, 0.0F);
        this.backleg2_1 = new ModelRenderer(this, 15, 9);
        this.backleg2_1.setRotationPoint(-1.6F, 19.2F, -0.1F);
        this.backleg2_1.addBox(0.0F, 0.0F, 0.0F, 1, 1, 4, 0.0F);
        this.setRotateAngle(backleg2_1, -0.4363323129985824F, -0.08726646259971647F, 0.0F);
        this.minihead = new ModelRenderer(this, 16, 25);
        this.minihead.mirror = true;
        this.minihead.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.minihead.addBox(-1.0F, -6.0F, 3.0F, 3, 4, 3, 0.0F);
        this.minilight = new ModelRenderer(this, 0, 28);
        this.minilight.mirror = true;
        this.minilight.setRotationPoint(0.5F, -7.0F, 4.5F);
        this.minilight.addBox(-4.0F, 0.0F, 0.0F, 8, 4, 0, 0.0F);
        this.head.addChild(this.Bottem);
        this.head.addChild(this.Line);
        this.head.addChild(this.headChild_2);
        this.head.addChild(this.Line_1);
        this.head.addChild(this.headChild_1);
        this.head.addChild(this.headChild);
        this.head.addChild(this.LineSquare);
        this.head.addChild(this.minihead);
        this.head.addChild(this.minilight);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
    	
    	this.minihead.isHidden = this.minilight.isHidden = !((EntitySentry)entity).isMini();
    	
    	this.legbase_1.render(f5);
        this.foot4.render(f5);
        this.leg1.render(f5);
        this.leg4.render(f5);
        this.foot3.render(f5);
        this.leg2.render(f5);
        this.backleg1.render(f5);
        this.foot2.render(f5);
        this.backleg2.render(f5);
        this.main.render(f5);
        this.legbase.render(f5);
        this.foot1.render(f5);
        this.leg3.render(f5);
        this.backleg2_1.render(f5);
        GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		if (((EntitySentry)entity).isMini()) {
			int i = 0xFFffff;
	        int j = i % 65536;
	        int k = i / 65536;
	        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
		}
        this.head.render(f5);
        GlStateManager.disableBlend();
        
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
    
    @Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
		this.head.rotateAngleY = netHeadYaw / (180F / (float) Math.PI);
		this.head.rotateAngleX = headPitch / (180F / (float) Math.PI);
		this.minilight.rotateAngleY = ageInTicks / 2;
	}
}
