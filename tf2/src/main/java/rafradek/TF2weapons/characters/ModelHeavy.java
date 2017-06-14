package rafradek.TF2weapons.characters;

import net.minecraft.client.model.ModelBiped;

public class ModelHeavy extends ModelBiped {

	public ModelHeavy() {
		super(0.0f, 0.0f, 64, 32);
		this.bipedBody.cubeList.clear();
		this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.5f);
		this.bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
	}
}
