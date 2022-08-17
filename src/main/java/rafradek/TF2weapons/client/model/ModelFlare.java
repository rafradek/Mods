package rafradek.TF2weapons.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelFlare - Undefined Created using Tabula 5.1.0
 */
public class ModelFlare extends ModelBase {
	public ModelRenderer shape1;

	public ModelFlare() {
		this.textureWidth = 16;
		this.textureHeight = 16;
		this.shape1 = new ModelRenderer(this, 0, 0);
		this.shape1.setRotationPoint(-2.5F, -1.0F, -1.0F);
		this.shape1.addBox(0.0F, 0.0F, 0.0F, 5, 2, 2, 0.0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		this.shape1.render(f5);
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
