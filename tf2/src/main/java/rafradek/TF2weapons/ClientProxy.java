package rafradek.TF2weapons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.lwjgl.input.Keyboard;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.EnumHand;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.boss.EntityHHH;
import rafradek.TF2weapons.boss.EntityMerasmus;
import rafradek.TF2weapons.boss.EntityMonoculus;
import rafradek.TF2weapons.boss.RenderHHH;
import rafradek.TF2weapons.boss.RenderMerasmus;
import rafradek.TF2weapons.boss.RenderMonoculus;
import rafradek.TF2weapons.building.BuildingSound;
import rafradek.TF2weapons.building.EntityDispenser;
import rafradek.TF2weapons.building.EntitySentry;
import rafradek.TF2weapons.building.EntityTeleporter;
import rafradek.TF2weapons.building.GuiSentry;
import rafradek.TF2weapons.building.GuiTeleporter;
import rafradek.TF2weapons.building.RenderDispenser;
import rafradek.TF2weapons.building.RenderSentry;
import rafradek.TF2weapons.building.RenderTeleporter;
import rafradek.TF2weapons.characters.EntitySaxtonHale;
import rafradek.TF2weapons.characters.EntityStatue;
import rafradek.TF2weapons.characters.EntityTF2Character;
import rafradek.TF2weapons.characters.GuiConfirm;
import rafradek.TF2weapons.characters.RenderStatue;
import rafradek.TF2weapons.characters.RenderTF2Character;
import rafradek.TF2weapons.decoration.LayerWearables;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.message.udp.TF2UdpClient;
import rafradek.TF2weapons.projectiles.EntityBall;
import rafradek.TF2weapons.projectiles.EntityCritEffect;
import rafradek.TF2weapons.projectiles.EntityFlameEffect;
import rafradek.TF2weapons.projectiles.EntityFlare;
import rafradek.TF2weapons.projectiles.EntityGrenade;
import rafradek.TF2weapons.projectiles.EntityJar;
import rafradek.TF2weapons.projectiles.EntityProjectileSimple;
import rafradek.TF2weapons.projectiles.EntityRocket;
import rafradek.TF2weapons.projectiles.EntityRocketEffect;
import rafradek.TF2weapons.projectiles.EntityStickybomb;
import rafradek.TF2weapons.projectiles.EntityStickProjectile;
import rafradek.TF2weapons.projectiles.RenderBall;
import rafradek.TF2weapons.projectiles.RenderFlare;
import rafradek.TF2weapons.projectiles.RenderGrenade;
import rafradek.TF2weapons.projectiles.RenderJar;
import rafradek.TF2weapons.projectiles.RenderProjectileSimple;
import rafradek.TF2weapons.projectiles.RenderRocket;
import rafradek.TF2weapons.projectiles.RenderStickybomb;
import rafradek.TF2weapons.projectiles.RenderSyringe;
import rafradek.TF2weapons.weapons.EntityBulletTracer;
import rafradek.TF2weapons.weapons.EntityMuzzleFlash;
import rafradek.TF2weapons.weapons.GuiDisguiseKit;
import rafradek.TF2weapons.weapons.ItemAmmo;
import rafradek.TF2weapons.weapons.ItemUsable;
import rafradek.TF2weapons.weapons.OnFireSound;
import rafradek.TF2weapons.weapons.ParticleBulletHole;
import rafradek.TF2weapons.weapons.ReloadSound;
import rafradek.TF2weapons.weapons.WeaponLoopSound;
import rafradek.TF2weapons.weapons.WeaponSound;

public class ClientProxy extends CommonProxy {
	
	public static HashMap<String, ModelBase> entityModel = new HashMap<String, ModelBase>();
	public static HashMap<String, ResourceLocation> textureDisguise = new HashMap<String, ResourceLocation>();
	public static RenderCustomModel disguiseRender;
	public static RenderLivingBase disguiseRenderPlayer;
	public static RenderLivingBase disguiseRenderPlayerSmall;
	public static TextureMap particleMap;
	public static KeyBinding reload = new KeyBinding("key.reload", Keyboard.KEY_R, "TF2");
	public static ResourceLocation scopeTexture = new ResourceLocation(TF2weapons.MOD_ID, "textures/misc/scope.png");
	// public static Map<MinigunLoopSound, EntityLivingBase > spinSounds;
	public static BiMap<EntityLivingBase, WeaponSound> fireSounds;
	public static Map<EntityLivingBase, ReloadSound> reloadSounds;
	public static Map<String, ModelResourceLocation> nameToModel;
	public static ConcurrentMap<EntityLivingBase, ItemStack> soundsToStart;
	public static ResourceLocation blackTexture = new ResourceLocation(TF2weapons.MOD_ID, "textures/misc/black.png");
	public static ResourceLocation healingTexture = new ResourceLocation(TF2weapons.MOD_ID, "textures/gui/healing.png");
	public static ResourceLocation buildingTexture = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/gui/buildings.png");
	public static ResourceLocation chargeTexture = new ResourceLocation(TF2weapons.MOD_ID, "textures/misc/charge.png");
	public static final ResourceLocation VIGNETTE = new ResourceLocation("textures/misc/vignette.png");
	public static List<WeaponSound> weaponSoundsToStart;
	public static int renderCritGlow;
	public static boolean inRenderHand;
	public static boolean inRenderHandTicked;

	public static final Logger LOGGER = (Logger) LogManager.getLogger();
	@Override
	public void registerItemBlock(ItemBlock item) {
		for (int i = 0; i < 16; i++)
			ModelLoader.setCustomModelResourceLocation(item, i,
					new ModelResourceLocation(item.getRegistryName(), "inventory"));

	}

	public static void RegisterWeaponData(WeaponData weapon) {
		String modelName = weapon.getString(PropertyType.RENDER);

		if (modelName == null || modelName.isEmpty())
			return;

		ModelResourceLocation model = new ModelResourceLocation(modelName, "inventory");
		ModelLoader.registerItemVariants(MapList.weaponClasses.get(weapon.getString(PropertyType.CLASS)), model);
		nameToModel.put(weapon.getName(), model);
		if (weapon.hasProperty(PropertyType.RENDER_BACKSTAB)) {
			modelName = weapon.getString(PropertyType.RENDER_BACKSTAB);
			model = new ModelResourceLocation(modelName, "inventory");
			ModelLoader.registerItemVariants(MapList.weaponClasses.get(weapon.getString(PropertyType.CLASS)), model);
			nameToModel.put(weapon.getName() + "/b", model);
		}
	}

	@Override
	public void registerRenderInformation() {

		
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) ->
			{
				if(stack.getItemDamage()<25){
					return stack.getItemDamage() % 2 == 0 ? 16711680 : 255;
				}
				else if(stack.getItemDamage() / 2 == 13)
					return 0xFFFFFF;
				else if(stack.getItemDamage() == 28)
					return 0x743501;
				else if(stack.getItemDamage() == 29)
					return 0x1B013A;
				else if(stack.getItemDamage() == 30)
					return 0x20582B;
				return stack.getItemDamage() / 2 == 13 ? 0xFFFFFF : (stack.getItemDamage() % 2 == 0 ? 16711680 : 255);
			}, TF2weapons.itemPlacer);
		List<Item> items = new ArrayList<Item>(ForgeRegistries.ITEMS.getValues());
		Iterator<Item> itemIterator = items.iterator();
		while (itemIterator.hasNext()) {
			Item item = itemIterator.next();
			if (!(item instanceof ItemUsable || item instanceof ItemTool || item instanceof ItemSword
					|| item instanceof ItemBow))
				itemIterator.remove();
		}
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor() {

			@Override
			public int getColorFromItemstack(ItemStack stack, int tintIndex) {
				if (renderCritGlow>1){
					int color;
					if (renderCritGlow==2)
						color=0xFFB060;
					else if (renderCritGlow==3)
						color=0x60B0FF;
					else if (renderCritGlow==4)
						color=0xFF5050;
					else
						color=0x5050FF;
					return color;
				}
				else if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("Australium"))
					return 0xFFD400;
				else
					return 0xFFFFFF;
			}

		}, items.toArray(new Item[items.size()]));

		for (RenderPlayer render : Minecraft.getMinecraft().getRenderManager().getSkinMap().values()) {
			render.addLayer(new LayerWearables(render, render.getMainModel()));
			/*render.addLayer(new LayerBipedArmor(render) {

				@Override
				protected void initArmor() {
					this.modelLeggings = new ModelBiped(0.5F);
					this.modelArmor = new ModelBiped(1.25F);
				}

				@Override
				public ItemStack getItemStackFromSlot(EntityLivingBase living, EntityEquipmentSlot slotIn) {

					if (slotIn == EntityEquipmentSlot.CHEST)
						return living.getCapability(TF2weapons.INVENTORY_CAP, null).getStackInSlot(3);

					return null;
				}

			});*/
		}

		/*
		 * Minecraft.getMinecraft().getRenderItem().getItemModelMesher().
		 * register(TF2weapons.itemPlacer, new ItemMeshDefinition() { public
		 * ModelResourceLocation getModelLocation(ItemStack stack) { return new
		 * ModelResourceLocation("spawn_egg", "inventory"); } });
		 */
		/*
		 * Minecraft.getMinecraft().getRenderItem().getItemModelMesher().
		 * register(TF2weapons.itemDisguiseKit, new ItemMeshDefinition() {
		 * public ModelResourceLocation getModelLocation(ItemStack stack) {
		 * return new ModelResourceLocation(TF2weapons.MOD_ID+":disguiseKit",
		 * "inventory"); } });
		 */
		/*
		 * Minecraft.getMinecraft().getRenderItem().getItemModelMesher().
		 * register(TF2weapons.itemBuildingBox, new ItemMeshDefinition() {
		 * public ModelResourceLocation getModelLocation(ItemStack stack) {
		 * return new ModelResourceLocation("spawn_egg", "inventory"); } });
		 */
		// Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(p_178086_1_,
		// p_178086_2_, p_178086_3_);
		
		reloadSounds = new HashMap<EntityLivingBase, ReloadSound>();
		soundsToStart = new ConcurrentHashMap<EntityLivingBase, ItemStack>();
		weaponSoundsToStart = new ArrayList<WeaponSound>();
		fireSounds = HashBiMap.create();
		ClientRegistry.registerKeyBinding(ClientProxy.reload);
		//disguiseRender = new RenderCustomModel(Minecraft.getMinecraft().getRenderManager(), new ModelBiped(), 0);
		disguiseRenderPlayer = new RenderPlayerDisguised(Minecraft.getMinecraft().getRenderManager(), false);
		disguiseRenderPlayerSmall = new RenderPlayerDisguised(Minecraft.getMinecraft().getRenderManager(), true);
		/*entityModel.put("Creeper", ((RenderLivingBase<?>) Minecraft.getMinecraft().getRenderManager().entityRenderMap
				.get(EntityCreeper.class)).getMainModel());
		textureDisguise.put("Creeper", new ResourceLocation("textures/entity/creeper/creeper.png"));
		entityModel.put("Zombie", ((RenderLivingBase<?>) Minecraft.getMinecraft().getRenderManager().entityRenderMap
				.get(EntityZombie.class)).getMainModel());
		textureDisguise.put("Zombie", new ResourceLocation("textures/entity/zombie/zombie.png"));
		entityModel.put("Enderman", ((RenderLivingBase<?>) Minecraft.getMinecraft().getRenderManager().entityRenderMap
				.get(EntityEnderman.class)).getMainModel());
		textureDisguise.put("Enderman", new ResourceLocation("textures/entity/enderman/enderman.png"));
		entityModel.put("Spider", ((RenderLivingBase<?>) Minecraft.getMinecraft().getRenderManager().entityRenderMap
				.get(EntitySpider.class)).getMainModel());
		textureDisguise.put("Spider", new ResourceLocation("textures/entity/spider/spider.png"));
		entityModel.put("Cow",
				((RenderLivingBase<?>) Minecraft.getMinecraft().getRenderManager().entityRenderMap.get(EntityCow.class))
						.getMainModel());
		textureDisguise.put("Cow", new ResourceLocation("textures/entity/cow/cow.png"));
		entityModel.put("Pig",
				((RenderLivingBase<?>) Minecraft.getMinecraft().getRenderManager().entityRenderMap.get(EntityPig.class))
						.getMainModel());
		textureDisguise.put("Pig", new ResourceLocation("textures/entity/pig/pig.png"));
		entityModel.put("Chicken", ((RenderLivingBase<?>) Minecraft.getMinecraft().getRenderManager().entityRenderMap
				.get(EntityChicken.class)).getMainModel());
		textureDisguise.put("Chicken", new ResourceLocation("textures/entity/chicken.png"));*/
		
			/*((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new IResourceManagerReloadListener(){

				@Override
				public void onResourceManagerReload(final IResourceManager resourceManager) {
					new Thread(new Runnable(){

						@Override
						public void run() {
							try {
								Field field=LanguageManager.class.getDeclaredField("CURRENT_LOCALE");
								field.setAccessible(true);
								Locale loc=(Locale) field.get(null);
								Method method=loc.getClass().getDeclaredMethod("loadLocaleData", new Class<?>[]{InputStream.class});
								method.setAccessible(true);
								method.invoke(loc, resourceManager.getResource(new ResourceLocation(TF2weapons.MOD_ID,"lange/en_us.lang")).getInputStream());
							} catch (Exception e){
								e.printStackTrace();
							}
						}
					}).run();
					
						
						/*Map<String,String> map=new HashMap<>();
						FMLCommonHandler.instance().loadLanguage(map, );
						for(Entry<String,String> entry:map.entrySet()){
							System.out.println(entry.getKey()+" "+entry.getValue());
						}
				}
				
			});*/
			
			
		
		// RenderingRegistry.registerEntityRenderingHandler(EntityScout.class,
		// new RenderTF2Character());
		// Minecraft.getMinecraft().renderEngine.loadTextureMap(new
		// ResourceLocation("textures/tfatlas/particles.png"), particleMap=new
		// TF2TextureMap("textures/particle"));
		try {
					//System.out.println("Is Class: "+logger.getClass().getCanonicalName());
					Filter filter= new AbstractFilter() {
						 @Override
						    public Result filter(final LogEvent event) {
							 	if(event.getLoggerName().equals("net.minecraft.client.multiplayer.GuiConnecting")) {
							 		//System.out.println(event.getMessage().getParameters()[0]);
							 		//System.out.println(event.getLoggerName());
							 		TF2UdpClient.addressToUse = (String) event.getMessage().getParameters()[0];
							 	}
						        return Result.NEUTRAL;
						    }
					};
					filter.start();
					/*Writer writer =new Writer() {

						@Override
						public void close() throws IOException {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void flush() throws IOException {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void write(char[] arg0, int arg1, int arg2) throws IOException {
							//System.out.println(new String(arg0));
						}
						
					};
					WriterAppender app=WriterAppender.createAppender(null, filter, writer, "lel", true, false);
					app.start();*/
					LOGGER.get().addFilter(filter);
					//logger.get().addAppender(app, org.apache.logging.log4j.Level.ALL, null);
		} catch (Exception e) {
			
		}
	}

	@Override
	public EntityPlayer getPlayerForSide(MessageContext ctx) {
		return ctx.side == Side.SERVER ? ctx.getServerHandler().player : Minecraft.getMinecraft().player;
	}

	@Override
	public void preInit() {
		// TODO Auto-generated method stub
		MinecraftForge.EVENT_BUS.register(new TF2EventsClient());
		OBJLoader.INSTANCE.addDomain(TF2weapons.MOD_ID.toLowerCase());
		for (int i = 1; i < ItemAmmo.AMMO_TYPES.length; i++){
			ModelLoader.setCustomModelResourceLocation(TF2weapons.itemAmmoPackage, i,
					new ModelResourceLocation(TF2weapons.MOD_ID + ":ammo_box", "inventory"));
			if (i != 10 && i != 12)
				ModelLoader.setCustomModelResourceLocation(TF2weapons.itemAmmo, i,
						new ModelResourceLocation(TF2weapons.MOD_ID + ":ammo_" + ItemAmmo.AMMO_TYPES[i], "inventory"));
		}

		// ModelLoader.registerItemVariants(TF2weapons.itemTF2, new
		// ModelResourceLocation(TF2weapons.MOD_ID+":copper_ingot",
		// "inventory"),new
		// ModelResourceLocation(TF2weapons.MOD_ID+":lead_ingot", "inventory"));
		nameToModel = new HashMap<String, ModelResourceLocation>();
		for (WeaponData weapon : MapList.nameToData.values())
			// System.out.println("Execut "+weapon.getName());
			RegisterWeaponData(weapon);
		
		for (Item item : MapList.weaponClasses.values()) {
			ModelLoader.setCustomMeshDefinition(item, stack -> {
				if (stack.hasCapability(TF2weapons.WEAPONS_DATA_CAP, null)) {
					if(stack.getCapability(TF2weapons.WEAPONS_DATA_CAP, null).inst!=ItemFromData.BLANK_DATA)
						return nameToModel.get(stack.getCapability(TF2weapons.WEAPONS_DATA_CAP, null).inst.getName());
					else if(stack.hasTagCompound())
						return nameToModel.get(stack.getTagCompound().getString("Type"));
				}
				return nameToModel.get("minigun");
			});
		}
		
		ModelResourceLocation spawnEgg = new ModelResourceLocation("spawn_egg", "inventory");

		for (int i = 0; i < 31; i++)
			ModelLoader.setCustomModelResourceLocation(TF2weapons.itemPlacer, i, spawnEgg);
		ModelLoader.setCustomModelResourceLocation(TF2weapons.itemDisguiseKit, 0,
				new ModelResourceLocation(TF2weapons.MOD_ID + ":disguise_kit", "inventory"));
		ModelLoader.setCustomModelResourceLocation(TF2weapons.itemSandvich, 0,
				new ModelResourceLocation(TF2weapons.MOD_ID + ":sandvich", "inventory"));
		ModelLoader.setCustomModelResourceLocation(TF2weapons.itemAmmoBelt, 0,
				new ModelResourceLocation(TF2weapons.MOD_ID + ":ammo_belt", "inventory"));
		ModelLoader.setCustomModelResourceLocation(TF2weapons.itemStatue, 0,
				new ModelResourceLocation(TF2weapons.MOD_ID + ":statue", "inventory"));
		// Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(ItemBlock.getItemFromBlock(TF2weapons.blockCabinet),
		// 0, new
		// ModelResourceLocation(TF2weapons.MOD_ID+":tf2workbench","inventory"));

		final ModelResourceLocation sentryRed = new ModelResourceLocation(TF2weapons.MOD_ID + ":sentryred",
				"inventory");
		final ModelResourceLocation sentryBlu = new ModelResourceLocation(TF2weapons.MOD_ID + ":sentryblu",
				"inventory");
		final ModelResourceLocation dispenserRed = new ModelResourceLocation(TF2weapons.MOD_ID + ":dispenserred",
				"inventory");
		final ModelResourceLocation dispenserBlu = new ModelResourceLocation(TF2weapons.MOD_ID + ":dispenserblu",
				"inventory");
		final ModelResourceLocation teleporterRed = new ModelResourceLocation(TF2weapons.MOD_ID + ":teleporterred",
				"inventory");
		final ModelResourceLocation teleporterBlu = new ModelResourceLocation(TF2weapons.MOD_ID + ":teleporterblu",
				"inventory");

		ModelBakery.registerItemVariants(TF2weapons.itemBuildingBox, sentryRed, sentryBlu, dispenserRed, dispenserBlu,
				teleporterRed, teleporterBlu);
		ModelLoader.setCustomMeshDefinition(TF2weapons.itemBuildingBox,
				new ItemMeshDefinition() {
					@Override
					public ModelResourceLocation getModelLocation(ItemStack stack) {
						if (stack.getItemDamage() == 18)
							return sentryRed;
						else if (stack.getItemDamage() == 19)
							return sentryBlu;
						else if (stack.getItemDamage() == 20)
							return dispenserRed;
						else if (stack.getItemDamage() == 21)
							return dispenserBlu;
						else if (stack.getItemDamage() == 22)
							return teleporterRed;
						else
							return teleporterBlu;
					}
				});
		ModelLoader.setCustomModelResourceLocation(TF2weapons.itemAmmoFire, 0,
				new ModelResourceLocation(TF2weapons.MOD_ID + ":ammo_fire", "inventory"));
		ModelLoader.setCustomModelResourceLocation(TF2weapons.itemChocolate, 0,
				new ModelResourceLocation(TF2weapons.MOD_ID + ":chocolate", "inventory"));
		ModelLoader.setCustomModelResourceLocation(TF2weapons.itemHorn, 0,
				new ModelResourceLocation(TF2weapons.MOD_ID + ":horn", "inventory"));
		ModelLoader.setCustomModelResourceLocation(TF2weapons.itemMantreads, 0,
				new ModelResourceLocation(TF2weapons.MOD_ID + ":mantreads", "inventory"));
		ModelLoader.setCustomModelResourceLocation(TF2weapons.itemScoutBoots, 0,
				new ModelResourceLocation(TF2weapons.MOD_ID + ":scout_shoes", "inventory"));
		ModelLoader.setCustomModelResourceLocation(TF2weapons.itemAmmoMedigun, 0,
				new ModelResourceLocation(TF2weapons.MOD_ID + ":ammo_medigun", "inventory"));
		ModelLoader.setCustomModelResourceLocation(TF2weapons.itemTF2, 0,
				new ModelResourceLocation(TF2weapons.MOD_ID + ":copper_ingot", "inventory"));
		ModelLoader.setCustomModelResourceLocation(TF2weapons.itemTF2, 1,
				new ModelResourceLocation(TF2weapons.MOD_ID + ":lead_ingot", "inventory"));
		ModelLoader.setCustomModelResourceLocation(TF2weapons.itemTF2, 2,
				new ModelResourceLocation(TF2weapons.MOD_ID + ":australium_ingot", "inventory"));
		ModelLoader.setCustomModelResourceLocation(TF2weapons.itemTF2, 3,
				new ModelResourceLocation(TF2weapons.MOD_ID + ":scrap_metal", "inventory"));
		ModelLoader.setCustomModelResourceLocation(TF2weapons.itemTF2, 4,
				new ModelResourceLocation(TF2weapons.MOD_ID + ":reclaimed_metal", "inventory"));
		ModelLoader.setCustomModelResourceLocation(TF2weapons.itemTF2, 5,
				new ModelResourceLocation(TF2weapons.MOD_ID + ":refined_metal", "inventory"));
		ModelLoader.setCustomModelResourceLocation(TF2weapons.itemTF2, 6,
				new ModelResourceLocation(TF2weapons.MOD_ID + ":australium_nugget", "inventory"));
		ModelLoader.setCustomModelResourceLocation(TF2weapons.itemTF2, 7,
				new ModelResourceLocation(TF2weapons.MOD_ID + ":key", "inventory"));
		ModelLoader.setCustomModelResourceLocation(TF2weapons.itemTF2, 8,
				new ModelResourceLocation(TF2weapons.MOD_ID + ":crate", "inventory"));
		ModelLoader.setCustomModelResourceLocation(TF2weapons.itemTF2, 9,
				new ModelResourceLocation(TF2weapons.MOD_ID + ":random_weapon", "inventory"));
		ModelLoader.setCustomModelResourceLocation(TF2weapons.itemTF2, 10,
				new ModelResourceLocation(TF2weapons.MOD_ID + ":random_hat", "inventory"));
		
		RenderingRegistry.registerEntityRenderingHandler(EntityTF2Character.class,
				new IRenderFactory<EntityTF2Character>() {
					@Override
					public Render<EntityTF2Character> createRenderFor(RenderManager manager) {
						// TODO Auto-generated method stub
						return new RenderTF2Character(manager);
					}
				});
		/*
		 * RenderingRegistry.registerEntityRenderingHandler(EntityProjectileBase
		 * .class, new IRenderFactory<Entity>(){
		 * 
		 * @Override public Render<Entity> createRenderFor(RenderManager
		 * manager) { // TODO Auto-generated method stub return new
		 * RenderEntity(manager); } });
		 */
		RenderingRegistry.registerEntityRenderingHandler(EntityRocket.class, new IRenderFactory<EntityRocket>() {
			@Override
			public Render<EntityRocket> createRenderFor(RenderManager manager) {
				// TODO Auto-generated method stub
				return new RenderRocket(manager);
			}
		});
		/*
		 * RenderingRegistry.registerEntityRenderingHandler(EntityFlame.class,
		 * new IRenderFactory<EntityFlame>(){
		 * 
		 * @Override public Render<EntityFlame> createRenderFor(RenderManager
		 * manager) { // TODO Auto-generated method stub return
		 * (Render<EntityFlame>) new RenderEntity(); } });
		 */
		RenderingRegistry.registerEntityRenderingHandler(EntityGrenade.class, new IRenderFactory<EntityGrenade>() {
			@Override
			public Render<EntityGrenade> createRenderFor(RenderManager manager) {
				// TODO Auto-generated method stub
				return new RenderGrenade(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityStickybomb.class,
				new IRenderFactory<EntityStickybomb>() {
					@Override
					public Render<EntityStickybomb> createRenderFor(RenderManager manager) {
						// TODO Auto-generated method stub
						return new RenderStickybomb(manager);
					}
				});
		RenderingRegistry.registerEntityRenderingHandler(EntityProjectileSimple.class, new IRenderFactory<EntityProjectileSimple>() {
			@Override
			public Render<EntityProjectileSimple> createRenderFor(RenderManager manager) {
				// TODO Auto-generated method stub
				return new RenderProjectileSimple(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityStickProjectile.class, new IRenderFactory<EntityStickProjectile>() {
			@Override
			public Render<EntityStickProjectile> createRenderFor(RenderManager manager) {
				// TODO Auto-generated method stub
				return new RenderSyringe(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityBall.class, new IRenderFactory<EntityBall>() {
			@Override
			public Render<EntityBall> createRenderFor(RenderManager manager) {
				// TODO Auto-generated method stub
				return new RenderBall(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityFlare.class, new IRenderFactory<EntityFlare>() {
			@Override
			public Render<EntityFlare> createRenderFor(RenderManager manager) {
				// TODO Auto-generated method stub
				return new RenderFlare(manager);
			}
		});
		
		RenderingRegistry.registerEntityRenderingHandler(EntityJar.class, new IRenderFactory<EntityJar>() {
			@Override
			public Render<EntityJar> createRenderFor(RenderManager manager) {
				// TODO Auto-generated method stub
				return new RenderJar(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntitySentry.class, new IRenderFactory<EntitySentry>() {
			@Override
			public Render<EntitySentry> createRenderFor(RenderManager manager) {
				// TODO Auto-generated method stub
				return new RenderSentry(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityDispenser.class, new IRenderFactory<EntityDispenser>() {
			@Override
			public Render<EntityDispenser> createRenderFor(RenderManager manager) {
				// TODO Auto-generated method stub
				return new RenderDispenser(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityTeleporter.class,
				new IRenderFactory<EntityTeleporter>() {
					@Override
					public Render<EntityTeleporter> createRenderFor(RenderManager manager) {
						// TODO Auto-generated method stub
						return new RenderTeleporter(manager);
					}
				});
		RenderingRegistry.registerEntityRenderingHandler(EntityStatue.class, new IRenderFactory<EntityStatue>() {
			@Override
			public Render<EntityStatue> createRenderFor(RenderManager manager) {
				// TODO Auto-generated method stub
				return new RenderStatue(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityHHH.class, new IRenderFactory<EntityHHH>() {
			@Override
			public Render<EntityHHH> createRenderFor(RenderManager manager) {
				// TODO Auto-generated method stub
				return new RenderHHH(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityMerasmus.class, new IRenderFactory<EntityMerasmus>() {
			@Override
			public Render<EntityMerasmus> createRenderFor(RenderManager manager) {
				// TODO Auto-generated method stub
				return new RenderMerasmus(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityMonoculus.class, new IRenderFactory<EntityMonoculus>() {
			@Override
			public Render<EntityMonoculus> createRenderFor(RenderManager manager) {
				// TODO Auto-generated method stub
				return new RenderMonoculus(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntitySaxtonHale.class,
				new IRenderFactory<EntitySaxtonHale>() {
					@Override
					public Render<EntitySaxtonHale> createRenderFor(RenderManager manager) {
						// TODO Auto-generated method stub
						return new RenderBiped<EntitySaxtonHale>(manager, new ModelBiped(), 0.5F) {
							private final ResourceLocation TEXTURE = new ResourceLocation(TF2weapons.MOD_ID,
									"textures/entity/tf2/SaxtonHale.png");

							@Override
							protected ResourceLocation getEntityTexture(EntitySaxtonHale entity) {
								return TEXTURE;
							}
						};
					}
				});
	}

	public static void playBuildingSound(BuildingSound sound) {
		Minecraft.getMinecraft().getSoundHandler().playSound(sound);
	}

	@Override
	public void registerTicks() {
	}

	public static void spawnFlameParticle(World world, EntityLivingBase ent, float step, boolean heater) {
		Particle entity = EntityFlameEffect.createNewEffect(world, ent, step, heater);
		spawnParticle(world, entity);
	}

	public static void spawnBulletParticle(World world, EntityLivingBase living, double startX, double startY,
			double startZ, double endX, double endY, double endZ, int j, int crits, boolean special) {
		Particle entity = new EntityBulletTracer(world, startX, startY, startZ, endX, endY, endZ, j, crits, living, special);
		spawnParticle(world, entity);
	}

	public static void spawnCritParticle(World world, double pX, double pY, double pZ, int teamForDisplay) {
		Particle entity = new EntityCritEffect(world, pX, pY, pZ, teamForDisplay);
		spawnParticle(world, entity);
	}
	public static void spawnBulletHoleParticle(World world, RayTraceResult origin) {
		Particle entity = new ParticleBulletHole(world, origin);
		spawnParticle(world, entity);
	}
	public static void spawnFlashParticle(World world, EntityLivingBase ent, EnumHand hand) {
		Particle entity = new EntityMuzzleFlash(world, ent, hand);
		spawnParticle(world, entity);
	}

	public static void spawnParticle(World world, Particle entity) {
		if (Minecraft.getMinecraft() != null && Minecraft.getMinecraft().getRenderViewEntity() != null
				&& Minecraft.getMinecraft().effectRenderer != null) {
			int i = Minecraft.getMinecraft().gameSettings.particleSetting;

			if (i == 1 && world.rand.nextInt(3) == 0)
				i = 2;
			if (i > 1) {
				entity.setExpired();
				return;
			}
			Minecraft.getMinecraft().effectRenderer.addEffect(entity);
		}
	}

	@Override
	public void playReloadSound(EntityLivingBase player, ItemStack stack) {
		if (!Thread.currentThread().getName().equals("Client thread") || !(stack.getItem() instanceof ItemUsable))
			return;
		// ResourceLocation soundName=new
		// ResourceLocation(ItemUsable.getData(stack).get("Reload
		// Sound").getString());
		SoundEvent event = ItemFromData.getSound(stack, PropertyType.RELOAD_SOUND);
		if(event != null) {
			ReloadSound sound = new ReloadSound(event, player);
			if (ClientProxy.reloadSounds.get(player) != null)
				ClientProxy.reloadSounds.get(player).done = true;
			ClientProxy.reloadSounds.put(player, sound);
			Minecraft.getMinecraft().getSoundHandler().playSound(sound);
		}
	}

	public static WeaponSound playWeaponSound(EntityLivingBase living, SoundEvent playSound, boolean loop, int type,
			ItemStack stack) {
		// System.out.println(sound.type);\
		if (playSound == null)
			return null;
		
		WeaponSound sound;
		if (loop)
			sound = new WeaponLoopSound(playSound, living, type < 2, ItemFromData.getData(stack), type == 1, type);
		else
			sound = new WeaponSound(playSound, living, type, ItemFromData.getData(stack));
		if (fireSounds.get(living) != null)
			// Minecraft.getMinecraft().getSoundHandler().stopSound(fireSounds.get(living));
			fireSounds.get(living).setDone();
		/*
		 * if(Thread.currentThread().getName().equals("Client thread")){
		 * Minecraft.getMinecraft().getSoundHandler().playSound(sound); } else{
		 */
		Minecraft.getMinecraft().getSoundHandler().playSound(sound);
		// }
		fireSounds.put(living, sound);
		return sound;
	}

	public static void removeReloadSound(EntityLivingBase entity) {
		if (reloadSounds.get(entity) != null)
			reloadSounds.remove(entity).done = true;
	}

	public static void playOnFireSound(Entity target, SoundEvent playSound) {
		if (!Thread.currentThread().getName().equals("Client thread") || playSound == null)
			return;
		Minecraft.getMinecraft().getSoundHandler().playSound(new OnFireSound(playSound, target));
	}

	public static void spawnRocketParticle(World world, EntityRocket rocket) {
		spawnParticle(world, new EntityRocketEffect(world, rocket));
	}

	public static class RenderCustomModel extends RenderLivingBase<EntityLivingBase> {

		private ResourceLocation texture;

		public RenderCustomModel(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
			super(renderManagerIn, modelBaseIn, shadowSizeIn);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ResourceLocation getEntityTexture(EntityLivingBase entity) {
			// TODO Auto-generated method stub
			return texture;
		}

		public void setRenderOptions(ModelBase model, ResourceLocation texture) {
			this.mainModel = model;
			this.texture = texture;
		}
		@Override
		protected boolean canRenderName(EntityLivingBase entity) {
			return false;
		}
	}

	public static EntityPlayer getLocalPlayer() {
		return Minecraft.getMinecraft().player;
	}

	public static void showGuiTeleporter(EntityTeleporter entityTeleporter) {
		Minecraft.getMinecraft().displayGuiScreen(new GuiTeleporter(entityTeleporter));
	}

	public static void displayScreenConfirm(String str1, String str2) {
		Minecraft.getMinecraft().displayGuiScreen(new GuiConfirm(str1, str2));
	}

	public static void displayScreenJoinTeam() {
		final GuiScreen prevScreen = Minecraft.getMinecraft().currentScreen;
		Minecraft.getMinecraft().displayGuiScreen(new GuiYesNo(new GuiYesNoCallback() {

			@Override
			public void confirmClicked(boolean result, int id) {
				if (result)
					TF2weapons.network.sendToServer(new TF2Message.ActionMessage(16));
				else
					TF2weapons.network.sendToServer(new TF2Message.ActionMessage(17));
				Minecraft.getMinecraft().displayGuiScreen(prevScreen);
			}

		}, "Choose your team", "Before using the store, you need to join a team", "RED", "BLU", 0));
	}

	public static void doChargeTick(EntityLivingBase player) {
		if (player == Minecraft.getMinecraft().player) {
			if (player.getActivePotionEffect(TF2weapons.charging) != null
					&& !(Minecraft.getMinecraft().player.movementInput instanceof MovementInputCharging)) {
				player.getCapability(TF2weapons.PLAYER_CAP,
						null).lastMovementInput = Minecraft.getMinecraft().player.movementInput;
				Minecraft.getMinecraft().player.movementInput = new MovementInputCharging();
				KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindSprint.getKeyCode(), true);
				Minecraft.getMinecraft().gameSettings.mouseSensitivity *= 0.1f;
			} else if (player.getActivePotionEffect(TF2weapons.charging) == null
					&& player.getCapability(TF2weapons.PLAYER_CAP, null).lastMovementInput != null) {

				Minecraft.getMinecraft().player.movementInput = player.getCapability(TF2weapons.PLAYER_CAP,
						null).lastMovementInput;
				player.getCapability(TF2weapons.PLAYER_CAP, null).lastMovementInput = null;
				Minecraft.getMinecraft().gameSettings.mouseSensitivity *= 10f;
			}
			//Minecraft.getMinecraft().player.movementInput.moveForward = 1f;
		}
	}

	public static class MovementInputCharging extends MovementInput {
		public MovementInputCharging() {
			this.moveStrafe = 0.0F;
			this.moveForward = 1.0F;
		}

	}

	public static void showGuiSentry(EntitySentry entitySentry) {
		Minecraft.getMinecraft().displayGuiScreen(new GuiSentry(entitySentry));
	}
	public static void showGuiDisguise() {
		Minecraft.getMinecraft().displayGuiScreen(new GuiDisguiseKit());
	}

	public static void removeSprint() {
		KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindSprint.getKeyCode(), false);
	}
	
	public static void setColor(int color, float alpha, float darken, float min, float max) {
		GlStateManager.color(MathHelper.clamp((color >> 16) / 255f + darken, min, max), 
				MathHelper.clamp((color >> 8 & 255) / 255f + darken, min, max), MathHelper.clamp((color & 255) / 255f + darken, min, max), alpha);
	}
	
}
