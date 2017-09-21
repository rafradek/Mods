package rafradek.rig;

import java.util.UUID;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemRIG extends Item {

	public ItemRIG() {
		this.setUnlocalizedName("rig");
		this.setCreativeTab(RIG.tabRig);
	}
	public static final UUID MAX_HEALTH_BONUS= UUID.fromString("4be2e2ce-feea-4cba-946e-ef5eb6b5fb75");
	
	public Multimap<String, AttributeModifier> getAttributeModifiers(ItemStack stack) {
		Multimap<String, AttributeModifier> map = HashMultimap.<String, AttributeModifier>create();
		if(!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		float health = RIGUpgrade.upgradesMap.get("Health").getAttributeValue(stack)*3;
		map.put(SharedMonsterAttributes.MAX_HEALTH.getName(), new AttributeModifier(MAX_HEALTH_BONUS, "Health modifier", health, 0));
		return map;
	}
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer living, EnumHand hand) {
		if(!world.isRemote) {
			living.setHeldItem(hand, RIG.equip(living, living.getHeldItem(hand)));
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, living.getHeldItem(hand));
	}
}
