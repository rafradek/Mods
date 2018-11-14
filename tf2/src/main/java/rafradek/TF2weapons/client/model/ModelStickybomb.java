package rafradek.TF2weapons.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelStickybomb extends ModelBase {
	// fields
	ModelRenderer Shape1;
	ModelRenderer Shape2;
	ModelRenderer Shape3;

	public ModelStickybomb() {
		textureWidth = 16;
		textureHeight = 32;

		Shape1 = new ModelRenderer(this, 0, 6);
		Shape1.addBox(0F, 0F, 0F, 3, 3, 5);
		Shape1.setRotationPoint(-1F, -1F, -2F);
		Shape1.setTextureSize(16, 32);
		Shape1.mirror = true;
		Shape2 = new ModelRenderer(this, 0, 14);
		Shape2.addBox(0F, 0F, 0F, 3, 5, 3);
		Shape2.setRotationPoint(-1F, -2F, -1F);
		Shape2.setTextureSize(16, 32);
		Shape2.mirror = true;
		Shape3 = new ModelRenderer(this, 0, 0);
		Shape3.addBox(0F, 0F, 0F, 5, 3, 3);
		Shape3.setRotationPoint(-2F, -1F, -1F);
		Shape3.setTextureSize(16, 32);
		Shape3.mirror = true;
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		Shape1.render(f5);
		Shape2.render(f5);
		Shape3.render(f5);
	}
}
