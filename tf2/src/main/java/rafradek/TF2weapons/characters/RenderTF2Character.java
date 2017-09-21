package rafradek.TF2weapons.characters;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import rafradek.TF2weapons.TF2EventsClient;
import rafradek.TF2weapons.TF2EventsCommon;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.weapons.ItemMeleeWeapon;
import rafradek.TF2weapons.weapons.WeaponsCapability;

public class RenderTF2Character extends RenderBiped<EntityTF2Character> {

	private static final ResourceLocation HEAVY_RED = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/red/Heavy.png");
	private static final ResourceLocation HEAVY_BLU = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/blu/Heavy.png");
	private static final ResourceLocation SCOUT_RED = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/red/Scout.png");
	private static final ResourceLocation SCOUT_BLU = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/blu/Scout.png");
	private static final ResourceLocation SNIPER_RED = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/red/Sniper.png");
	private static final ResourceLocation SNIPER_BLU = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/blu/Sniper.png");
	private static final ResourceLocation SOLDIER_RED = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/red/Soldier.png");
	private static final ResourceLocation SOLDIER_BLU = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/blu/Soldier.png");
	private static final ResourceLocation DEMOMAN_RED = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/red/Demoman.png");
	private static final ResourceLocation DEMOMAN_BLU = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/blu/Demoman.png");
	private static final ResourceLocation PYRO_RED = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/red/Pyro.png");
	private static final ResourceLocation PYRO_BLU = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/blu/Pyro.png");
	private static final ResourceLocation MEDIC_RED = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/red/Medic.png");
	private static final ResourceLocation MEDIC_BLU = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/blu/Medic.png");
	private static final ResourceLocation SPY_RED = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/red/Spy.png");
	private static final ResourceLocation SPY_BLU = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/blu/Spy.png");
	private static final ResourceLocation ENGINEER_RED = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/red/Engineer.png");
	private static final ResourceLocation ENGINEER_BLU = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/blu/Engineer.png");

	public ModelBiped modelHeavy = new ModelHeavy();
	public ModelBiped modelMain;

	public RenderTF2Character(RenderManager renderManager) {
		super(renderManager, new ModelBiped(), 0.5F);
		this.modelMain=(ModelBiped) this.mainModel;
		this.addLayer(new LayerHeldItem(this));
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityTF2Character par1EntityLiving) {
		String clazz = null;
		boolean sameTeam=Minecraft.getMinecraft().player != null && TF2weapons.isOnSameTeam(Minecraft.getMinecraft().player, par1EntityLiving);
		if ( !sameTeam && WeaponsCapability.get(par1EntityLiving).isDisguised()
				&& WeaponsCapability.get(par1EntityLiving).getDisguiseType().startsWith("T:"))
			clazz = WeaponsCapability.get(par1EntityLiving).getDisguiseType().substring(2);
		else
			clazz = par1EntityLiving.getClass().getSimpleName().substring(6);
		// System.out.println("class: "+clazz);
		if (par1EntityLiving.getEntTeam() == 0 || (!sameTeam && par1EntityLiving.getEntTeam() == 1
				&& WeaponsCapability.get(par1EntityLiving).isDisguised()))
			switch (clazz) {
			case "Heavy":
				return HEAVY_RED;
			case "Scout":
				return SCOUT_RED;
			case "Sniper":
				return SNIPER_RED;
			case "Soldier":
				return SOLDIER_RED;
			case "Demoman":
				return DEMOMAN_RED;
			case "Pyro":
				return PYRO_RED;
			case "Spy":
				return SPY_RED;
			case "Medic":
				return MEDIC_RED;
			case "Engineer":
				return ENGINEER_RED;
			}
		else
			switch (clazz) {
			case "Heavy":
				return HEAVY_BLU;
			case "Scout":
				return SCOUT_BLU;
			case "Sniper":
				return SNIPER_BLU;
			case "Soldier":
				return SOLDIER_BLU;
			case "Demoman":
				return DEMOMAN_BLU;
			case "Pyro":
				return PYRO_BLU;
			case "Spy":
				return SPY_BLU;
			case "Medic":
				return MEDIC_BLU;
			case "Engineer":
				return ENGINEER_BLU;
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
			sniperZoomed = living.getCapability(TF2weapons.WEAPONS_CAP, null).charging;
			// System.out.println("pos: "+p_76986_2_+" "+p_76986_4_+"
			// "+p_76986_8_);
			((ModelBiped)this.mainModel).rightArmPose = ((living.getCapability(TF2weapons.WEAPONS_CAP, null).state & 3) > 0)
					|| sniperZoomed? ModelBiped.ArmPose.BOW_AND_ARROW : ModelBiped.ArmPose.EMPTY;
		}
		super.doRender(living, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
		if (living.isEntityAlive()) {
			TF2EventsClient.renderBeam(living, p_76986_9_);
			if (sniperZoomed) {
				Tessellator tessellator = Tessellator.getInstance();
				VertexBuffer renderer = tessellator.getBuffer();
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
				if (TF2weapons.getTeamForDisplay(living) == 0)
					GL11.glColor4f(1.0F, 0.0F, 0.0F, 0.28F);
				else
					GL11.glColor4f(0.0F, 0.0F, 1.0F, 0.28F);
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
				 * renderer.addVertex(lookVec.xCoord*50+0.03,lookVec.yCoord*64+
				 * p_76986_1_.getEyeHeight()+0.03, lookVec.zCoord*64);
				 * renderer.addVertex(lookVec.xCoord*50-0.03,lookVec.yCoord*64+
				 * p_76986_1_.getEyeHeight()-0.03, lookVec.zCoord*64);
				 * tessellator.draw();
				 */
				/*
				 * renderer.startDrawingQuads(); renderer.addVertex(-0.03,
				 * p_76986_1_.getEyeHeight()-0.03, -0.03);
				 * renderer.addVertex(0.03, p_76986_1_.getEyeHeight()+0.03,
				 * +0.03);
				 * renderer.addVertex(lookVec.xCoord*50+0.03,lookVec.yCoord*64+
				 * p_76986_1_.getEyeHeight()+0.03, lookVec.zCoord*64+0.03);
				 * renderer.addVertex(lookVec.xCoord*50-0.03,lookVec.yCoord*64+
				 * p_76986_1_.getEyeHeight()-0.03, lookVec.zCoord*64-0.03);
				 * tessellator.draw(); renderer.startDrawingQuads();
				 * renderer.addVertex(0.03, p_76986_1_.getEyeHeight()-0.03,
				 * 0.03); renderer.addVertex(-0.03,
				 * p_76986_1_.getEyeHeight()+0.03, -0.03);
				 * renderer.addVertex(lookVec.xCoord*50-0.03,lookVec.yCoord*64+
				 * p_76986_1_.getEyeHeight()+0.03, lookVec.zCoord*64-0.03);
				 * renderer.addVertex(lookVec.xCoord*50+0.03,lookVec.yCoord*64+
				 * p_76986_1_.getEyeHeight()-0.03, lookVec.zCoord*64+0.03);
				 * tessellator.draw();
				 */
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1F);
				GL11.glDisable(GL11.GL_BLEND);
				GlStateManager.enableTexture2D();
				GlStateManager.enableLighting();
				GlStateManager.popMatrix();
			}
		}
	}
	protected void preRenderCallback(EntityTF2Character entitylivingbaseIn, float partialTickTime)
    {
		float f = 0.9375F;
		if(entitylivingbaseIn instanceof EntityHeavy)
			f=1f;
        
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
