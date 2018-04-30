package rafradek.TF2weapons.decoration;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.TF2Util;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.weapons.TF2Explosion;

public class ItemWearable extends ItemFromData {

	public static int usedModel;

	public ItemWearable() {
		super();
		this.addPropertyOverride(new ResourceLocation("bodyModel"), new IItemPropertyGetter() {
			@Override
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				if (ItemWearable.usedModel == 1)
					return 1;
				return 0;
			}
		});
		this.addPropertyOverride(new ResourceLocation("headModel"), new IItemPropertyGetter() {
			@Override
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				if (ItemWearable.usedModel == 2)
					return 1;
				return 0;
			}
		});
	}

	@Override
	public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot slot, Entity player) {
		return slot == (isHat(stack) ? EntityEquipmentSlot.HEAD : EntityEquipmentSlot.CHEST);
	}

	public EntityEquipmentSlot getEquipmentSlot(ItemStack stack) {
        return isHat(stack) ? EntityEquipmentSlot.HEAD : EntityEquipmentSlot.CHEST;
    }
	
	@Override
	public ActionResult<ItemStack> onItemRightClick( World world, EntityPlayer living, EnumHand hand) {
		if (!world.isRemote)
			FMLNetworkHandler.openGui(living, TF2weapons.instance, 0, world, 0, 0, 0);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, living.getHeldItem(hand));
	}

	public boolean isHat(ItemStack stack) {
		return (getData(stack).getInt(PropertyType.WEAR) & 1) == 1;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return getData(stack).getString(PropertyType.ARMOR_IMAGE);
	}
	
	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack){
		onUpdateWearing(itemStack,world,player);
	}
	
	public void applyRandomEffect(ItemStack stack, Random rand){
		stack.getTagCompound().setByte("UEffect", (byte) rand.nextInt(11));
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		String name = super.getItemStackDisplayName(stack);
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("UEffect"))
			name = TextFormatting.DARK_PURPLE + "Unusual " + name;
		return name;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> tooltip,
			ITooltipFlag advanced) {
		super.addInformation(stack, world, tooltip, advanced);
		if (stack.hasTagCompound()) {
			
			if (stack.getTagCompound().hasKey("UEffect")) {
				tooltip.add("");
				String str;
				
				switch (stack.getTagCompound().getByte("UEffect")){
					case 0:str="Burning Flames"; break;
					case 1:str="Hearts"; break;
					case 2:str="Steaming"; break;
					case 3:str="Bubbling"; break;
					case 4:str="Stormy Storm"; break;
					case 5:str="Arcana"; break;
					case 6:str="Massed Flies"; break;
					case 7:str="Slimy"; break;
					case 8:str="Nebula"; break;
					case 9:str="Dragon's breath"; break;
					default:str="Musically";
				}
				tooltip.add("Effect - "+str);
			}
		}
	}
	public void onUpdateWearing(ItemStack stack, World par2World, EntityLivingBase living) {
		if(!living.world.isRemote && living.deathTime == 18 && TF2Attribute.getModifier("Explode Death", stack, 0, living) != 0){
			TF2Explosion explosion = new TF2Explosion(living.world, living, living.posX, living.posY + 0.5, living.posZ, 5, null, 0,3);
			// System.out.println("ticks: "+this.ticksExisted);
			explosion.isFlaming = false;
			explosion.isSmoking = true;
			explosion.doExplosionA();
			explosion.doExplosionB(true);
			Iterator<Entity> affectedIterator = explosion.affectedEntities.keySet().iterator();
			while (affectedIterator.hasNext()) {
				Entity ent = affectedIterator.next();
				TF2Util.dealDamage(ent, living.world, living, ItemStack.EMPTY, 2, explosion.affectedEntities.get(ent) * 26,
						TF2Util.causeDirectDamage(stack, living, 2).setExplosion());
			}
			Iterator<EntityPlayer> iterator = living.world.playerEntities.iterator();

			while (iterator.hasNext()) {
				EntityPlayer entityplayer = iterator.next();

				if (entityplayer.getDistanceSq(living.posX, living.posY + 0.5, living.posZ) < 4096.0D) {
					((EntityPlayerMP) entityplayer).connection.sendPacket(
							new SPacketExplosion(living.posX, living.posY + 0.5, living.posZ, 4, explosion.affectedBlockPositions, explosion.getKnockbackMap().get(entityplayer)));
				}
			}
		}
		if(living.world.isRemote && (living != ClientProxy.getLocalPlayer() || Minecraft.getMinecraft().gameSettings.thirdPersonView != 0) && stack.getTagCompound().hasKey("UEffect")){
			EnumParticleTypes type;
			switch(stack.getTagCompound().getByte("UEffect")){
			case 0: type=EnumParticleTypes.FLAME; break;
			case 1: type=EnumParticleTypes.HEART; break;
			case 2: type=EnumParticleTypes.SMOKE_LARGE; break;
			case 3: type=EnumParticleTypes.WATER_BUBBLE; break;
			case 4: type=EnumParticleTypes.VILLAGER_ANGRY; break;
			case 5: type=EnumParticleTypes.VILLAGER_HAPPY; break;
			case 6: type=EnumParticleTypes.SUSPENDED_DEPTH; break;
			case 7: type=EnumParticleTypes.SLIME; break;
			case 8: type=EnumParticleTypes.CRIT_MAGIC; break;
			case 9: type=EnumParticleTypes.DRAGON_BREATH; break;
			default: type=EnumParticleTypes.NOTE;
			}
			par2World.spawnParticle(type, living.posX+living.getRNG().nextDouble()*living.width-(living.width/2),
				living.posY+living.height+living.getRNG().nextDouble()*0.2, 
				living.posZ+living.getRNG().nextDouble()*living.width-(living.width/2), living.getRNG().nextDouble()*0.02-0.01,
				living.getRNG().nextDouble()*0.02, living.getRNG().nextDouble()*0.02-0.01, new int[0]);
		}
	}
}
