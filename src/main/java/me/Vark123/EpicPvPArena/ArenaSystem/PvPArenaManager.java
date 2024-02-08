package me.Vark123.EpicPvPArena.ArenaSystem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import lombok.Getter;
import me.Vark123.EpicPvPArena.Config;
import me.Vark123.EpicPvPArena.Main;
import me.Vark123.EpicPvPArena.PlayerSystem.EpicPvPPlayer;
import me.Vark123.EpicPvPArena.Tools.Pair;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;

@Getter
public final class PvPArenaManager {

	private static final PvPArenaManager inst = new PvPArenaManager();
	
	private final Collection<ArenaTier> tiers;
	private final Collection<PvPArena> arenas;
	
	private final List<EpicPvPPlayer> recordedPlayers;
	private final Collection<ArenaFight> fights;
	private final Collection<FightConfirmation> confirmations;
	
	private BukkitTask arenaParser;
	
	private final String findMessage;
	private final TextComponent confirmationMessage;
	
	private PvPArenaManager() {
		tiers = new ArrayList<>();
		arenas = new ArrayList<>();
		
		recordedPlayers = new ArrayList<>();
		fights = new HashSet<>();
		confirmations = new HashSet<>();
		
		findMessage = "§7[§x§f§b§c§b§8§4§lE§x§f§5§b§f§6§8§lp§x§e§f§b§4§4§d§li§x§e§8§a§8§3§1§lc§x§e§2§9§c§1§5§lA§x§e§9§b§1§3§9§lr§x§f§1§c§6§5§d§le§x§f§8§d§b§8§0§ln§x§f§f§f§0§a§4§la§7] "
				+ "§eZnalezlismy dla Ciebie odpowiedniego przeciwnika. "
				+ "Kliknij §aAKCEPTUJ§e, by zaakceptowac wyzwanie. "
				+ "Kliknij §cODRZUC§e, by odrzucic wyzwanie";
		confirmationMessage = new TextComponent("");
		TextComponent accept = new TextComponent("§7[§aAKCEPTUJ§7]");
		accept.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/arena accept"));
		TextComponent reject = new TextComponent("§7[§cODRZUC§7]");
		reject.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/arena reject"));
		TextComponent space = new TextComponent("        ");
		confirmationMessage.addExtra(accept);
		confirmationMessage.addExtra(space);
		confirmationMessage.addExtra(reject);
		
		arenaParser = new BukkitRunnable() {
			@Override
			public void run() {
				if(isCancelled())
					return;

				if(recordedPlayers.isEmpty())
					return;
				Collections.shuffle(recordedPlayers);
				
				List<PvPArena> occupiedArenas = new LinkedList<>();
				occupiedArenas.addAll(fights.stream()
						.map(fight -> fight.getArena())
						.collect(Collectors.toList()));
				occupiedArenas.addAll(confirmations.stream()
						.map(confirm -> confirm.getFight().getArena())
						.collect(Collectors.toList()));
				
				List<PvPArena> freeArenas = arenas.stream()
						.filter(arena -> !occupiedArenas.contains(arena))
						.collect(Collectors.toList());
				if(freeArenas.isEmpty())
					return;
				
				List<Pair<EpicPvPPlayer, EpicPvPPlayer>> matchedPlayers = new LinkedList<>();
				for(EpicPvPPlayer pp1 : recordedPlayers) {
					if(matchedPlayers.size() >= freeArenas.size())
						break;
					if(matchedPlayers.stream()
							.filter(pair -> pair.getKey().equals(pp1) || pair.getValue().equals(pp1))
							.findAny()
							.isPresent())
						continue;
					for(EpicPvPPlayer pp2 : recordedPlayers) {
						if(pp1.equals(pp2))
							continue;
						if(matchedPlayers.stream()
								.filter(pair -> pair.getKey().equals(pp2) || pair.getValue().equals(pp2))
								.findAny()
								.isPresent())
							continue;
						
						ArenaTier pp1Tier = getPlayerTier(pp1.getPlayer()).get();
						ArenaTier pp2Tier = getPlayerTier(pp2.getPlayer()).get();
						if(!pp1Tier.equals(pp2Tier))
							continue;
						
						if(Math.abs(pp1.getPoints() - pp2.getPoints()) > Config.get().getPointDiffLimit())
							continue;
						
						matchedPlayers.add(new Pair<>(pp1, pp2));
						break;
					}
				}
				
//				MutableInt arenaIndex = new MutableInt();
				Random rand = new Random();
				matchedPlayers.stream()
					.forEach(pair -> {
						EpicPvPPlayer pp1 = pair.getKey();
						EpicPvPPlayer pp2 = pair.getValue();
						if(freeArenas.size() < 1)
							return;
						int index = rand.nextInt(freeArenas.size());
						
						PvPArena arena = freeArenas.get(index);
						freeArenas.remove(index);
						ArenaFight fight = ArenaFight.builder()
								.arena(arena)
								.player1(pp1)
								.player2(pp2)
								.status(FightStatus.CONFIRMATION)
								.build();
						
						recordedPlayers.remove(pp1);
						recordedPlayers.remove(pp2);
						prepareFight(fight);
					});
			}
		}.runTaskTimerAsynchronously(Main.getInst(), 0, 20*15);
	}
	
	public static final PvPArenaManager get() {
		return inst;
	}
	
	public void registerTier(ArenaTier tier) {
		tiers.add(tier);
	}
	
	public void registerArena(PvPArena arena) {
		arenas.add(arena);
	}
	
	public Optional<ArenaTier> getPlayerTier(Player p) {
		return tiers.stream()
				.filter(tier -> tier.isPlayerInTier(p))
				.findAny();
	}
	
	private void prepareFight(ArenaFight fight) {
		Player p1 = fight.getPlayer1().getPlayer();
		Player p2 = fight.getPlayer2().getPlayer();
		
		p1.sendMessage(findMessage);
		p1.spigot().sendMessage(ChatMessageType.CHAT, confirmationMessage);
		p2.sendMessage(findMessage);
		p2.spigot().sendMessage(ChatMessageType.CHAT, confirmationMessage);
		
		FightConfirmation confirm = new FightConfirmation(fight);
		confirmations.add(confirm);
	}
	
	public void signUpPlayer(EpicPvPPlayer pp) {
		Player p = pp.getPlayer();
		p.sendMessage("§7["+Config.get().getPrefix()+"§7] §eZapisales sie na walki na arenach PvP!");
		recordedPlayers.add(pp);
	}
	
	public void unsignFromPlayer(EpicPvPPlayer pp) {
		Player p = pp.getPlayer();
		p.sendMessage("§7["+Config.get().getPrefix()+"§7] §eWypisales sie z walk na arenach PvP!");
		recordedPlayers.remove(pp);
		
	}
	
	public void acceptFight(EpicPvPPlayer pp) {
		Player p = pp.getPlayer();
		p.sendMessage("§7["+Config.get().getPrefix()+"§7] §eZaakceptowales udzial w wylosowanej walce na arenie PvP!");
		confirmations.stream()
			.filter(confirm -> confirm.getFight().getPlayer1().equals(pp)
					|| confirm.getFight().getPlayer2().equals(pp))
			.findAny()
			.ifPresent(confirm -> confirm.acceptFight(pp));
	}
	
	public void rejectFight(EpicPvPPlayer pp) {
		Player p = pp.getPlayer();
		p.sendMessage("§7["+Config.get().getPrefix()+"§7] §eOdrzuciles udzial w wylosowanej walce na arenie PvP!");
		confirmations.stream()
			.filter(confirm -> confirm.getFight().getPlayer1().equals(pp)
					|| confirm.getFight().getPlayer2().equals(pp))
			.findAny()
			.ifPresent(confirm -> confirm.rejectFight(pp));
	}
	
	public void startFight(ArenaFight fight) {
		fight.calcPoints();
		fights.add(fight);
		fight.setStatus(FightStatus.TELEPORTATION);
		fight.startTeleportation();
	}
	
}
