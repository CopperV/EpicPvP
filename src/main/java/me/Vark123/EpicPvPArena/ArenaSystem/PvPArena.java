package me.Vark123.EpicPvPArena.ArenaSystem;

import lombok.Builder;
import lombok.Getter;
import me.Vark123.EpicPvPArena.Tools.EpicLocation;

@Getter
@Builder
public class PvPArena {

	private String id;
	private String display;
	
	private EpicLocation resp1;
	private EpicLocation resp2;
	
}
