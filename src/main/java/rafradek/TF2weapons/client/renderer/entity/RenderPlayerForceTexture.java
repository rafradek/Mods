package rafradek.TF2weapons.client.renderer.entity;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.ResourceLocation;
import rafradek.TF2weapons.TF2PlayerCapability;
import rafradek.TF2weapons.common.WeaponsCapability;
import rafradek.TF2weapons.util.TF2Util;

public class RenderPlayerForceTexture extends RenderPlayer {

	public RenderPlayerForceTexture(RenderManager renderManager) {
		super(renderManager);
	}

	public RenderPlayerForceTexture(RenderManager renderManager, boolean useSmallArms) {
		super(renderManager, useSmallArms);
	}

	@Override
	public ResourceLocation getEntityTexture(final AbstractClientPlayer entity) {

		if (TF2PlayerCapability.get(entity).isForceClassTexture()
				&& WeaponsCapability.get(entity).getUsedToken() >= 0) {
			int clazz = WeaponsCapability.get(entity).getUsedToken();
			if (TF2Util.getTeamForDisplay(entity) == 0) {
				return RenderTF2Character.RED_TEXTURES[clazz];
			} else if (TF2Util.getTeamForDisplay(entity) == 1) {
				return RenderTF2Character.BLU_TEXTURES[clazz];
			}
		}
		return RenderPlayer.class.cast(this.renderManager.getEntityRenderObject(entity)).getEntityTexture(entity);
	}
}
