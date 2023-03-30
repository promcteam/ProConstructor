package fr.weefle.constructor.API;

import fr.weefle.constructor.block.EmptyBuildBlock;
import org.bukkit.block.Block;

import java.util.List;
import java.util.Map;
import java.util.Queue;

public interface Util {

    String printList(Map<String, Integer> neededMaterials);

    List<EmptyBuildBlock> LinearPrintLayer(int y, int ylayers, EmptyBuildBlock[][][] blocks, boolean b);

    List<EmptyBuildBlock> spiralPrintLayer(int y, int ylayers, EmptyBuildBlock[][][] blocks, boolean b);

    Map<String, Integer> MaterialsList(Queue<EmptyBuildBlock> buildQueue);

    boolean canStand(Block relative);

}