package rafradek.minecraft2source;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class GuiFactory implements IModGuiFactory {

	@Override
	public void initialize(Minecraft minecraftInstance) {
		// TODO Auto-generated method stub

	}
	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static class SpinGuiConfig extends GuiConfig {

		public SpinGuiConfig(GuiScreen parentScreen) {
			super(parentScreen, getElements(), "minecraft2source", false, false, "Minecraft map to Source Configuration");
		}

		public static List<IConfigElement> getElements() {
			ArrayList<IConfigElement> elements = new ArrayList<>();
			for (String category : Minecraft2Source.conf.getCategoryNames()) {
				elements.addAll(new ConfigElement(Minecraft2Source.conf.getCategory(category)).getChildElements());
			}
			return elements;
		}
	}

	@Override
	public boolean hasConfigGui() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen) {
		// TODO Auto-generated method stub
		return new SpinGuiConfig(parentScreen);
	}
}
