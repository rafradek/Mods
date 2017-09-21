package rafradek.TF2weapons.message;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.MapList;
import rafradek.TF2weapons.TF2Achievements;
import rafradek.TF2weapons.TF2Sounds;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.building.EntityTeleporter;
import rafradek.TF2weapons.building.EntityTeleporter.TeleporterData;
import rafradek.TF2weapons.characters.EntityStatue;
import rafradek.TF2weapons.pages.Contract;
import rafradek.TF2weapons.weapons.ItemUsable;
import rafradek.TF2weapons.weapons.ItemWrench;
import rafradek.TF2weapons.weapons.WeaponsCapability;

public class TF2ActionHandler implements IMessageHandler<TF2Message.ActionMessage, IMessage> {

	/*
	 * public static Map<EntityLivingBase,Integer> playerAction=new
	 * HashMap<EntityLivingBase,Integer>(); public static
	 * Map<EntityLivingBase,Integer> playerActionClient=new
	 * HashMap<EntityLivingBase,Integer>();
	 */
	// public static ThreadLocalMap<EntityLivingBase,Integer> playerAction=new
	// ThreadLocalMap<EntityLivingBase,Integer>();
	// public static ThreadLocalMap<EntityLivingBase,Integer>
	// previousPlayerAction=new ThreadLocalMap<EntityLivingBase,Integer>();
	@Override
	public IMessage onMessage(final TF2Message.ActionMessage message, final MessageContext ctx) {
		if (ctx.side == Side.SERVER) {
			final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
			((WorldServer) player.world).addScheduledTask(new Runnable() {

				@Override
				public void run() {
					if (message.value <= 15) {
						handleMessage(message, player, false);
						message.entity = player.getEntityId();
						TF2weapons.sendTracking(message, player);
					} else if (message.value == 99) {
						Entity wearer = ctx.getServerHandler().playerEntity.world.getEntityByID(message.entity);
						// System.out.println("ID: "+message.entity+" "+wearer);
						if (wearer == null || !(wearer instanceof EntityPlayer))
							wearer = player;
						TF2weapons.network.sendTo(
								new TF2Message.WearableChangeMessage((EntityPlayer) wearer, 0,
										wearer.getCapability(TF2weapons.INVENTORY_CAP, null).getStackInSlot(0)),
								player);
						TF2weapons.network.sendTo(
								new TF2Message.WearableChangeMessage((EntityPlayer) wearer, 1,
										wearer.getCapability(TF2weapons.INVENTORY_CAP, null).getStackInSlot(1)),
								player);
						TF2weapons.network.sendTo(
								new TF2Message.WearableChangeMessage((EntityPlayer) wearer, 2,
										wearer.getCapability(TF2weapons.INVENTORY_CAP, null).getStackInSlot(2)),
								player);
						TF2weapons.network.sendTo(
								new TF2Message.WearableChangeMessage((EntityPlayer) wearer, 3,
										wearer.getCapability(TF2weapons.INVENTORY_CAP, null).getStackInSlot(3)),
								player);
					} else if (message.value == 16) {
						player.world.getScoreboard().addPlayerToTeam(player.getName(), "RED");
						player.addStat(TF2Achievements.JOIN_TEAM);
					} else if (message.value == 17) {
						player.world.getScoreboard().addPlayerToTeam(player.getName(), "BLU");
						player.addStat(TF2Achievements.JOIN_TEAM);
					} else if (message.value == 18 && player.openContainer != null && player.openContainer instanceof ContainerMerchant &&
							player.world.getCapability(TF2weapons.WORLD_CAP, null).lostItems.containsKey(player.getName())) {
						player.closeScreen();
						player.displayVillagerTradeGui(new IMerchant(){

							MerchantRecipeList list;
							@Override
							public void setCustomer(EntityPlayer player) {
								if(player==null && list !=null){
									Iterator<MerchantRecipe> iterator=list.iterator();
									while(iterator.hasNext()){
										MerchantRecipe recipe=iterator.next();
										if(recipe != null && recipe.isRecipeDisabled()){
											iterator.remove();
										}
									}
								}
								
							}

							@Override
							public EntityPlayer getCustomer() {
								// TODO Auto-generated method stub
								return player;
							}

							@Override
							public MerchantRecipeList getRecipes(EntityPlayer player) {
								// TODO Auto-generated method stub
								if(list==null)
									list=player.world.getCapability(TF2weapons.WORLD_CAP, null).lostItems.get(player.getName());
								return list;
							}

							@Override
							public void setRecipes(MerchantRecipeList recipeList) {
								list=recipeList;
							}

							@Override
							public void useRecipe(MerchantRecipe recipe) {
								recipe.incrementToolUses();
								
							}

							@Override
							public void verifySellingItem(ItemStack stack) {
								
							}

							@Override
							public ITextComponent getDisplayName() {
								// TODO Auto-generated method stub
								return new TextComponentString("Recover lost items");
							}

							@Override
							public World getWorld() {
								// TODO Auto-generated method stub
								return player.world;
							}

							@Override
							public BlockPos getPos() {
								// TODO Auto-generated method stub
								return player.getPosition();
							}
							
						});
						//FMLNetworkHandler.openGui(player, TF2weapons.instance, 4, player.world,(int) player.posX,(int)  player.posY,(int)  player.posZ);
					} else if (message.value == 23 && !player.getCapability(TF2weapons.WEAPONS_CAP, null).doubleJumped) {
						player.fallDistance = 0;
						player.getCapability(TF2weapons.WEAPONS_CAP, null).doubleJumped=true;
						player.getServerWorld().spawnParticle(EnumParticleTypes.CLOUD, player.posX, player.posY, player.posZ, 12, 1, 0.2, 1, 0D);
					} else if (message.value >=32 && message.value <48) {
						int id=message.value-32;
						if(player != null && id<player.getCapability(TF2weapons.PLAYER_CAP, null).contracts.size()) {
							player.getCapability(TF2weapons.PLAYER_CAP, null).contracts.get(id).active=true;
						}
					} else if (message.value >=48 && message.value <64) {
						int id=message.value-48;
						if(player != null && id<player.getCapability(TF2weapons.PLAYER_CAP, null).contracts.size()) {
							Contract contract=player.getCapability(TF2weapons.PLAYER_CAP, null).contracts.get(id);
							if((contract.rewards&1)==1) {
								ItemStack reward=player.getRNG().nextBoolean()?new ItemStack(TF2weapons.itemTF2,1,2):ItemFromData.getRandomWeapon(player.getRNG(), ItemFromData.VISIBLE_WEAPON);
								if(!player.inventory.addItemStackToInventory(reward))
									player.dropItem(reward, true);
								player.addExperience(200);
							}
							if((contract.rewards&2)==2) {
								ItemStack reward=player.getRNG().nextBoolean()?new ItemStack(TF2weapons.itemTF2,4,2):new ItemStack(TF2weapons.itemTF2,1,7);
								if(!player.inventory.addItemStackToInventory(reward))
									player.dropItem(reward, true);
								player.addExperience(400);
							}
							contract.rewards=0;
							if(contract.progress>=150)
								player.getCapability(TF2weapons.PLAYER_CAP, null).contracts.remove(id);
						}
					} else if (message.value >=64 && message.value <80) {
						int id=message.value-64;
						if(player != null && id<player.getCapability(TF2weapons.PLAYER_CAP, null).contracts.size()) {
							player.getCapability(TF2weapons.PLAYER_CAP, null).contracts.remove(id);
							player.getStatFile().unlockAchievement(player, TF2Achievements.CONTRACT_DAY, (int) (player.world.getWorldTime()/24000+1));
						}
					}
					else if (message.value >= 100 && message.value<109) {
						int id=message.value-100;
						if(player != null && player.getActiveItemStack().getItem() instanceof ItemWrench) {
							int dimension = 0;
							BlockPos pos = null;
							if(id == 8) {
								dimension = player.dimension;
								pos=player.getBedLocation(player.dimension);
								if(pos == null) {
									pos = player.getBedLocation(0);
									dimension = 0;
								}
								if(pos != null)
									pos = EntityPlayer.getBedSpawnLocation(TF2weapons.server.worldServerForDimension(dimension), pos, player.isSpawnForced(dimension));
								else
									pos = TF2weapons.server.worldServerForDimension(0).provider.getRandomizedSpawnPoint();
							}
							else if(EntityTeleporter.teleporters.containsKey(player.getUniqueID())) {
								TeleporterData[] data=EntityTeleporter.teleporters.get(player.getUniqueID());
								if(data[id]!=null) {
									dimension = data[id].dimension;
									pos = data[id];
								}
							}
							if (pos != null) {
								if (dimension != player.dimension)
									player.changeDimension(dimension);
								player.setPositionAndUpdate(pos.getX()+0.5, pos.getY()+0.23, pos.getZ()+0.5);
								player.getCooldownTracker().setCooldown(MapList.weaponClasses.get("wrench"), 200);
								TF2weapons.playSound(player, TF2Sounds.MOB_TELEPORTER_SEND, 1.0F, 1.0F);
								player.resetActiveHand();
								player.getCapability(TF2weapons.WEAPONS_CAP, null).setMetal(player.getCapability(TF2weapons.WEAPONS_CAP, null).getMetal()-20);
							}
						}
					}
					
				}

			});
		} else {
			final EntityLivingBase player = (EntityLivingBase) Minecraft.getMinecraft().world
					.getEntityByID(message.entity);
			Minecraft.getMinecraft().addScheduledTask(new Runnable() {

				@Override
				public void run() {
					if (message.value <= 15)
						handleMessage(message, player, true);
					else if (message.value == 19) {
						if (player != null) {
							player.setDead();
							player.world.spawnEntity(new EntityStatue(player.world, player,false));
						}
					} 
					else if (message.value == 24) {
						if (player != null) {
							player.world.spawnEntity(new EntityStatue(player.world, player,true));
						}
					} 
					else if (message.value == 22) {
						if (player != null && player.getHeldItemMainhand() != null
								&& player.getHeldItemMainhand().hasTagCompound())
							player.getHeldItemMainhand().getTagCompound().setByte("active", (byte) 2);
					} 
				}

			});
		}
		return null;
	}

	
	/*
	 * public static class TF2ActionHandlerReturn implements
	 * IMessageHandler<TF2Message.ActionMessage, IMessage> {
	 * 
	 * @Override public IMessage onMessage(TF2Message.ActionMessage message,
	 * MessageContext ctx) { EntityLivingBase player=(EntityLivingBase)
	 * Minecraft.getMinecraft().theWorld.getEntityByID(message.entity);
	 * handleMessage(message, player); return null; }
	 * 
	 * }
	 */
	public static void handleMessage(TF2Message.ActionMessage message, EntityLivingBase player, boolean client) {
		if (player != null) {
			/*
			 * int
			 * oldValue=playerAction.get().containsKey(player)?playerAction.get(
			 * ).get(player):0; if(player.getHeldItem(EnumHand.MAIN_HAND) !=
			 * null && player.getHeldItem(EnumHand.MAIN_HAND).getItem()
			 * instanceof ItemUsable){
			 * if((oldValue&1)==0&&(message.value&1)!=0){
			 * ((ItemUsable)player.getHeldItem(EnumHand.MAIN_HAND).getItem()).
			 * startUse(player.getHeldItem(EnumHand.MAIN_HAND), player,
			 * player.world); } if((oldValue&1)==0&&(message.value&1)!=0){
			 * ((ItemUsable)player.getHeldItem(EnumHand.MAIN_HAND).getItem()).
			 * endUse(player.getHeldItem(EnumHand.MAIN_HAND), player,
			 * player.world); } }
			 */
			/*
			 * if(previousPlayerAction.get(player.world.isRemote).containsKey
			 * (player)){
			 * previousPlayerAction.get(player.world.isRemote).put(player,
			 * 0); } int
			 * oldState=previousPlayerAction.get(player.world.isRemote).get(
			 * player);
			 * 
			 * previousPlayerAction.get(player.world.isRemote).put(player,
			 * playerAction.get(true).get(player));
			 */

			WeaponsCapability cap = player.getCapability(TF2weapons.WEAPONS_CAP, null);
			ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
			int oldState = cap.state & 3;

			cap.state = message.value + (cap.state & 8);

			if (!stack.isEmpty() && stack.getItem() instanceof ItemUsable && oldState != (message.value & 3)
					&& stack.getCapability(TF2weapons.WEAPONS_DATA_CAP, null).active == 2) {
				if ((oldState & 2) < (message.value & 2)) {
					((ItemUsable) stack.getItem()).startUse(stack, player, player.world, oldState,
							message.value & 3);
					cap.stateDo(player, stack);
				} else if ((oldState & 2) > (message.value & 2))
					((ItemUsable) stack.getItem()).endUse(stack, player, player.world, oldState, message.value & 3);
				if ((oldState & 1) < (message.value & 1)) {
					((ItemUsable) stack.getItem()).startUse(stack, player, player.world, oldState,
							message.value & 3);
					cap.stateDo(player, stack);
				} else if ((oldState & 1) > (message.value & 1))
					((ItemUsable) stack.getItem()).endUse(stack, player, player.world, oldState, message.value & 3);
			}
			// System.out.println("change
			// "+playerAction.get(player.world.isRemote).get(player));
			// System.out.println("dostal: "+message.value);
		}
	}

	public static ArrayList<EnumAction> value = new ArrayList<EnumAction>();

	public static enum EnumAction {

		IDLE, FIRE, ALTFIRE, RELOAD;

		private EnumAction() {
			value.add(this);
		}

		/*
		 * public boolean leftClick(){ return this==FIRE||this==DOUBLE; } public
		 * boolean rightClick(){ return this==ALTFIRE||this==DOUBLE; }
		 */
	}
}
