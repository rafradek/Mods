package rafradek.TF2weapons.contract;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;

import net.minecraft.advancements.AdvancementManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;

public class ContractManagerProxy extends MinecraftServer {

	private MinecraftServer original;
	private ContractManager manager;
	public ContractManagerProxy(MinecraftServer original, ContractManager manager) {
		super(new File("."), Proxy.NO_PROXY, null, null, null, null, null);
		this.original = original;
		this.manager = manager;
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean init() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canStructuresSpawn() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public GameType getGameType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EnumDifficulty getDifficulty() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isHardcore() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getOpPermissionLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean shouldBroadcastRconToOps() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean shouldBroadcastConsoleToOps() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDedicatedServer() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean shouldUseNativeTransport() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCommandBlockEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String shareToLAN(GameType type, boolean allowCheats) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public AdvancementManager getAdvancementManager()
    {
        return this.manager;
    }
	
	public PlayerList getPlayerList()
    {
        return this.original.getPlayerList();
    }
	
}
