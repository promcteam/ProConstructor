package com.promcteam.blueprint.API;

import com.promcteam.blueprint.schematic.blocks.EmptyBuildBlock;
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
