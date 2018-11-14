package rafradek.TF2weapons.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.ResourceLocation;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.common.WeaponsCapability;
import rafradek.TF2weapons.util.TF2Util;

public class RenderPlayerDisguised extends RenderPlayer {

	public RenderPlayerDisguised(RenderManager renderManager) {
		super(renderManager);
		// TODO Auto-generated constructor stub
	}

	public RenderPlayerDisguised(RenderManager renderManager, boolean useSmallArms) {
		super(renderManager, useSmallArms);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ResourceLocation getEntityTexture(final AbstractClientPlayer entity) {

		return entity.getCapability(TF2weapons.WEAPONS_CAP, null).skinDisguise != null
				? entity.getCapability(TF2weapons.WEAPONS_CAP, null).skinDisguise
				: DefaultPlayerSkin.getDefaultSkin(entity.getUniqueID());
	}

	@Override
	protected int getTeamColor(AbstractClientPlayer entityIn) {
		int i = 16777215;
		ScorePlayerTeam scoreplayerteam = entityIn.world.getScoreboard()
				.getPlayersTeam(WeaponsCapability.get(entityIn).getDisguiseType().substring(2));

		if (scoreplayerteam != null) {
			String s = FontRenderer.getFormatFromString(scoreplayerteam.getPrefix());

			if (s.length() >= 2)
				i = this.getFontRendererFromRenderManager().getColorCode(s.charAt(1));
		}

		return i;
	}

	/*@Override
	protected void renderEntityName(AbstractClientPlayer entityIn, double x, double y, double z, String name,
			double p_188296_9_) {
		String username = entityIn.getDataManager().get(TF2EventsCommon.ENTITY_DISGUISE_TYPE).substring(2);

		if (p_188296_9_ < 100.0D) {
			Scoreboard scoreboard = entityIn.getWorldScoreboard();
			ScoreObjective scoreobjective = scoreboard.getObjectiveInDisplaySlot(2);

			if (scoreobjective != null) {
				Score score = scoreboard.getOrCreateScore(username, scoreobjective);
				this.renderLivingLabel(entityIn, score.getScorePoints() + " " + scoreobjective.getDisplayName(), x, y,
						z, 64);
				y += this.getFontRendererFromRenderManager().FONT_HEIGHT * 1.15F * 0.025F;
			}
		}
		if (TF2weapons.isOnSameTeam(entityIn, Minecraft.getMinecraft().player))
			super.renderEntityName(entityIn, x, y, z, name + " [" + username + "]", p_188296_9_);
		else
			super.renderEntityName(entityIn, x, y, z, username, p_188296_9_);

	}*/

	@Override
	protected boolean canRenderName(AbstractClientPlayer entity) {
		EntityPlayerSP entityplayersp = Minecraft.getMinecraft().player;
		if (TF2Util.isOnSameTeam(entity, entityplayersp))
			return super.canRenderName(entity);
		else {
			boolean flag = !entity.isInvisibleToPlayer(entityplayersp);

			if (entity != entityplayersp) {
				Team team = Minecraft.getMinecraft().world.getScoreboard().getPlayersTeam(
						WeaponsCapability.get(entity).getDisguiseType().substring(2));
				Team team1 = entityplayersp.getTeam();

				if (team != null) {
					Team.EnumVisible team$enumvisible = team.getNameTagVisibility();

					switch (team$enumvisible) {
					case ALWAYS:
						return flag;
					case NEVER:
						return false;
					case HIDE_FOR_OTHER_TEAMS:
						return team1 == null ? flag
								: team.isSameTeam(team1) && (team.getSeeFriendlyInvisiblesEnabled() || flag);
					case HIDE_FOR_OWN_TEAM:
						return team1 == null ? flag : !team.isSameTeam(team1) && flag;
					default:
						return true;
					}
				}
			}
			return Minecraft.isGuiEnabled() && entity != this.renderManager.renderViewEntity && flag
					&& !entity.isBeingRidden();
		}
	}
}
