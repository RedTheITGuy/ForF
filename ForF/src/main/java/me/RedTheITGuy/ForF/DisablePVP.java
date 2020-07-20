package me.RedTheITGuy.ForF;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

public class DisablePVP implements Runnable {
	@Override
	public void run() {
		// Gets the config file
		FileConfiguration config = Bukkit.getPluginManager().getPlugin("ForF").getConfig();
		// Gets info from the config
		long minTime = config.getLong("minPVP");
		long maxTime = config.getLong("maxPVP");
		String title = config.getString("messages.disabled.title");
		String subtitle = config.getString("messages.disabled.subtitle");
		String message = config.getString("messages.disabled.message");
		String soundString = config.getString("sound.disabled");
		
		// Converts the colour codes in the text
		if (title != null) title = ChatColor.translateAlternateColorCodes('&', title);
		if (subtitle != null) subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
		if (message != null) message = ChatColor.translateAlternateColorCodes('&', message);
		
		// Attempts to convert the sound string to a sound
		Sound sound = Sound.valueOf(soundString);
		// Outputs to the console if the config is invalid
		if (sound == null) Bukkit.getLogger().warning("Invalid enable sound in config.");
		
		// Creates the random number generator
		Random random = new Random();
		
		try {
			TimeUnit.MINUTES.sleep(random.longs(minTime, (maxTime + 1)).findFirst().getAsLong());
		} catch (InterruptedException e1) {
			// Output error to the console
			Bukkit.getLogger().warning("Sleep interupted.");
		}
		
		// Gets the region container to load the regions
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		
		// Creates a list to store the region manager of all the worlds
		List<RegionManager> regions = new ArrayList<>();
		// Runs for ever world loaded by spigot
		for (World world : Bukkit.getServer().getWorlds()) {
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
				Bukkit.getLogger().warning("Exception when saving region data: ");
				// Outputs the error to the console
				e.printStackTrace();
			}
		}
		
		// Runs for every player in the server
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			// Sends the title to the player
			player.sendTitle(title, subtitle, 10, 70, 20);
			// Plays the sound if there is one
			if (sound != null) player.playSound(player.getLocation(), sound, SoundCategory.VOICE, 10F, 1F);
			// Sends a message if there is one
			if (message != "" && message != null) player.sendMessage(message); 
		}
		
		// Creates the thread to run the EnablePVP class
		Thread thread = new Thread(new EnablePVP());
		// Runs the EnablePVP class on a seperate thread
		thread.start();
		
	}
}
