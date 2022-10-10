package rafradek.TF2weapons.contract;

import java.io.File;

import javax.annotation.Nullable;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.SPacketSelectAdvancementsTab;
import net.minecraft.server.MinecraftServer;

public class PlayerContracts extends PlayerAdvancements {

	private EntityPlayerMP player;
	private PlayerContractsProxy networkProxy;
	private PlayerAdvancements advancementsVanilla;
	public PlayerContracts(PlayerAdvancements advancements, MinecraftServer server, File p_i47422_2_, EntityPlayerMP player) {
		super(server, p_i47422_2_, player);
		this.player = player;
		this.networkProxy = new PlayerContractsProxy(player.connection,server,player);
		this.advancementsVanilla = advancements;
		// TODO Auto-generated constructor stub
	}

	public void setPlayer(EntityPlayerMP player)
    {
        this.player = player;
        advancementsVanilla.setPlayer(player);
    }

    public void dispose()
    {
        advancementsVanilla.dispose();
    }

    public void reload()
    {
        advancementsVanilla.reload();
    }
    
	public void setSelectedTab(@Nullable Advancement p_194220_1_)
    {
		NetHandlerPlayServer old = player.connection;
        player.connection = networkProxy;
        advancementsVanilla.setSelectedTab(p_194220_1_);
        player.connection = old;
        
    }
	
	public void flushDirty(EntityPlayerMP p_192741_1_) {
		advancementsVanilla.flushDirty(p_192741_1_);
		NetHandlerPlayServer old = player.connection;
        player.connection = networkProxy;
        super.flushDirty(p_192741_1_);
        player.connection = old;
	}
	
	public void save()
    {
		this.advancementsVanilla.save();
    }
}
