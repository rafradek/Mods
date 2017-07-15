package rafradek.TF2weapons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.google.common.base.Predicate;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.boss.EntityHHH;
import rafradek.TF2weapons.boss.EntityMerasmus;
import rafradek.TF2weapons.boss.EntityMonoculus;
import rafradek.TF2weapons.boss.EntityTF2Boss;
import rafradek.TF2weapons.building.EntityBuilding;
import rafradek.TF2weapons.building.EntityDispenser;
import rafradek.TF2weapons.building.EntitySentry;
import rafradek.TF2weapons.building.EntityTeleporter;
import rafradek.TF2weapons.characters.EntityDemoman;
import rafradek.TF2weapons.characters.EntityEngineer;
import rafradek.TF2weapons.characters.EntityHeavy;
import rafradek.TF2weapons.characters.EntityMedic;
import rafradek.TF2weapons.characters.EntityPyro;
import rafradek.TF2weapons.characters.EntityScout;
import rafradek.TF2weapons.characters.EntitySniper;
import rafradek.TF2weapons.characters.EntitySoldier;
import rafradek.TF2weapons.characters.EntitySpy;
import rafradek.TF2weapons.characters.EntityTF2Character;
import rafradek.TF2weapons.decoration.GuiWearables;
import rafradek.TF2weapons.decoration.InventoryWearables;
import rafradek.TF2weapons.decoration.ItemWearable;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.pages.Contract;
import rafradek.TF2weapons.pages.Contract.Objective;
import rafradek.TF2weapons.projectiles.EntityProjectileBase;
import rafradek.TF2weapons.weapons.ItemAmmo;
import rafradek.TF2weapons.weapons.ItemAmmoPackage;
import rafradek.TF2weapons.weapons.ItemChargingTarge;
import rafradek.TF2weapons.weapons.ItemCloak;
import rafradek.TF2weapons.weapons.ItemDisguiseKit;
import rafradek.TF2weapons.weapons.ItemHorn;
import rafradek.TF2weapons.weapons.ItemMedigun;
import rafradek.TF2weapons.weapons.ItemMeleeWeapon;
import rafradek.TF2weapons.weapons.ItemMinigun;
import rafradek.TF2weapons.weapons.ItemWeapon;
import rafradek.TF2weapons.weapons.ItemWrangler;
import rafradek.TF2weapons.weapons.TF2Explosion;
import rafradek.TF2weapons.weapons.ItemSniperRifle;
import rafradek.TF2weapons.weapons.ItemSoldierBackpack;
import rafradek.TF2weapons.weapons.ItemStickyLauncher;
import rafradek.TF2weapons.weapons.ItemUsable;
import rafradek.TF2weapons.weapons.WeaponsCapability;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.RecipesBanners;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.GameRules.ValueType;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryTable;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;

public class TF2EventsCommon {
	public int tickleft;
	
	// public ModelSkeleton skeletonModel=new ModelSkeleton();
	// private HashMap eligibleChunksForSpawning = new HashMap();
	public static final String[] STRANGE_TITLES = new String[] { "Strange", "Unremarkable", "Scarely lethal", "Mildly Menacing", "Somewhat threatening", "Uncharitable",
			"Notably dangerous", "Sufficiently lethal", "Truly feared", "Spectacularly lethal", "Gore-spatterer", "Wicked nasty", "Positively inhumane", "Totally ordinary",
			"Face-melting", "Rage-inducing", "Server-clearing", "Epic", "Legendary", "Australian", "Hale's own" };
	public static final int[] STRANGE_KILLS = new int[] { 0, 10, 25, 45, 70, 100, 135, 175, 225, 275, 350, 500, 750, 999, 1000, 1500, 2500, 5000, 7500, 7616, 8500 };
	public static HashMap<EntityLivingBase, EntityLivingBase> fakeEntities = new HashMap<>();
	public static HashMap<World, Integer> spawnEvents = new HashMap<>();
	public static ArrayList<EntityLivingBase> pathsToDefine = new ArrayList<>();
	public static ArrayList<DestroyBlockEntry> destroyProgress = new ArrayList<>();
	//public static final DataParameter<Boolean> ENTITY_UBER = new DataParameter<Boolean>(169, DataSerializers.BOOLEAN);
	public static final DataParameter<Float> ENTITY_OVERHEAL = new DataParameter<Float>(170, DataSerializers.FLOAT);
	public static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(0, 2, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());

	public static long[] tickTimeLiving=new long[20];
	public static long[] tickTimeMercUpdate=new long[20];
	public static long[] tickTimeOther=new long[20];
	/*
	 * @SubscribeEvent public void spawn(WorldEvent.PotentialSpawns event){ int
	 * time=(int) (event.getWorld().getWorldInfo().getWorldTotalTime()/24000);
	 * if(MapList.scoutSpawn.containsKey(event.list)){
	 * MapList.scoutSpawn.get(event.list).itemWeight=time; } else{
	 * System.out.println("add"); SpawnListEntry entry=new
	 * SpawnListEntry(EntityScout.class, time, 1, 3); event.list.add(entry);
	 * MapList.scoutSpawn.put(event.list,entry); } }
	 */
	

	@SubscribeEvent
	public void untargetable(LivingSetAttackTargetEvent event) {
		if (event.getTarget() != null
				&& ((event.getTarget().hasCapability(TF2weapons.WEAPONS_CAP, null) && event.getTarget().getCapability(TF2weapons.WEAPONS_CAP, null).invisTicks >= 20))) {
			event.getEntityLiving().setRevengeTarget(null);
			if (event.getEntityLiving() instanceof EntityLiving) {
				((EntityLiving) event.getEntity()).setAttackTarget(null);
			}
		}
		if (event.getTarget() != null
				&& (event.getTarget().hasCapability(TF2weapons.WEAPONS_CAP, null) && 
						ItemDisguiseKit.isDisguised(event.getTarget(),event.getEntityLiving()) && event.getEntityLiving().getAttackingEntity() != event.getTarget()))
			if (event.getEntityLiving() instanceof EntityLiving) {
				((EntityLiving) event.getEntity()).setAttackTarget(null);
			}
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
		// TF2weapons.syncConfig();
		if (eventArgs.getModID().equals("rafradek_tf2_weapons")) {
			TF2weapons.syncConfig();
		}
	}

	@SubscribeEvent
	public void serverTickEnd(TickEvent.ServerTickEvent event) {

		if (event.phase == TickEvent.Phase.START)
			if (tickleft <= 0) {
				tickleft = 20;
				Object[] entArray = ItemUsable.lastDamage.keySet().toArray();
				for (int x = 0; x < entArray.length; x++) {
					Entity entity = (Entity) entArray[x];
					float[] dmg = ItemUsable.lastDamage.get(entArray[x]);
					for (int i = 19; i >= 0; i--)
						if (i > 0) {
							dmg[i] = dmg[i - 1];
						} else {
							dmg[0] = 0;
						}
				}

			} else {
				tickleft--;
			}
	}

	public static double avg(long[] values) {
		long totalticktime=0L;
		for(long val : values) {
			totalticktime+=val;
		}
		totalticktime/=values.length;
		return (double)totalticktime* 1.0E-6D;
	}
	
	@SubscribeEvent
	public void worldTick(TickEvent.WorldTickEvent event) {
		if (event.phase == TickEvent.Phase.START && event.side == Side.SERVER) {
			
			/*if(TF2weapons.server.getTickCounter()%20 == 0) {
				System.out.println("TickTimeLiving: "+avg(tickTimeLiving));
				System.out.println("TickTimeTF2Mob: "+avg(tickTimeMercUpdate));
				System.out.println("TickTimeOther: "+avg(tickTimeOther));
				System.out.println("TickTimeTotal: "+avg(TF2weapons.server.tickTimeArray));
			}*/
			tickTimeOther[TF2weapons.server.getTickCounter()%20]=0;
			tickTimeLiving[TF2weapons.server.getTickCounter()%20]=0;
			tickTimeMercUpdate[TF2weapons.server.getTickCounter()%20]=0;
			long worldTime=event.world.getWorldTime();
			long nanoTickStart=System.nanoTime();
			if(worldTime%4==0){
				for (int i = 0; i < destroyProgress.size(); i++) {
					DestroyBlockEntry entry = destroyProgress.get(i);
	
					if (entry != null && entry.world == event.world) {
	
						entry.curDamage -= 0.05f;
						if (entry.curDamage <= 0 || entry.world.isAirBlock(entry.pos)) {
							destroyProgress.set(i, null);
							event.world.sendBlockBreakProgress(Math.min(Integer.MAX_VALUE, 0xFFFF + i), entry.pos, -1);
							continue;
						}
	
						if (worldTime % 20 == 0) {
							int val = (int) ((entry.curDamage / TF2weapons.getHardness(entry.world.getBlockState(entry.pos), entry.world, entry.pos)) * 10);
							event.world.sendBlockBreakProgress(Math.min(Integer.MAX_VALUE, 0xFFFF + i), entry.pos, val);
						}
					}
				}
			}
			if (!TF2weapons.disableEvent){
				int dayTime=(int) (worldTime % 24000);
				if (dayTime == 1){
					TF2WorldStorage events=event.world.getCapability(TF2weapons.WORLD_CAP, null);
					if (events!=null){//(TF2WorldStorage) event.world.getPerWorldStorage().getOrLoadData(TF2WorldStorage.class, TF2weapons.MOD_ID);
						if (events.eventFlag == 1) {
							for (EntityPlayer player : event.world.playerEntities) {
								player.sendMessage(new TextComponentString("The event has just ended"));
							}
							events.eventFlag=0;
						} else if (new Random(event.world.getSeed() + worldTime * worldTime * 4987142 + worldTime * 5947611)
								.nextInt(20) == 0) {
							for (EntityPlayer player : event.world.playerEntities) {
								player.sendMessage(new TextComponentString("A crowd of RED and BLU mercenaries invades the area"));
							}
							events.eventFlag=1;
						}
					}
				}
				if (dayTime >= 14000 && dayTime <= 21000 && dayTime % 1000 == 0 && event.world.getCurrentMoonPhaseFactor() == 1
						&& worldTime > 24000) {
					for (EntityPlayer player : event.world.playerEntities)
						if (player.getCapability(TF2weapons.PLAYER_CAP, null).nextBossTicks <= worldTime
								&& event.world.getEntitiesWithinAABB(EntityTF2Boss.class, player.getEntityBoundingBox().grow(200, 200, 200)).isEmpty()) {
							player.getCapability(TF2weapons.PLAYER_CAP, null).nextBossTicks = (int) (worldTime + Math.min(40000,TF2weapons.bossReappear)
							+ player.getRNG().nextInt(TF2weapons.bossReappear-40000));
							EntityTF2Boss boss;
							switch(player.getRNG().nextInt(3)){
							case 0: boss= new EntityMonoculus(event.world);break;
							case 1: boss= new EntityHHH(event.world);break;
							default: boss= new EntityMerasmus(event.world);break;
							}
							
							BlockPos spawnPos = null;
							int i = 0;
							do {
								i++;
								spawnPos = event.world.getTopSolidOrLiquidBlock(player.getPosition().add(player.getRNG().nextInt(48) - 24, 0, player.getRNG().nextInt(48) - 24));
								boss.setPosition(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
							} while (i < 6 && !event.world.getCollisionBoxes(null, boss.getEntityBoundingBox()).isEmpty());
	
							if(spawnPos!=null){
								boss.onInitialSpawn(event.world.getDifficultyForLocation(spawnPos), null);
								event.world.spawnEntity(boss);
							}
						}
				}
			}
			tickTimeOther[TF2weapons.server.getTickCounter()%20]+=System.nanoTime()-nanoTickStart;
		}
	}

	public static boolean isSpawnEvent(World world) {
		return world.hasCapability(TF2weapons.WORLD_CAP, null) && world.getCapability(TF2weapons.WORLD_CAP, null).eventFlag == 1;
	}

	/*
	 * @SubscribeEvent public void spawnCharacters(TickEvent.WorldTickEvent
	 * event){ if(!event.getWorld().isRemote &&
	 * event.phase==TickEvent.Phase.END){
	 * 
	 * //if(time!=0&&event.getWorld().rand.nextInt(2500/time)!=0) return;
	 * this.eligibleChunksForSpawning.clear(); int i; int k;
	 * 
	 * for (i = 0; i < event.getWorld().playerEntities.size(); ++i) {
	 * EntityPlayer entityplayer =
	 * (EntityPlayer)event.getWorld().playerEntities.get(i); int j =
	 * MathHelper.floor(entityplayer.posX / 16.0D); k =
	 * MathHelper.floor(entityplayer.posZ / 16.0D); byte b0 = 8;
	 * 
	 * for (int l = -b0; l <= b0; ++l) { for (int i1 = -b0; i1 <= b0; ++i1) {
	 * boolean flag3 = l == -b0 || l == b0 || i1 == -b0 || i1 == b0;
	 * ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(l + j, i1 +
	 * k);
	 * 
	 * if (!flag3) { this.eligibleChunksForSpawning.put(chunkcoordintpair,
	 * Boolean.valueOf(false)); } else if
	 * (!this.eligibleChunksForSpawning.containsKey(chunkcoordintpair)) {
	 * this.eligibleChunksForSpawning.put(chunkcoordintpair,
	 * Boolean.valueOf(true)); } } } }
	 * 
	 * i = 0; ChunkCoordinates chunkcoordinates =
	 * event.getWorld().getSpawnPoint(); Iterator iterator =
	 * this.eligibleChunksForSpawning.keySet().iterator();
	 * ArrayList<ChunkCoordIntPair> tmp = new
	 * ArrayList(eligibleChunksForSpawning.keySet()); Collections.shuffle(tmp);
	 * iterator = tmp.iterator(); label110:
	 * 
	 * while (iterator.hasNext()) { ChunkCoordIntPair chunkcoordintpair1 =
	 * (ChunkCoordIntPair)iterator.next();
	 * 
	 * if (!((Boolean)this.eligibleChunksForSpawning.get(chunkcoordintpair1)).
	 * booleanValue()) { Chunk chunk =
	 * event.getWorld().getChunkFromChunkCoords(chunkcoordintpair1.chunkXPos,
	 * chunkcoordintpair1.chunkZPos); int x = chunkcoordintpair1.chunkXPos * 16
	 * + event.getWorld().rand.nextInt(16); int z = chunkcoordintpair1.chunkZPos
	 * * 16 + event.getWorld().rand.nextInt(16); int y =
	 * event.getWorld().rand.nextInt(chunk == null ?
	 * event.getWorld().getActualHeight() : chunk.getTopFilledSegment() + 16 -
	 * 1); System.out.println("tick2"); if (!event.getWorld().getBlock(x, y,
	 * z).isNormalCube() && event.getWorld().getBlock(x, y, z).getMaterial() ==
	 * Material.air) { int i2 = 0; int j2 = 0; int
	 * team=event.getWorld().rand.nextInt(2); System.out.println("tick3"); while
	 * (j2 < 1) { int k2 = x; int l2 = y; int i3 = z; byte b1 = 6;
	 * IEntityLivingData ientitylivingdata = null; int j3 = 0;
	 * System.out.println("tick4"); while (true) { System.out.println("tick5");
	 * if (j3 < 4) { label103: { k2 += event.getWorld().rand.nextInt(b1) -
	 * event.getWorld().rand.nextInt(b1); l2 += event.getWorld().rand.nextInt(1)
	 * - event.getWorld().rand.nextInt(1); i3 +=
	 * event.getWorld().rand.nextInt(b1) - event.getWorld().rand.nextInt(b1);
	 * 
	 * if (canCreatureTypeSpawnAtLocation(EnumCreatureType.monster,
	 * event.getWorld(), k2, l2, i3)) {System.out.println("tick6"); float f =
	 * (float)k2 + 0.5F; float f1 = (float)l2; float f2 = (float)i3 + 0.5F;
	 * 
	 * if (event.getWorld().getClosestPlayer((double)f, (double)f1, (double)f2,
	 * 24.0D) == null) { System.out.println("tick7"); float f3 = f -
	 * (float)chunkcoordinates.posX; float f4 = f1 -
	 * (float)chunkcoordinates.posY; float f5 = f2 -
	 * (float)chunkcoordinates.posZ; float f6 = f3 * f3 + f4 * f4 + f5 * f5;
	 * 
	 * if (f6 >= 576.0F) { EntityTF2Character entityliving=null;
	 * 
	 * System.out.println("tick8"); try { switch
	 * (event.getWorld().rand.nextInt(2)){ case 1:entityliving = new
	 * EntityScout(event.getWorld()); default:entityliving = new
	 * EntityHeavy(event.getWorld()); } entityliving.setEntTeam(team); } catch
	 * (Exception exception) { exception.printStackTrace(); }
	 * 
	 * entityliving.setLocationAndAngles((double)f, (double)f1, (double)f2,
	 * event.getWorld().rand.nextFloat() * 360.0F, 0.0F);
	 * 
	 * Result canSpawn = ForgeEventFactory.canEntitySpawn(entityliving,
	 * event.getWorld(), f, f1, f2); if (canSpawn == Result.ALLOW || (canSpawn
	 * == Result.DEFAULT && entityliving.getCanSpawnHere())) {
	 * System.out.println("tick9"); ++i2;
	 * event.getWorld().spawnEntity(entityliving); if
	 * (!ForgeEventFactory.doSpecialSpawn(entityliving, event.getWorld(), f, f1,
	 * f2)) { ientitylivingdata =
	 * entityliving.onSpawnWithEgg(ientitylivingdata); }
	 * 
	 * if (j2 >= ForgeEventFactory.getMaxSpawnPackSize(entityliving)) { continue
	 * label110; } }
	 * 
	 * i += i2; } } }
	 * 
	 * ++j3; continue; } } j2++; break; } } } } } } } public static boolean
	 * canCreatureTypeSpawnAtLocation(EnumCreatureType p_77190_0_, World
	 * p_77190_1_, int p_77190_2_, int p_77190_3_, int p_77190_4_) { if
	 * (!World.doesBlockHaveSolidTopSurface(p_77190_1_, p_77190_2_, p_77190_3_ -
	 * 1, p_77190_4_)) { return false; } else { Block block =
	 * p_77190_1_.getBlock(p_77190_2_, p_77190_3_ - 1, p_77190_4_); boolean
	 * spawnBlock = block.canCreatureSpawn(p_77190_0_, p_77190_1_, p_77190_2_,
	 * p_77190_3_ - 1, p_77190_4_); return spawnBlock && block != Blocks.bedrock
	 * && !p_77190_1_.getBlock(p_77190_2_, p_77190_3_,
	 * p_77190_4_).isNormalCube() && !p_77190_1_.getBlock(p_77190_2_,
	 * p_77190_3_, p_77190_4_).getMaterial().isLiquid() &&
	 * !p_77190_1_.getBlock(p_77190_2_, p_77190_3_ + 1,
	 * p_77190_4_).isNormalCube(); } }
	 */
	@SubscribeEvent
	public void stopHurt(LivingAttackEvent event) {
		if (event.getSource().getTrueSource() != null && event.getSource().getTrueSource() instanceof EntityLivingBase &&
				(event.getSource().damageType.equals("mob") || event.getSource().damageType.equals("player"))) {
			EntityLivingBase damageSource = (EntityLivingBase) event.getSource().getTrueSource();
			if (damageSource.getActivePotionEffect(TF2weapons.stun) != null || damageSource.getActivePotionEffect(TF2weapons.bonk) != null) {
				event.setCanceled(true);
			}
			if (damageSource.hasCapability(TF2weapons.WEAPONS_CAP, null) && damageSource.getCapability(TF2weapons.WEAPONS_CAP, null).invisTicks > 0) {
				event.setCanceled(true);
			}
			if (damageSource.hasCapability(TF2weapons.WEAPONS_CAP, null) && WeaponsCapability.get(damageSource).isDisguised()) {
				disguise(damageSource, false);
			}
		}

		if (!event.isCanceled() && event.getAmount() > 0) {
			/*
			 * if(event.getEntity().getEntityData().getByte("IsCloaked")!=0){
			 * event.getEntity().getEntityData().setInteger("VisTicks",
			 * Math.min(10,event.getEntity().getEntityData().getInteger(
			 * "VisTicks"))); event.getEntity().setInvisible(false);
			 * //System.out.println("notInvisible"); }
			 */
			event.getEntityLiving().getEntityData().setInteger("lasthit", event.getEntityLiving().ticksExisted);
		}

	}

	@SubscribeEvent
	public void clonePlayer(final PlayerEvent.Clone event) {
		InventoryWearables oldInv = event.getOriginal().getCapability(TF2weapons.INVENTORY_CAP, null);
		InventoryWearables newInv = event.getEntityPlayer().getCapability(TF2weapons.INVENTORY_CAP, null);
		for (int i = 0; i < 3; i++) {
			newInv.setInventorySlotContents(i, oldInv.getStackInSlot(i));
		}
		for (Entry<Class<? extends Entity>, Short> entry : event.getOriginal().getCapability(TF2weapons.PLAYER_CAP, null).highestBossLevel.entrySet()) {
			event.getEntityPlayer().getCapability(TF2weapons.PLAYER_CAP, null).highestBossLevel.put(entry.getKey(), entry.getValue());
		}

		event.getEntityPlayer().getCapability(TF2weapons.PLAYER_CAP, null).contracts=event.getOriginal().getCapability(TF2weapons.PLAYER_CAP, null).contracts;
		event.getEntityPlayer().getCapability(TF2weapons.PLAYER_CAP, null).newContracts=event.getOriginal().getCapability(TF2weapons.PLAYER_CAP, null).newContracts;
		event.getEntityPlayer().getCapability(TF2weapons.PLAYER_CAP, null).nextContractDay=event.getOriginal().getCapability(TF2weapons.PLAYER_CAP, null).nextContractDay;
		if(event.getEntityPlayer() != null)
			if(event.getEntityPlayer() instanceof EntityPlayerMP) {
				event.getEntityPlayer().getCapability(TF2weapons.PLAYER_CAP, null).sendContractsNextTick=true;
		}
	}

	@SubscribeEvent
	public void uber(LivingHurtEvent event) {
		/*
		 * if(event.getEntity().getEntityData().getByte("IsCloaked")!=0){
		 * event.getEntity().getEntityData().setInteger("VisTicks",
		 * Math.min(10,event.getEntity().getEntityData().getInteger("VisTicks"))
		 * ); event.getEntity().setInvisible(false);
		 * //System.out.println("notInvisible"); }
		 */
		EntityLivingBase living=null;
		if(event.getSource().getTrueSource() != null && event.getSource().getTrueSource() instanceof EntityLivingBase){
			living=(EntityLivingBase) event.getSource().getTrueSource();
		}
		if (event.getEntityLiving().getActivePotionEffect(TF2weapons.crit) != null) {
			event.setAmount(event.getAmount() * 1.1f);
		}
		if (event.getEntityLiving().hasCapability(TF2weapons.WEAPONS_CAP, null) && event.getAmount()>1 &&event.getSource() == DamageSource.FALL 
				&& event.getEntityLiving().getCapability(TF2weapons.WEAPONS_CAP, null).isExpJump()) {
			event.setAmount((float) Math.sqrt(event.getAmount()));
		}
		for (ItemStack stack : event.getEntityLiving().getEquipmentAndArmor())
			if (!stack.isEmpty()) {
				// System.out.println("Damaged");
				float initialDamage=event.getAmount();
				event.setAmount(TF2Attribute.getModifier("Damage Resist", stack, event.getAmount(), event.getEntityLiving()));
				if (event.getSource().isExplosion()) {
					event.setAmount(TF2Attribute.getModifier("Explosion Resist", stack, event.getAmount(), event.getEntityLiving()));
				}
				// System.out.println("Absorbed:
				// "+TF2Attribute.getModifier("Explosion Resist", stack, 1,
				// event.getEntityLiving()));
				if (event.getSource().isFireDamage()) {
					event.setAmount(TF2Attribute.getModifier("Fire Resist", stack, event.getAmount(), event.getEntityLiving()));
				}
				
				if(initialDamage != event.getAmount()){
					float mult=TF2Attribute.getModifier("Breakable", stack, 0, event.getEntityLiving());
					if(mult!=0)
						stack.damageItem(MathHelper.ceil((initialDamage-event.getAmount())*mult), event.getEntityLiving());
				}
			}
		if (event.getEntityLiving().getActivePotionEffect(TF2weapons.backup) != null) {
			if (event.getSource().getImmediateSource() instanceof EntityArrow) {
				event.setAmount(Math.min(event.getAmount(), 8f));
			}
			if ((event.getSource().damageType.equals("mob") || event.getSource().damageType.equals("player")) && (living != null && living.getAttributeMap()
							.getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE).getModifier(UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF")) != null)) {
				event.setAmount((float) Math.min(event.getAmount(),
						1 + living.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE)
								.getModifier(UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF"))
								.getAmount()))/*
												 * ((EntityLivingBase)event.
												 * getSource().getEntity()).
												 * getHeldItemMainhand().
												 * getAttributeModifiers(
												 * EntityEquipmentSlot.MAINHAND)
												 * .get(SharedMonsterAttributes.
												 * ATTACK_DAMAGE.
												 * getAttributeUnlocalizedName()
												 * ).toArray(new
												 * AttributeModifier[2])[0].))
												 */;
			}
			event.setAmount(event.getAmount() * 0.65f);
		}
		if (event.getEntityLiving() instanceof EntityTF2Character && event.getSource().getTrueSource() != null && event.getSource().getTrueSource() == event.getEntity()) {
			event.setAmount(event.getAmount() * 0.35f);
		}

		if ((event.getEntityLiving().getActivePotionEffect(TF2weapons.bonk) != null || event.getEntityLiving().getActivePotionEffect(TF2weapons.uber)!=null)
				&& !event.getSource().canHarmInCreative()) {
			event.setCanceled(true);
			if (event.getEntityLiving() instanceof EntityPlayer && event.getEntityLiving().getActivePotionEffect(TF2weapons.bonk) != null) {
				event.getEntityLiving().getCapability(TF2weapons.PLAYER_CAP, null).dodgedDmg+=event.getAmount();
				/*if(event.getEntityLiving().getCapability(TF2weapons.PLAYER_CAP, null).dodgedDmg>100){
					((EntityPlayer)event.getEntityLiving()).addStat(TF2Achievements.DODGE_DAMAGE);
				}*/
			}
			return;
		}
		int crit=0;
		if (living != null && living.hasCapability(TF2weapons.WEAPONS_CAP, null)) {
			if (living.getActivePotionEffect(TF2weapons.it) != null && (event.getSource().damageType.equals("mob") || event.getSource().damageType.equals("player"))){
				living.removePotionEffect(TF2weapons.it);
				living.getCapability(TF2weapons.WEAPONS_CAP, null).itProtection=15;
				event.getEntityLiving().addPotionEffect(new PotionEffect(TF2weapons.it,600));
			}
			if (living.getActivePotionEffect(TF2weapons.bonk) != null)
				event.setCanceled(true);
			
			ItemStack stack=living==event.getSource().getImmediateSource()?living.getHeldItemMainhand():ItemStack.EMPTY;
			crit = TF2weapons.calculateCritPre(stack, living);
				
			ItemStack backpack = ItemHorn.getBackpack(living);
			if (!backpack.isEmpty() && !backpack.getTagCompound().getBoolean("Active")) {
				((ItemSoldierBackpack) backpack.getItem()).addRage(backpack, event.getAmount(), event.getEntityLiving());
			}
			if (living.getActivePotionEffect(TF2weapons.conch) != null) {
				living.heal(0.35f * getDamageReduction(event.getSource(), event.getEntityLiving(), event.getAmount()));
			}
			if (event.getEntityLiving().getActivePotionEffect(TF2weapons.madmilk) != null) {
				living.heal(0.6f * getDamageReduction(event.getSource(), event.getEntityLiving(), event.getAmount()));
			}
		}
		if (!(event.getSource() instanceof TF2DamageSource)) {
			crit=TF2weapons.calculateCritPost(event.getEntityLiving(),living,crit, ItemStack.EMPTY);
			if (crit == 1) {
				event.setAmount(event.getAmount() * 1.35f);
				TF2weapons.playSound(event.getEntityLiving(), TF2Sounds.MISC_MINI_CRIT, 1.5F, 1.2F / (event.getEntityLiving().getRNG().nextFloat() * 0.2F + 0.9F));
			} else if (crit == 2) {
				event.setAmount(event.getAmount() * 2f);
				TF2weapons.playSound(event.getEntityLiving(), TF2Sounds.MISC_CRIT, 1.5F, 1.2F / (event.getEntityLiving().getRNG().nextFloat() * 0.2F + 0.9F));
			}
		}
		if (!event.getEntityLiving().world.isRemote && event.getEntityLiving().getDataManager().get(ENTITY_OVERHEAL) > 0) {
			event.getEntityLiving().getDataManager().set(ENTITY_OVERHEAL, event.getEntityLiving().getAbsorptionAmount());
			if (event.getEntityLiving().getDataManager().get(ENTITY_OVERHEAL) <= 0) {
				event.getEntityLiving().getDataManager().set(ENTITY_OVERHEAL, -1f);
			}
		}
	}

	public static float getDamageReduction(DamageSource source, EntityLivingBase living, float damage) {
		return CombatRules.getDamageAfterMagicAbsorb(
				CombatRules.getDamageAfterAbsorb(damage, living.getTotalArmorValue(),
						(float) living.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue()),
				EnchantmentHelper.getEnchantmentModifierDamage(living.getArmorInventoryList(), source));
	}

	@SubscribeEvent
	public void stopBreak(BlockEvent.BreakEvent event) {

		if (event.getPlayer().getHeldItem(EnumHand.MAIN_HAND) != null && event.getPlayer().getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemUsable) {
			event.setCanceled(true);
		}
		if (event.getPlayer().getActivePotionEffect(TF2weapons.bonk) != null) {
			event.setCanceled(true);
		}
		if (event.getPlayer().getCapability(TF2weapons.WEAPONS_CAP, null).invisTicks > 0) {
			event.setCanceled(true);
		}
		if (event.getPlayer().getActivePotionEffect(TF2weapons.stun) != null) {
			event.setCanceled(true);
		}
		if (WeaponsCapability.get(event.getPlayer()).isDisguised()) {
			disguise(event.getPlayer(), false);
		}
	}

	/*
	 * @SubscribeEvent public void stopInteract(PlayerInteractEvent event){
	 * if(!((event.==PlayerInteractEvent.Action.RIGHT_CLICK_AIR||event.action==
	 * PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)&&event.getEntity()Player.
	 * getHeldItem(EnumHand.MAIN_HAND)!=null&&(event.getEntity()Player.
	 * getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemCloak ||
	 * event.getEntity()Player.getHeldItem(EnumHand.MAIN_HAND).getItem()
	 * instanceof ItemDisguiseKit))){
	 * if(event.getEntity()Player.getEntityData().getByte("Disguised")!=0){
	 * disguise(event.getEntity()Player,false); }
	 * if((event.getEntity()Player.getHeldItem(EnumHand.MAIN_HAND) !=
	 * null&&event.getEntity()Player.getHeldItem(EnumHand.MAIN_HAND).getItem()
	 * instanceof ItemUsable &&
	 * event.action==PlayerInteractEvent.Action.LEFT_CLICK_BLOCK)||event.
	 * getEntity()Player.getEntityData().getInteger("VisTicks")!=0){
	 * event.setCanceled(true); } } }
	 */
	@SubscribeEvent
	public void startTracking(PlayerEvent.StartTracking event) {
		if (event.getTarget() instanceof EntityPlayer && !event.getTarget().world.isRemote) {
			// System.out.println("Tracking");
			InventoryWearables inv = event.getTarget().getCapability(TF2weapons.INVENTORY_CAP, null);
			TF2weapons.network.sendTo(new TF2Message.WearableChangeMessage((EntityPlayer) event.getTarget(), 0, inv.getStackInSlot(0)), (EntityPlayerMP) event.getEntityPlayer());
			TF2weapons.network.sendTo(new TF2Message.WearableChangeMessage((EntityPlayer) event.getTarget(), 1, inv.getStackInSlot(1)), (EntityPlayerMP) event.getEntityPlayer());
			TF2weapons.network.sendTo(new TF2Message.WearableChangeMessage((EntityPlayer) event.getTarget(), 2, inv.getStackInSlot(2)), (EntityPlayerMP) event.getEntityPlayer());
			TF2weapons.network.sendTo(new TF2Message.WearableChangeMessage((EntityPlayer) event.getTarget(), 3, inv.getStackInSlot(3)), (EntityPlayerMP) event.getEntityPlayer());
		}
		/*
		 * if(event.getEntity() instanceof EntityPlayer){
		 * if(event.getEntity().world.isRemote){
		 * TF2weapons.network.sendToServer(new TF2Message.ActionMessage(99,
		 * (EntityLivingBase) event.getEntity())); } }
		 */

		/*
		 * if(event.getEntity().getDataManager().get(ENTITY_DISGUISE_TICKS)==40)
		 * { disguise((EntityLivingBase) event.getEntity(), true); }
		 */
	}

	@SubscribeEvent
	public void onSpawn(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof EntityPlayer){
			if (event.getEntity().world.isRemote) {
				TF2weapons.network.sendToServer(new TF2Message.ActionMessage(99, (EntityLivingBase) event.getEntity()));
			}
			if (event.getEntity().world != null && !event.getEntity().world.isRemote && event.getEntity() instanceof EntityPlayerMP){
				EntityPlayerMP player=((EntityPlayerMP)event.getEntity());
				player.inventoryContainer.addListener(new TF2ContainerListener(player));
			}
		}
	}

	@SubscribeEvent
	public void entityConstructing(final EntityEvent.EntityConstructing event) {

		
		if (event.getEntity() instanceof EntityLivingBase) {
			//event.getEntity().getDataManager().register(ENTITY_UBER, false);
			event.getEntity().getDataManager().register(ENTITY_OVERHEAL, 0f);
		}
	}

	/*
	 * @SubscribeEvent public void ChunkLoad(ChunkEvent.Load event){
	 * if(!event.getWorld().isRemote){ List<EntityTeleporter>
	 * teleporter=event.getWorld().getEntitiesWithinAABB(EntityTeleporter.class,
	 * new AxisAligned())); } }
	 */
	@SubscribeEvent
	public void cleanPlayer(PlayerLoggedOutEvent event) {
		ItemUsable.lastDamage.remove(event.player);
	}

	@SubscribeEvent
	public void loadPlayer(PlayerLoggedInEvent event) {
		// System.out.println("LoggedIn");
		if (TF2weapons.server.isDedicatedServer() || Minecraft.getMinecraft().getIntegratedServer().getPublic()) {
			TF2weapons.network.sendTo(new TF2Message.WeaponDataMessage(TF2weapons.itemDataCompressed), (EntityPlayerMP) event.player);
		}
		int i=0;
		for (Contract contract:event.player.getCapability(TF2weapons.PLAYER_CAP, null).contracts) {
			TF2weapons.network.sendTo(new TF2Message.ContractMessage(i, contract), (EntityPlayerMP) event.player);
			i++;
		}
	}

	/*
	 * @SubscribeEvent public void onConnect(ServerConnectionFromClientEvent
	 * event){ new NetHandlerPlayServer(MinecraftServer.getServer(),
	 * event.manager, ((NetHandlerPlayServer)event.handler).playerEntity); }
	 */
	/*
	 * public static class PacketReceiveHack extends
	 * SimpleChannelInboundHandler<Packet>{
	 * 
	 * @Override protected void channelRead0(ChannelHandlerContext ctx, Packet
	 * msg) throws Exception { System.out.println(msg); }
	 * 
	 * }
	 */
	/*
	 * public static class MovePacketHack extends NetHandlerPlayServer{
	 * 
	 * public MovePacketHack(MinecraftServer server, NetworkManager
	 * networkManagerIn, EntityPlayerMP playerIn) { super(server,
	 * networkManagerIn, playerIn); // TODO Auto-generated constructor stub }
	 * public void processPlayer(C03PacketPlayer packetIn) {
	 * System.out.println("send"); super.processPlayer(packetIn); } }
	 */
	@SubscribeEvent
	public void stopUsing(PlayerInteractEvent.RightClickBlock event) {
		if (event.getEntityPlayer().getActivePotionEffect(TF2weapons.stun) != null) {
			event.setCanceled(true);
		}
		if (event.getEntity().getCapability(TF2weapons.WEAPONS_CAP, null).invisTicks > 0) {
			event.setCanceled(true);
		}
		event.getEntityPlayer().removePotionEffect(TF2weapons.charging);
	}

	@SubscribeEvent
	public void stopUsing(PlayerInteractEvent.RightClickItem event) {
		ItemStack item = event.getEntityPlayer().getHeldItem(event.getHand());
		if (event.getEntityPlayer().getActivePotionEffect(TF2weapons.stun) != null) {
			event.setCanceled(true);
		}
		if (event.getEntity().getCapability(TF2weapons.WEAPONS_CAP, null).invisTicks > 0 && !(item.getItem() instanceof ItemDisguiseKit)
				&& !(item.getItem() instanceof ItemCloak && item.getTagCompound().getBoolean("Active"))) {
			event.setCanceled(true);
		}
		if (WeaponsCapability.get(event.getEntity()).isDisguised() 
				&& !(item.getItem() instanceof ItemFood || item.getItem() instanceof ItemCloak || item.getItem() instanceof ItemDisguiseKit)) {
			disguise(event.getEntityPlayer(), false);
		}
		if (event.getEntityLiving().getActivePotionEffect(TF2weapons.bonk) != null) {
			event.setCanceled(true);
		}
		event.getEntityPlayer().removePotionEffect(TF2weapons.charging);
	}

	@SubscribeEvent
	public void stopUsing(PlayerInteractEvent.EntityInteract event) {

		if (event.getEntityPlayer().getActivePotionEffect(TF2weapons.stun) != null) {
			event.setCanceled(true);
		}
		if (event.getEntity().getCapability(TF2weapons.WEAPONS_CAP, null).invisTicks > 0) {
			event.setCanceled(true);
		}
		if (WeaponsCapability.get(event.getEntity()).isDisguised() && !(event.getEntityPlayer().getHeldItem(event.getHand()).getItem() instanceof ItemFood)) {
			disguise(event.getEntityPlayer(), false);
		}
		if (event.getEntityLiving().getActivePotionEffect(TF2weapons.bonk) != null) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void stopUsing(PlayerInteractEvent.LeftClickBlock event) {
		if (event.getEntityPlayer().getActivePotionEffect(TF2weapons.stun) != null) {
			event.setCanceled(true);
		}
		if (event.getEntity().getCapability(TF2weapons.WEAPONS_CAP, null).invisTicks > 0) {
			event.setCanceled(true);
		}
		event.getEntityPlayer().removePotionEffect(TF2weapons.charging);
	}

	@SubscribeEvent
	public void stopJump(LivingEvent.LivingJumpEvent event) {

		EntityLivingBase living=event.getEntityLiving();
		if ((living.getActivePotionEffect(TF2weapons.stun) != null && living.getActivePotionEffect(TF2weapons.bombmrs) == null)
			|| living.getActivePotionEffect(TF2weapons.charging) != null
				|| (living.getHeldItemMainhand() != null && living.getHeldItemMainhand().getItem() instanceof ItemMinigun
						&& living.hasCapability(TF2weapons.WEAPONS_CAP, null) && living.getCapability(TF2weapons.WEAPONS_CAP, null).chargeTicks > 0)) {
			living.isAirBorne = false;
			living.motionY -= 0.5f;
			if (living.isSprinting()) {
				float f = living.rotationYaw * 0.017453292F;
				living.motionX += MathHelper.sin(f) * 0.2F;
				living.motionZ -= MathHelper.cos(f) * 0.2F;
			}
		}

	}

	public static void disguise(EntityLivingBase entity, boolean active) {
		WeaponsCapability.get(entity).setDisguised(active);
		entity.getCapability(TF2weapons.WEAPONS_CAP, null).disguiseTicks = 0;
		if(active && entity.getCapability(TF2weapons.WEAPONS_CAP, null).invisTicks < 20 && entity.world instanceof WorldServer)
			((WorldServer)entity.world).spawnParticle(EnumParticleTypes.SMOKE_LARGE, entity.posX, entity.posY, entity.posZ, 20, 0.2, 1, 0.2, 0.04f, new int[0]);
		// System.out.println("disguised: "+active);
		/*
		 * if(!entity.world.isRemote){ TF2weapons.sendTracking(new
		 * TF2Message.PropertyMessage("Disguised",
		 * (byte)(active?1:0),entity),entity); }
		 */
		// ItemCloak.setInvisiblity(entity);
		/*
		 * if(entity.world instanceof WorldServer && active){ if(active){
		 * EntityCreeper creeper=new EntityCreeper(entity.world);
		 * creeper.tasks.taskEntries.clear();
		 * creeper.targetTasks.taskEntries.clear(); fakeEntities.put(entity,
		 * creeper); creeper.setPositionAndRotation(entity.posX, entity.posY,
		 * entity.posZ, entity.rotationYaw, entity.rotationPitch);
		 * entity.world.spawnEntity(creeper);
		 * //((WorldServer)event.getEntity().world).getEntityTracker().
		 * untrackEntity(event.getEntity()); } else{ EntityLivingBase
		 * ent=fakeEntities.remove(entity); if(ent!=null){ ent.setDead(); } } }
		 * if(entity.world.isRemote){ if(active){ EntityCreeper creeper=new
		 * EntityCreeper(entity.world); creeper.tasks.taskEntries.clear();
		 * creeper.targetTasks.taskEntries.clear(); fakeEntities.put(entity,
		 * creeper); creeper.setPositionAndRotation(entity.posX, entity.posY,
		 * entity.posZ, entity.rotationYaw, entity.rotationPitch);
		 * //((WorldServer)event.getEntity().world).getEntityTracker().
		 * untrackEntity(event.getEntity()); System.out.println("Disguise"); }
		 * else{ EntityLivingBase ent=fakeEntities.remove(entity);
		 * if(ent!=null){ ent.setDead(); } } }
		 */
	}

	@SubscribeEvent
	public void livingUpdate(final LivingEvent.LivingUpdateEvent event) {
		final EntityLivingBase living = event.getEntityLiving();
		if (living.hasCapability(TF2weapons.INVENTORY_CAP, null)) {
			for (int i = 0; i < 3; i++) {
				ItemStack stack = living.getCapability(TF2weapons.INVENTORY_CAP, null).getStackInSlot(i);
				if(!stack.isEmpty() && stack.getItem() instanceof ItemWearable)
					((ItemWearable)stack.getItem()).onUpdateWearing(stack, living.world, living);
				
			}
				
		}
		if (living.isEntityAlive() && (living.hasCapability(TF2weapons.WEAPONS_CAP, null))) {

			long nanoTickStart=System.nanoTime();
			final WeaponsCapability cap = living.getCapability(TF2weapons.WEAPONS_CAP, null);
			cap.tick();
			
			if(living.hasCapability(TF2weapons.PLAYER_CAP, null))
				living.getCapability(TF2weapons.PLAYER_CAP, null).tick();

			if (cap.isExpJump()) {
				if (living.onGround) {
					cap.killsAirborne=0;
					cap.setExpJump(false);
				}
				if(living.isElytraFlying())
					cap.setExpJump(false);
				living.motionX = living.motionX * 1.035;
				living.motionZ = living.motionZ * 1.035;
			}
			if (cap.doubleJumped && living.onGround) {
				cap.doubleJumped = false;
			}
			/*
			 * if(living instanceof EntityPlayer){
			 * System.out.println("Invisible: "+living.isInvisible())
			 * ; }
			 */
			if (!living.world.isRemote && cap.disguiseTicks > 0){
				// System.out.println("disguise progress:
				// "+living.getEntityData().getByte("DisguiseTicks"));
				if(cap.invisTicks < 20)
					((WorldServer)living.world).spawnParticle(EnumParticleTypes.SMOKE_LARGE, living.posX, living.posY, living.posZ, 2, 0.2, 1, 0.2, 0.04f, new int[0]);
				if (++cap.disguiseTicks >= 40) {
				disguise(living, true);
				}
			}
			/*
			 * if(living.world.isRemote &&
			 * living.getEntityData().getByte("Disguised")==1&&(
			 * fakeEntities.get(living)==null||fakeEntities.get(living).isDead))
			 * { disguise(living,true); }
			 */
			PotionEffect charging = living.getActivePotionEffect(TF2weapons.charging);
			/*if(living instanceof EntityPlayerMP && ((EntityPlayerMP)living).getStatFile().readStat(StatList.getObjectUseStats(TF2weapons.itemSandvich))>=50){
				((EntityPlayer)living).addStat(TF2Achievements.SANDVICH);
			}*/
			if (living.world.isRemote) {
				
				ClientProxy.doChargeTick(living);
			}
			if (!living.world.isRemote && charging != null) {
				if (ItemChargingTarge.getChargingShield(living).isEmpty()) {
					living.removePotionEffect(TF2weapons.charging);
				}
				Vec3d start = living.getPositionVector().addVector(0, living.height / 2, 0);
				Vec3d end = start.addVector(-MathHelper.sin(living.rotationYaw / 180.0F * (float) Math.PI) * 0.7, 0,
						MathHelper.cos(living.rotationYaw / 180.0F * (float) Math.PI) * 0.7);
				// Vec3d
				// end=start.addVector(living.motionX*10,0,living.motionZ*10);
				// System.out.println("yay: "+living.motionX+"
				// "+living.motionZ);
				RayTraceResult result = TF2weapons.pierce(living.world, living, start.x, start.y, start.z, end.x, end.y, end.z, false, 0.5f, false)
						.get(0);
				if (result.entityHit != null) {
					float damage = 5;
					if (charging.getDuration() > 30) {
						damage *= 0.5f;
					}
					TF2weapons.dealDamage(result.entityHit, result.entityHit.world, living, ItemChargingTarge.getChargingShield(living), 0, damage,
							TF2weapons.causeDirectDamage(ItemChargingTarge.getChargingShield(living), living, 0));
					/*if(living instanceof EntityPlayer && !result.entityHit.isEntityAlive())
						((EntityPlayer)living).addStat(TF2Achievements.CHARGE_TARGE);*/
					cap.bashCritical = charging.getDuration() < 20;
					if(charging.getDuration()<12)
						TF2weapons.playSound(living, TF2Sounds.WEAPON_SHIELD_HIT_RANGE, 3F, 1F);
					else
						TF2weapons.playSound(living, TF2Sounds.WEAPON_SHIELD_HIT, 0.8F, 1F);
					cap.ticksBash = 20;
					living.motionX = 0;
					living.motionZ = 0;
					living.removePotionEffect(TF2weapons.charging);

				}
			}
			if (living.world.isRemote && WeaponsCapability.get(event.getEntity()).isDisguised()  != cap.lastDisgused) {
				cap.lastDisgused=WeaponsCapability.get(event.getEntity()).isDisguised();
				if(living instanceof EntityPlayer)
					((EntityPlayer)living).refreshDisplayName();
			}
			String disguisetype=cap.getDisguiseType();
			
			if (living.world.isRemote && !disguisetype.equals(cap.lastDisguiseValue)){
				if(living instanceof EntityPlayer) {
					((EntityPlayer)living).refreshDisplayName();
				}
				
				cap.lastDisguiseValue = disguisetype;
				if(cap.getDisguiseType().startsWith("P:")) {
					cap.skinDisguise = null;
					cap.skinType = DefaultPlayerSkin.getSkinType(living.getUniqueID());
					THREAD_POOL.submit(new Runnable() {

						@Override
						public void run() {
							GameProfile profile = TileEntitySkull
									.updateGameprofile(new GameProfile(null, cap.getDisguiseType().substring(2)));
							if (profile.getId() != null) {
								cap.skinType = DefaultPlayerSkin.getSkinType(profile.getId());
								cap.skinDisguise= DefaultPlayerSkin.getDefaultSkin(profile.getId());
							}
							Minecraft.getMinecraft().getSkinManager().loadProfileTextures(profile, new SkinManager.SkinAvailableCallback() {
								@Override
								public void skinAvailable(Type typeIn, ResourceLocation location, MinecraftProfileTexture profileTexture) {
									if (typeIn == Type.SKIN) {
										if (typeIn == Type.SKIN) {
											cap.skinDisguise = location;
										}
										cap.skinType = profileTexture.getMetadata("model");

										if (cap.skinType == null) {
											cap.skinType = "default";
										}
									}
								}
							}, false);
						}

					});
					
				}
			}
				

				/*
				 * Minecraft.getMinecraft().getSkinManager().loadSkin(new
				 * MinecraftProfileTexture(
				 * "http://skins.minecraft.net/MinecraftSkins/"+living.
				 * getDataManager().get(TF2EventBusListener.ENTITY_DISGUISE_TYPE
				 * ).substring(2)+".png",null), Type.SKIN,new
				 * SkinAvailableCallback(){
				 * 
				 * @Override public void skinAvailable(Type typeIn,
				 * ResourceLocation location, MinecraftProfileTexture
				 * profileTexture) { if(typeIn==Type.SKIN){
				 * cap.skinDisguise=location; System.out.println("RetrieveD"); }
				 * }
				 * 
				 * });
				 */
			if (living.world.isRemote && living != ClientProxy.getLocalPlayer()){
				//System.out.println("uber "+living.getActivePotionEffect(TF2weapons.uber).getDuration());
				Iterator<PotionEffect> iterator=living.getActivePotionEffects().iterator();
				while(iterator.hasNext()){
					PotionEffect effect=iterator.next();
					if(effect.getDuration()<=0 && (effect.getPotion() instanceof PotionTF2 || effect.getPotion() instanceof PotionTF2Item)){
						iterator.remove();
					}
				}
			}
			if (!living.world.isRemote && living.fallDistance > 0 && living.getItemStackFromSlot(EntityEquipmentSlot.FEET) != null
					&& living.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() == TF2weapons.itemMantreads) {
				for (EntityLivingBase target : living.world.getEntitiesWithinAABB(EntityLivingBase.class,
						living.getEntityBoundingBox().grow(0.25, -living.motionY, 0.25), new Predicate<EntityLivingBase>() {

							@Override
							public boolean apply(EntityLivingBase input) {
								// TODO Auto-generated method stub
								return input != living && !TF2weapons.isOnSameTeam(input, living);
							}

						})) {

					float damage = Math.max(0, living.fallDistance - 3) * 1.8f;
					living.fallDistance = 0;
					if (damage > 0) {
						target.attackEntityFrom(new EntityDamageSource("fallpl", living), damage);
						TF2weapons.playSound(living, TF2Sounds.WEAPON_MANTREADS, 1.5F, 1F);
					}
				}
			}
			if (cap.isInvisible()) {
				// System.out.println("cloak");
				boolean visible = living.hurtTime == 10;
				if (!visible) {
					List<Entity> closeEntities = living.world.getEntitiesInAABBexcluding(living, living.getEntityBoundingBox().grow(1, 2, 1),
							new Predicate<Entity>() {

								@Override
								public boolean apply(Entity input) {
									// TODO Auto-generated method stub
									return input instanceof EntityLivingBase && !TF2weapons.isOnSameTeam(living, input);
								}

							});
					for (Entity ent : closeEntities) {
						if (ent.getDistanceSqToEntity(living) < 1) {
							visible = true;
						}
						break;
					}
				}
				if (visible) {
					// System.out.println("reveal");
					cap.invisTicks = Math.min(10, cap.invisTicks);
					living.setInvisible(false);
				}
				if (living.getCapability(TF2weapons.WEAPONS_CAP, null).invisTicks < 20) {
					cap.invisTicks = Math.min(20, cap.invisTicks + 2);
				} else if (!living.isInvisible()) {
					// System.out.println("full");
					living.setInvisible(true);
				}
				boolean active = living.world.isRemote || !ItemCloak.searchForWatches(living).getSecond().isEmpty();
				if (!active) {
					cap.setInvisible(false);
					living.setInvisible(false);
					// System.out.println("decloak");
				}
			} else if (living.getCapability(TF2weapons.WEAPONS_CAP, null).invisTicks > 0) {
				cap.invisTicks--;
				if (living.getCapability(TF2weapons.WEAPONS_CAP, null).invisTicks == 0) {
					living.setInvisible(false);
				}
			}
			if(!living.world.isRemote)
				tickTimeLiving[TF2weapons.server.getTickCounter()%20]+=System.nanoTime()-nanoTickStart;
		}

		if (living.getRevengeTarget() != null && living.getRevengeTarget().hasCapability(TF2weapons.WEAPONS_CAP, null)
				&& living.getRevengeTarget().getCapability(TF2weapons.WEAPONS_CAP, null).invisTicks >= 20) {
			living.setRevengeTarget(null);
		}

		if (living instanceof EntityLiving && ((EntityLiving) living).getAttackTarget() != null
				&& ((EntityLiving) living).getAttackTarget().hasCapability(TF2weapons.WEAPONS_CAP, null)
				&& ((EntityLiving) living).getAttackTarget().getCapability(TF2weapons.WEAPONS_CAP, null).invisTicks >= 20) {
			((EntityLiving) living).setAttackTarget(null);
		}

		if (living.getDataManager().get(ENTITY_OVERHEAL) == -1) {
			living.getDataManager().set(ENTITY_OVERHEAL, 0F);
			living.setAbsorptionAmount(0);
		}
		if (living.getDataManager().get(ENTITY_OVERHEAL) > 0) {
			if (living.world.isRemote) {
				living.setAbsorptionAmount(living.getDataManager().get(ENTITY_OVERHEAL));
			}
			living.setAbsorptionAmount(living.getAbsorptionAmount() - living.getMaxHealth() * 0.001666f);
			if (!living.world.isRemote && living.ticksExisted%4==0)
				if (living.getAbsorptionAmount() <= 0) {
					living.getDataManager().set(ENTITY_OVERHEAL, -1f);
					//living.getEntityData().setFloat("Overheal", -1f);
				} else {
					living.getDataManager().set(ENTITY_OVERHEAL, living.getAbsorptionAmount());
				}
		}

		/*if (living.getActivePotionEffect(TF2weapons.uber)!=null && !(living.getHeldItem(EnumHand.MAIN_HAND) != null
				&& living.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemMedigun && living.getHeldItem(EnumHand.MAIN_HAND).getTagCompound().getBoolean("Activated"))) {
			List<EntityLivingBase> list = living.world.getEntitiesWithinAABB(EntityLivingBase.class,
					new AxisAlignedBB(living.posX - 8, living.posY - 8, living.posZ - 8, living.posX + 8, living.posY + 8, living.posZ + 8), new Predicate<EntityLivingBase>() {

						@Override
						public boolean apply(EntityLivingBase input) {
							// TODO Auto-generated method stub
							return input.world.getEntityByID(
									input.getCapability(TF2weapons.WEAPONS_CAP, null) != null ? input.getCapability(TF2weapons.WEAPONS_CAP, null).healTarget : -1) == living
									&& input.getHeldItem(EnumHand.MAIN_HAND).getTagCompound().getBoolean("Activated");
						}

					});
			boolean isOK = !list.isEmpty();
			if (!isOK) {
				living.getEntityData().setBoolean("Ubercharge", false);
			}
		}*/
	}

	@SubscribeEvent
	public void loadWorld(WorldEvent.Load event) {
		/*if (!event.getWorld().isRemote && event.getWorld().getPerWorldStorage().getOrLoadData(TF2WorldStorage.class, TF2weapons.MOD_ID)==null){
			event.getWorld().getPerWorldStorage().setData(TF2weapons.MOD_ID, new TF2WorldStorage());
			
		}*/
		if(!event.getWorld().getGameRules().hasRule("doTF2AI"))
			event.getWorld().getGameRules().addGameRule("doTF2AI", "true", ValueType.BOOLEAN_VALUE);
		if (!event.getWorld().isRemote && event.getWorld().getScoreboard().getTeam("RED") == null) {
			ScorePlayerTeam teamRed = event.getWorld().getScoreboard().createTeam("RED");
			ScorePlayerTeam teamBlu = event.getWorld().getScoreboard().createTeam("BLU");

			teamRed.setSeeFriendlyInvisiblesEnabled(true);
			teamRed.setAllowFriendlyFire(false);
			teamBlu.setSeeFriendlyInvisiblesEnabled(true);
			teamBlu.setAllowFriendlyFire(false);
			teamRed.setPrefix(TextFormatting.RED.toString());
			teamBlu.setPrefix(TextFormatting.BLUE.toString());
			event.getWorld().getScoreboard().broadcastTeamInfoUpdate(teamRed);
			event.getWorld().getScoreboard().broadcastTeamInfoUpdate(teamBlu);

		}
		if (!event.getWorld().isRemote && event.getWorld().getScoreboard().getTeam("TF2Bosses") == null) {
			ScorePlayerTeam teamBosses = event.getWorld().getScoreboard().createTeam("TF2Bosses");
			teamBosses.setSeeFriendlyInvisiblesEnabled(true);
			teamBosses.setAllowFriendlyFire(false);
			teamBosses.setPrefix(TextFormatting.DARK_PURPLE.toString());
			event.getWorld().getScoreboard().broadcastTeamInfoUpdate(teamBosses);
		}

	}

	@SubscribeEvent
	public void medicSpawn(LivingSpawnEvent.SpecialSpawn event) {
		float chance = 0;
		if (event.getEntity() instanceof EntityHeavy) {
			chance = 0.16f;
		} else if (event.getEntity() instanceof EntitySoldier) {
			chance = 0.08f;
		} else if (event.getEntity() instanceof EntityDemoman) {
			chance = 0.07f;
		} else if (event.getEntity() instanceof EntityPyro) {
			chance = 0.06f;
		} else
			return;
		if (event.getWorld().rand.nextFloat() < event.getWorld().getDifficulty().getDifficultyId() * chance) {
			EntityMedic medic = new EntityMedic(event.getWorld());
			medic.setLocationAndAngles(event.getEntity().posX + event.getWorld().rand.nextDouble() * 0.5 - 0.25, event.getEntity().posY,
					event.getEntity().posZ + event.getWorld().rand.nextDouble() * 0.5 - 0.25, event.getWorld().rand.nextFloat() * 360.0F, 0.0F);
			medic.natural = true;
			// medic.setEntTeam(event.getWorld().rand.nextInt(2));
			medic.onInitialSpawn(event.getWorld().getDifficultyForLocation(new BlockPos(event.getX(), event.getY(), event.getZ())), null);
			EntityTF2Character.nextEntTeam = medic.getEntTeam();

			event.getWorld().spawnEntity(medic);
		}
	}

	@SubscribeEvent
	public void attachCapabilityEnt(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof EntityPlayer || event.getObject() instanceof EntityTF2Character) {
			event.addCapability(new ResourceLocation(TF2weapons.MOD_ID, "weaponcapability"), new WeaponsCapability((EntityLivingBase) event.getObject()));
		}
		if (event.getObject() instanceof EntityPlayer) {
			event.addCapability(new ResourceLocation(TF2weapons.MOD_ID, "playercapability"), new TF2PlayerCapability((EntityPlayer) event.getObject()));
		}
		if (event.getObject() instanceof EntityPlayer && !event.getObject().hasCapability(TF2weapons.INVENTORY_CAP, null)) {
			final InventoryWearables inv=new InventoryWearables((EntityPlayer) event.getObject());
			
			event.addCapability(new ResourceLocation(TF2weapons.MOD_ID, "wearablescapability"), inv);
			
		}
	}
	@SubscribeEvent
	public void attachCapabilityWorld(AttachCapabilitiesEvent<World> event) {
		event.addCapability(new ResourceLocation(TF2weapons.MOD_ID, "tf2worldcapability"), new TF2WorldStorage());
	}
	
	@SubscribeEvent
	public void placeBanner(BlockEvent.PlaceEvent event) {
		TileEntity banner = event.getWorld().getTileEntity(event.getBlockSnapshot().getPos());
		if(banner != null && banner instanceof TileEntityBanner){
			if(((TileEntityBanner)banner).getPatternList().contains(TF2weapons.redPattern) || ((TileEntityBanner)banner).getPatternList().contains(TF2weapons.bluPattern)){
				event.getWorld().getCapability(TF2weapons.WORLD_CAP, null).banners.add(event.getPos());
			}
		}
	}
	
	@SubscribeEvent
	public void removeBanner(BlockEvent.BreakEvent event) {
		TileEntity banner = event.getWorld().getTileEntity(event.getPos());
		if(banner != null && banner instanceof TileEntityBanner){
			if(((TileEntityBanner)banner).getPatternList().contains(TF2weapons.redPattern)){
				event.getWorld().getCapability(TF2weapons.WORLD_CAP, null).banners.remove(event.getPos());
			}
		}
	}
	
	@SubscribeEvent
	public void craftItem(net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent event) {
		ItemStack withPattern=ItemStack.EMPTY;
		ItemStack noPattern=ItemStack.EMPTY;
		for(int i=0; i<event.craftMatrix.getSizeInventory();i++){
			ItemStack stack=event.craftMatrix.getStackInSlot(i);
			if(!stack.isEmpty() && stack.getItem() instanceof ItemBanner){
				if(TileEntityBanner.getPatterns(stack)>0){
					withPattern=stack;
				}
				else{
					noPattern=stack;
				}
				if(!withPattern.isEmpty()&&!noPattern.isEmpty()){
					withPattern.shrink(1);
				}
			}
		}
	}
	/*@SubscribeEvent
	public void attachCapabilityItem(AttachCapabilitiesEvent.Item event) {
		if (event.getItem() instanceof ItemFromData) {
			System.out.println("Adding cap");
			event.addCapability(new ResourceLocation(TF2weapons.MOD_ID, "weapondatacapability"), new WeaponData.WeaponDataCapability());
		}
	}*/
	@SubscribeEvent
	public void livingDeath(LivingDeathEvent event) {
		
		if (!event.getEntity().world.isRemote && event.getSource() != null && event.getSource().getTrueSource() != null
				&& event.getSource().getTrueSource() instanceof EntityLivingBase) {
			ItemStack stack = ItemStack.EMPTY;
			final EntityLivingBase living = (EntityLivingBase) event.getSource().getTrueSource();
			if (event.getSource() instanceof TF2DamageSource) {
				stack = ((TF2DamageSource) event.getSource()).getWeapon();
			} else {
				stack = living.getHeldItemMainhand();
			}
			if(living instanceof EntityPlayerMP && TF2weapons.isEnemy(living, event.getEntityLiving())){
				EntityPlayerMP player=(EntityPlayerMP)living;
				TF2PlayerCapability plcap = player.getCapability(TF2weapons.PLAYER_CAP, null);
				/*if(living.getCapability(TF2weapons.WEAPONS_CAP, null).doubleJumped){
					player.addStat(TF2Achievements.KILLED_DOUBLEJUMP);
					if(player.getStatFile().readStat(TF2Achievements.KILLED_DOUBLEJUMP)>=20){
						player.addStat(TF2Achievements.DOUBLE_JUMP_KILL);
					}
				}
				if(living.getDataManager().get(ENTITY_EXP_JUMP)){
					living.getCapability(TF2weapons.WEAPONS_CAP, null).killsAirborne++;
					if(living.getCapability(TF2weapons.WEAPONS_CAP, null).killsAirborne>=2){
						player.addStat(TF2Achievements.DEATH_FROM_ABOVE);
					}
				}
				if(event.getEntityLiving().getActivePotionEffect(TF2weapons.stun)!=null){
					player.addStat(TF2Achievements.KILLED_STUNNED);
					if(player.getStatFile().readStat(TF2Achievements.KILLED_STUNNED)>=40){
						player.addStat(TF2Achievements.KILL_STUNNED);
					}
				}
				if(event.getEntityLiving() instanceof EntitySpy && event.getEntityLiving().getDataManager().get(ENTITY_INVIS)
						&& ((EntitySpy)event.getEntityLiving()).prevHealth>=event.getEntityLiving().getMaxHealth()){
					player.addStat(TF2Achievements.KILL_SPY_CLOAKED);
				}
				if(!living.onGround && !event.getEntityLiving().onGround){
					player.addStat(TF2Achievements.WINGS_OF_GLORY);
				}
				if(!stack.isEmpty() && stack.getItem() instanceof ItemMeleeWeapon && ItemFromData.getData(stack).getString(PropertyType.MOB_TYPE).contains("heavy")){
					player.addStat(TF2Achievements.CRIT_PUNCH);
				}
				if(!stack.isEmpty() && ItemFromData.getData(stack).getName().contains("sentry")){
					player.addStat(TF2Achievements.KILLED_SENTRYGUN);
					if(event.getEntity() instanceof EntityTF2Character)
						plcap.completeObjective(Objective.KILL_W_SENTRY,stack);
					if(player.getStatFile().readStat(TF2Achievements.KILLED_SENTRYGUN)>=500)
						player.addStat(TF2Achievements.SENTRYGUN_KILLS);
					if(event.getEntityLiving() instanceof EntitySniper && living.getHeldItemMainhand() != null && living.getHeldItemMainhand().getItem() instanceof ItemWrangler){
						player.addStat(TF2Achievements.KILLED_WRANGLER_SNIPER);
						if(player.getStatFile().readStat(TF2Achievements.KILLED_WRANGLER_SNIPER)>=10)
							player.addStat(TF2Achievements.KILL_SNIPER_WRANGLER);
					}
				}*/
				if(event.getEntity() instanceof EntityTF2Character) {
					if((plcap.fastKillTimer+=120)>360) {
						plcap.fastKillTimer=0;
						plcap.completeObjective(Objective.KILLS,stack);
					}
					plcap.completeObjective(Objective.KILL,stack);
					if(event.getSource() instanceof TF2DamageSource && (((TF2DamageSource)event.getSource()).getAttackFlags()&TF2DamageSource.HEADSHOT)==TF2DamageSource.HEADSHOT) {
						plcap.completeObjective(Objective.HEADSHOT,stack);
					}
					if(event.getSource() instanceof TF2DamageSource && (((TF2DamageSource)event.getSource()).getAttackFlags()&TF2DamageSource.BACKSTAB)==TF2DamageSource.BACKSTAB) {
						plcap.completeObjective(Objective.BACKSTAB,stack);
					}
					if(event.getSource().getImmediateSource() instanceof EntityProjectileBase && ((EntityProjectileBase)event.getSource().getImmediateSource()).reflected) {
						plcap.completeObjective(Objective.KILL_REFLECTED,stack);
					}
					if(stack.getItem() instanceof ItemStickyLauncher) {
						plcap.completeObjective(Objective.STICKY_KILL,stack);
					}
					if(stack.getItem() instanceof ItemMeleeWeapon) {
						plcap.completeObjective(Objective.KILL_MELEE,stack);
					}
					if(player.getCapability(TF2weapons.WEAPONS_CAP, null).isExpJump()) {
						plcap.completeObjective(Objective.KILL_BLAST,stack);
					}
					if(player.getCapability(TF2weapons.WEAPONS_CAP, null).doubleJumped) {
						plcap.completeObjective(Objective.KILL_DOUBLE,stack);
					}
				}
				if(event.getEntity() instanceof EntityScout)
					plcap.completeObjective(Objective.KILL_SCOUT,stack);
				else if(event.getEntity() instanceof EntityPyro)
					plcap.completeObjective(Objective.KILL_PYRO,stack);
				else if(event.getEntity() instanceof EntitySoldier)
					plcap.completeObjective(Objective.KILL_SOLDIER,stack);
				else if(event.getEntity() instanceof EntityHeavy)
					plcap.completeObjective(Objective.KILL_HEAVY,stack);
				else if(event.getEntity() instanceof EntityDemoman)
					plcap.completeObjective(Objective.KILL_DEMOMAN,stack);
				else if(event.getEntity() instanceof EntityEngineer)
					plcap.completeObjective(Objective.KILL_ENGINEER,stack);
				else if(event.getEntity() instanceof EntityMedic)
					plcap.completeObjective(Objective.KILL_MEDIC,stack);
				else if(event.getEntity() instanceof EntitySpy)
					plcap.completeObjective(Objective.KILL_SPY,stack);
				else if(event.getEntity() instanceof EntitySniper)
					plcap.completeObjective(Objective.KILL_SNIPER,stack);
				
				
			}
			else if(living instanceof EntityPlayerMP && event.getEntity() instanceof EntityBuilding && !TF2weapons.isOnSameTeam(living, event.getEntity())) {
				living.getCapability(TF2weapons.PLAYER_CAP, null).completeObjective(Objective.DESTROY_BUILDING,stack);
			}
			if(event.getEntity() instanceof EntitySentry) {
				for(EntityPlayerMP player:living.world.getEntitiesWithinAABB(EntityPlayerMP.class, living.getEntityBoundingBox().grow(8, 8, 8), new Predicate<EntityPlayerMP>() {

					@Override
					public boolean apply(EntityPlayerMP input) {
						// TODO Auto-generated method stub
						return input.getCapability(TF2weapons.WEAPONS_CAP, null).getHealTarget()==living.getEntityId() && input.getHeldItemMainhand().getTagCompound().getBoolean("Activated");
					}
					
				})) {
					player.getCapability(TF2weapons.PLAYER_CAP, null).completeObjective(Objective.DESTROY_SENTRY_UBER,stack);
				}
				
			}
			if (!stack.isEmpty() && stack.getItem() instanceof ItemWeapon) {
				if(stack.hasTagCompound() && stack.getTagCompound().getBoolean("Strange")){
					ItemStack originalStack=((TF2DamageSource) event.getSource()).getWeaponOrig();
					if (!(event.getEntityLiving() instanceof EntityPlayer)) {
						originalStack.getTagCompound().setInteger("Kills", originalStack.getTagCompound().getInteger("Kills") + 1);
					} else {
						originalStack.getTagCompound().setInteger("PlayerKills", originalStack.getTagCompound().getInteger("PlayerKills") + 1);
					}
					onStrangeUpdate(originalStack, living);
					if (stack.getTagCompound().getBoolean("Australium")) {
						TF2weapons.sendTracking(new TF2Message.ActionMessage(19, event.getEntityLiving()), event.getEntity());
						event.getEntity().playSound(TF2Sounds.WEAPON_TO_GOLD, 1.5f, 2f);
						//event.getEntityLiving().deathTime = 20;
						//event.getEntityLiving().onEntityUpdate();
						if (!(event.getEntity() instanceof EntityPlayer)) {
							event.getEntity().setSilent(true);
						}
					}
				}
				if (TF2weapons.isEnemy(living, event.getEntityLiving())&&living.hasCapability(TF2weapons.WEAPONS_CAP, null) && TF2Attribute.getModifier("Kill Count", stack, 0, living) != 0) {
					living.getCapability(TF2weapons.WEAPONS_CAP, null).addHead();
				}
				float toHeal = TF2Attribute.getModifier("Health Kill", stack, 0, living);
				if (toHeal != 0) {
					living.heal(toHeal);
				}
				if (TF2Attribute.getModifier("Crit Kill", stack, 0, living) > 0) {
					living.addPotionEffect(new PotionEffect(TF2weapons.critBoost, (int) TF2Attribute.getModifier("Crit Kill", stack, -1, living) * 20, 1));
				}
			}
		}
		if (event.getEntityLiving() instanceof EntityPlayer && !event.getEntity().world.isRemote) {
			ItemStack held=event.getEntityLiving().getHeldItemMainhand();
			if(held != null && held.getItem() instanceof ItemUsable){
				((ItemUsable)held.getItem()).holster(event.getEntityLiving().getCapability(TF2weapons.WEAPONS_CAP, null), held, event.getEntityLiving(), event.getEntityLiving().world);
			}
			InventoryWearables inv = event.getEntityLiving().getCapability(TF2weapons.INVENTORY_CAP, null);
			for (int i = 3; i < 13; i++)
				if (inv.getStackInSlot(i) != null) {
					event.getEntityLiving().entityDropItem(inv.getStackInSlot(i), 0.5f);
				}
		}
	}

	@SubscribeEvent
	public void generateOres(OreGenEvent.Post event) {
		if (event.getWorld().provider.getDimension() == 0) {
			if (TF2weapons.generateCopper) {
				generateOre(TF2weapons.blockCopperOre.getDefaultState(), 7, 9, 32, 80, event.getWorld(), event.getRand(), event.getPos());
				generateOre(TF2weapons.blockCopperOre.getDefaultState(), 7, 1, 0, 32, event.getWorld(), event.getRand(), event.getPos());
			}
			if (TF2weapons.generateLead) {
				generateOre(TF2weapons.blockLeadOre.getDefaultState(), 5, 7, 24, 74, event.getWorld(), event.getRand(), event.getPos());
				generateOre(TF2weapons.blockLeadOre.getDefaultState(), 5, 1, 0, 24, event.getWorld(), event.getRand(), event.getPos());
			}
			if (TF2weapons.generateAustralium) {
				generateOre(TF2weapons.blockAustraliumOre.getDefaultState(), 3, 4, 0, 24, event.getWorld(), event.getRand(), event.getPos());
			}
		}
	}

	public void generateOre(IBlockState state, int size, int count, int minY, int maxY, World world, Random random, BlockPos chunkPos) {
		for (int i = 0; i < count; i++) {
			BlockPos pos = chunkPos.add(random.nextInt(16), minY + random.nextInt(maxY - minY), random.nextInt(16));
			new WorldGenMinable(state, size).generate(world, random, pos);
		}
	}

	@SubscribeEvent
	public void pickAmmo(EntityItemPickupEvent event) {
		ItemStack stack = event.getItem().getItem();
		if (stack.getItem() instanceof ItemAmmoPackage){
			ItemStack weapon=event.getEntityPlayer().getHeldItemMainhand();
			if(!weapon.isEmpty() && weapon.getItem() instanceof ItemFromData && ItemFromData.getData(weapon).getInt(PropertyType.AMMO_TYPE)!=0)
				stack.setItemDamage(ItemFromData.getData(weapon).getInt(PropertyType.AMMO_TYPE));
			stack=ItemAmmoPackage.convertPackage(stack, event.getEntityPlayer());
			event.getItem().setItem(stack);
			event.setResult(Result.DENY);
			event.setCanceled(true);
			return;
		}
		/*if (!(stack.getItem() instanceof ItemCrate) && stack.hasTagCompound() && stack.getTagCompound().getBoolean("DropFrom")) {
			event.getEntityPlayer().addStat(TF2Achievements.SPOILS_WAR);
		}*/
		if (stack.getItem() instanceof ItemAmmo && event.getEntityLiving().hasCapability(TF2weapons.INVENTORY_CAP, null) ) {
			
			if (!event.getEntityLiving().getCapability(TF2weapons.INVENTORY_CAP, null).getStackInSlot(3).isEmpty()) {
				IItemHandler inv = event.getEntityLiving().getCapability(TF2weapons.INVENTORY_CAP, null).getStackInSlot(3)
						.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
				ItemStack orig=stack.copy();
				orig.setCount(0);
				stack=ItemHandlerHelper.insertItemStacked(inv, stack, false);
				if(stack.isEmpty()){
					event.getItem().setItem(orig);
				}
				if(!event.getEntityPlayer().getHeldItemMainhand().isEmpty() && event.getEntityPlayer().getHeldItemMainhand().getItem() instanceof ItemWeapon)
					TF2weapons.network.sendTo(new TF2Message.UseMessage(event.getEntityPlayer().getHeldItemMainhand().getItemDamage(), false,
							ItemAmmo.getAmmoAmount(event.getEntityPlayer(), event.getEntityPlayer().getHeldItemMainhand()), EnumHand.MAIN_HAND),(EntityPlayerMP) event.getEntityPlayer());
				/*for (int i = 0; i < inv.getSlots(); i++) {
					ItemStack inSlot = inv.getStackInSlot(i);
					if (inSlot == null) {fg
						inv.insertItem(i, stack.copy(),false);v
						stack.setCount( 0;
					} else if (stack.isItemEqual(inSlot) && ItemStack.areItemStackTagsEqual(stack, inSlot)) {
						int size = stack.getCount() + inSlot.getCount();

						if (size > stack.getMaxStackSize()) {
							stack.setCount( size - inSlot.getMaxStackSize();
							inSlot.setCount( stack.getMaxStackSize();
						} else {
							inSlot.setCount( size;
							stack.setCount( 0;
						}
					}
					if (stack.getCount() <= 0) {
						break;
					}
				}*/
			}
			if (stack.isEmpty()) {
				event.setResult(Result.ALLOW);
				return;
			}
		}
		event.setResult(Result.DEFAULT);
	}

	public static void onStrangeUpdate(ItemStack stack, EntityLivingBase player) {
		int points = 0;
		if (stack.getItem() instanceof ItemMedigun) {
			points = stack.getTagCompound().getInteger("Ubercharges");
		} else if (stack.getItem() instanceof ItemCloak) {
			points = stack.getTagCompound().getInteger("CloakTicks") / 400;
		} else {
			points = stack.getTagCompound().getInteger("Kills");
			points += stack.getTagCompound().getInteger("PlayerKills") * 5;
		}
		int calculatedLevel = 0;

		if (points >= STRANGE_KILLS[STRANGE_KILLS.length - 1]) {
			calculatedLevel = STRANGE_KILLS.length - 1;
		} else {
			for (int i = 1; i < STRANGE_KILLS.length; i++)
				if (points < STRANGE_KILLS[i]) {
					calculatedLevel = i - 1;
					break;
				}
		}

		if (calculatedLevel > stack.getTagCompound().getInteger("StrangeLevel")) {
			stack.getTagCompound().setInteger("StrangeLevel", calculatedLevel);
			/*final int level = calculatedLevel;
			if (player instanceof EntityPlayerMP) {
				((EntityPlayerMP) player).addStat(new Achievement(Integer.toString(player.getRNG().nextInt()), "strangeUp", 0, 0, stack, null) {
					@Override
					public ITextComponent getStatName() {
						return super.getStatName().appendText(STRANGE_TITLES[level]);
					}
				});
			}*/
		}
	}

	@SubscribeEvent
	public void addTable(LootTableLoadEvent event){
		if(event.getName().equals(LootTableList.CHESTS_SIMPLE_DUNGEON)||event.getName().equals(LootTableList.CHESTS_END_CITY_TREASURE)
				||event.getName().equals(LootTableList.CHESTS_NETHER_BRIDGE)||event.getName().equals(LootTableList.CHESTS_ABANDONED_MINESHAFT)
				||event.getName().equals(LootTableList.CHESTS_STRONGHOLD_CORRIDOR)){
			//System.out.println("loaded: "+new ResourceLocation(TF2weapons.MOD_ID,event.getName().getResourcePath()));
			event.getTable().addPool(getLootPool(new ResourceLocation(TF2weapons.MOD_ID,event.getName().getResourcePath())));
		}
	}
	@SubscribeEvent
	public void craftEvent(net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent event) {
		/*if (event.crafting != null && Block.getBlockFromItem(event.crafting.getItem()) == TF2weapons.blockCabinet) {
			event.player.addStat(TF2Achievements.WEAPON_CRAFTING);
		}*/
	}
	@SubscribeEvent
	public void itemToss(ItemTossEvent event) {
		ItemStack stack=(event.getEntityItem().getItem());
		if(!event.getPlayer().world.isRemote && stack.getItem() instanceof ItemUsable && stack.getTagCompound().getByte("active")>0){
		
		//event.getPlayer().getCapability(TF2weapons.WEAPONS_CAP, null).state = 0;
			if(stack.getTagCompound().getByte("active")==2){
				TF2weapons.network.sendTo(new TF2Message.WeaponDroppedMessage(ItemFromData.getData(stack).getName()), (EntityPlayerMP) event.getPlayer());
				((ItemUsable) stack.getItem()).holster(event.getPlayer().getCapability(TF2weapons.WEAPONS_CAP, null), stack, event.getPlayer(), event.getPlayer().world);
			}
			stack.getTagCompound().removeTag("active");
		}
		
		if(stack.getItem() instanceof ItemFromData && stack.hasTagCompound() && (stack.getTagCompound().getBoolean("Bought") || stack.getTagCompound().getBoolean("Valve"))){
			event.getEntity().setEntityInvulnerable(true);
			event.getEntityItem().setThrower(event.getPlayer().getName());
		}
	}
	@SubscribeEvent
	public void itemExpire(ItemExpireEvent event) {
		ItemStack stack=event.getEntityItem().getItem();
		String thrower=event.getEntityItem().getThrower();
		if(stack.getItem() instanceof ItemFromData && thrower!=null){
			//System.out.println("put");
			HashMap<String,MerchantRecipeList> map=event.getEntityItem().world.getCapability(TF2weapons.WORLD_CAP, null).lostItems;
			if(!map.containsKey(thrower))
				map.put(thrower, new MerchantRecipeList());
			int cost = ItemFromData.getData(stack).getInt(PropertyType.COST)/3;
			ItemStack ingot = new ItemStack(TF2weapons.itemTF2, cost / 9, 2);
			ItemStack nugget = new ItemStack(TF2weapons.itemTF2, cost % 9, 6);
			map.get(thrower).add(new MerchantRecipe(ingot.getCount() > 0 ? ingot : nugget,
					nugget.getCount() > 0 && ingot.getCount() > 0 ? nugget : null, stack, 0, 1));
			
			//ItemHandlerHelper.insertItemStacked(map.get(thrower), stack, false);
		}
	}
	
	@SubscribeEvent
	public void startTrack(PlayerEvent.StartTracking event) {
		if(event.getTarget().hasCapability(TF2weapons.WEAPONS_CAP, null)) {
			TF2weapons.network.sendTo(new TF2Message.CapabilityMessage(event.getTarget(), true), (EntityPlayerMP) event.getEntityPlayer());
		}
	}
	
	@SubscribeEvent
	public void register(RegistryEvent.Register<SoundEvent> event) {
		//System.out.println("SoundEvents:"+TF2Sounds.SOUND_EVENTS.size());
		TF2Sounds.registerSounds();
		System.out.println("Registering sounds: "+ MapList.nameToData.values());
		for (WeaponData weapon : MapList.nameToData.values())
			for (PropertyType propType : weapon.properties.keySet())
				if (propType.name.contains("sound")) {
					ResourceLocation soundLocation = new ResourceLocation(propType.getString(weapon));
					if (!"".equals(soundLocation.getResourcePath())) {
						TF2Sounds.register(soundLocation);
						if (propType==WeaponData.PropertyType.FIRE_SOUND || propType==WeaponData.PropertyType.FIRE_LOOP_SOUND || propType==WeaponData.PropertyType.CHARGED_FIRE_SOUND)
							TF2Sounds.register(new ResourceLocation(propType.getString(weapon) + ".crit"));
					}
				}
		for(SoundEvent sevent:TF2Sounds.SOUND_EVENTS.values()) {
			event.getRegistry().register(sevent);
		}
	}
	
	public static LootPool getLootPool(ResourceLocation res){
		return new LootPool(new LootEntry[]{new LootEntryTable(res, 1, 0, new LootCondition[0], "combined")},
				new LootCondition[0], new RandomValueRange(1), new RandomValueRange(0), "combined");
	}
	public static class DestroyBlockEntry {
		public BlockPos pos;
		public float curDamage;
		public World world;

		public DestroyBlockEntry(BlockPos pos, World world) {
			this.world = world;
			this.pos = pos;
		}
	}

	public static class TF2WorldStorage implements ICapabilityProvider, INBTSerializable<NBTTagCompound>{

		public int eventFlag;

		public ArrayList<BlockPos> banners=new ArrayList<>();
		public HashMap<String,MerchantRecipeList> lostItems=new HashMap<>();
		/*@Override
		public void readFromNBT(NBTTagCompound nbt) {
			
			System.out.println("Load world nbt");
		}

		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound compound) {
			
			System.out.println("SAVE world nbt");
			return compound;
		}*/
		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound nbt=new NBTTagCompound();
			nbt.setInteger("Event", eventFlag);
			NBTTagCompound items=new NBTTagCompound();
			nbt.setTag("Items", items);
			NBTTagList bannersS = new NBTTagList();
			for(BlockPos pos:banners){
				
				NBTTagList coords = new NBTTagList();
				coords.appendTag(new NBTTagInt(pos.getX()));
				coords.appendTag(new NBTTagInt(pos.getY()));
				coords.appendTag(new NBTTagInt(pos.getZ()));
				bannersS.appendTag(coords);
			}
			nbt.setTag("Banners", bannersS);
			for(Entry<String,MerchantRecipeList> entry:lostItems.entrySet()){
				items.setTag(entry.getKey(), entry.getValue().getRecipiesAsTags());
			}
			return nbt;
		}
		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			eventFlag=nbt.getInteger("Event");
			NBTTagCompound items=nbt.getCompoundTag("Items");
			for(String key:items.getKeySet()){
				MerchantRecipeList handler=new MerchantRecipeList();
				handler.readRecipiesFromTags(items.getCompoundTag(key));
				lostItems.put(key, handler);
			}
			NBTTagList bannersS=nbt.getTagList("Banners", 9);
			for(int i=0;i<bannersS.tagCount();i++){
				NBTTagList coords=(NBTTagList) bannersS.get(i);
				banners.add(new BlockPos(coords.getIntAt(0),coords.getIntAt(1),coords.getIntAt(2)));
			}
		}
		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			// TODO Auto-generated method stub
			return TF2weapons.WORLD_CAP != null && capability == TF2weapons.WORLD_CAP;
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			if (TF2weapons.WORLD_CAP != null && capability == TF2weapons.WORLD_CAP)
				return TF2weapons.WORLD_CAP.cast(this);
			return null;
		}
		
	}
	public static class TF2ContainerListener implements IContainerListener{

		public EntityPlayerMP player;
		public TF2ContainerListener(EntityPlayerMP player){
			this.player=player;
		}

		@Override
		public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) {
			if(player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() instanceof ItemWeapon)
				TF2weapons.network.sendTo(new TF2Message.UseMessage(player.getHeldItemMainhand().getItemDamage(), false,ItemAmmo.getAmmoAmount(player, player.getHeldItemMainhand()), EnumHand.MAIN_HAND),player);
		}

		@Override
		public void sendAllWindowProperties(Container containerIn, IInventory inventory) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
