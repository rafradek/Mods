package rafradek.TF2weapons.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import rafradek.TF2weapons.TF2weapons;

public class ClientRender {

	public static ResourceLocation ATTACK_DIR_ICON = new ResourceLocation(TF2weapons.MOD_ID,"textures/misc/robot.png");
	public static ResourceLocation VAC_EFFECT = new ResourceLocation(TF2weapons.MOD_ID, "textures/misc/vac_effect.png");
	public static ResourceLocation VAC_SHIELD = new ResourceLocation(TF2weapons.MOD_ID, "textures/misc/vac_shield.png");
	public static void renderAttackDirection(float attackdir) {
		GlStateManager.pushMatrix();
        Minecraft.getMinecraft().getTextureManager().bindTexture(ATTACK_DIR_ICON);
        Vec3d forward = new Vec3d(1,0,0).rotateYaw((float) (attackdir-Math.PI*0.5));
        GlStateManager.translate((float)forward.x, (float)Minecraft.getMinecraft().player.getEyeHeight(), (float)forward.z);
        GlStateManager.scale(0.07f, 0.07f, 0.07f);
        GlStateManager.disableLighting();
        renderIconWorld(0f,0f,1f,1f,1,1);
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
	}
	
	public static void renderIconWorld(TextureAtlasSprite sprite) {
		renderIconWorld(sprite.getMinU(), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV(),sprite.getIconWidth(),sprite.getIconHeight());
	}
	
	public static void renderIconWorld(double minu, double minv, double maxu, double maxv,double xsize, double ysize) {
		GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.rotate(180.0F - Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float)(Minecraft.getMinecraft().getRenderManager().options.thirdPersonView == 2 ? -1 : 1) *
        		-Minecraft.getMinecraft().getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);

        double xsizeh = xsize/2D;
        double ysizeh = ysize/2D;
        
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        bufferbuilder.pos(-xsizeh, -ysizeh, 0.0D).tex(minu, maxv).normal(0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.pos(xsizeh, -ysizeh, 0.0D).tex(maxu, maxv).normal(0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.pos(xsizeh, ysizeh, 0.0D).tex(maxu, minv).normal(0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.pos(-xsizeh, ysizeh, 0.0D).tex(minu, minv).normal(0.0F, 1.0F, 0.0F).endVertex();
        tessellator.draw();


        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
	}
	
	public static void renderAABBTexture(AxisAlignedBB boundingBox, double minu, double minv, double maxu, double maxv)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        bufferbuilder.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).tex(minu, maxv).normal(0.0F, 0.0F, -1.0F).endVertex();
        bufferbuilder.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).tex(maxu, maxv).normal(0.0F, 0.0F, -1.0F).endVertex();
        bufferbuilder.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).tex(maxu, minv).normal(0.0F, 0.0F, -1.0F).endVertex();
        bufferbuilder.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).tex(minu, minv).normal(0.0F, 0.0F, -1.0F).endVertex();
        bufferbuilder.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).tex(minu, maxv).normal(0.0F, 0.0F, 1.0F).endVertex();
        bufferbuilder.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).tex(maxu, maxv).normal(0.0F, 0.0F, 1.0F).endVertex();
        bufferbuilder.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).tex(maxu, minv).normal(0.0F, 0.0F, 1.0F).endVertex();
        bufferbuilder.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).tex(minu, minv).normal(0.0F, 0.0F, 1.0F).endVertex();
        bufferbuilder.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).tex(minu, maxv).normal(0.0F, -1.0F, 0.0F).endVertex();
        bufferbuilder.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).tex(maxu, maxv).normal(0.0F, -1.0F, 0.0F).endVertex();
        bufferbuilder.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).tex(maxu, minv).normal(0.0F, -1.0F, 0.0F).endVertex();
        bufferbuilder.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).tex(minu, minv).normal(0.0F, -1.0F, 0.0F).endVertex();
        bufferbuilder.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).tex(minu, maxv).normal(0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).tex(maxu, maxv).normal(0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).tex(maxu, minv).normal(0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).tex(minu, minv).normal(0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).tex(minu, maxv).normal(-1.0F, 0.0F, 0.0F).endVertex();
        bufferbuilder.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).tex(maxu, maxv).normal(-1.0F, 0.0F, 0.0F).endVertex();
        bufferbuilder.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).tex(maxu, minv).normal(-1.0F, 0.0F, 0.0F).endVertex();
        bufferbuilder.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).tex(minu, minv).normal(-1.0F, 0.0F, 0.0F).endVertex();
        bufferbuilder.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).tex(minu, maxv).normal(1.0F, 0.0F, 0.0F).endVertex();
        bufferbuilder.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).tex(maxu, maxv).normal(1.0F, 0.0F, 0.0F).endVertex();
        bufferbuilder.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).tex(maxu, minv).normal(1.0F, 0.0F, 0.0F).endVertex();
        bufferbuilder.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).tex(minu, minv).normal(1.0F, 0.0F, 0.0F).endVertex();
        tessellator.draw();
    }
}
