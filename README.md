# Friend or Foe Manager Plugin
The plugin which allows the Friend or Foe server to function, adding needed behaviour.

## What does it do?
This plugin hooks into WorldGuard to enable/disable PVP at random time intervals. It also sends a title to the users to signify this change.

## Requirments
* Spigot
  * This plugin uses the Spigot API to communicate with the server and client.
  * Bukkit may work, but only Spigot, and servers based on Spigot such as Paper, are officialy supported.
* WorldGuard
  * This plugin changes the WorldGuard PVP flag in global to toggle PVP, so will need WorldGuard to function.
