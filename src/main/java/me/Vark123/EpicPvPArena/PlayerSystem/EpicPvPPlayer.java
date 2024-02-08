package me.Vark123.EpicPvPArena.PlayerSystem;

import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class EpicPvPPlayer {

	private Player player;
	@Setter
	private int points;
	private int tokens;
	private int fights;
	private int wins;
	
	public void addPoints(int points) {this.points += points;}
	public void removePoints(int points) {this.points -= points;}
	public void addTokens(int tokens) {this.tokens += tokens;}
	public void removeTokens(int tokens) {this.tokens -= tokens;}
	public boolean hasEnoughTokens(int tokens) {return this.tokens >= tokens;}
	public void incrementFight() {++this.fights;}
	public void incrementWins() {++this.wins;}
	public double getKD() {
		if(fights == 0)
			return 1.;
		return (double) wins / (double) fights;
	}
	public void resetFights() {
		this.fights = 0;
		this.wins = 0;
	}
	
}
