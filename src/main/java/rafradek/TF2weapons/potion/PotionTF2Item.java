package rafradek.TF2weapons.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionTF2Item extends Potion {

	public ResourceLocation texture;

	public PotionTF2Item(boolean isBadEffectIn, int liquidColorIn, ResourceLocation texture) {
		super(isBadEffectIn, liquidColorIn);
		this.texture = texture;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
		mc.getTextureManager().bindTexture(texture);
		// mc.ingameGUI.drawTexturedModalRect(x+6,y+7,0,0,16,16);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder renderer = tessellator.getBuffer();
		renderer.begin(7, DefaultVertexFormats.POSITION_TEX);

		renderer.pos(x + 7, y + 23, 0.0D).tex(0.0D, 1D).endVertex();
		renderer.pos(x + 23, y + 23, 0.0D).tex(1.0D, 1D).endVertex();
		renderer.pos(x + 23, y + 7, 0.0D).tex(1.0D, 0.0D).endVertex();
		renderer.pos(x + 7, y + 7, 0.0D).tex(0.0D, 0.0D).endVertex();
		tessellator.draw();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderHUDEffect(int x, int y, PotionEffect effect, net.minecraft.client.Minecraft mc, float alpha) {
		mc.getTextureManager().bindTexture(texture);
		// mc.ingameGUI.drawTexturedModalRect(x+3,y+3,0,0,16,16);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder renderer = tessellator.getBuffer();

		renderer.begin(7, DefaultVertexFormats.POSITION_TEX);

		renderer.pos(x + 4, y + 20, 0.0D).tex(0.0D, 1D).endVertex();
		renderer.pos(x + 20, y + 20, 0.0D).tex(1.0D, 1D).endVertex();
		renderer.pos(x + 20, y + 4, 0.0D).tex(1.0D, 0.0D).endVertex();
		renderer.pos(x + 4, y + 4, 0.0D).tex(0.0D, 0.0D).endVertex();
		tessellator.draw();
	}
}
