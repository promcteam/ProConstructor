package studio.magemonkey.blueprint.hooks;

/*import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;*/

public class WorldEditManager {

    /*public static Vector getWorldEditSelection(Player ply) {
        Plugin we = Bukkit.getPluginManager().getPlugin("WorldEdit");
        if(we instanceof WorldEditPlugin) {

            LocalSession l = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(ply));


            Region s = null;
            try {
                s = l.getSelection(WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(ply)).getSelectionWorld());
            } catch (IncompleteRegionException e) {

            }
            return new Vector(s.getWidth(), s.getHeight(), s.getLength());


        }

        return null;
    }

    public static Location getWorldEditLocation(Player ply){
        Plugin we = Bukkit.getPluginManager().getPlugin("WorldEdit");
        if(we instanceof WorldEditPlugin) {

            LocalSession l = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(ply));


            Region s = null;
            try {
                s = l.getSelection(WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(ply)).getSelectionWorld());
            } catch (IncompleteRegionException e) {

            }
            int x = s.getMinimumPoint().getBlockX();
            int y = s.getMinimumPoint().getBlockY();
            int z = s.getMinimumPoint().getBlockZ();

            return new Location(ply.getWorld(), x, y, z);



        }
        return null;
    }*/

}
