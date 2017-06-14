package rafradek.TF2weapons.characters;

import java.util.Collection;

import net.minecraft.scoreboard.Team;
import net.minecraft.util.text.TextFormatting;

public class TeamTF2 extends Team {

	public String name;

	public TeamTF2(String name) {
		this.name = name;
	}

	@Override
	public String getRegisteredName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	@Override
	public String formatString(String input) {
		// TODO Auto-generated method stub
		return input;
	}

	@Override
	public boolean getSeeFriendlyInvisiblesEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean getAllowFriendlyFire() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public EnumVisible getNameTagVisibility() {
		// TODO Auto-generated method stub
		return EnumVisible.NEVER;
	}

	@Override
	public Collection<String> getMembershipCollection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EnumVisible getDeathMessageVisibility() {
		// TODO Auto-generated method stub
		return EnumVisible.ALWAYS;
	}

	@Override
	public TextFormatting getChatFormat() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CollisionRule getCollisionRule() {
		// TODO Auto-generated method stub
		return CollisionRule.ALWAYS;
	}

}
