package com.promcteam.blueprint.menus;

import com.google.common.base.Preconditions;
import com.promcteam.blueprint.Blueprint;
import com.promcteam.blueprint.hooks.citizens.BuilderTrait;
import com.promcteam.codex.manager.api.menu.Menu;
import com.promcteam.codex.manager.api.menu.Slot;
import com.promcteam.codex.manager.api.menu.YAMLMenu;
import com.promcteam.codex.util.ItemUT;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class CitizensActionsMenu extends Menu {
    public static final YAMLMenu<BuilderTrait> CONFIG =
            new YAMLMenu<BuilderTrait>(Blueprint.getInstance(), "menus/citizens-actions.yml") {
                @Override
                protected String getTitle(String yamlTitle, BuilderTrait parameter) {
                    return yamlTitle.replace("%npc%", parameter.getNPC().getName());
                }

                @Override
                @Nullable
                public Slot getSlot(String function, BuilderTrait builder, Player player) {
                    switch (function) {
                        case "on-start": {
                            ItemStack itemStack = this.getItem(function);
                            String    current   = builder.getOnStart() == null ? "null" : builder.getOnStart();
                            ItemUT.replaceLore(itemStack, "%current%", current);
                            return new Slot(itemStack) {
                                @Override
                                public void onLeftClick() {
                                    Menu menu = this.menu;
                                    BaseComponent component =
                                            new TextComponent("▸ Enter the desired action on start, or ");
                                    BaseComponent component1 = new TextComponent(
                                            ChatColor.GOLD.toString() + ChatColor.UNDERLINE + "cancel");
                                    component1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                                            "cancel"));
                                    component.addExtra(component1);
                                    component.addExtra(new TextComponent(" to go back. "));
                                    component1 = new TextComponent(
                                            ChatColor.GOLD.toString() + ChatColor.UNDERLINE + "Current value");
                                    component1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                                            current));
                                    component.addExtra(component1);
                                    player.spigot().sendMessage(component);

                                    this.menu.fakeClose();
                                    menu.registerListener(new Listener() {
                                        @EventHandler
                                        public void onChat(AsyncPlayerChatEvent event) {
                                            if (!event.getPlayer().equals(player)) {
                                                return;
                                            }
                                            HandlerList.unregisterAll(this);
                                            event.setCancelled(true);
                                            String message = event.getMessage().strip();
                                            if (!message.equalsIgnoreCase("cancel")) {
                                                if (message.equalsIgnoreCase("null")) {
                                                    builder.setOnStart(null);
                                                } else {
                                                    builder.setOnStart(message);
                                                }
                                            }
                                            menu.openSync();
                                        }
                                    });
                                }

                                @Override
                                public void onDrop() {
                                    builder.setOnStart(null);
                                    this.menu.open();
                                }
                            };
                        }
                        case "on-complete": {
                            ItemStack itemStack = this.getItem(function);
                            String    current   = builder.getOnComplete() == null ? "null" : builder.getOnComplete();
                            ItemUT.replaceLore(itemStack, "%current%", current);
                            return new Slot(itemStack) {
                                @Override
                                public void onLeftClick() {
                                    Menu menu = this.menu;
                                    BaseComponent component =
                                            new TextComponent("▸ Enter the desired action on complete, or ");
                                    BaseComponent component1 = new TextComponent(
                                            ChatColor.GOLD.toString() + ChatColor.UNDERLINE + "cancel");
                                    component1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                                            "cancel"));
                                    component.addExtra(component1);
                                    component.addExtra(new TextComponent(" to go back. "));
                                    component1 = new TextComponent(
                                            ChatColor.GOLD.toString() + ChatColor.UNDERLINE + "Current value");
                                    component1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                                            current));
                                    component.addExtra(component1);
                                    player.spigot().sendMessage(component);

                                    this.menu.fakeClose();
                                    menu.registerListener(new Listener() {
                                        @EventHandler
                                        public void onChat(AsyncPlayerChatEvent event) {
                                            if (!event.getPlayer().equals(player)) {
                                                return;
                                            }
                                            HandlerList.unregisterAll(this);
                                            event.setCancelled(true);
                                            String message = event.getMessage().strip();
                                            if (!message.equalsIgnoreCase("cancel")) {
                                                if (message.equalsIgnoreCase("null")) {
                                                    builder.setOnComplete(null);
                                                } else {
                                                    builder.setOnComplete(message);
                                                }
                                            }
                                            menu.openSync();
                                        }
                                    });
                                }

                                @Override
                                public void onDrop() {
                                    builder.setOnComplete(null);
                                    this.menu.open();
                                }
                            };
                        }
                        case "on-cancel": {
                            ItemStack itemStack = this.getItem(function);
                            String    current   = builder.getOnCancel() == null ? "null" : builder.getOnCancel();
                            ItemUT.replaceLore(itemStack, "%current%", current);
                            return new Slot(itemStack) {
                                @Override
                                public void onLeftClick() {
                                    Menu menu = this.menu;
                                    BaseComponent component =
                                            new TextComponent("▸ Enter the desired action on cancel, or ");
                                    BaseComponent component1 = new TextComponent(
                                            ChatColor.GOLD.toString() + ChatColor.UNDERLINE + "cancel");
                                    component1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                                            "cancel"));
                                    component.addExtra(component1);
                                    component.addExtra(new TextComponent(" to go back. "));
                                    component1 = new TextComponent(
                                            ChatColor.GOLD.toString() + ChatColor.UNDERLINE + "Current value");
                                    component1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                                            current));
                                    component.addExtra(component1);
                                    player.spigot().sendMessage(component);

                                    this.menu.fakeClose();
                                    menu.registerListener(new Listener() {
                                        @EventHandler
                                        public void onChat(AsyncPlayerChatEvent event) {
                                            if (!event.getPlayer().equals(player)) {
                                                return;
                                            }
                                            HandlerList.unregisterAll(this);
                                            event.setCancelled(true);
                                            String message = event.getMessage().strip();
                                            if (!message.equalsIgnoreCase("cancel")) {
                                                if (message.equalsIgnoreCase("null")) {
                                                    builder.setOnCancel(null);
                                                } else {
                                                    builder.setOnCancel(message);
                                                }
                                            }
                                            menu.openSync();
                                        }
                                    });
                                }

                                @Override
                                public void onDrop() {
                                    builder.setOnCancel(null);
                                    this.menu.open();
                                }
                            };
                        }
                    }
                    return null;
                }
            };

    protected final NPC npc;

    public CitizensActionsMenu(Player player, NPC npc) {
        super(player,
                CONFIG.getRows(),
                CONFIG.getTitle(Objects.requireNonNull(Blueprint.getBuilder(npc),
                        npc.getName() + " is not a builder")));
        this.npc = npc;
    }

    @Override
    public void setContents() {
        BuilderTrait builder = Blueprint.getBuilder(npc);
        Preconditions.checkArgument(builder != null, npc.getName() + " is not a builder");
        CONFIG.setSlots(this, builder);
    }
}
