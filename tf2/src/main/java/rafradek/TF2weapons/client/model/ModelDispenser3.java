package rafradek.TF2weapons.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.GlStateManager;

/**
 * ModelDispenser3 - Undefined Created using Tabula 5.1.0
 */
public class ModelDispenser3 extends ModelBase {
	public ModelRenderer shapemed2;
	public ModelRenderer shapelvl31;
	public ModelRenderer shape2;
	public ModelRenderer shape3;
	public ModelRenderer shape4;
	public ModelRenderer shape5;
	public ModelRenderer shape1;
	public ModelRenderer shape4_1;
	public ModelRenderer shape3_1;
	public ModelRenderer shape2_1;
	public ModelRenderer shape5_1;
	public ModelRenderer shapemain;
	public ModelRenderer shapemed;
	public ModelRenderer shapemed_1;
	public ModelRenderer shapeammo;
	public ModelRenderer shapelvl22;
	public ModelRenderer shapelvl21;
	public ModelRenderer shape5_2;
	public ModelRenderer shape5_3;
	public ModelRenderer shape1_1;
	public ModelRenderer shapelvl32;

	public ModelDispenser3() {
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.shape1_1 = new ModelRenderer(this, 32, 0);
		this.shape1_1.setRotationPoint(0.0F, 8.0F, 0.0F);
		this.shape1_1.addBox(-4.5F, 0.0F, 2.0F, 9, 2, 1, 0.0F);
		this.shape4 = new ModelRenderer(this, 52, 0);
		this.shape4.setRotationPoint(0.0F, 8.0F, 0.0F);
		this.shape4.addBox(3.5F, 1.9F, -2.5F, 1, 5, 1, -0.1F);
		this.shapelvl31 = new ModelRenderer(this, 0, 26);
		this.shapelvl31.setRotationPoint(0.0F, 5.0F, 0.0F);
		this.shapelvl31.addBox(-3.5F, 0.0F, -1.5F, 7, 3, 3, 0.0F);
		this.shape5 = new ModelRenderer(this, 32, 13);
		this.shape5.setRotationPoint(0.0F, 21.9F, -3.0F);
		this.shape5.addBox(-4.5F, -2.0F, -0.5F, 9, 4, 1, 0.0F);
		this.setRotateAngle(shape5, 0.47123889803846897F, 0.0F, 0.0F);
		this.shape5_1 = new ModelRenderer(this, 32, 13);
		this.shape5_1.setRotationPoint(0.0F, 21.9F, 3.0F);
		this.shape5_1.addBox(-4.5F, -2.0F, -0.5F, 9, 4, 1, 0.0F);
		this.setRotateAngle(shape5_1, -0.47123889803846897F, 0.0F, 0.0F);
		this.shape1 = new ModelRenderer(this, 32, 0);
		this.shape1.setRotationPoint(0.0F, 8.0F, 0.0F);
		this.shape1.addBox(-4.5F, 0.0F, -3.0F, 9, 2, 1, 0.0F);
		this.shapemed = new ModelRenderer(this, 32, 18);
		this.shapemed.setRotationPoint(-6.5F, 19.3F, 0.0F);
		this.shapemed.addBox(-1.0F, -5.0F, -1.5F, 3, 5, 3, -0.2F);
		this.shapeammo = new ModelRenderer(this, 56, 0);
		this.shapeammo.setRotationPoint(4.2F, 10.0F, -1.5F);
		this.shapeammo.addBox(0.0F, 0.0F, 0.0F, 1, 9, 3, 0.0F);
		this.shape5_3 = new ModelRenderer(this, 32, 13);
		this.shape5_3.setRotationPoint(0.5F, 19.3F, -2.4F);
		this.shape5_3.addBox(-4.5F, -2.0F, -0.5F, 8, 4, 1, 0.3F);
		this.setRotateAngle(shape5_3, 0.9162978572970231F, 0.0F, 0.0F);
		this.shapemed2 = new ModelRenderer(this, 44, 18);
		this.shapemed2.setRotationPoint(-6.5F, 13.0F, -0.5F);
		this.shapemed2.addBox(0.0F, 0.0F, 0.0F, 1, 9, 1, 0.0F);
		this.shape4_1 = new ModelRenderer(this, 52, 0);
		this.shape4_1.setRotationPoint(0.0F, 8.0F, 0.0F);
		this.shape4_1.addBox(3.5F, 1.9F, 1.5F, 1, 5, 1, -0.1F);
		this.shapelvl21 = new ModelRenderer(this, 8, 20);
		this.shapelvl21.setRotationPoint(0.5F, 8.2F, -3.0F);
		this.shapelvl21.addBox(-4.5F, 7.0F, -1.2F, 8, 3, 2, 0.2F);
		this.shapelvl32 = new ModelRenderer(this, 20, 27);
		this.shapelvl32.setRotationPoint(-0.5F, 4.8F, -0.5F);
		this.shapelvl32.addBox(-3.5F, 0.0F, -1.5F, 8, 1, 4, 0.0F);
		this.shape2 = new ModelRenderer(this, 32, 3);
		this.shape2.setRotationPoint(0.0F, 8.0F, 0.0F);
		this.shape2.addBox(-4.5F, 7.0F, -2.7F, 9, 9, 1, -0.1F);
		this.shape2_1 = new ModelRenderer(this, 32, 3);
		this.shape2_1.setRotationPoint(0.0F, 8.0F, 0.0F);
		this.shape2_1.addBox(-4.5F, 7.0F, 1.7F, 9, 9, 1, -0.1F);
		this.shapelvl22 = new ModelRenderer(this, 8, 20);
		this.shapelvl22.setRotationPoint(0.5F, 8.2F, 3.5F);
		this.shapelvl22.addBox(-4.5F, 7.0F, -1.2F, 8, 3, 2, 0.2F);
		this.shapemain = new ModelRenderer(this, 0, 0);
		this.shapemain.setRotationPoint(0.0F, 16.0F, 0.0F);
		this.shapemain.addBox(-4.5F, -8.0F, -2.0F, 9, 16, 4, 0.0F);
		this.shapemed_1 = new ModelRenderer(this, 0, 20);
		this.shapemed_1.setRotationPoint(-6.5F, 16.0F, 1.0F);
		this.shapemed_1.addBox(-1.0F, -5.7F, -1.5F, 3, 3, 1, 0.1F);
		this.shape3 = new ModelRenderer(this, 52, 0);
		this.shape3.setRotationPoint(0.0F, 8.0F, 0.0F);
		this.shape3.addBox(-4.5F, 1.9F, -2.5F, 1, 5, 1, -0.1F);
		this.shape3_1 = new ModelRenderer(this, 52, 0);
		this.shape3_1.setRotationPoint(0.0F, 8.0F, 0.0F);
		this.shape3_1.addBox(-4.5F, 1.9F, 1.5F, 1, 5, 1, -0.1F);
		this.shape5_2 = new ModelRenderer(this, 32, 13);
		this.shape5_2.setRotationPoint(0.5F, 19.3F, 2.4F);
		this.shape5_2.addBox(-4.5F, -2.0F, -0.5F, 8, 4, 1, 0.3F);
		this.setRotateAngle(shape5_2, -0.9162978572970231F, 0.0F, 0.0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		this.shape1_1.render(f5);
		this.shape4.render(f5);
		this.shapelvl31.render(f5);
		this.shape5.render(f5);
		this.shape5_1.render(f5);
		this.shape1.render(f5);
		this.shapemed.render(f5);
		this.shapeammo.render(f5);
		this.shape5_3.render(f5);
		GlStateManager.pushMatrix();
		GlStateManager.translate(this.shapemed2.offsetX, this.shapemed2.offsetY, this.shapemed2.offsetZ);
		GlStateManager.translate(this.shapemed2.rotationPointX * f5, this.shapemed2.rotationPointY * f5,
				this.shapemed2.rotationPointZ * f5);
		GlStateManager.scale(1.0D, 1.0D, 0.9D);
		GlStateManager.translate(-this.shapemed2.offsetX, -this.shapemed2.offsetY, -this.shapemed2.offsetZ);
		GlStateManager.translate(-this.shapemed2.rotationPointX * f5, -this.shapemed2.rotationPointY * f5,
				-this.shapemed2.rotationPointZ * f5);
		this.shapemed2.render(f5);
		GlStateManager.popMatrix();
		this.shape4_1.render(f5);
		this.shapelvl21.render(f5);
		this.shapelvl32.render(f5);
		this.shape2.render(f5);
		this.shape2_1.render(f5);
		this.shapelvl22.render(f5);
		this.shapemain.render(f5);
		this.shapemed_1.render(f5);
		this.shape3.render(f5);
		this.shape3_1.render(f5);
		this.shape5_2.render(f5);
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
