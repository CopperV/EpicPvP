package me.Vark123.EpicPvPArena.PlayerSystem;

import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class EpicPvPPlayer {

	private Player player;
	private int points;
	private int tokens;
	
	public void addPoints(int points) {this.points += points;}
	public void removePoints(int points) {this.points -= points;}
	public void addTokens(int tokens) {this.tokens += tokens;}
	public void removeTokens(int tokens) {this.tokens -= tokens;}
	public boolean hasEnoughTokens(int tokens) {return this.tokens >= tokens;}
	
}
