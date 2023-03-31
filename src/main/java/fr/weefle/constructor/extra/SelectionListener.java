package fr.weefle.constructor.extra;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Objects;

public class SelectionListener implements Listener {

    private       boolean                   leftClick = false;
    private       Location                  leftLocation;
    public static HashMap<Player, Vector>   vector    = new HashMap<>();
    public static HashMap<Player, Location> location  = new HashMap<>();

    @EventHandler
    public void onSelectEvent(PlayerInteractEvent e) {

        if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {


            if (e.getMaterial().equals(Material.BLAZE_ROD)) {


                if (e.getPlayer().hasPermission("schematicbuilder.select")) {

                    e.setCancelled(true);

                    if (!leftClick) {
                        leftClick = true;
                        //partie ci-dessous pour faire affichage preview schematic
                        /*BlockData bdata = Bukkit.createBlockData(Material.STONE);
                        Location loc = e.getPlayer().getLocation();
                        e.getPlayer().sendBlockChange(loc, bdata);
                        Bukkit.getServer().getWorld(e.getPlayer().getWorld().getName()).getBlockAt(loc).getState().update();*/
                        leftLocation = Objects.requireNonNull(e.getClickedBlock()).getLocation();
                        e.getPlayer().sendMessage(ChatColor.GREEN + "First point selected!");
                        e.getPlayer().sendMessage(ChatColor.RED + "Please select an other location!");
                    }
                }


            }

        } else if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {


            if (e.getMaterial().equals(Material.BLAZE_ROD)) {


                if (e.getPlayer().hasPermission("schematicbuilder.select")) {
                    e.setCancelled(true);


                    if (leftClick) {
                        leftClick = false;
                        Location rightLocation = Objects.requireNonNull(e.getClickedBlock()).getLocation();

                        Cuboid selection = new Cuboid(leftLocation, rightLocation);

                        Vector fin = new Vector(selection.getSizeX(), selection.getSizeY(), selection.getSizeZ());
                        if (selection.getLowerY() < selection.getUpperY()) {
                            location.put(e.getPlayer(), selection.getLowerNE());
                        } else {
                            location.put(e.getPlayer(), selection.getUpperSW());
                        }
                        vector.put(e.getPlayer(), fin);
                        e.getPlayer().sendMessage(ChatColor.GREEN + "Second point selected!");
                        e.getPlayer().sendMessage(ChatColor.BLUE + "Area selected!");
                    }
                }


            }


        }
    }

}