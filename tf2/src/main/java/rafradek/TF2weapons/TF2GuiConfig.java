package rafradek.TF2weapons;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class TF2GuiConfig extends GuiConfig {

	public TF2GuiConfig(GuiScreen parentScreen) {
		super(parentScreen, getElements(), "rafradek_tf2_weapons", false, false, "TF2 Stuff Configuration");
	}

	public static List<IConfigElement> getElements() {
		List<IConfigElement> list = new ConfigElement(TF2weapons.conf.getCategory("gameplay")).getChildElements();
		list.addAll(new ConfigElement(TF2weapons.conf.getCategory("modcompatibility")).getChildElements());
		return list;
	}
}
