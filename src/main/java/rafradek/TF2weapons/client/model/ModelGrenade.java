package rafradek.TF2weapons.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelGrenade extends ModelBase {
	// fields
	ModelRenderer Shape1;
	ModelRenderer Shape2;
	ModelRenderer Shape3;
	ModelRenderer Shape4;

	public ModelGrenade() {
		textureWidth = 32;
		textureHeight = 32;

		Shape1 = new ModelRenderer(this, 0, 0);
		Shape1.addBox(0F, 0F, 0F, 4, 4, 5);
		Shape1.setRotationPoint(-2F, -2F, -1F);
		Shape1.setTextureSize(32, 32);
		Shape2 = new ModelRenderer(this, 0, 9);
		Shape2.addBox(0F, 0F, 0F, 3, 3, 1);
		Shape2.setRotationPoint(-1.5F, -1.5F, 4F);
		Shape2.setTextureSize(32, 32);
		Shape3 = new ModelRenderer(this, 0, 9);
		Shape3.addBox(0F, 0F, 0F, 3, 3, 1);
		Shape3.setRotationPoint(-1.5F, -1.5F, -2F);
		Shape3.setTextureSize(32, 32);
		Shape4 = new ModelRenderer(this, 18, 0);
		Shape4.addBox(0F, 0F, 0F, 4, 4, 1);
		Shape4.setRotationPoint(-2F, -2F, -3F);
		Shape4.setTextureSize(32, 32);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		Shape1.render(f5);
		Shape2.render(f5);
		Shape3.render(f5);
		Shape4.render(f5);
	}
}
