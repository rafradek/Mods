package rafradek.TF2weapons.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelBomb - rafradek
 * Created using Tabula 5.1.0
 */
public class ModelBomb extends ModelBase {
    public ModelRenderer bomb1;
    public ModelRenderer bomb2;
    public ModelRenderer bomb3;
    public ModelRenderer bomb4;
    public ModelRenderer bomb5;

    public ModelBomb() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.bomb2 = new ModelRenderer(this, 0, 0);
        this.bomb2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bomb2.addBox(-3.0F, -4.0F, -3.0F, 6, 8, 6, 0.0F);
        this.bomb1 = new ModelRenderer(this, 0, 0);
        this.bomb1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bomb1.addBox(-4.0F, -3.0F, -3.0F, 8, 6, 6, 0.0F);
        this.bomb5 = new ModelRenderer(this, 0, 0);
        this.bomb5.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bomb5.addBox(-0.5F, -7.0F, -1.3F, 1, 3, 1, 0.0F);
        this.setRotateAngle(bomb5, -0.22759093446006054F, 0.0F, 0.0F);
        this.bomb3 = new ModelRenderer(this, 0, 0);
        this.bomb3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bomb3.addBox(-3.0F, -3.0F, -4.0F, 6, 6, 8, 0.0F);
        this.bomb4 = new ModelRenderer(this, 0, 9);
        this.bomb4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bomb4.addBox(-1.0F, -5.0F, -1.0F, 2, 1, 2, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.bomb2.render(f5);
        this.bomb1.render(f5);
        this.bomb5.render(f5);
        this.bomb3.render(f5);
        this.bomb4.render(f5);
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
