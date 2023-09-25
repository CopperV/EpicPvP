package me.Vark123.EpicPvPArena.Tools;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class EpicLocation {

	private String world;
	
	private double x;
	private double y;
	private double z;
	
	private float pitch;
	private float yaw;
	
	public EpicLocation(String world, double x, double y, double z) {
		this(world, x, y, z, 0, 0);
	}
	
	public Location toBukkitLocation() {
		World w = Bukkit.getWorld(world);
		return new Location(w, x, y, z, yaw, pitch);
	}
	
}
