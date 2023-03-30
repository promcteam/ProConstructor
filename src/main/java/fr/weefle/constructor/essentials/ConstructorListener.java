package fr.weefle.constructor.essentials;


import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import fr.weefle.constructor.Constructor;
import fr.weefle.constructor.essentials.ConstructorTrait.BuilderState;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.npc.NPC;
//import net.citizensnpcs.npc.ai.speech.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
//import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@SuppressWarnings("deprecation")
public class ConstructorListener implements Listener {

	public Constructor plugin;
	public static Map<String, Integer> materials = new HashMap<>();

	public ConstructorListener(Constructor builderplugin) {

		plugin = builderplugin;

	}


	@EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
	public void place(org.bukkit.event.block.BlockPlaceEvent event){
		ConstructorTrait inst = plugin.getBuilder(event.getPlayer());
		if (inst!=null) event.setCancelled(false);
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
		ItemStack clicked = event.getCurrentItem(); // The item that was clicked
		Inventory inventory = event.getInventory(); // The inventory that was clicked in
		if(inventory.getHolder() instanceof Player){
			Player p = (Player) inventory.getHolder();
			InventoryView view = p.getOpenInventory();
			if(view.getTitle().equals("Constructor - Schematics")){
				assert clicked != null;
				try{
				if (clicked.getType() == Material.MAP) { // The item that the player clicked it dirt
					event.setCancelled(true);
					Objects.requireNonNull(Bukkit.getServer().getWorld(p.getWorld().getUID())).playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
						p.performCommand("constructor load " + Objects.requireNonNull(clicked.getItemMeta()).getDisplayName());
						p.closeInventory();

				}else if (clicked.getType() == Material.PLAYER_HEAD && Objects.requireNonNull(Objects.requireNonNull(clicked.getItemMeta()).getLore()).get(0).equals("Next Page")) { // The item that the player clicked it dirt
					Objects.requireNonNull(Bukkit.getServer().getWorld(p.getWorld().getUID())).playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
					Inventory inv = Bukkit.createInventory(p, 54, "Constructor - Schematics");
					int nb = 0;
					for (String fileName : materials.keySet()) {
						int count = ConstructorListener.materials.get(fileName);
						if (nb < 53) {
							if (count > 0) {
								ItemStack is = new ItemStack(Material.MAP);
								ItemMeta im = is.getItemMeta();
								assert im != null;
								im.setDisplayName(fileName);
								is.setItemMeta(im);
								inv.setItem(nb, is);
								nb++;
							}
						}
						ConstructorListener.materials.put(fileName, 0);
						if (nb == 53) {
							ItemStack ims = getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGVmMzU2YWQyYWE3YjE2NzhhZWNiODgyOTBlNWZhNWEzNDI3ZTVlNDU2ZmY0MmZiNTE1NjkwYzY3NTE3YjgifX19");
							inv.setItem(nb, ims);
							break;
						}
					}
					p.openInventory(inv);
				}else{
					event.setCancelled(true);
				}
			}catch (NullPointerException e){
					event.setCancelled(true);
			}
			}else if(view.getTitle().equals("Constructor - NPCs")) {
				assert clicked != null;
				try {
					if (clicked.getType() == Material.PLAYER_HEAD) {
						event.setCancelled(true);
						Objects.requireNonNull(Bukkit.getServer().getWorld(p.getWorld().getUID())).playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
						if ((event.getClick().equals(ClickType.RIGHT)) && (p.hasPermission("constructor.npc.click"))){
							for(NPC npc : CitizensAPI.getNPCRegistry().sorted()){
								if(npc.getName().equals(Objects.requireNonNull(clicked.getItemMeta()).getDisplayName())){
									if(npc.hasTrait(ConstructorTrait.class)){
										npc.removeTrait(ConstructorTrait.class);
										p.closeInventory();
									}else{
										npc.addTrait(ConstructorTrait.class);
										p.closeInventory();
									}
								}
							}
						} else if ((event.getClick().equals(ClickType.LEFT)) && (p.hasPermission("constructor.npc.click"))){
							for(NPC npc : CitizensAPI.getNPCRegistry().sorted()) {
								if (npc.getName().equals(Objects.requireNonNull(clicked.getItemMeta()).getDisplayName())) {
									if (npc.hasTrait(ConstructorTrait.class)) {
										p.performCommand("npc select " + npc.getId());
										Inventory inv = Bukkit.createInventory(p, 54, "Constructor - Parameters");
										if(plugin.getBuilder(npc) != null && (plugin.getBuilder(npc).Excavate == null ||plugin.getBuilder(npc).RequireMaterials == null ||plugin.getBuilder(npc).IgnoreLiquid == null || plugin.getBuilder(npc).IgnoreAir == null)) {
											plugin.getBuilder(npc).IgnoreLiquid = false;
											plugin.getBuilder(npc).Excavate = false;
											plugin.getBuilder(npc).RequireMaterials = false;
											plugin.getBuilder(npc).IgnoreAir = false;
										}
										ItemStack itemStack1, itemStack2, itemStack3, itemStack4, itemStack5, itemStack6;
										ItemMeta im1, im2, im3, im4, im5, im6;
										if(plugin.getBuilder(npc).State == BuilderState.building){
											itemStack1 = getHead(npc.getName());
											im1 = itemStack1.getItemMeta();
											assert im1 != null;
											im1.setDisplayName(npc.getName());
											ArrayList<String> Lore = new ArrayList<>();
											Lore.add(npc.getName() + " is building...");
											Lore.add("Click to cancel building");
											im1.setLore(Lore);
										}else{
											itemStack1 = getHead(npc.getName());
											im1 = itemStack1.getItemMeta();
											assert im1 != null;
											im1.setDisplayName(npc.getName());
											ArrayList<String> Lore = new ArrayList<>();
											Lore.add(npc.getName() + " isn't building.");
											Lore.add("Click to start building !");
											im1.setLore(Lore);
										}
										itemStack1.setItemMeta(im1);
										inv.setItem(0, itemStack1);
										if(plugin.getBuilder(npc).Excavate){
											itemStack2 = new ItemStack(Material.GREEN_CONCRETE);
											im2 = itemStack2.getItemMeta();
											assert im2 != null;
											im2.setDisplayName("Excavate");
										}else{
											itemStack2 = new ItemStack(Material.RED_CONCRETE);
											im2 = itemStack2.getItemMeta();
											assert im2 != null;
											im2.setDisplayName("Excavate");
										}
										itemStack2.setItemMeta(im2);
										inv.setItem(1, itemStack2);

										if(plugin.getBuilder(npc).IgnoreAir){
											itemStack3 = new ItemStack(Material.GREEN_CONCRETE);
											im3 = itemStack3.getItemMeta();
											assert im3 != null;
											im3.setDisplayName("IgnoreAir");
										}else{
											itemStack3 = new ItemStack(Material.RED_CONCRETE);
											im3 = itemStack3.getItemMeta();
											assert im3 != null;
											im3.setDisplayName("IgnoreAir");
										}

										itemStack3.setItemMeta(im3);
										inv.setItem(2, itemStack3);

										if(plugin.getBuilder(npc).IgnoreLiquid){
											itemStack4 = new ItemStack(Material.GREEN_CONCRETE);
											im4 = itemStack4.getItemMeta();
											assert im4 != null;
											im4.setDisplayName("IgnoreLiquid");
										}else{
											itemStack4 = new ItemStack(Material.RED_CONCRETE);
											im4 = itemStack4.getItemMeta();
											assert im4 != null;
											im4.setDisplayName("IgnoreLiquid");
										}
										itemStack4.setItemMeta(im4);
										inv.setItem(3, itemStack4);

										if(plugin.getBuilder(npc).RequireMaterials){
											itemStack5 = new ItemStack(Material.GREEN_CONCRETE);
											im5 = itemStack4.getItemMeta();
											assert im5 != null;
											im5.setDisplayName("RequireMaterials");
										}else{
											itemStack5 = new ItemStack(Material.RED_CONCRETE);
											im5 = itemStack5.getItemMeta();
											assert im5 != null;
											im5.setDisplayName("RequireMaterials");
										}
										itemStack5.setItemMeta(im5);
										inv.setItem(4, itemStack5);

										itemStack6 = new ItemStack(Material.BOOK);
										im6 = itemStack6.getItemMeta();
										assert im6 != null;
										im6.setDisplayName("Choose your schematic !");


										itemStack6.setItemMeta(im6);
										inv.setItem(5, itemStack6);


										p.openInventory(inv);

									}else {
										p.sendMessage(ChatColor.GOLD + Objects.requireNonNull(clicked.getItemMeta()).getDisplayName() + ChatColor.RED + " isn't a constructor, right-click on it to make it one!");
										p.closeInventory();
									}
								}
							}
					}else{
							event.setCancelled(true);


						}
					}else if (clicked.getType() == Material.PLAYER_HEAD && Objects.requireNonNull(Objects.requireNonNull(clicked.getItemMeta()).getLore()).get(0).equals("Next Page")) { // The item that the player clicked it dirt
						Objects.requireNonNull(Bukkit.getServer().getWorld(p.getWorld().getUID())).playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
						Inventory inv = Bukkit.createInventory(p, 54, "Constructor - NPCs");
						int nb = 0;
						for(String npcName : ConstructorListener.materials.keySet()){
							int count = ConstructorListener.materials.get(npcName);

							if (nb < 53) {
								if (count > 0) {
									ItemStack is = getHead(npcName);
									ItemMeta im = is.getItemMeta();
									assert im != null;
									im.setDisplayName(npcName);
									ArrayList<String> Lore = new ArrayList<String>();
									for (NPC npcc : CitizensAPI.getNPCRegistry().sorted()) {
										if (npcc.getName().equals(npcName)) {
											if (npcc.hasTrait(ConstructorTrait.class)) {
												Lore.add("This NPC can build.");
												Lore.add("Right-click to remove constructor's trait");
												Lore.add("Left-click to enter parameters");
											} else {
												Lore.add("This NPC can't build!");
												Lore.add("Right-click to add constructor's trait");
											}
										}
									}
									im.setLore(Lore);
									is.setItemMeta(im);
									inv.setItem(nb, is);
									nb++;
								}
							}
							ConstructorListener.materials.put(npcName, 0);
							if (nb == 53) {
								ItemStack ims = getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGVmMzU2YWQyYWE3YjE2NzhhZWNiODgyOTBlNWZhNWEzNDI3ZTVlNDU2ZmY0MmZiNTE1NjkwYzY3NTE3YjgifX19");
								inv.setItem(nb, ims);
								break;
							}

						}
						p.openInventory(inv);
					}else{
						event.setCancelled(true);
					}
				}catch (NullPointerException e){
					event.setCancelled(true);
				}

			}else if(view.getTitle().equals("Constructor - Materials")){
				assert clicked != null;
				try{
					if (clicked.getType() == Material.PLAYER_HEAD && Objects.requireNonNull(Objects.requireNonNull(clicked.getItemMeta()).getLore()).get(0).equals("Next Page")) { // The item that the player clicked it dirt
						event.setCancelled(true);
						Objects.requireNonNull(Bukkit.getServer().getWorld(p.getWorld().getUID())).playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
						Inventory inv = Bukkit.createInventory(p, 54, "Constructor - Materials");
						int nb = 0;
						for (String item : materials.keySet()) {

							int count = materials.get(item);
							//Bukkit.getLogger().warning(item + " : " + count);
							while (count > 64) {
								if(nb==52) break;
								ItemStack itemStack = new ItemStack(Objects.requireNonNull(Material.getMaterial(item)), count);
								ItemMeta im = itemStack.getItemMeta();
								//im.setDisplayName(item);
								itemStack.setItemMeta(im);
								inv.setItem(nb, itemStack);
								count -= 64;
								nb++;
							}
							if(nb<53){
								if (count > 0) {
									ItemStack itemStack = new ItemStack(Objects.requireNonNull(Material.getMaterial(item)), count);
									ItemMeta im = itemStack.getItemMeta();
									//im.setDisplayName(item);
									itemStack.setItemMeta(im);
									inv.setItem(nb, itemStack);
									nb++;
								}
							}
							materials.put(item, 0);
							if(nb==53){
								ItemStack ims = getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGVmMzU2YWQyYWE3YjE2NzhhZWNiODgyOTBlNWZhNWEzNDI3ZTVlNDU2ZmY0MmZiNTE1NjkwYzY3NTE3YjgifX19");
								inv.setItem(nb, ims);
								break;
							}




						}
						p.openInventory(inv);
					}else{
						event.setCancelled(true);
					}
				}catch (NullPointerException e){
					event.setCancelled(true);
				}
			}else if(view.getTitle().equals("Constructor - Parameters")){
				assert clicked != null;
				try{

					if (clicked.getType() == Material.PLAYER_HEAD) {
						event.setCancelled(true);
						Objects.requireNonNull(Bukkit.getServer().getWorld(p.getWorld().getUID())).playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
						for(NPC npc : CitizensAPI.getNPCRegistry().sorted()){
							if(npc.getName().equals(Objects.requireNonNull(clicked.getItemMeta()).getDisplayName())){
								if(npc.hasTrait(ConstructorTrait.class)){
									plugin.getBuilder(npc).oncancel = null;
									plugin.getBuilder(npc).oncomplete = null;
									plugin.getBuilder(npc).onStart = null;
									plugin.getBuilder(npc).ContinueLoc = null;
									plugin.getBuilder(npc).GroupByLayer = true;
									plugin.getBuilder(npc).BuildYLayers = 1;
									plugin.getBuilder(npc).Silent = false;
									plugin.getBuilder(npc).BuildPatternXY = fr.weefle.constructor.essentials.ConstructorTrait.BuildPatternsXZ.spiral;
									if (plugin.getBuilder(npc).State == ConstructorTrait.BuilderState.building){
										plugin.getBuilder(npc).CancelBuild();
										p.sendMessage(ChatColor.RED + npc.getName() + " isn't building anymore.");
									}else{
										if(plugin.getBuilder(npc).RequireMaterials){
											plugin.getBuilder(npc).GetMatsList(plugin.getBuilder(npc).Excavate);
										}
										//Bukkit.getLogger().warning(plugin.getBuilder(npc).RequireMaterials.toString());
										if(!plugin.getBuilder(npc).TryBuild(p)) p.sendMessage(ChatColor.RED + npc.getName() + " need a structure to build first !");
									}
									p.closeInventory();
								}
							}
						}

					}else if (clicked.getType() == Material.GREEN_CONCRETE) {
						event.setCancelled(true);
						Objects.requireNonNull(Bukkit.getServer().getWorld(p.getWorld().getUID())).playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
						if(Objects.requireNonNull(clicked.getItemMeta()).getDisplayName().equals("Excavate")){
							for(ItemStack isss : inventory.getContents()){
								for(NPC npc : CitizensAPI.getNPCRegistry().sorted()) {
									if(Objects.requireNonNull(isss.getItemMeta()).getDisplayName().equals(npc.getName())){
										if(npc.hasTrait(ConstructorTrait.class)){
											plugin.getBuilder(npc).Excavate = false;
											clicked.setType(Material.RED_CONCRETE);
											p.updateInventory();
										}
									}
								}

							}

						}else if(Objects.requireNonNull(clicked.getItemMeta()).getDisplayName().equals("IgnoreAir")){
							for(ItemStack isss : inventory.getContents()){
								for(NPC npc : CitizensAPI.getNPCRegistry().sorted()) {
									if(Objects.requireNonNull(isss.getItemMeta()).getDisplayName().equals(npc.getName())){
										if(npc.hasTrait(ConstructorTrait.class)){
											plugin.getBuilder(npc).IgnoreAir = false;
											clicked.setType(Material.RED_CONCRETE);
											p.updateInventory();
										}
									}
								}

							}

						}else if(Objects.requireNonNull(clicked.getItemMeta()).getDisplayName().equals("IgnoreLiquid")){
							for(ItemStack isss : inventory.getContents()){
								for(NPC npc : CitizensAPI.getNPCRegistry().sorted()) {
									if(Objects.requireNonNull(isss.getItemMeta()).getDisplayName().equals(npc.getName())){
										if(npc.hasTrait(ConstructorTrait.class)){
											plugin.getBuilder(npc).IgnoreLiquid = false;
											clicked.setType(Material.RED_CONCRETE);
											p.updateInventory();
										}
									}
								}

							}

						}else if(Objects.requireNonNull(clicked.getItemMeta()).getDisplayName().equals("RequireMaterials")){
							for(ItemStack isss : inventory.getContents()){
								for(NPC npc : CitizensAPI.getNPCRegistry().sorted()) {
									if(Objects.requireNonNull(isss.getItemMeta()).getDisplayName().equals(npc.getName())){
										if(npc.hasTrait(ConstructorTrait.class)){
											plugin.getBuilder(npc).RequireMaterials = false;
											clicked.setType(Material.RED_CONCRETE);
											p.updateInventory();
										}
									}
								}

							}

						}



					}else if (clicked.getType() == Material.RED_CONCRETE) {
						event.setCancelled(true);
						Objects.requireNonNull(Bukkit.getServer().getWorld(p.getWorld().getUID())).playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
						if(Objects.requireNonNull(clicked.getItemMeta()).getDisplayName().equals("Excavate")){
							for(ItemStack isss : inventory.getContents()){
							for(NPC npc : CitizensAPI.getNPCRegistry().sorted()) {
									if(Objects.requireNonNull(isss.getItemMeta()).getDisplayName().equals(npc.getName())){
										if(npc.hasTrait(ConstructorTrait.class)){
											plugin.getBuilder(npc).Excavate = true;
											clicked.setType(Material.GREEN_CONCRETE);
											p.updateInventory();
										}
									}
								}

							}

						}else if(Objects.requireNonNull(clicked.getItemMeta()).getDisplayName().equals("IgnoreAir")){
							for(ItemStack isss : inventory.getContents()){
								for(NPC npc : CitizensAPI.getNPCRegistry().sorted()) {
									if(Objects.requireNonNull(isss.getItemMeta()).getDisplayName().equals(npc.getName())){
										if(npc.hasTrait(ConstructorTrait.class)){
											plugin.getBuilder(npc).IgnoreAir = true;
											clicked.setType(Material.GREEN_CONCRETE);
											p.updateInventory();
										}
									}
								}

							}

						}else if(Objects.requireNonNull(clicked.getItemMeta()).getDisplayName().equals("IgnoreLiquid")){
							for(ItemStack isss : inventory.getContents()){
								for(NPC npc : CitizensAPI.getNPCRegistry().sorted()) {
									if(Objects.requireNonNull(isss.getItemMeta()).getDisplayName().equals(npc.getName())){
										if(npc.hasTrait(ConstructorTrait.class)){
											plugin.getBuilder(npc).IgnoreLiquid = true;
											clicked.setType(Material.GREEN_CONCRETE);
											p.updateInventory();
										}
									}
								}

							}

						}else if(Objects.requireNonNull(clicked.getItemMeta()).getDisplayName().equals("RequireMaterials")){
							for(ItemStack isss : inventory.getContents()){
								for(NPC npc : CitizensAPI.getNPCRegistry().sorted()) {
									if(Objects.requireNonNull(isss.getItemMeta()).getDisplayName().equals(npc.getName())){
										if(npc.hasTrait(ConstructorTrait.class)){
											plugin.getBuilder(npc).RequireMaterials = true;
											clicked.setType(Material.GREEN_CONCRETE);
											p.updateInventory();
										}
									}
								}

							}

						}


					}else if (clicked.getType() == Material.BOOK) {
						event.setCancelled(true);
						Objects.requireNonNull(Bukkit.getServer().getWorld(p.getWorld().getUID())).playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
						p.performCommand("constructor list");
						p.sendMessage(ChatColor.GREEN + "Choose a schematic you want to load !");
					}else {
						event.setCancelled(true);
					}
				}catch (NullPointerException e){
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void clickedme2(NPCLeftClickEvent event){

		Player player = event.getClicker();
		ConstructorTrait inst = plugin.getBuilder(event.getNPC());
		if(inst!=null) {
			if (inst.State == BuilderState.collecting) {
				//list what is still needed
				player.sendMessage(plugin.format(plugin.SupplyListMessage, inst.getNPC(), inst.schematic, player, null, "0"));
				//player.sendMessage(NMS.getInstance().getUtil().printList(inst.NeededMaterials));
				for (String item : inst.NeededMaterials.keySet()) {
					materials.put(item, inst.NeededMaterials.get(item));
				}

				Inventory inv = Bukkit.createInventory(player, 54, "Constructor - Materials");
				int nb = 0;
				for (String item : materials.keySet()) {


					int count = materials.get(item);
					//Bukkit.getLogger().warning(item + " : " + count);
					while (count > 64) {
						if (nb == 52) break;
						ItemStack itemStack = new ItemStack(Objects.requireNonNull(Material.getMaterial(item)), count);
						ItemMeta im = itemStack.getItemMeta();
						//im.setDisplayName(item);
						itemStack.setItemMeta(im);
						inv.setItem(nb, itemStack);
						count -= 64;
						nb++;
					}
					if (nb < 53) {
						if (count > 0) {
							ItemStack itemStack = new ItemStack(Objects.requireNonNull(Material.getMaterial(item)), count);
							ItemMeta im = itemStack.getItemMeta();
							//im.setDisplayName(item);
							itemStack.setItemMeta(im);
							inv.setItem(nb, itemStack);
							nb++;
						}
					}
					materials.put(item, 0);
					if (nb == 53) {
						ItemStack ims = getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGVmMzU2YWQyYWE3YjE2NzhhZWNiODgyOTBlNWZhNWEzNDI3ZTVlNDU2ZmY0MmZiNTE1NjkwYzY3NTE3YjgifX19");
						inv.setItem(nb, ims);
						break;
					}

				}
				player.openInventory(inv);
			} else if(inst.State == BuilderState.building && inst.Excavate) {
				if (!inst.ExcavateMaterials.isEmpty()) {


					for (BlockData embb : inst.ExcavateMaterials) {
						//Bukkit.getLogger().warning(embb.getMaterial().name());
						int count = (int) inst.ExcavateMaterials.stream().filter(emptyBuildBlock -> emptyBuildBlock.getMaterial().equals(embb.getMaterial())).count();
						materials.put(embb.getMaterial().name(), count);
					}

					Inventory inv = Bukkit.createInventory(player, 54, "Constructor - Materials");
					int nb = 0;
					for (String item : materials.keySet()) {


						int count = materials.get(item);
						//Bukkit.getLogger().warning(item + " : " + count);
						while (count > 64) {
							if (nb == 52) break;
							ItemStack itemStack = new ItemStack(Objects.requireNonNull(Material.getMaterial(item)), count);
							ItemMeta im = itemStack.getItemMeta();
							//im.setDisplayName(item);
							itemStack.setItemMeta(im);
							inv.setItem(nb, itemStack);
							count -= 64;
							nb++;
						}
						if (nb < 53) {
							if (count > 0) {
								ItemStack itemStack = new ItemStack(Objects.requireNonNull(Material.getMaterial(item)), count);
								ItemMeta im = itemStack.getItemMeta();
								//im.setDisplayName(item);
								itemStack.setItemMeta(im);
								inv.setItem(nb, itemStack);
								nb++;
							}
						}
						materials.put(item, 0);
						if (nb == 53) {
							ItemStack ims = getItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGVmMzU2YWQyYWE3YjE2NzhhZWNiODgyOTBlNWZhNWEzNDI3ZTVlNDU2ZmY0MmZiNTE1NjkwYzY3NTE3YjgifX19");
							inv.setItem(nb, ims);
							break;
						}

					}
					player.openInventory(inv);
				}
			}
		}
			/*TextComponent message = new TextComponent("Winner (Click to view inventory)");
			message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/winnerinv"));
			player.spigot().sendMessage(message);*/
	}

	@EventHandler
	public void clickedme(net.citizensnpcs.api.event.NPCRightClickEvent event){
		ConstructorTrait inst = plugin.getBuilder(event.getNPC());
		Player player = event.getClicker();
		if(inst==null || inst.State == BuilderState.idle) {

			NPC npc = event.getNPC();

					if ((npc.hasTrait(ConstructorTrait.class)) && (player.hasPermission("constructor.npc.click"))) {
						player.performCommand("npc select " + npc.getId());
						Inventory inv = Bukkit.createInventory(player, 54, "Constructor - Parameters");
						if(plugin.getBuilder(npc) != null && (plugin.getBuilder(npc).Excavate == null ||plugin.getBuilder(npc).RequireMaterials == null ||plugin.getBuilder(npc).IgnoreLiquid == null || plugin.getBuilder(npc).IgnoreAir == null)) {
							plugin.getBuilder(npc).IgnoreLiquid = false;
							plugin.getBuilder(npc).Excavate = false;
							plugin.getBuilder(npc).RequireMaterials = false;
							plugin.getBuilder(npc).IgnoreAir = false;
						}
						ItemStack itemStack1, itemStack2, itemStack3, itemStack4, itemStack5, itemStack6;
						ItemMeta im1, im2, im3, im4, im5, im6;
						if(plugin.getBuilder(npc).State == BuilderState.building){
							itemStack1 = getHead(npc.getName());
							im1 = itemStack1.getItemMeta();
							assert im1 != null;
							im1.setDisplayName(npc.getName());
							ArrayList<String> Lore = new ArrayList<>();
							Lore.add(npc.getName() + " is building...");
							Lore.add("Click to cancel building");
							im1.setLore(Lore);
						}else{
							itemStack1 = getHead(npc.getName());
							im1 = itemStack1.getItemMeta();
							assert im1 != null;
							im1.setDisplayName(npc.getName());
							ArrayList<String> Lore = new ArrayList<>();
							Lore.add(npc.getName() + " isn't building.");
							Lore.add("Click to start building !");
							im1.setLore(Lore);
						}
						itemStack1.setItemMeta(im1);
						inv.setItem(0, itemStack1);
						if(plugin.getBuilder(npc).Excavate){
							itemStack2 = new ItemStack(Material.GREEN_CONCRETE);
							im2 = itemStack2.getItemMeta();
							assert im2 != null;
							im2.setDisplayName("Excavate");
						}else{
							itemStack2 = new ItemStack(Material.RED_CONCRETE);
							im2 = itemStack2.getItemMeta();
							assert im2 != null;
							im2.setDisplayName("Excavate");
						}
						itemStack2.setItemMeta(im2);
						inv.setItem(1, itemStack2);

						if(plugin.getBuilder(npc).IgnoreAir){
							itemStack3 = new ItemStack(Material.GREEN_CONCRETE);
							im3 = itemStack3.getItemMeta();
							assert im3 != null;
							im3.setDisplayName("IgnoreAir");
						}else{
							itemStack3 = new ItemStack(Material.RED_CONCRETE);
							im3 = itemStack3.getItemMeta();
							assert im3 != null;
							im3.setDisplayName("IgnoreAir");
						}

						itemStack3.setItemMeta(im3);
						inv.setItem(2, itemStack3);

						if(plugin.getBuilder(npc).IgnoreLiquid){
							itemStack4 = new ItemStack(Material.GREEN_CONCRETE);
							im4 = itemStack4.getItemMeta();
							assert im4 != null;
							im4.setDisplayName("IgnoreLiquid");
						}else{
							itemStack4 = new ItemStack(Material.RED_CONCRETE);
							im4 = itemStack4.getItemMeta();
							assert im4 != null;
							im4.setDisplayName("IgnoreLiquid");
						}
						itemStack4.setItemMeta(im4);
						inv.setItem(3, itemStack4);

						if(plugin.getBuilder(npc).RequireMaterials){
							itemStack5 = new ItemStack(Material.GREEN_CONCRETE);
							im5 = itemStack4.getItemMeta();
							assert im5 != null;
							im5.setDisplayName("RequireMaterials");
						}else{
							itemStack5 = new ItemStack(Material.RED_CONCRETE);
							im5 = itemStack5.getItemMeta();
							assert im5 != null;
							im5.setDisplayName("RequireMaterials");
						}
						itemStack5.setItemMeta(im5);
						inv.setItem(4, itemStack5);


							itemStack6 = new ItemStack(Material.BOOK);
							im6 = itemStack6.getItemMeta();
							assert im6 != null;
							im6.setDisplayName("Choose your schematic !");


						itemStack6.setItemMeta(im6);
						inv.setItem(5, itemStack6);


						player.openInventory(inv);

					}


		}else if(inst.State == BuilderState.building) {
			//Bukkit.getLogger().warning("yo excavate man !");

	NPC npc = event.getNPC();

	if ((npc.hasTrait(ConstructorTrait.class)) && (player.hasPermission("constructor.npc.click"))) {
		player.performCommand("npc select " + npc.getId());
		Inventory inv = Bukkit.createInventory(player, 54, "Constructor - Parameters");
		if(plugin.getBuilder(npc) != null && (plugin.getBuilder(npc).Excavate == null ||plugin.getBuilder(npc).RequireMaterials == null ||plugin.getBuilder(npc).IgnoreLiquid == null || plugin.getBuilder(npc).IgnoreAir == null)) {
			plugin.getBuilder(npc).IgnoreLiquid = false;
			plugin.getBuilder(npc).Excavate = false;
			plugin.getBuilder(npc).RequireMaterials = false;
			plugin.getBuilder(npc).IgnoreAir = false;
		}
		ItemStack itemStack1, itemStack2, itemStack3, itemStack4, itemStack5, itemStack6;
		ItemMeta im1, im2, im3, im4, im5, im6;
		if(plugin.getBuilder(npc).State == BuilderState.building){
			itemStack1 = getHead(npc.getName());
			im1 = itemStack1.getItemMeta();
			assert im1 != null;
			im1.setDisplayName(npc.getName());
			ArrayList<String> Lore = new ArrayList<>();
			Lore.add(npc.getName() + " is building...");
			Lore.add("Click to cancel building");
			im1.setLore(Lore);
		}else{
			itemStack1 = getHead(npc.getName());
			im1 = itemStack1.getItemMeta();
			assert im1 != null;
			im1.setDisplayName(npc.getName());
			ArrayList<String> Lore = new ArrayList<>();
			Lore.add(npc.getName() + " isn't building.");
			Lore.add("Click to start building !");
			im1.setLore(Lore);
		}
		itemStack1.setItemMeta(im1);
		inv.setItem(0, itemStack1);
		if(plugin.getBuilder(npc).Excavate){
			itemStack2 = new ItemStack(Material.GREEN_CONCRETE);
			im2 = itemStack2.getItemMeta();
			assert im2 != null;
			im2.setDisplayName("Excavate");
		}else{
			itemStack2 = new ItemStack(Material.RED_CONCRETE);
			im2 = itemStack2.getItemMeta();
			assert im2 != null;
			im2.setDisplayName("Excavate");
		}
		itemStack2.setItemMeta(im2);
		inv.setItem(1, itemStack2);

		if(plugin.getBuilder(npc).IgnoreAir){
			itemStack3 = new ItemStack(Material.GREEN_CONCRETE);
			im3 = itemStack3.getItemMeta();
			assert im3 != null;
			im3.setDisplayName("IgnoreAir");
		}else{
			itemStack3 = new ItemStack(Material.RED_CONCRETE);
			im3 = itemStack3.getItemMeta();
			assert im3 != null;
			im3.setDisplayName("IgnoreAir");
		}

		itemStack3.setItemMeta(im3);
		inv.setItem(2, itemStack3);

		if(plugin.getBuilder(npc).IgnoreLiquid){
			itemStack4 = new ItemStack(Material.GREEN_CONCRETE);
			im4 = itemStack4.getItemMeta();
			assert im4 != null;
			im4.setDisplayName("IgnoreLiquid");
		}else{
			itemStack4 = new ItemStack(Material.RED_CONCRETE);
			im4 = itemStack4.getItemMeta();
			assert im4 != null;
			im4.setDisplayName("IgnoreLiquid");
		}
		itemStack4.setItemMeta(im4);
		inv.setItem(3, itemStack4);

		if(plugin.getBuilder(npc).RequireMaterials){
			itemStack5 = new ItemStack(Material.GREEN_CONCRETE);
			im5 = itemStack4.getItemMeta();
			assert im5 != null;
			im5.setDisplayName("RequireMaterials");
		}else{
			itemStack5 = new ItemStack(Material.RED_CONCRETE);
			im5 = itemStack5.getItemMeta();
			assert im5 != null;
			im5.setDisplayName("RequireMaterials");
		}
		itemStack5.setItemMeta(im5);
		inv.setItem(4, itemStack5);


		itemStack6 = new ItemStack(Material.BOOK);
		im6 = itemStack6.getItemMeta();
		assert im6 != null;
		im6.setDisplayName("Choose your schematic !");


		itemStack6.setItemMeta(im6);
		inv.setItem(5, itemStack6);


		player.openInventory(inv);

	}


		}else if(inst.State==BuilderState.collecting){
			ItemStack is = player.getItemInHand();

			if (is.getType().isBlock() && !(is.getType() == Material.AIR)) {


				String itemname = is.getType().name();


				if (!player.hasPermission("constructor.donate")) {
					player.sendMessage(ChatColor.RED + "You do not have permission to donate");
					return;
				}

				int needed = inst.NeededMaterials.getOrDefault(itemname, 0);
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

						inst.NeededMaterials.put(itemname, (needed - taking));
						player.sendMessage(plugin.format(plugin.SupplyTakenMessage, inst.getNPC(), inst.schematic, player, itemname, taking + ""));

						//check if can start
						inst.TryBuild(null);

					} else {
						player.sendMessage(plugin.format(plugin.SupplyNeedMessage, inst.getNPC(), inst.schematic, player, itemname, needed + ""));
						inst.Sessions.put(player, System.currentTimeMillis());
					}

				} else {
					player.sendMessage(plugin.format(plugin.SupplyDontNeedMessage, inst.getNPC(), inst.schematic, player, itemname, "0"));
					//don't need it or already have it.
				}
			}
		}



	}





	@EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
	public void NavCom(net.citizensnpcs.api.ai.event.NavigationCompleteEvent event) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalArgumentException, InvocationTargetException, IllegalAccessException {
		NPC npc =null;
		for(NPC n : CitizensAPI.getNPCRegistry()){
			if (n.getNavigator() == event.getNavigator()){
				npc = n;
				break;
			}

		}

		//	plugin.getLogger().info("nav complete " + npc);

		ConstructorTrait inst = plugin.getBuilder(npc);

		if(inst==null) return;
		if(inst.State!=BuilderState.idle){
			inst.PlaceNextBlock();
		}

	}


	@EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
	public void NavCan(net.citizensnpcs.api.ai.event.NavigationCancelEvent event) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalArgumentException, InvocationTargetException, IllegalAccessException {
		NPC npc =null;
		for(NPC n : CitizensAPI.getNPCRegistry()){
			if (n.getNavigator() == event.getNavigator()){
				npc = n;
				break;
			}
		}
		ConstructorTrait inst = plugin.getBuilder(npc);

		//	plugin.getLogger().info("nav cancel " + npc);

		if(inst==null) return;

		if(inst.State!=BuilderState.idle){
			inst.PlaceNextBlock();
		}

	}


	private static ItemStack getItem(String b64stringtexture) {
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		PropertyMap propertyMap = profile.getProperties();
		if (propertyMap == null) {
			throw new IllegalStateException("Profile doesn't contain a property map");
		}
		propertyMap.put("textures", new Property("textures", b64stringtexture));
		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		ItemMeta headMeta = head.getItemMeta();
		assert headMeta != null;
		Class<?> headMetaClass = headMeta.getClass();
		try {
			getField(headMetaClass, "profile", GameProfile.class, 0).set(headMeta, profile);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		headMeta.setDisplayName("Materials");
		ArrayList<String> lore = new ArrayList<>();
		lore.add("Next Page");
		headMeta.setLore(lore);
		head.setItemMeta(headMeta);
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

	public ItemStack getHead(String p){
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
		ItemMeta m = skull.getItemMeta();
		SkullMeta s = (SkullMeta) m;
		assert s != null;
		s.setOwner(p);
		skull.setItemMeta(s);
		return skull;
	}

}
