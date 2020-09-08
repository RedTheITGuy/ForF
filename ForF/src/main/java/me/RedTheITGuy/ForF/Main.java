package me.RedTheITGuy.ForF;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
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

public class Main extends JavaPlugin implements Listener{
	// Fired when the plugin is first enabled
	@Override
	public void onEnable() {
		// Creates the config if it does not exist
		this.saveDefaultConfig();
		
		// Gets the region container to load the regions
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		
		// Creates a list to store the region manager of all the worlds
		List<RegionManager> regions = new ArrayList<>();
		// Runs for ever world loaded by Spigot
		for (World world : this.getServer().getWorlds()) {
			// Converts the world to a world edit variable because the world guard API is terrible :)
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
		
		// Creates the variable to store the bar style
		BarStyle barStyle = BarStyle.SOLID;
		// Tries to get the bar style from the config
		try {
			barStyle = BarStyle.valueOf(this.getConfig().getString("bar.style"));
		}
		// Catches if the config is set incorrectly
		catch (IllegalArgumentException exception) {
			Bukkit.getLogger().warning("Cannot get bar style for entry in config, please insure the config has a valid bar style.");
		}
		
		// Creates the variable to store the bar style
		BarColor barColour = BarColor.GREEN;
		// Tries to get the bar style from the config
		try {
			barColour = BarColor.valueOf(this.getConfig().getString("bar.disabled.colour"));
		}
		// Catches if the config is set incorrectly
		catch (IllegalArgumentException exception) {
			Bukkit.getLogger().warning("Cannot get bar colour for entry in config, please insure the config has a valid bar style.");
		}
		
		// Gets the bar title
		String barTitle = this.getConfig().getString("bar.disabled.title");
		// Converts the colour codes in the text
		if (barTitle != null) barTitle = ChatColor.translateAlternateColorCodes('&', barTitle);
		
		// Creates the key for the boss bar
		NamespacedKey barKey = new NamespacedKey(this, "pvpBar");
		// Gets the boss bar
		KeyedBossBar bossBar = Bukkit.getServer().getBossBar(barKey);
		// Creates the bar if it doesn't exist
		if (bossBar == null) bossBar = Bukkit.getServer().createBossBar(barKey, barTitle, barColour, barStyle);
		
		// Registers the listener for the player violation event
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		
		// Creates the thread to run the EnablePVP class
		Thread thread = new Thread(new EnablePVP());
		// Runs the EnablePVP class on a separate thread
		thread.start();
	}
	
	// Runs when a player joins
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		// Creates the key for the boss bar
		NamespacedKey barKey = new NamespacedKey(this, "pvpBar");
		// Gets the boss bar
		KeyedBossBar bossBar = Bukkit.getServer().getBossBar(barKey);
		// Adds the player to the bar if it exists
		if (bossBar != null) bossBar.addPlayer(event.getPlayer());
	}
}
