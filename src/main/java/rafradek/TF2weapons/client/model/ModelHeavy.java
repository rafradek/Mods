package rafradek.TF2weapons.client.model;

public class ModelHeavy extends ModelTF2Character {

	public ModelHeavy() {
		super();
		this.bipedBody.cubeList.clear();
		this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.5f);
		this.bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
	}
}
