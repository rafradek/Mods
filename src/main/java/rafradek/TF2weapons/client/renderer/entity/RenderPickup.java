package rafradek.TF2weapons.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import rafradek.TF2weapons.entity.EntityPickup;

public class RenderPickup extends Render<EntityPickup> {

	// if you want a model, be sure to add it here:

	public RenderPickup(RenderManager manager) {
		super(manager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityPickup entity) {
		return TextureMap.LOCATION_BLOCKS_TEXTURE;
	}

	@Override
	@SuppressWarnings("deprecation")
	public void doRender(EntityPickup entity, double x, double y, double z, float yaw, float partialTick) {

		if (entity.isDisabled())
			return;
		boolean flag = false;

		RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
		if (this.bindEntityTexture(entity)) {
			this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).setBlurMipmap(false, false);
			flag = true;
		}

		ItemStack model = entity.getType().model;
		GlStateManager.enableRescaleNormal();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableBlend();
		RenderHelper.enableStandardItemLighting();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		GlStateManager.pushMatrix();
		IBakedModel ibakedmodel = itemRenderer.getItemModelWithOverrides(model, entity.world, (EntityLivingBase) null);

		boolean is3d = ibakedmodel.isGui3d();
		float f1 = /* true ? */ MathHelper.sin((entity.age + partialTick) / 10.0F + entity.hoverStart) * 0.1F + 0.1F;// :
																														// 0;
		float f2 = ibakedmodel.getItemCameraTransforms()
				.getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y;
		GlStateManager.translate((float) x, (float) y + f1 + 0.25F * f2, (float) z);

		if (is3d || this.renderManager.options != null) {
			float f3 = ((entity.age + partialTick) / 20.0F + entity.hoverStart) * (180F / (float) Math.PI);
			GlStateManager.rotate(f3, 0.0F, 1.0F, 0.0F);
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		GlStateManager.pushMatrix();

		IBakedModel transformedModel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(ibakedmodel,
				ItemCameraTransforms.TransformType.GROUND, false);
		itemRenderer.renderItem(model, transformedModel);
		GlStateManager.popMatrix();
		GlStateManager.translate(0.0F, 0.0F, 0.09375F);

		GlStateManager.popMatrix();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableBlend();
		this.bindEntityTexture(entity);

		if (flag) {
			this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).restoreLastBlurMipmap();
		}

		super.doRender(entity, x, y, z, yaw, partialTick);
	}

}
