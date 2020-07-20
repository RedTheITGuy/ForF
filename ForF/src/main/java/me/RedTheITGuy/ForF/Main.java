package me.RedTheITGuy.ForF;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

public class Main extends JavaPlugin {
	// Fired when the plugin is first enabled
	@Override
	public void onEnable() {
		// Creates the config if it does not exist
		this.saveDefaultConfig();
		// Gets all the info from the config
		int minFirstPVP = this.getConfig().getInt("minFirstPVP");
		int maxFirstPVP = this.getConfig().getInt("maxFirstPVP");
		int minPVP = this.getConfig().getInt("minPVP");
		int maxPVP = this.getConfig().getInt("maxPVP");
		String pvpOnTitle = this.getConfig().getString("messages.enabled.title");
		String pvpOnSub = this.getConfig().getString("messages.enabled.subtitle");
		String pvpOnMessage = this.getConfig().getString("messages.enabled.message");
		String pvpOffTitle = this.getConfig().getString("messages.disabled.title");
		String pvpOffSub = this.getConfig().getString("messages.disabled.subtitle");
		String pvpOffMessage = this.getConfig().getString("messages.disabled.message");
		
		// Gets the region container to load the regions
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		
		// Creates a list to store the region manager of all the worlds
		List<RegionManager> regions = new ArrayList<>();
		// Runs for ever world loaded by spigot
		for (World world : this.getServer().getWorlds()) {
			// Convers the world to a world edit varible because the world guard api is terrible :)
			com.sk89q.worldedit.world.World worldEditWorld = BukkitAdapter.adapt(world);
			// Adds the region manager for the world to the regions list
			regions.add(container.get(worldEditWorld));
		}
		
		// Runs for every world in the regions list
		for (RegionManager localRegions : regions) {
			// Gets the global region (world default region)
			ProtectedRegion global = localRegions.getRegion("__global__");
			// Runs if the region doesn't exist
			if (global == null) {
				// Creates the global region because I have to create it because... ?
				global = new GlobalProtectedRegion("__global__");
				// Adds the region to the world
				localRegions.addRegion(global);
			}
			// Disables PVP in that region
			global.setFlag(Flags.PVP, StateFlag.State.DENY);
		}
	}
}
