package rafradek.TF2weapons.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.client.ClientProxy;
import rafradek.TF2weapons.client.TF2EventsClient;
import rafradek.TF2weapons.client.model.ModelHeavy;
import rafradek.TF2weapons.client.model.ModelTF2Character;
import rafradek.TF2weapons.client.renderer.LayerArmorTint;
import rafradek.TF2weapons.client.renderer.LayerWearables;
import rafradek.TF2weapons.common.WeaponsCapability;
import rafradek.TF2weapons.entity.mercenary.EntityHeavy;
import rafradek.TF2weapons.entity.mercenary.EntityTF2Character;
import rafradek.TF2weapons.item.ItemMeleeWeapon;
import rafradek.TF2weapons.item.ItemToken;
import rafradek.TF2weapons.util.TF2Util;

public class RenderTF2Character extends RenderBiped<EntityTF2Character> {

	private static final ResourceLocation HEAVY_RED = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/red/Heavy.png");
	private static final ResourceLocation HEAVY_BLU = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/blu/Heavy.png");
	private static final ResourceLocation HEAVY_ROBOT = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/robot/Heavy.png");
	private static final ResourceLocation SCOUT_RED = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/red/Scout.png");
	private static final ResourceLocation SCOUT_BLU = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/blu/Scout.png");
	private static final ResourceLocation SCOUT_ROBOT = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/robot/Scout.png");
	private static final ResourceLocation SNIPER_RED = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/red/Sniper.png");
	private static final ResourceLocation SNIPER_BLU = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/blu/Sniper.png");
	private static final ResourceLocation SNIPER_ROBOT = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/robot/Sniper.png");
	private static final ResourceLocation SOLDIER_RED = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/red/Soldier.png");
	private static final ResourceLocation SOLDIER_BLU = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/blu/Soldier.png");
	private static final ResourceLocation SOLDIER_ROBOT = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/robot/Soldier.png");
	private static final ResourceLocation DEMOMAN_RED = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/red/Demoman.png");
	private static final ResourceLocation DEMOMAN_BLU = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/blu/Demoman.png");
	private static final ResourceLocation DEMOMAN_ROBOT = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/robot/Demoman.png");
	private static final ResourceLocation PYRO_RED = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/red/Pyro.png");
	private static final ResourceLocation PYRO_BLU = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/blu/Pyro.png");
	private static final ResourceLocation PYRO_ROBOT = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/robot/Pyro.png");
	private static final ResourceLocation MEDIC_RED = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/red/Medic.png");
	private static final ResourceLocation MEDIC_BLU = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/blu/Medic.png");
	private static final ResourceLocation MEDIC_ROBOT = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/robot/Medic.png");
	private static final ResourceLocation SPY_RED = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/red/Spy.png");
	private static final ResourceLocation SPY_BLU = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/blu/Spy.png");
	private static final ResourceLocation SPY_ROBOT = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/robot/Spy.png");
	private static final ResourceLocation ENGINEER_RED = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/red/Engineer.png");
	private static final ResourceLocation ENGINEER_BLU = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/blu/Engineer.png");
	private static final ResourceLocation ENGINEER_ROBOT = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/robot/Engineer.png");

	public ModelBiped modelHeavy = new ModelHeavy();
	public ModelBiped modelMain;

	public RenderTF2Character(RenderManager renderManager) {
		super(renderManager, new ModelTF2Character(), 0.5F);
		this.modelMain=(ModelBiped) this.mainModel;
		this.addLayer(new LayerHeldItem(this));
		this.addLayer(new LayerArmorTint(this));
		this.addLayer(new LayerWearables(this));
		this.layerRenderers.removeIf(layer -> (LayerRenderer<?>)layer instanceof LayerCustomHead);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityTF2Character par1EntityLiving) {
		//String clazz = null;
		int clazz;
		boolean sameTeam=Minecraft.getMinecraft().player != null && TF2Util.isOnSameTeam(Minecraft.getMinecraft().player, par1EntityLiving);
		if ( !sameTeam && WeaponsCapability.get(par1EntityLiving).isDisguised()
				&& WeaponsCapability.get(par1EntityLiving).getDisguiseType().startsWith("T:"))
			clazz= ItemToken.getClassID(WeaponsCapability.get(par1EntityLiving).getDisguiseType().substring(2).toLowerCase());
		else
			clazz = par1EntityLiving.getClassIndex();
		// System.out.println("class: "+clazz);
		if (par1EntityLiving.getEntTeam() == 2 && !WeaponsCapability.get(par1EntityLiving).isDisguised()) {
			switch (clazz) {
			case 0:
				return SCOUT_ROBOT;
			case 1:
				return SOLDIER_ROBOT;
			case 2:
				return PYRO_ROBOT;
			case 3:
				return DEMOMAN_ROBOT;
			case 4:
				return HEAVY_ROBOT;
			case 5:
				return ENGINEER_ROBOT;
			case 6:
				return MEDIC_ROBOT;
			case 7:
				return SNIPER_ROBOT;
			case 8:
				return SPY_ROBOT;
			}
		}
		else if (par1EntityLiving.getEntTeam() == 0 || (!sameTeam && par1EntityLiving.getEntTeam() == 1
				&& WeaponsCapability.get(par1EntityLiving).isDisguised()))
			switch (clazz) {
			case 0:
				return SCOUT_RED;
			case 1:
				return SOLDIER_RED;
			case 2:
				return PYRO_RED;
			case 3:
				return DEMOMAN_RED;
			case 4:
				return HEAVY_RED;
			case 5:
				return ENGINEER_RED;
			case 6:
				return MEDIC_RED;
			case 7:
				return SNIPER_RED;
			case 8:
				return SPY_RED;
			}
		else
			switch (clazz) {
			case 0:
				return SCOUT_BLU;
			case 1:
				return SOLDIER_BLU;
			case 2:
				return PYRO_BLU;
			case 3:
				return DEMOMAN_BLU;
			case 4:
				return HEAVY_BLU;
			case 5:
				return ENGINEER_BLU;
			case 6:
				return MEDIC_BLU;
			case 7:
				return SNIPER_BLU;
			case 8:
				return SPY_BLU;
			}
		return HEAVY_BLU;
	}

	@Override
	public void doRender(EntityTF2Character living, double p_76986_2_, double p_76986_4_, double p_76986_6_,
			float p_76986_8_, float p_76986_9_) {
		this.setModel(living);
		boolean sniperZoomed = false;
		if (living.getHeldItemMainhand() != null
				&& !(living.getHeldItemMainhand().getItem() instanceof ItemMeleeWeapon)) {
			sniperZoomed = living.getCapability(TF2weapons.WEAPONS_CAP, null).isCharging();
			// System.out.println("pos: "+p_76986_2_+" "+p_76986_4_+"
			// "+p_76986_8_);
			((ModelBiped)this.mainModel).rightArmPose = ((living.getCapability(TF2weapons.WEAPONS_CAP, null).state & 3) > 0)
					|| sniperZoomed? ModelBiped.ArmPose.BOW_AND_ARROW : ModelBiped.ArmPose.EMPTY;
		}
		super.doRender(living, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
		if (living.isEntityAlive()) {
			TF2EventsClient.renderBeam(living, p_76986_9_, 0.04f);
			if (sniperZoomed) {
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder renderer = tessellator.getBuffer();
				GlStateManager.pushMatrix();
				GlStateManager.translate((float) p_76986_2_, (float) p_76986_4_ + living.getEyeHeight(),
						(float) p_76986_6_);
				GL11.glRotatef(
						(living.prevRotationYawHead
								+ (living.rotationYawHead - living.prevRotationYawHead) * p_76986_9_) * -1,
						0.0F, 1.0F, 0.0F);
				GL11.glRotatef(
						(living.prevRotationPitch + (living.rotationPitch - living.prevRotationPitch) * p_76986_9_),
						1.0F, 0.0F, 0.0F);
				GlStateManager.disableTexture2D();
				GlStateManager.disableLighting();
				GL11.glEnable(GL11.GL_BLEND);
				OpenGlHelper.glBlendFunc(770, 771, 1, 0);
				ClientProxy.setColor(TF2Util.getTeamColor(living), 0.28f, 0, 0f, 1f);
				renderer.begin(7, DefaultVertexFormats.POSITION);
				renderer.pos(-0.03, -0.03, 0).endVertex();
				renderer.pos(0.03, 0.03, 0).endVertex();
				renderer.pos(0.03, 0.03, 50).endVertex();
				renderer.pos(-0.03, -0.03, 50).endVertex();
				tessellator.draw();
				renderer.begin(7, DefaultVertexFormats.POSITION);
				renderer.pos(-0.03, -0.03, 50).endVertex();
				renderer.pos(0.03, 0.03, 50).endVertex();
				renderer.pos(0.03, 0.03, 0).endVertex();
				renderer.pos(-0.03, -0.03, 0).endVertex();
				tessellator.draw();
				renderer.begin(7, DefaultVertexFormats.POSITION);
				renderer.pos(0.03, -0.03, 0).endVertex();
				renderer.pos(-0.03, 0.03, 0).endVertex();
				renderer.pos(-0.03, 0.03, 50).endVertex();
				renderer.pos(0.03, -0.03, 50).endVertex();
				tessellator.draw();
				renderer.begin(7, DefaultVertexFormats.POSITION);
				renderer.pos(0.03, -0.03, 50).endVertex();
				renderer.pos(-0.03, 0.03, 50).endVertex();
				renderer.pos(-0.03, 0.03, 0).endVertex();
				renderer.pos(0.03, -0.03, 0).endVertex();
				tessellator.draw();
				/*
				 * renderer.startDrawingQuads(); renderer.addVertex(-0.03,
				 * p_76986_1_.getEyeHeight()-0.03,0); renderer.addVertex(0.03,
				 * p_76986_1_.getEyeHeight()+0.03,0);
				 * renderer.addVertex(lookVec.x*50+0.03,lookVec.y*64+
				 * p_76986_1_.getEyeHeight()+0.03, lookVec.z*64);
				 * renderer.addVertex(lookVec.x*50-0.03,lookVec.y*64+
				 * p_76986_1_.getEyeHeight()-0.03, lookVec.z*64);
				 * tessellator.draw();
				 */
				/*
				 * renderer.startDrawingQuads(); renderer.addVertex(-0.03,
				 * p_76986_1_.getEyeHeight()-0.03, -0.03);
				 * renderer.addVertex(0.03, p_76986_1_.getEyeHeight()+0.03,
				 * +0.03);
				 * renderer.addVertex(lookVec.x*50+0.03,lookVec.y*64+
				 * p_76986_1_.getEyeHeight()+0.03, lookVec.z*64+0.03);
				 * renderer.addVertex(lookVec.x*50-0.03,lookVec.y*64+
				 * p_76986_1_.getEyeHeight()-0.03, lookVec.z*64-0.03);
				 * tessellator.draw(); renderer.startDrawingQuads();
				 * renderer.addVertex(0.03, p_76986_1_.getEyeHeight()-0.03,
				 * 0.03); renderer.addVertex(-0.03,
				 * p_76986_1_.getEyeHeight()+0.03, -0.03);
				 * renderer.addVertex(lookVec.x*50-0.03,lookVec.y*64+
				 * p_76986_1_.getEyeHeight()+0.03, lookVec.z*64-0.03);
				 * renderer.addVertex(lookVec.x*50+0.03,lookVec.y*64+
				 * p_76986_1_.getEyeHeight()-0.03, lookVec.z*64+0.03);
				 * tessellator.draw();
				 */
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1F);
				GL11.glDisable(GL11.GL_BLEND);
				GlStateManager.enableTexture2D();
				GlStateManager.enableLighting();
				GlStateManager.popMatrix();
			}
		}
	}
	@Override
	protected void preRenderCallback(EntityTF2Character entitylivingbaseIn, float partialTickTime)
	{
		float f = 0.9375F;
		if(entitylivingbaseIn instanceof EntityHeavy)
			f = 1f;
		if (entitylivingbaseIn.getRobotSize() == 2)
			f *= 1.75f;
		else if (entitylivingbaseIn.getRobotSize() == 3)
			f *= 2f;
		GlStateManager.scale(f, f, f);
	}
	private void setModel(EntityLivingBase living) {
		if (living instanceof EntityHeavy) {
			this.mainModel = this.modelHeavy;
		} else {
			this.mainModel = this.modelMain;
		}
		/*
		 * if(living.getEntityData().getByte("Disguised")!=0){ String
		 * mobType=living.getEntityData().getString("DisguiseType");
		 * this.mainModel=ClientProxy.entityModel.get(mobType); }
		 */
	}
}
