package fr.weefle.constructor.menus;

import com.google.common.base.Preconditions;
import fr.weefle.constructor.SchematicBuilder;
import fr.weefle.constructor.hooks.citizens.BuilderTrait;
import mc.promcteam.engine.api.menu.Menu;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;

import java.util.Objects;

public class SettingsMenu extends Menu {
    public static final BuilderMenu.YAMLBuilderMenu PLAYER = new BuilderMenu.YAMLBuilderMenu(SchematicBuilder.getInstance(), "menus/builder-settings.yml");
    public static final BuilderMenu.YAMLBuilderMenu ADMIN = new BuilderMenu.YAMLBuilderMenu(SchematicBuilder.getInstance(), "menus/builder-settings-admin.yml");

    protected final NPC npc;
    private final BuilderMenu.YAMLBuilderMenu yamlMenu;

    public SettingsMenu(Player player, NPC npc, BuilderMenu.YAMLBuilderMenu yamlMenu) {
        super(player, yamlMenu.getRows(), yamlMenu.getTitle(Objects.requireNonNull(SchematicBuilder.getBuilder(npc), npc.getName()+" is not a builder")));
        this.npc = npc;
        this.yamlMenu = yamlMenu;
    }

    @Override
    public void setContents() {
        BuilderTrait builder = SchematicBuilder.getBuilder(npc);
        Preconditions.checkArgument(builder != null, npc.getName()+" is not a builder");
        this.yamlMenu.setSlots(this, builder);
    }
}
