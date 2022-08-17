package rafradek.TF2weapons.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelOverheadDoor extends ModelBase {

	public ModelRenderer roller;
	public ModelRenderer door;
	public ModelOverheadDoor() {
		roller = new ModelRenderer(this, 0, 0).setTextureSize(32, 16);
		door = new ModelRenderer(this, 16, 0).setTextureSize(32, 16);
	}
}
