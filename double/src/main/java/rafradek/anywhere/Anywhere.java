package rafradek.anywhere;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "rafradek_anywhere", name = "Access anywhere", version = "1.0")
public class Anywhere {

	public static final String MOD_ID = "rafradek_anywhere";
	
	@Mod.EventHandler
	public void init(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	
}
