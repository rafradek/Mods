package rafradek.TF2weapons.building;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.message.TF2Message;

public class GuiSentry extends GuiScreen {

	public EntitySentry sentry;
	public GuiButton doneBtn;
	public GuiButton grab;
	public int channel;
	public boolean exit;
	private GuiButton attackEnemyTeamBtn;
	private GuiButton attackOnHurtBtn;
	private GuiButton attackOtherPlayersBtn;
	private GuiButton attackHostileMobsBtn;
	private GuiButton attackFriendlyMobsBtn;
	public int attackFlags;

	public GuiSentry(EntitySentry sentry) {
		this.sentry = sentry;
		this.attackFlags = sentry.getAttackFlags();
	}

	@Override
	public void initGui() {
		this.buttonList.clear();
		this.buttonList.add(this.doneBtn = new GuiButton(0, this.width / 2 + 5, this.height / 2 + 60, 40, 20,
				I18n.format("gui.done", new Object[0])));
		this.buttonList
				.add(this.attackOnHurtBtn = new GuiButton(1, this.width / 2 + 80, this.height / 2 - 60, 25, 20, "no"));
		this.buttonList.add(
				this.attackOtherPlayersBtn = new GuiButton(2, this.width / 2 + 80, this.height / 2 - 35, 25, 20, "no"));
		this.buttonList.add(
				this.attackHostileMobsBtn = new GuiButton(3, this.width / 2 + 80, this.height / 2 - 10, 25, 20, "no"));
		this.buttonList.add(
				this.attackFriendlyMobsBtn = new GuiButton(4, this.width / 2 + 80, this.height / 2 + 15, 25, 20, "no"));
		this.buttonList.add(this.grab = new GuiButton(5, this.width / 2 - 45, this.height / 2 + 60, 40, 20,
				I18n.format("gui.teleporter.drop", new Object[0])));
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.enabled)
			if (button.id == 0) {
				this.mc.displayGuiScreen(null);
				TF2weapons.network.sendToServer(
						new TF2Message.GuiConfigMessage(this.sentry.getEntityId(), (byte) 0, attackFlags));
			} else if (button.id >= 1 && button.id <= 4) {
				attackFlags ^= 1 << (button.id - 1);
				if ((attackFlags & (1 << (button.id - 1))) == 0)
					button.displayString = "no";
				else
					button.displayString = "yes";
			} else if (button.id == 5) {
				this.mc.displayGuiScreen(null);
				TF2weapons.network
						.sendToServer(new TF2Message.GuiConfigMessage(this.sentry.getEntityId(), (byte) 127, 1));
			}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.attackOnHurtBtn.displayString = (attackFlags & 1) == 0 ? "no" : "yes";
		this.attackOtherPlayersBtn.displayString = (attackFlags & 2) == 0 ? "no" : "yes";
		this.attackHostileMobsBtn.displayString = (attackFlags & 4) == 0 ? "no" : "yes";
		this.attackFriendlyMobsBtn.displayString = (attackFlags & 8) == 0 ? "no" : "yes";
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRendererObj, I18n.format("gui.sentry.info", new Object[0]), this.width / 2 - 5,
				20, 16777215);
		this.drawString(this.fontRendererObj, I18n.format("gui.sentry.onhurt", new Object[0]), this.width / 2 - 80,
				this.height / 2 - 50, 16777215);
		this.drawString(this.fontRendererObj, I18n.format("gui.sentry.player", new Object[0]), this.width / 2 - 80,
				this.height / 2 - 25, 16777215);
		this.drawString(this.fontRendererObj, I18n.format("gui.sentry.hostile", new Object[0]), this.width / 2 - 80,
				this.height / 2, 16777215);
		this.drawString(this.fontRendererObj, I18n.format("gui.sentry.friendly", new Object[0]), this.width / 2 - 80,
				this.height / 2 + 25, 16777215);
		super.drawScreen(mouseX, mouseY, partialTicks);

	}
}
