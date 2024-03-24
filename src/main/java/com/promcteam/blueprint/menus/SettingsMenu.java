package com.promcteam.blueprint.menus;

import com.google.common.base.Preconditions;
import com.promcteam.blueprint.Blueprint;
import com.promcteam.blueprint.hooks.citizens.BuilderTrait;
import com.promcteam.codex.manager.api.menu.Menu;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;

import java.util.Objects;

public class SettingsMenu extends Menu {
    public static final BuilderMenu.YAMLBuilderMenu PLAYER =
            new BuilderMenu.YAMLBuilderMenu(Blueprint.getInstance(), "menus/builder-settings.yml");
    public static final BuilderMenu.YAMLBuilderMenu ADMIN  =
            new BuilderMenu.YAMLBuilderMenu(Blueprint.getInstance(), "menus/builder-settings-admin.yml");

    protected final NPC                         npc;
    private final   BuilderMenu.YAMLBuilderMenu yamlMenu;

    public SettingsMenu(Player player, NPC npc, BuilderMenu.YAMLBuilderMenu yamlMenu) {
        super(player,
                yamlMenu.getRows(),
                yamlMenu.getTitle(Objects.requireNonNull(Blueprint.getBuilder(npc),
                        npc.getName() + " is not a builder")));
        this.npc = npc;
        this.yamlMenu = yamlMenu;
    }

    @Override
    public void setContents() {
        BuilderTrait builder = Blueprint.getBuilder(npc);
        Preconditions.checkArgument(builder != null, npc.getName() + " is not a builder");
        this.yamlMenu.setSlots(this, builder);
    }
}
