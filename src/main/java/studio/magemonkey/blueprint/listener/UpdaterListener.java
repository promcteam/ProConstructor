package studio.magemonkey.blueprint.listener;

import studio.magemonkey.blueprint.Updater;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdaterListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        if (Updater.enabledingame) {
            if (e.getPlayer().isOp()) {
                if (Updater.update) {
                    e.getPlayer()
                            .sendMessage(
                                    "§6[§2Updater§6] §3There is an update for §5Blueprint§3, look at the link in the console!");
                } else {
                    return;
                }
            }
        }

    }

}