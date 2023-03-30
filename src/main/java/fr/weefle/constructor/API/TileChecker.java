package fr.weefle.constructor.API;

import org.bukkit.block.Block;

import fr.weefle.constructor.block.EmptyBuildBlock;

import java.lang.reflect.InvocationTargetException;

public interface TileChecker {

	void check(EmptyBuildBlock next, Block pending) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;
	
	

}
