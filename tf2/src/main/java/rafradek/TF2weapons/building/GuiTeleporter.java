package rafradek.TF2weapons.building;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.message.TF2Message;

public class GuiTeleporter extends GuiScreen {

	public EntityTeleporter teleporter;
	public GuiButton doneBtn;
	public GuiButton teleportUpBtn;
	public GuiButton teleportDownBtn;
	public GuiButton exitToggle;
	public GuiButton grab;
	private GuiTextField teleportField;
	public int channel;
	public boolean exit;

	public GuiTeleporter(EntityTeleporter teleporter) {
		this.teleporter = teleporter;
		this.exit = this.teleporter.isExit();
		this.channel = this.teleporter.getID();
	}

	@Override
	public void initGui() {
		this.buttonList.clear();
		this.teleportField=new GuiTextField(5, fontRenderer, this.width / 2 -40, this.height / 2 - 10, 30, 20);
		this.teleportField.setMaxStringLength(3);
		this.teleportField.setFocused(true);
		this.teleportField.setText(Integer.toString(this.channel+1));
		this.buttonList.add(this.doneBtn = new GuiButton(0, this.width / 2 + 5, this.height / 2 + 60, 40, 20,
				I18n.format("gui.done", new Object[0])));
		this.buttonList
				.add(this.teleportUpBtn = new GuiButton(1, this.width / 2 - 60, this.height / 2 - 10, 20, 20, "+"));
		this.buttonList
				.add(this.teleportDownBtn = new GuiButton(2, this.width / 2 - 10, this.height / 2 - 10, 20, 20, "-"));
		this.buttonList
				.add(this.exitToggle = new GuiButton(3, this.width / 2 + 10, this.height / 2 - 10, 50, 20, "Exit"));
		this.buttonList.add(this.grab = new GuiButton(4, this.width / 2 - 45, this.height / 2 + 60, 40, 20,
				I18n.format("gui.teleporter.drop", new Object[0])));
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.enabled)
			if (button.id == 0) {
				this.mc.displayGuiScreen(null);
				TF2weapons.network.sendToServer(
						new TF2Message.GuiConfigMessage(this.teleporter.getEntityId(), (byte) 0, channel));
				TF2weapons.network.sendToServer(
						new TF2Message.GuiConfigMessage(this.teleporter.getEntityId(), (byte) 1, exit ? 1 : 0));
			} else if (button.id == 1) {
				channel++;
				if (channel >= EntityTeleporter.TP_PER_PLAYER)
					channel = 0;
				this.teleportField.setText(Integer.toString(this.channel + 1));
			} else if (button.id == 2) {
				channel--;
				if (channel < 0)
					channel = EntityTeleporter.TP_PER_PLAYER - 1;
				this.teleportField.setText(Integer.toString(this.channel + 1));
			} else if (button.id == 3 && !this.teleporter.isExit())
				exit = !exit;
			else if (button.id == 4) {
				this.mc.displayGuiScreen(null);
				TF2weapons.network
						.sendToServer(new TF2Message.GuiConfigMessage(this.teleporter.getEntityId(), (byte) 127, 0));
			}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		this.teleportField.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		this.teleportField.textboxKeyTyped(typedChar, keyCode);
		try {
			channel = MathHelper.clamp(Integer.parseInt(this.teleportField.getText())-1,0,EntityTeleporter.TP_PER_PLAYER-1);
		}
		catch (Exception ex) {
			
		}
		//this.teleportField.setText(Integer.toString(channel));
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, I18n.format("gui.teleporter.info", new Object[0]),
				this.width / 2 - 5, 20, 16777215);
		//this.drawCenteredString(this.fontRendererObj, Integer.toString(channel), this.width / 2 - 25, this.height / 2,
		//		16777215);
		this.exitToggle.displayString = exit ? "Exit" : "Entry";
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.teleportField.drawTextBox();

	}
	@Override
	public void updateScreen() {
		super.updateScreen();
		this.teleportField.updateCursorCounter();
	}
}
