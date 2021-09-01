package com.civuniverse.goldplocks;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import java.util.logging.Level;

public final class Goldplocks extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getLogger().log(Level.INFO, "Goldplocks plugin has loaded!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onElevator(PlayerInteractEvent event) {
        if (event.hasBlock() && event.getHand() == EquipmentSlot.HAND) {
            if (event.getClickedBlock().getType() == Material.GOLD_BLOCK) {
                Location loc = event.getPlayer().getLocation();
                loc.add(0, -1, 0);
                if (event.getClickedBlock().getLocation().distanceSquared(loc) < 1.0) {

                    int airCount = 0;
                    int relativeAltitude = 1;
                    boolean pocketFound = false;
                    boolean goldFound = false;
                    Material itemInHand = event.getPlayer().getInventory().getItemInMainHand().getType();
                    String itemInHandString = itemInHand.toString();
                    if (event.getAction().name() == "RIGHT_CLICK_BLOCK" && itemInHand != Material.TORCH && !itemInHandString.contains("_SIGN")) {
                        while (!pocketFound && relativeAltitude + event.getClickedBlock().getY() < 256) {
                            relativeAltitude++;
                            Material toCheck = event.getClickedBlock().getRelative(0, relativeAltitude, 0).getType();
                            String materialString = toCheck.toString();
                            if (toCheck == Material.AIR || materialString.contains("_SIGN")) {
                                airCount++;
                                if (goldFound && airCount > 1) {
                                    loc.setY(event.getClickedBlock().getY() + relativeAltitude - 1);
                                    loc.setX(event.getClickedBlock().getX() + 0.5);
                                    loc.setZ(event.getClickedBlock().getZ() + 0.5);
                                    pocketFound = true;
                                }
                            } else {
                                airCount = 0;
                                goldFound = false;
                                if (event.getClickedBlock().getRelative(0, relativeAltitude, 0)
                                        .getType() == Material.GOLD_BLOCK) {
                                    goldFound = true;
                                }
                            }
                        }
                        if (relativeAltitude + event.getClickedBlock().getY() > 255) {
                            return;
                        }
                    } else { // LEFT CLICK
                        while (!pocketFound && relativeAltitude + event.getClickedBlock().getY() > 0) {
                            relativeAltitude--;
                            Material toCheck = event.getClickedBlock().getRelative(0, relativeAltitude, 0).getType();
                            String materialString = toCheck.toString();
                            if (toCheck == Material.AIR || materialString.contains("_SIGN")) {
                                airCount++;
                            } else {
                                goldFound = false;
                                if (event.getClickedBlock().getRelative(0, relativeAltitude, 0)
                                        .getType() == Material.GOLD_BLOCK) {
                                    goldFound = true;
                                }
                                if (goldFound && airCount > 1) {
                                    loc.setY(event.getClickedBlock().getY() + relativeAltitude + 1);
                                    loc.setX(event.getClickedBlock().getX() + 0.5);
                                    loc.setZ(event.getClickedBlock().getZ() + 0.5);
                                    pocketFound = true;
                                }
                                airCount = 0;
                            }
                        }
                        if (relativeAltitude + event.getClickedBlock().getY() < 1) {
                            return;
                        }
                    }
                    event.getPlayer().teleport(loc);
                    event.getPlayer().setVelocity(new Vector(0, 0, 0));
                }
            }
        }
    }
}
