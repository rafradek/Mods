package rafradek.TF2weapons.building;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelSentry2 - rafradek Created using Tabula 5.1.0
 */
public class ModelSentry2 extends ModelBase {
	public ModelRenderer main;
	public ModelRenderer leg1;
	public ModelRenderer leg3;
	public ModelRenderer head;
	public ModelRenderer leg2;
	public ModelRenderer leg4;
	public ModelRenderer ammo2;
	public ModelRenderer ammo1;
	public ModelRenderer shape1;
	public ModelRenderer shape2;
	public ModelRenderer gun1base;
	public ModelRenderer gun2base;
	public ModelRenderer gun1barrel1;
	public ModelRenderer gun1barrel2;
	public ModelRenderer gun1barrel3;
	public ModelRenderer gun1barrel4;
	public ModelRenderer gun1barrel6;
	public ModelRenderer gun1barrel5;
	public ModelRenderer gun2barrel1;
	public ModelRenderer gun2barrel2;
	public ModelRenderer gun2barrel3;
	public ModelRenderer gun2barrel4;
	public ModelRenderer gun2barrel6;
	public ModelRenderer gun2barrel5;

	public ModelSentry2() {
		this.textureWidth = 64;
		this.textureHeight = 32;

		this.main = new ModelRenderer(this, 0, 0);
		this.main.setRotationPoint(0.0F, 17.0F, -0.5F);
		this.main.addBox(-0.5F, -3.5F, -0.5F, 1, 8, 1, 0.2F);

		this.ammo2 = new ModelRenderer(this, 32, 0);
		this.ammo2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.ammo2.addBox(-4.0F, -5.5F, 3.0F, 8, 5, 6, 0.0F);

		this.leg4 = new ModelRenderer(this, 0, 0);
		this.leg4.setRotationPoint(0.0F, 19.2F, -0.5F);
		this.leg4.addBox(-0.5F, -6.3F, 5.6F, 1, 1, 2, 0.0F);
		this.setRotateAngle(leg4, -1.2248720690496206F, 0.0F, 0.0F);

		this.leg1 = new ModelRenderer(this, 0, 0);
		this.leg1.setRotationPoint(0.0F, 19.4F, -0.5F);
		this.leg1.addBox(0.0F, -0.5F, -0.3F, 1, 1, 7, 0.0F);
		this.setRotateAngle(leg1, -0.7853981633974483F, -2.530727415391778F, 0.0F);
		this.shape2 = new ModelRenderer(this, 0, 0);
		this.shape2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape2.addBox(-4.5F, -2.5F, -2.0F, 1, 2, 4, 0.0F);
		this.ammo1 = new ModelRenderer(this, 32, 0);
		this.ammo1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.ammo1.addBox(2.0F, -5.5F, -3.0F, 8, 5, 6, 0.0F);
		this.setRotateAngle(ammo1, 0.0F, -1.570796F, 0.0F);

		this.leg2 = new ModelRenderer(this, 0, 0);
		this.leg2.setRotationPoint(0.0F, 19.4F, -0.5F);
		this.leg2.addBox(-1.0F, -0.5F, -0.3F, 1, 1, 7, 0.0F);
		this.setRotateAngle(leg2, -0.7853981633974483F, 2.516764781375823F, 0.045553093477052F);
		this.shape1 = new ModelRenderer(this, 0, 0);
		this.shape1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.shape1.addBox(3.5F, -2.5F, -2.0F, 1, 2, 4, 0.0F);

		this.head = new ModelRenderer(this, 0, 15);
		this.head.setRotationPoint(0.0F, 13.8F, -0.5F);
		this.head.addBox(-4.5F, -0.5F, -2.0F, 9, 1, 4, 0.0F);
		this.gun2base = new ModelRenderer(this, 0, 0);
		this.gun2base.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.gun2base.addBox(2.0F, -5.0F, -3.0F, 4, 4, 5, 0.0F);
		this.gun1base = new ModelRenderer(this, 0, 0);
		this.gun1base.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.gun1base.addBox(-6.0F, -5.0F, -3.0F, 4, 4, 5, 0.0F);
		this.leg3 = new ModelRenderer(this, 0, 0);
		this.leg3.setRotationPoint(0.0F, 19.2F, -0.5F);
		this.leg3.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 9, 0.0F);
		this.setRotateAngle(leg3, -0.44837508483734323F, 0.0F, 0.0F);
		this.gun1barrel1 = new ModelRenderer(this, 0, 0);
		this.gun1barrel1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.gun1barrel1.addBox(-5.3F, -4.8F, -11.0F, 1, 1, 8, 0.0F);
		this.gun1barrel2 = new ModelRenderer(this, 0, 0);
		this.gun1barrel2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.gun1barrel2.addBox(-3.8F, -4.8F, -11.0F, 1, 1, 8, 0.0F);
		this.gun1barrel3 = new ModelRenderer(this, 0, 0);
		this.gun1barrel3.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.gun1barrel3.addBox(-3.3F, -3.5F, -11.0F, 1, 1, 8, 0.0F);
		this.gun1barrel4 = new ModelRenderer(this, 0, 0);
		this.gun1barrel4.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.gun1barrel4.addBox(-5.7F, -3.5F, -11.0F, 1, 1, 8, 0.0F);
		this.gun1barrel5 = new ModelRenderer(this, 0, 0);
		this.gun1barrel5.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.gun1barrel5.addBox(-3.8F, -2.2F, -11.0F, 1, 1, 8, 0.0F);
		this.gun1barrel6 = new ModelRenderer(this, 0, 0);
		this.gun1barrel6.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.gun1barrel6.addBox(-5.3F, -2.2F, -11.0F, 1, 1, 8, 0.0F);

		this.gun2barrel6 = new ModelRenderer(this, 0, 0);
		this.gun2barrel6.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.gun2barrel6.addBox(2.7F, -2.2F, -11.0F, 1, 1, 8, 0.0F);
		this.gun2barrel3 = new ModelRenderer(this, 0, 0);
		this.gun2barrel3.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.gun2barrel3.addBox(4.7F, -3.5F, -11.0F, 1, 1, 8, 0.0F);
		this.gun2barrel2 = new ModelRenderer(this, 0, 0);
		this.gun2barrel2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.gun2barrel2.addBox(4.2F, -4.8F, -11.0F, 1, 1, 8, 0.0F);
		this.gun2barrel4 = new ModelRenderer(this, 0, 0);
		this.gun2barrel4.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.gun2barrel4.addBox(2.3F, -3.5F, -11.0F, 1, 1, 8, 0.0F);
		this.gun2barrel5 = new ModelRenderer(this, 0, 0);
		this.gun2barrel5.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.gun2barrel5.addBox(4.2F, -2.2F, -11.0F, 1, 1, 8, 0.0F);
		this.gun2barrel1 = new ModelRenderer(this, 0, 0);
		this.gun2barrel1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.gun2barrel1.addBox(2.7F, -4.8F, -11.0F, 1, 1, 8, 0.0F);
		this.head.addChild(this.gun1barrel6);
		this.head.addChild(this.gun1barrel3);
		this.head.addChild(this.ammo2);
		this.head.addChild(this.gun1barrel2);
		this.head.addChild(this.gun1barrel4);
		this.head.addChild(this.shape2);
		this.head.addChild(this.ammo1);
		this.head.addChild(this.gun1barrel5);
		this.head.addChild(this.shape1);
		this.head.addChild(this.gun1barrel1);
		this.head.addChild(this.gun2base);
		this.head.addChild(this.gun1base);
		this.head.addChild(this.gun2barrel1);
		this.head.addChild(this.gun2barrel2);
		this.head.addChild(this.gun2barrel3);
		this.head.addChild(this.gun2barrel4);
		this.head.addChild(this.gun2barrel5);
		this.head.addChild(this.gun2barrel6);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		this.main.render(f5);
		this.leg4.render(f5);
		this.leg1.render(f5);
		this.leg2.render(f5);
		this.head.render(f5);
		this.leg3.render(f5);
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
