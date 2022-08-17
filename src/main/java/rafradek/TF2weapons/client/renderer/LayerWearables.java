package rafradek.TF2weapons.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.entity.mercenary.EntityTF2Character;
import rafradek.TF2weapons.inventory.InventoryWearables;
import rafradek.TF2weapons.item.ItemAmmoBelt;
import rafradek.TF2weapons.item.ItemFromData;
import rafradek.TF2weapons.item.ItemWearable;
import rafradek.TF2weapons.util.PropertyType;

@SideOnly(Side.CLIENT)
public class LayerWearables implements LayerRenderer<EntityLivingBase> {
	public final ModelBiped modelBig;
	public final ModelBiped modelMedium;
	public final ModelBiped modelSmall;
	public RenderLivingBase<?> renderer;

	public LayerWearables(RenderLivingBase<?> render) {
		this.renderer = render;
		this.modelBig = new ModelBiped(1.15F);
		this.modelMedium = new ModelBiped(0.75F);
		this.modelSmall = new ModelBiped(0.25F);
	}

	@Override
	public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount,
			float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		// System.out.println("Rendering layer");
		InventoryWearables inventory = entitylivingbaseIn.getCapability(TF2weapons.INVENTORY_CAP, null);
		if (inventory != null) {
			for (int i = 0; i < 4; i++) {
				ItemStack stack = inventory.getStackInSlot(i);
				if (!stack.isEmpty())
					renderModel(entitylivingbaseIn, stack, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw,
							headPitch, scale);
			}
		}
		if (entitylivingbaseIn instanceof EntityTF2Character) {
			ItemStackHandler loadout = ((EntityTF2Character)entitylivingbaseIn).loadout;
			for (int i = 0; i < loadout.getSlots(); i++) {
				ItemStack stack = loadout.getStackInSlot(i);
				if (!stack.isEmpty())
					renderModel(entitylivingbaseIn, stack, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw,
							headPitch, scale);
			}
		}
		for (ItemStack stack : entitylivingbaseIn.getArmorInventoryList())
			if (!stack.isEmpty())
				renderModel(entitylivingbaseIn, stack, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw,
						headPitch, scale);
	}

	public void renderModel(EntityLivingBase living, ItemStack stack, float limbSwing,
			float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch,
			float scale) {
		if (stack.getItem() instanceof ItemFromData) {

			Minecraft minecraft = Minecraft.getMinecraft();
			int visibility = ((ItemFromData)stack.getItem()).getVisibilityFlags(stack, living);
			if ((visibility & 1) == 1) {
				GlStateManager.pushMatrix();

				if (living.isSneaking())
					GlStateManager.translate(0.0F, 0.2F, 0.0F);
				getHead().postRender(0.0625F);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

				GlStateManager.translate(0.0F, -0.25F, 0.0F);
				GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.scale(0.65F, -0.65F, -0.65F);

				ItemWearable.usedModel = 2;
				minecraft.getItemRenderer().renderItem(living, stack,
						ItemCameraTransforms.TransformType.HEAD);
				GlStateManager.popMatrix();
			}

			if ((visibility & 2) == 2) {
				GlStateManager.pushMatrix();
				GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
				if (living.isSneaking())
					GlStateManager.translate(0.0F, -0.2F, 0.0F);
				this.getBody().postRender(0);
				
				ItemWearable.usedModel = 1;
				minecraft.getItemRenderer().renderItem(living, stack,
						ItemCameraTransforms.TransformType.HEAD);
				GlStateManager.popMatrix();
			}

			if (!ItemFromData.getData(stack).getString(PropertyType.ARMOR_IMAGE).isEmpty()) {
				this.renderer
						.bindTexture(this.getArmorResource(living, stack, EntityEquipmentSlot.CHEST, null));
				ModelBase model = this.modelBig;
				model.setModelAttributes(this.renderer.getMainModel());
				model.setLivingAnimations(living, limbSwing, limbSwingAmount, partialTicks);
				model.render(living, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
			}
			ItemWearable.usedModel = 0;
		}
		if (stack.getItem() instanceof ItemAmmoBelt) {
			this.renderer
					.bindTexture(this.getArmorResource(living, stack, EntityEquipmentSlot.CHEST, null));
			ModelBase model = this.modelBig;
			model.setModelAttributes(this.renderer.getMainModel());
			model.setLivingAnimations(living, limbSwing, limbSwingAmount, partialTicks);
			model.render(living, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		}
	}

	public ResourceLocation getArmorResource(net.minecraft.entity.Entity entity, ItemStack stack,
			EntityEquipmentSlot slot, String type) {
		return new ResourceLocation(
				net.minecraftforge.client.ForgeHooksClient.getArmorTexture(entity, stack, "", slot, type));
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
	
	private ModelRenderer getHead() {
		return ((ModelBiped)this.renderer.getMainModel()).bipedHead;
	}
	
	private ModelRenderer getBody() {
		return ((ModelBiped)this.renderer.getMainModel()).bipedBody;
	}
}