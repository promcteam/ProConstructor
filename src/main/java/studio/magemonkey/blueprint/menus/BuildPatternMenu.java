package studio.magemonkey.blueprint.menus;

import com.google.common.base.Preconditions;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import studio.magemonkey.blueprint.Blueprint;
import studio.magemonkey.blueprint.hooks.citizens.BuilderTrait;
import studio.magemonkey.codex.manager.api.menu.Menu;
import studio.magemonkey.codex.manager.api.menu.Slot;
import studio.magemonkey.codex.manager.api.menu.YAMLMenu;
import studio.magemonkey.codex.util.ItemUT;

import java.util.Objects;

public class BuildPatternMenu extends Menu {
    public static final YAMLMenu<BuilderTrait> CONFIG =
            new YAMLMenu<BuilderTrait>(Blueprint.getInstance(), "menus/build-pattern.yml") {
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
                    } catch (IllegalArgumentException ignored) {
                    }
                    return null;
                }
            };

    protected final NPC npc;

    public BuildPatternMenu(Player player, NPC npc) {
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
