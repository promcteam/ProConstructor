package fr.weefle.constructor.menu.menus;

import com.google.common.base.Preconditions;
import fr.weefle.constructor.SchematicBuilder;
import fr.weefle.constructor.hooks.citizens.BuilderTrait;
import fr.weefle.constructor.menu.Menu;
import fr.weefle.constructor.menu.Slot;
import fr.weefle.constructor.menu.YAMLMenu;
import mc.promcteam.engine.utils.ItemUT;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class BuildPatternMenu extends Menu {
    public static final YAMLMenu<BuilderTrait> CONFIG = new YAMLMenu<>("build-pattern") {
        @Override
        protected String getTitle(String yamlTitle, BuilderTrait parameter) {
            return yamlTitle.replace("%npc%", parameter.getNPC().getName());
        }

        @Override
        @Nullable
        public Slot getSlot(String function, BuilderTrait builder, Player player) {
            ItemStack itemStack = this.getItem(function);
            ItemUT.replaceLore(itemStack, "%current%", builder.getBuildPatternXZ().name()
                    .toLowerCase()
                    .replace('_', ' '));
            try {
                BuilderTrait.BuildPatternXZ patternXZ = BuilderTrait.BuildPatternXZ.valueOf(function
                        .toUpperCase()
                        .replace('-', '_'));
                return new Slot(itemStack) {
                    @Override
                    public void onLeftClick() {
                        builder.setBuildPatternXZ(patternXZ);
                        player.closeInventory();
                    }
                };
            } catch (IllegalArgumentException ignored) {}
            return null;
        }
    };

    protected final NPC npc;

    public BuildPatternMenu(Player player, NPC npc) {
        super(player, CONFIG.getRows(), CONFIG.getTitle(Objects.requireNonNull(SchematicBuilder.getBuilder(npc), npc.getName()+" is not a builder")));
        this.npc = npc;
    }

    @Override
    public void setContents() {
        BuilderTrait builder = SchematicBuilder.getBuilder(npc);
        Preconditions.checkArgument(builder != null, npc.getName()+" is not a builder");
        CONFIG.setSlots(this, builder);
    }
}
