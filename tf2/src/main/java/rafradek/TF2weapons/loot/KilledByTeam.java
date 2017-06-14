package rafradek.TF2weapons.loot;

import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootContext.EntityTarget;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import rafradek.TF2weapons.TF2weapons;

public class KilledByTeam implements LootCondition {

	@Override
	public boolean testCondition(Random rand, LootContext context) {

		return context.getKillerPlayer() != null && context.getKillerPlayer().getTeam() != null && !context.getKillerPlayer().isOnSameTeam(context.getLootedEntity());
	}

	public static class Serializer extends LootCondition.Serializer<KilledByTeam> {
		public Serializer() {
			super(new ResourceLocation("killed_by_player_team"), KilledByTeam.class);
		}

		@Override
		public void serialize(JsonObject json, KilledByTeam value, JsonSerializationContext context) {
			// json.addProperty("inverse", Boolean.valueOf(value));
		}

		@Override
		public KilledByTeam deserialize(JsonObject json, JsonDeserializationContext context) {
			return new KilledByTeam();
		}
	}
}
