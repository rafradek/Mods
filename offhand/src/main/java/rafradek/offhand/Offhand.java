package rafradek.offhand;

import java.util.ArrayList;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "rafradek_offhand", name = "Offhand Improvements", version = "1.0", guiFactory = "rafradek.offhand.GuiFactory")
public class Offhand {
	
	public static DataParameter<Boolean> SPIN_TIME;
	
	
	public static UUID SPIN_AS=UUID.fromString("0706d45a-daae-429c-843c-23c03b721b32");
	public static UUID SPIN_AD=UUID.fromString("b308e311-0557-405d-bb3e-551fd34edc25");
	
	private static final ResourceLocation SPIN_TEXTURE = new ResourceLocation("rafradek_spin",
			"textures/misc/spin.png");

	public static ArrayList<ResourceLocation> whitelistItems;
	public static ArrayList<String> whitelistMods;
	public static EntityPlayer fakePlayer;

	public static Configuration conf;
	
	@Mod.EventHandler
	public void init(FMLPreInitializationEvent event) {
		conf = new Configuration(event.getSuggestedConfigurationFile());
		syncConfig();
		//SPIN_TIME=new DataParameter<Boolean>(spinID, DataSerializers.BOOLEAN);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	public void syncConfig(){
		/*spinID=conf.getInt("Spin data parameter ID", "config", 173, 32, 255, "Change this if you encounter problems with duplicated data parameter id");
		range=conf.getFloat("Spin range", "config", 3.7f, 0, 1000, "Ability's range");
		swordDmg=conf.getFloat("Sword damage multiplier", "config", 0.7f, 0, 1000, "");
		axeDmg=conf.getFloat("Axe damage multiplier", "config", 1.15f, 0, 1000, "");
		speed=1f/conf.getFloat("Spin speed multiplier", "config", 1f, 0, 100, "");
		cooldownAxe=conf.getInt("Axe spin cooldown", "config", 220, 0, 1000, "");
		cooldownSword=conf.getInt("Sword spin cooldown", "config", 280, 0, 1000, "");
		duration=conf.getInt("Sword spin duration", "config", 90, 0, 1000, "");
		offhandBlock=conf.getBoolean("Offhand item blocks", "config", false, "If true, an offhand item will always be activated before the spin");
		//requireSneak=conf.getBoolean("Sneaking stop", "config", false, "If true, the player must hold the sneak key before using the ability");
		conf.get("config", "Spin data parameter ID", 173).setRequiresMcRestart(true);*/
		String[] listwl=conf.getStringList("Whitelist items", "config", new String[0], "Registry names of items which should not be blocking offhand. @modid to whitelist all items in the mod");
		whitelistItems=new ArrayList<ResourceLocation>();
		whitelistMods=new ArrayList<String>();
		for(int i=0; i<listwl.length;i++){
			if(listwl[i].charAt(0) != '@') {
				whitelistItems.add(new ResourceLocation(listwl[i]));
			}
			else {
				whitelistMods.add(listwl[i].substring(1));
			}
		}
		if(conf.hasChanged())
			conf.save();
	}
	
	@SubscribeEvent
	public void stopUsing(PlayerInteractEvent.RightClickItem event) {
		
		if (event.getHand()==EnumHand.OFF_HAND){
			ItemStack stack=event.getEntityPlayer().getHeldItemMainhand();
		}
	}
}
