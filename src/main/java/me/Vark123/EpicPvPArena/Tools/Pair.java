package me.Vark123.EpicPvPArena.Tools;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Pair <K, V> {

	private K key;
	private V value;
	
}
