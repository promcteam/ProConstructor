package fr.weefle.constructor.essentials;


import fr.weefle.constructor.Config;
import fr.weefle.constructor.SchematicBuilder;
import fr.weefle.constructor.essentials.BuilderTrait.BuilderState;
import fr.weefle.constructor.menu.Menu;
import fr.weefle.constructor.menu.Slot;
import fr.weefle.constructor.menu.menus.ExcavatedMenu;
import fr.weefle.constructor.menu.menus.MaterialsMenu;
import fr.weefle.constructor.menu.menus.ParameterMenu;
import mc.promcteam.engine.utils.ItemUT;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("deprecation")
public class BuilderListener implements Listener {

    public static Map<String, Integer> materials = new HashMap<>();

    @EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
    public void place(org.bukkit.event.block.BlockPlaceEvent event) {
        BuilderTrait inst = SchematicBuilder.getBuilder(event.getPlayer());
        if (inst != null) event.setCancelled(false);
    }

	/*@EventHandler
	public void onItemDrop(PlayerDropItemEvent e){
		Player p = (Player) e.getPlayer();
		if(e.getPlayer().getOpenInventory().getTitle().equals("Constructor - Schematics")) {
			e.getItemDrop().remove();



		}else if(e.getPlayer().getOpenInventory().getTitle().equals("Constructor - NPCs")){
			e.getItemDrop().remove();
			for(NPC npc : CitizensAPI.getNPCRegistry().sorted()) {
				if (npc.getName().equals(Objects.requireNonNull(e.getItemDrop().getItemStack().getItemMeta()).getDisplayName())) {
					if (npc.hasTrait(ConstructorTrait.class)) {
						p.performCommand("constructor " + npc.getId() + " cancel");
					}
				}
			}
			//p.performCommand("npc remove " + Objects.requireNonNull(e.getItemDrop().getItemStack().getItemMeta()).getDisplayName());

		}
	}*/

	/*@EventHandler
	public void onInventoryClose(InventoryCloseEvent event){
		Player p = (Player) event.getPlayer();

		InventoryView view = p.getOpenInventory();
		if(view.getTitle().equals("Constructor - Materials") || view.getTitle().equals("Constructor - Excavated")){
			if(materials.values().stream().noneMatch(integer -> integer > 0)) materials = new HashMap<>();
		}

	}*/

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();

        InventoryView view = event.getView();
        Inventory otherInventory = view.getTopInventory() == inventory ? view.getBottomInventory() : view.getTopInventory();
        if (otherInventory.getHolder() instanceof Menu && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            event.setCancelled(true);
        }

        InventoryHolder holder = inventory.getHolder();
        if (holder instanceof Menu) {
            event.setCancelled(true);
            Menu menu = (Menu) holder;
            Slot slot = menu.getSlot(event.getSlot());
            if (slot != null) {
                switch (event.getClick()) {
                    case LEFT: {
                        slot.onLeftClick();
                        break;
                    }
                    case SHIFT_LEFT: {
                        slot.onShiftLeftClick();
                        break;
                    }
                    case RIGHT: {
                        slot.onRightClick();
                        break;
                    }
                    case SHIFT_RIGHT: {
                        slot.onShiftRightClick();
                        break;
                    }
                    case NUMBER_KEY: {
                        slot.onNumberClick(event.getHotbarButton());
                        break;
                    }
                    case DOUBLE_CLICK: {
                        slot.onDoubleClick();
                        break;
                    }
                    case DROP: {
                        slot.onDrop();
                        break;
                    }
                    case CONTROL_DROP: {
                        slot.onControlDrop();
                        break;
                    }
                    case SWAP_OFFHAND: {
                        slot.onSwapOffhand();
                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Menu) {
            Menu menu = (Menu) holder;
            if (!menu.isOpening()) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        menu.onClose();
                    }
                }.runTask(SchematicBuilder.getInstance());
            }
        }
    }

    @EventHandler
    public void clickedme2(NPCLeftClickEvent event) {
        Player player = event.getClicker();
        NPC npc = event.getNPC();
        BuilderTrait inst = SchematicBuilder.getBuilder(npc);
        if (inst != null) {
            if (inst.State == BuilderState.collecting) {
                player.sendMessage(SchematicBuilder.format(SchematicBuilder.getInstance().config().getSupplyListMessage(),
                        npc,
                        inst.schematic,
                        player,
                        null,
                        "0"));
                new MaterialsMenu(player, npc).open();
            } else if (inst.State == BuilderState.building && inst.Excavate && !inst.ExcavateMaterials.isEmpty()) {
                new ExcavatedMenu(player, npc).open();
            }
        }
			/*TextComponent message = new TextComponent("Winner (Click to view inventory)");
			message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/winnerinv"));
			player.spigot().sendMessage(message);*/
    }

    @EventHandler
    public void clickedme(net.citizensnpcs.api.event.NPCRightClickEvent event) {
        NPC npc = event.getNPC();
        BuilderTrait inst = SchematicBuilder.getBuilder(npc);
        if (inst == null) { return; }
        Player player = event.getClicker();
        if (inst.State == BuilderState.idle || inst.State == BuilderState.building) {
            player.performCommand("npc select " + npc.getId());
            new ParameterMenu(event.getClicker(), npc).open();
        } else if (inst.State == BuilderState.collecting) {
            ItemStack is = player.getItemInHand();

            if (is.getType().isBlock() && !(is.getType() == Material.AIR)) {


                String itemname = is.getType().name();


                if (!player.hasPermission("schematicbuilder.donate")) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to donate");
                    return;
                }

                int needed = inst.NeededMaterials.getOrDefault(itemname, 0);
                Config config = SchematicBuilder.getInstance().config();
                if (needed > 0) {

                    //yup, i need it
                    int taking = Math.min(is.getAmount(), needed);

                    if (inst.Sessions.containsKey(player) && System.currentTimeMillis() < inst.Sessions.get(player) + 5 * 1000) {
                        //take it

                        //update player hand item
                        ItemStack newis;

                        if (is.getAmount() - taking > 0) newis = is.clone();
                        else newis = new ItemStack(Material.AIR);
                        newis.setAmount(is.getAmount() - taking);
                        event.getClicker().setItemInHand(newis);

                        //update needed

                        inst.NeededMaterials.put(is.getType(), (needed - taking));
                        player.sendMessage(SchematicBuilder.format(config.getSupplyTakenMessage(), inst.getNPC(),
                                inst.schematic,
                                player,
                                itemname,
                                taking + ""));
                        //check if can start
                        inst.TryBuild(null);

                    } else {
                        player.sendMessage(SchematicBuilder.format(config.getSupplyNeedMessage(),
                                inst.getNPC(),
                                inst.schematic,
                                player,
                                itemname,
                                needed + ""));
                        inst.Sessions.put(player, System.currentTimeMillis());
                    }

                } else {
                    player.sendMessage(SchematicBuilder.format(config.getSupplyDontNeedMessage(),
                            inst.getNPC(),
                            inst.schematic,
                            player,
                            itemname,
                            "0"));
                    //don't need it or already have it.
                }
            }
        }


    }


    @EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
    public void NavCom(net.citizensnpcs.api.ai.event.NavigationCompleteEvent event) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalArgumentException, InvocationTargetException, IllegalAccessException {
        NPC npc = null;
        for (NPC n : CitizensAPI.getNPCRegistry()) {
            if (n.getNavigator() == event.getNavigator()) {
                npc = n;
                break;
            }

        }

        //	plugin.getLogger().info("nav complete " + npc);

        BuilderTrait inst = SchematicBuilder.getBuilder(npc);

        if (inst == null) return;
        if (inst.State != BuilderState.idle) {
            inst.PlaceNextBlock();
        }

    }


    @EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
    public void NavCan(net.citizensnpcs.api.ai.event.NavigationCancelEvent event) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalArgumentException, InvocationTargetException, IllegalAccessException {
        NPC npc = null;
        for (NPC n : CitizensAPI.getNPCRegistry()) {
            if (n.getNavigator() == event.getNavigator()) {
                npc = n;
                break;
            }
        }
        BuilderTrait inst = SchematicBuilder.getBuilder(npc);

        //	plugin.getLogger().info("nav cancel " + npc);

        if (inst == null) return;

        if (inst.State != BuilderState.idle) {
            inst.PlaceNextBlock();
        }

    }


    private static ItemStack getItem(String b64stringtexture) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        ItemUT.addSkullTexture(head, b64stringtexture);
        ItemMeta headMeta = head.getItemMeta();
        if (headMeta != null) {
            headMeta.setDisplayName("Materials");
            ArrayList<String> lore = new ArrayList<>();
            lore.add("Next Page");
            headMeta.setLore(lore);
            head.setItemMeta(headMeta);
        }
        return head;
    }

    private static <T> Field getField(Class<?> target, String name, Class<T> fieldType, int index) {
        for (final Field field : target.getDeclaredFields()) {
            if ((name == null || field.getName().equals(name)) && fieldType.isAssignableFrom(field.getType()) && index-- <= 0) {
                field.setAccessible(true);
                return field;
            }
        }

        // Search in parent classes
        if (target.getSuperclass() != null)
            return getField(target.getSuperclass(), name, fieldType, index);
        throw new IllegalArgumentException("Cannot find field with type " + fieldType);
    }

    public ItemStack getHead(String p) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        ItemMeta  m     = skull.getItemMeta();
        SkullMeta s     = (SkullMeta) m;
        assert s != null;
        s.setOwner(p);
        skull.setItemMeta(s);
        return skull;
    }

}
