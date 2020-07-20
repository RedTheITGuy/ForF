package me.RedTheITGuy.ForF;
import org.bukkit.plugin.java.JavaPlugin;

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
	}
}
