package rafradek.TF2weapons.building;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelSentry - Undefined Created using Tabula 5.1.0
 */
public class ModelSentry extends ModelBase {
	public ModelRenderer main;
	public ModelRenderer leg1;
	public ModelRenderer leg3;
	public ModelRenderer head;
	public ModelRenderer leg2;
	public ModelRenderer ammo2;
	public ModelRenderer ammo1;
	public ModelRenderer muzzle;

	public ModelSentry() {
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.leg1 = new ModelRenderer(this, 0, 0);
		this.leg1.setRotationPoint(0.0F, 20.0F, -1.5F);
		this.leg1.addBox(0.0F, -0.5F, -0.3F, 1, 1, 7, 0.0F);
		this.setRotateAngle(leg1, -0.6981317007977318F, -2.530727415391778F, 0.0F);
		this.leg3 = new ModelRenderer(this, 0, 0);
		this.leg3.setRotationPoint(0.0F, 20.0F, -1.5F);
		this.leg3.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 9, 0.0F);
		this.setRotateAngle(leg3, -0.4553564018453205F, 0.0F, 0.0F);
		this.main = new ModelRenderer(this, 0, 0);
		this.main.setRotationPoint(0.0F, 18.0F, -1.5F);
		this.main.addBox(-0.5F, -3.5F, -0.5F, 1, 8, 1, 0.2F);
		this.head = new ModelRenderer(this, 0, 15);
		this.head.setRotationPoint(0.0F, 13.0F, -1.5F);
		this.head.addBox(-2.0F, -1.5F, -3.0F, 4, 3, 4, 0.25F);
		this.ammo1 = new ModelRenderer(this, 32, 10);
		this.ammo1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.ammo1.addBox(-2.5F, -2.0F, 1.0F, 5, 4, 7, 0.1F);
		this.leg2 = new ModelRenderer(this, 0, 0);
		this.leg2.setRotationPoint(0.0F, 20.0F, -1.5F);
		this.leg2.addBox(-1.0F, -0.5F, -0.3F, 1, 1, 7, 0.0F);
		this.setRotateAngle(leg2, -0.6981317007977318F, 2.530727415391778F, 0.045553093477052F);
		this.ammo2 = new ModelRenderer(this, 32, 10);
		this.ammo2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.ammo2.addBox(-7F, -2.0F, -3.5F, 5, 4, 7, 0.1F);
		this.setRotateAngle(ammo2, 0, 1.570796F, 0F);
		this.muzzle = new ModelRenderer(this, 0, 17);
		this.muzzle.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.muzzle.addBox(0.0F, 0.0F, -4.0F, 1, 1, 1, 0.1F);
		this.head.addChild(this.ammo1);
		this.head.addChild(this.ammo2);
		this.head.addChild(this.muzzle);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		this.leg1.render(f5);
		this.leg3.render(f5);
		this.main.render(f5);
		this.head.render(f5);
		this.leg2.render(f5);
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
	public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_,
			float p_78087_5_, float p_78087_6_, Entity entityIn) {
		this.head.rotateAngleY = p_78087_4_ / (180F / (float) Math.PI);
		this.head.rotateAngleX = p_78087_5_ / (180F / (float) Math.PI);
	}
}
