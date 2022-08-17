package rafradek.TF2weapons.loot;

import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public class KilledByTeam implements LootCondition {

	public boolean team;

	public KilledByTeam(boolean team) {
		this.team = team;
	}

	@Override
	public boolean testCondition(Random rand, LootContext context) {

		Entity player = this.team ? context.getKiller() : context.getKillerPlayer();
		if(player instanceof IEntityOwnable && ((IEntityOwnable)player).getOwner() instanceof EntityPlayer)
			player = ((IEntityOwnable)player).getOwner();
		return player != null && player.getTeam() != null && !player.isOnSameTeam(context.getLootedEntity());
	}

	public static class Serializer extends LootCondition.Serializer<KilledByTeam> {
		public Serializer() {
			super(new ResourceLocation("killed_by_player_team"), KilledByTeam.class);
		}

		@Override
		public void serialize(JsonObject json, KilledByTeam value, JsonSerializationContext context) {
			json.addProperty("team", value.team);
		}

		@Override
		public KilledByTeam deserialize(JsonObject json, JsonDeserializationContext context) {
			return new KilledByTeam(JsonUtils.getBoolean(json, "team", false));
		}
	}
}
