package me.RedTheITGuy.ForF;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

public class Main extends JavaPlugin {
	// Fired when the plugin is first enabled
	@Override
	public void onEnable() {
		// Creates the config if it does not exist
		this.saveDefaultConfig();
		
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
			// Attempts to save the region
			try {
				localRegions.save();
			// Called on error
			} catch (StorageException e) {
				// Logs the error to the consoles
				this.getLogger().warning("Exception when saving region data: ");
				// Outputs the error to the console
				e.printStackTrace();
			}
		}
		
		// Creates the thread to run the EnablePVP class
		Thread thread = new Thread(new EnablePVP());
		// Runs the EnablePVP class on a seperate thread
		thread.start();
		
	}
}
