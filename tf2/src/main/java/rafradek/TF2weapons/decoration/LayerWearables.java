package rafradek.TF2weapons.decoration;

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
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.characters.EntityTF2Character;
import rafradek.TF2weapons.weapons.ItemAmmoBelt;

@SideOnly(Side.CLIENT)
public class LayerWearables implements LayerRenderer<EntityLivingBase> {
	private final ModelRenderer head;
	private final ModelRenderer body;
	public final ModelBiped modelBig;
	public final ModelBiped modelMedium;
	public final ModelBiped modelSmall;
	public RenderLivingBase<?> renderer;

	public LayerWearables(RenderLivingBase<?> render, ModelBiped model) {
		this.renderer = render;
		this.head = model.bipedHead;
		this.body = model.bipedBody;
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

	public void renderModel(EntityLivingBase entitylivingbaseIn, ItemStack stack, float limbSwing,
			float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch,
			float scale) {
		if (stack.getItem() instanceof ItemFromData) {

			Minecraft minecraft = Minecraft.getMinecraft();
			int visibility = ((ItemFromData)stack.getItem()).getVisibilityFlags(stack, entitylivingbaseIn);
			if ((visibility & 1) == 1) {
				GlStateManager.pushMatrix();

				if (entitylivingbaseIn.isSneaking())
					GlStateManager.translate(0.0F, 0.2F, 0.0F);
				this.head.postRender(0.0625F);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

				GlStateManager.translate(0.0F, -0.25F, 0.0F);
				GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.scale(0.65F, -0.65F, -0.65F);

				ItemWearable.usedModel = 2;
				minecraft.getItemRenderer().renderItem(entitylivingbaseIn, stack,
						ItemCameraTransforms.TransformType.HEAD);
				GlStateManager.popMatrix();
			}

			if ((visibility & 2) == 2) {
				GlStateManager.pushMatrix();
				GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
				ItemWearable.usedModel = 1;
				minecraft.getItemRenderer().renderItem(entitylivingbaseIn, stack,
						ItemCameraTransforms.TransformType.FIXED);
				GlStateManager.popMatrix();
			}

			if (!ItemFromData.getData(stack).getString(PropertyType.ARMOR_IMAGE).isEmpty()) {
				this.renderer
						.bindTexture(this.getArmorResource(entitylivingbaseIn, stack, EntityEquipmentSlot.CHEST, null));
				ModelBase model = this.modelBig;
				model.setModelAttributes(this.renderer.getMainModel());
				model.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
				model.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
			}
			ItemWearable.usedModel = 0;
		}
		if (stack.getItem() instanceof ItemAmmoBelt) {
			this.renderer
					.bindTexture(this.getArmorResource(entitylivingbaseIn, stack, EntityEquipmentSlot.CHEST, null));
			ModelBase model = this.modelBig;
			model.setModelAttributes(this.renderer.getMainModel());
			model.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
			model.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
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
}