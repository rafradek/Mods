package rafradek.TF2weapons.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelRocket extends ModelBase {
	// fields
	ModelRenderer bigger;
	ModelRenderer longer;

	public ModelRocket() {
		textureWidth = 32;
		textureHeight = 32;

		bigger = new ModelRenderer(this, 0, 0);
		bigger.addBox(0F, 0F, 0F, 3, 2, 2);
		bigger.setRotationPoint(-1F, -1F, -1F);
		bigger.setTextureSize(32, 32);
		bigger.mirror = true;
		longer = new ModelRenderer(this, 0, 4);
		longer.addBox(0F, 0F, 0F, 7, 1, 1);
		longer.setRotationPoint(-4F, -0.5F, -0.5F);
		longer.setTextureSize(32, 32);
		longer.mirror = true;
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		bigger.render(f5);
		longer.render(f5);
	}

}