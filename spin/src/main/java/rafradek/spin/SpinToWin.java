package rafradek.spin;

import java.util.Arrays;
import java.util.UUID;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Predicate;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBiped.ArmPose;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = "rafradek_spin", name = "Spin To Win", version = "1.4", guiFactory = "rafradek.spin.SpinGuiFactory")
public class SpinToWin {
	
	public static DataParameter<Boolean> SPIN_TIME;
	
	public static Enchantment ench;
	
	public static UUID SPIN_AS=UUID.fromString("0706d45a-daae-429c-843c-23c03b721b32");
	public static UUID SPIN_AD=UUID.fromString("b308e311-0557-405d-bb3e-551fd34edc25");
	
	private static final ResourceLocation SPIN_TEXTURE = new ResourceLocation("rafradek_spin",
			"textures/misc/spin.png");

	public static Configuration conf;
	public static int spinID;
	public static double range;
	public static float swordDmg;
	public static float axeDmg;
	public static float speed;
	public static int cooldownAxe;
	public static int cooldownSword;
	public static int duration;
	public static boolean offhandBlock;
	public static ResourceLocation[] blacklistItems;
	public static ResourceLocation[] swordItems;
	public static ResourceLocation[] toolItems;
	public static EntityPlayer fakePlayer;
	
	@Mod.EventHandler
	public void init(FMLPreInitializationEvent event) {
		conf = new Configuration(event.getSuggestedConfigurationFile());
		syncConfig();
		SPIN_TIME=new DataParameter<Boolean>(spinID, DataSerializers.BOOLEAN);
		ench=GameRegistry.register(new EnchantmentSpin().setRegistryName("rafradek_spin", "spin"));
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	public void syncConfig(){
		spinID=conf.getInt("Spin data parameter ID", "config", 173, 32, 255, "Change this if you encounter problems with duplicated data parameter id");
		range=conf.getFloat("Spin range", "config", 3.7f, 0, 1000, "Ability's range");
		swordDmg=conf.getFloat("Sword damage multiplier", "config", 0.7f, 0, 1000, "");
		axeDmg=conf.getFloat("Axe damage multiplier", "config", 1.15f, 0, 1000, "");
		speed=1f/conf.getFloat("Spin speed multiplier", "config", 1f, 0, 100, "");
		cooldownAxe=conf.getInt("Axe spin cooldown", "config", 220, 0, 1000, "");
		cooldownSword=conf.getInt("Sword spin cooldown", "config", 280, 0, 1000, "");
		duration=conf.getInt("Sword spin duration", "config", 90, 0, 1000, "");
		offhandBlock=conf.getBoolean("Offhand item blocks", "config", false, "If true, an offhand item will always be activated before the spin");
		//requireSneak=conf.getBoolean("Sneaking stop", "config", false, "If true, the player must hold the sneak key before using the ability");
		conf.get("config", "Spin data parameter ID", 173).setRequiresMcRestart(true);
		String[] listbl=conf.getStringList("Blacklist items", "config", new String[0], "Registry names of items which should not be spinnable");
		String[] listsw=conf.getStringList("Swords", "config", new String[0], "Registry names of items that spin multiple times");
		String[] listtl=conf.getStringList("Axes/tools", "config", new String[0], "Registry names of items that spin once");
		blacklistItems=new ResourceLocation[listbl.length];
		for(int i=0; i<blacklistItems.length;i++){
			blacklistItems[i]=new ResourceLocation(listbl[i]);
		}
		swordItems=new ResourceLocation[listsw.length];
		for(int i=0; i<swordItems.length;i++){
			swordItems[i]=new ResourceLocation(listsw[i]);
		}
		toolItems=new ResourceLocation[listtl.length];
		for(int i=0; i<toolItems.length;i++){
			toolItems[i]=new ResourceLocation(listtl[i]);
		}
		if(conf.hasChanged())
			conf.save();
	}
	public boolean isValid(Item stack){
		for(ResourceLocation location: blacklistItems){
			if(location.equals(stack.getRegistryName()))
				return false;
		}
		return (isSword(stack) || isTool(stack));
	}
	public boolean isSword(Item stack){
		for(ResourceLocation location: swordItems){
			if(location.equals(stack.getRegistryName()))
				return true;
		}
		return stack instanceof ItemSword;
	}
	public boolean isTool(Item stack){
		for(ResourceLocation location: toolItems){
			if(location.equals(stack.getRegistryName()))
				return true;
		}
		return stack instanceof ItemTool;
	}
	@SideOnly(Side.CLIENT)
	public boolean addToBlacklist(ItemStack stack){
		if(stack.useItemRightClick(Minecraft.getMinecraft().world, new EntityOtherPlayerMP(Minecraft.getMinecraft().world, new GameProfile(null, "fake")), EnumHand.MAIN_HAND).getType()!=EnumActionResult.PASS){
			Property blackList=conf.get("config", "Blacklist items", new String[0]);
			blackList.set(Arrays.copyOf(blackList.getStringList(),blackList.getStringList().length+1));
			blackList.getStringList()[blackList.getStringList().length-1]=stack.getItem().getRegistryName().toString();
			syncConfig();
			return true;
		}
		return false;
	}
	public boolean allowAttack(EntityPlayer player,ItemStack stack) {
		return player.getCooldownTracker().getCooldown(stack.getItem(), 0)==0 && ((!offhandBlock && player.isSneaking()) || player.getHeldItemOffhand().isEmpty());
	}
	@SubscribeEvent
	public void stopUsing(PlayerInteractEvent.RightClickItem event) {
		
		if(event.getHand()==EnumHand.MAIN_HAND){
			ItemStack stack=event.getEntityPlayer().getHeldItemMainhand();
			if(!stack.isEmpty() && isValid(stack.getItem()) && allowAttack(event.getEntityPlayer(),stack)){
				event.getEntityPlayer().getEntityData().setInteger("SpinTime", (int) (getDuration(stack,event.getEntityPlayer())*(isTool(stack.getItem())?(1-0.075*EnchantmentHelper.getEnchantmentLevel(ench, stack)):1)));
				if(!event.getEntity().world.isRemote){
					int cooldown=isSword(stack.getItem())?cooldownSword:cooldownAxe-EnchantmentHelper.getEnchantmentLevel(ench, stack)*13;
					if(isTool(stack.getItem())){
						
					}
					for(Item item:GameRegistry.findRegistry(Item.class)){
						if(isValid(item))
							event.getEntityPlayer().getCooldownTracker().setCooldown(item, cooldown);
					}
					if(!stack.hasTagCompound())
						stack.setTagCompound(new NBTTagCompound());
					stack.getTagCompound().setBoolean("SpinningS", true);
					
					//event.getEntityPlayer().getEntityData().setInteger("SpinTime", getDuration(stack,event.getEntityPlayer()));
					event.getEntityPlayer().getDataManager().set(SPIN_TIME, true);
				}
				else if(stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemTool){
					boolean found=false;
					for(ResourceLocation location: swordItems){
						if(location.equals(stack.getItem().getRegistryName())){
							found=true;
							break;
						}
					}
					for(ResourceLocation location: toolItems){
						if(location.equals(stack.getItem().getRegistryName())){
							found=true;
							break;
						}
					}
					if(!found){
						addToBlacklist(stack);
						event.setCanceled(true);
					}
				}
			}
		}
		else{
			ItemStack stack=event.getEntityPlayer().getHeldItemMainhand();
			if(event.getEntityPlayer().getDataManager().get(SPIN_TIME) || (event.getEntity().world.isRemote && this.isValid(stack.getItem()) && allowAttack(event.getEntityPlayer(),stack)))
				event.setCanceled(true);
		}
	}
	
	public int getDuration(ItemStack stack,EntityPlayer player){
		return (int) (isSword(stack.getItem())?duration+EnchantmentHelper.getEnchantmentLevel(ench, stack)*20:(player.getCooldownPeriod()-1)*2);
	}
	public int getSpinCooldown(ItemStack stack,EntityPlayer player){
		return (int) Math.max(1,(int) (player.getCooldownPeriod()*(isSword(stack.getItem())?(1f-EnchantmentHelper.getEnchantmentLevel(ench, stack)*0.1f):1)*speed));
	}
	@SubscribeEvent
	public void entityConstructing(final EntityEvent.EntityConstructing event) {
		if(event.getEntity() instanceof EntityPlayer){
			event.getEntity().getDataManager().register(SPIN_TIME, false);
		}
	}
	
	@SubscribeEvent
	public void livingUpdate(final LivingEvent.LivingUpdateEvent event) {
		if(!event.getEntity().world.isRemote &&event.getEntity() instanceof EntityPlayer && event.getEntity().getDataManager().get(SPIN_TIME)){
			final EntityPlayer player=(EntityPlayer) event.getEntity();
			ItemStack stack=player.getHeldItemMainhand();
			if(stack.isEmpty() || !stack.hasTagCompound() || !stack.getTagCompound().getBoolean("SpinningS")){
				for(int i=0;i<player.inventory.getSizeInventory();i++){
					ItemStack inSlot=player.inventory.getStackInSlot(i);
					if(inSlot.hasTagCompound() && inSlot.getTagCompound().hasKey("SpinningS"))
						inSlot.getTagCompound().setBoolean("SpinningS", false);
				}
				event.getEntity().getDataManager().set(SPIN_TIME, false);
			}
			else{
				int spin=player.getEntityData().getInteger("SpinTime");
				int cooldown=getSpinCooldown(stack,player);
				int enchantlevel=EnchantmentHelper.getEnchantmentLevel(ench, stack);
				final double range=SpinToWin.range+(0.36*enchantlevel);
				
				
				
				NBTTagList list=player.getEntityData().getTagList("EntitiesDelayS", 3);

				if((this.getDuration(stack, player)-spin)%cooldown==cooldown-1) {
					player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0f, 1.0f);
					if(player.getEntityData().hasKey("EntitiesDelayS"))
						player.getEntityData().setTag("EntitiesDelayS", new NBTTagList());
					
					
					for(EntityLivingBase target:player.world.getEntitiesWithinAABB(EntityLivingBase.class, 
							player.getEntityBoundingBox().expand(range, 0, range), new Predicate<EntityLivingBase>(){
	
						@Override
						public boolean apply(EntityLivingBase input) {
							// TODO Auto-generated method stub
							return input != event.getEntity() && isSuitableTarget(player, input, false, true) && input.getDistanceSqToEntity(player)<(range+input.width/2)*(range+input.width/2);
						}
						
					})){
							list.appendTag(new NBTTagInt(target.getEntityId()));
							if(list.tagCount()>=cooldown)
								break;
					}
					
				}
				if(list.tagCount()>0){
					try{
						AttributeModifier spinAD=new AttributeModifier(SPIN_AD,"SpinAD", isSword(stack.getItem())? (swordDmg-1+enchantlevel*0.05):
							(axeDmg-1+enchantlevel*0.15), 2);
						player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).applyModifier(spinAD);
						AttributeModifier spinAS=new AttributeModifier(SPIN_AS,"SpinA", 1000, 2);
						player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).applyModifier(spinAS);
						
						Entity target=player.world.getEntityByID(list.getIntAt(0));
						if(target != null && target.isEntityAlive()){
							player.attackTargetEntityWithCurrentItem(target);
							if(target instanceof EntityLivingBase && ((EntityLivingBase)target).maxHurtResistantTime>=cooldown*2){
								((EntityLivingBase)target).hurtResistantTime=cooldown*2-2;
							}
						}
						list.removeTag(0);
					}catch(Exception e){
						throw e;
					}
					finally{
						player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).removeModifier(SPIN_AS);
						player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).removeModifier(SPIN_AD);
					}
				}
				
				player.getEntityData().setTag("EntitiesDelayS", list);
				player.getEntityData().setInteger("SpinTime", spin-1);
				if(spin==1)
					player.getDataManager().set(SPIN_TIME, false);
				if(spin==1 && stack.hasTagCompound()){
					for(int i=0;i<list.tagCount();i++)
						list.removeTag(list.tagCount()-1);
					stack.getTagCompound().setBoolean("SpinningS", false);
				}
			}
		}
	}
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderPlayer(RenderPlayerEvent.Pre event) {
		if(event.getEntity().getDataManager().get(SPIN_TIME)){
			
			event.getRenderer().getMainModel().rightArmPose=ArmPose.BOW_AND_ARROW;
			GlStateManager.pushMatrix();
			ItemStack stack=event.getEntityPlayer().getHeldItemMainhand();
			int spinCooldown=this.getSpinCooldown(stack, event.getEntityPlayer());
			if(event.getEntityPlayer().getEntityData().getInteger("SpinTime")>this.getDuration(stack, event.getEntityPlayer())-spinCooldown*0.8f){
				GlStateManager.rotate((this.getDuration(stack, event.getEntityPlayer())-
						(event.getEntity().getEntityData().getInteger("SpinTime")-event.getPartialRenderTick()))*-(90/this.getSpinCooldown(stack, event.getEntityPlayer())), 0, 1, 0);
			}
			else{
				GlStateManager.rotate((this.getDuration(stack, event.getEntityPlayer())-
						(event.getEntity().getEntityData().getInteger("SpinTime")-event.getPartialRenderTick()))*(360/this.getSpinCooldown(stack, event.getEntityPlayer())), 0, 1, 0);
			}
			
		}
	}
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderLivingEntity(RenderLivingEvent.Pre<EntityPlayer> event) {
		if(event.getEntity() instanceof EntityPlayer && event.getEntity().getDataManager().get(SPIN_TIME))
			((ModelPlayer)event.getRenderer().getMainModel()).rightArmPose=ArmPose.BOW_AND_ARROW;
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderPlayer(RenderPlayerEvent.Post event) {
		ItemStack stack=event.getEntityPlayer().getHeldItemMainhand();
		if(event.getEntity().getDataManager().get(SPIN_TIME)){
			
			GlStateManager.popMatrix();
			if(!stack.isEmpty()&&!event.getEntity().isInvisible()){
				Tessellator tessellator = Tessellator.getInstance();
				VertexBuffer renderer = tessellator.getBuffer();
				GlStateManager.pushMatrix();
				GlStateManager.translate((float) 0, (float) 0 + 0.1D, (float) 0);
				// GL11.glRotatef((living.prevRotationYawHead +
				// (living.rotationYawHead - living.prevRotationYawHead) *
				// p_76986_9_)*-1, 0.0F, 1.0F, 0.0F);
				// GL11.glRotatef((living.prevRotationPitch + (living.rotationPitch
				// - living.prevRotationPitch) * p_76986_9_), 1.0F, 0.0F, 0.0F);
				// GlStateManager.disableTexture2D();
				int cooldown=getSpinCooldown(stack,event.getEntityPlayer());
				double range=SpinToWin.range + EnchantmentHelper.getEnchantmentLevel(ench, stack)*0.36+0.1;
				float alpha= 0.3f + (float)((this.getDuration(stack, event.getEntityPlayer())-event.getEntityPlayer().getEntityData().getInteger("SpinTime"))%cooldown)/(float)cooldown*0.5f;
				Minecraft.getMinecraft().getTextureManager().bindTexture(SPIN_TEXTURE);
				GlStateManager.disableLighting();
				GL11.glEnable(GL11.GL_BLEND);
				OpenGlHelper.glBlendFunc(770, 771, 1, 0);
				if(isSword(stack.getItem()))
					GlStateManager.color(0.8F, 0.65F, 0.4F, alpha);
				else
					GlStateManager.color(0.6F, 0.18F, 0.1F, alpha);
				/*
				 * if(TF2weapons.getTeamForDisplay(living)==0){ GL11.glColor4f(1.0F,
				 * 0.0F, 0.0F, 0.28F); } else{ GL11.glColor4f(0.0F, 0.0F, 1.0F,
				 * 0.28F); }
				 */
				renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
				renderer.pos(-range, 0.0D, range).tex(0D, 1D).endVertex();
				renderer.pos(range, 0.0D, range).tex(1D, 1D).endVertex();
				renderer.pos(range, 0.0D, -range).tex(1D, 0.0D).endVertex();
				renderer.pos(-range, 0.0D, -range).tex(0D, 0.0D).endVertex();
				tessellator.draw();
	
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1F);
				GL11.glDisable(GL11.GL_BLEND);
				// GlStateManager.enableTexture2D();
				GlStateManager.enableLighting();
				GlStateManager.popMatrix();
			}
		}
	}
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void tickEvent(TickEvent.ClientTickEvent event) {
		if(event.phase==Phase.START){
			EntityPlayer player=Minecraft.getMinecraft().player;
			if(player != null && player.getDataManager().get(SPIN_TIME) && !player.getHeldItemMainhand().isEmpty()){
				player.getEntityData().setInteger("SpinTime", player.getEntityData().getInteger("SpinTime")-1);
				if(player.getEntityData().getInteger("SpinTime")<=0)
					player.getEntityData().setInteger("SpinTime", this.getDuration(player.getHeldItemMainhand(), player));
				if(Minecraft.getMinecraft().gameSettings.thirdPersonView==0 ){
					
					Minecraft.getMinecraft().gameSettings.thirdPersonView=1;
					player.getEntityData().setBoolean("SetFirstpersonAfter", true);
				}
			}
			if(player != null && !player.getDataManager().get(SPIN_TIME) && player.getEntityData().getBoolean("SetFirstpersonAfter")){
				Minecraft.getMinecraft().gameSettings.thirdPersonView=0;
				player.getEntityData().setBoolean("SetFirstpersonAfter",false);
			}
		}
		
	}
	public static boolean isSuitableTarget(EntityPlayer attacker, @Nullable EntityLivingBase target, boolean includeInvincibles, boolean checkSight)
    {
        if (target == null)
        {
            return false;
        }
        else if (target == attacker)
        {
            return false;
        }
        else if (!target.isEntityAlive())
        {
            return false;
        }
        else if (target instanceof EntityPlayer && !attacker.canAttackPlayer((EntityPlayer) target))
        {
            return false;
        }
        else
        {
            if (attacker instanceof IEntityOwnable && ((IEntityOwnable)attacker).getOwnerId() != null)
            {
                if (target instanceof IEntityOwnable && ((IEntityOwnable)attacker).getOwnerId().equals(target.getUniqueID()))
                {
                    return false;
                }

                if (target == ((IEntityOwnable)attacker).getOwner())
                {
                    return false;
                }
            }
            else if (target instanceof EntityPlayer && !includeInvincibles && ((EntityPlayer)target).capabilities.disableDamage)
            {
                return false;
            }

            return !checkSight || attacker.canEntityBeSeen(target);
        }
    }
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
		// TF2weapons.syncConfig();
		if (eventArgs.getModID().equals("rafradek_spin")) {
			syncConfig();
		}
	}
}
