package me.Vark123.EpicPvPArena.ArenaSystem;

import org.bukkit.entity.Player;

import lombok.Builder;
import lombok.Getter;
import me.Vark123.EpicRPG.Players.PlayerManager;
import me.Vark123.EpicRPG.Players.RpgPlayer;
import me.Vark123.EpicRPG.Players.Components.RpgPlayerInfo;

@Getter
@Builder
public class ArenaTier {

	private String id;
	private String display;
	private int minLvl;
	private int maxLvl;
	
	public boolean isPlayerInTier(Player p) {
		RpgPlayer rpg = PlayerManager.getInstance().getRpgPlayer(p);
		if(rpg == null)
			return false;
		RpgPlayerInfo info = rpg.getInfo();
		if(info.getLevel() < minLvl)
			return false;
		if(info.getLevel() > maxLvl)
			return false;
		return true;
	}
	
}
