package studio.magemonkey.blueprint.API;

import studio.magemonkey.blueprint.schematic.blocks.EmptyBuildBlock;
import org.bukkit.block.Block;

import java.lang.reflect.InvocationTargetException;

public interface TileChecker {

    void check(EmptyBuildBlock next, Block pending) throws
            NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException,
            ClassNotFoundException,
            InstantiationException;


}
