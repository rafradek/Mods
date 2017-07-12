package rafradek.crit;

import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class SpinGuiFactory implements IModGuiFactory {

	@Override
	public void initialize(Minecraft minecraftInstance) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasConfigGui() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*public static class SpinGuiConfig extends GuiConfig {

		public SpinGuiConfig(GuiScreen parentScreen) {
			super(parentScreen, getElements(), "rafradek_spin", false, false, "Spin To Win Configuration");
		}

		public static List<IConfigElement> getElements() {
			List<IConfigElement> list = new ConfigElement(RandomCrits.conf.getCategory("config")).getChildElements();
			return list;
		}
	}*/
}
