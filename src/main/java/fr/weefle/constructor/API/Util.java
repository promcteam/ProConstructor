package fr.weefle.constructor.api;

import fr.weefle.constructor.schematic.blocks.EmptyBuildBlock;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;
import java.util.Map;
import java.util.Queue;

public interface Util {

    String printList(Map<Material, Integer> neededMaterials);

    List<EmptyBuildBlock> LinearPrintLayer(int y, int ylayers, EmptyBuildBlock[][][] blocks, boolean b);

    List<EmptyBuildBlock> spiralPrintLayer(int y, int ylayers, EmptyBuildBlock[][][] blocks, boolean b);

    Map<Material, Integer> MaterialsList(Queue<EmptyBuildBlock> buildQueue);

    boolean canStand(Block relative);

}