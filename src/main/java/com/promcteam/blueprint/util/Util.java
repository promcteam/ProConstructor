package com.promcteam.blueprint.util;

import com.promcteam.blueprint.schematic.blocks.EmptyBuildBlock;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.Map.Entry;


public class Util {

    public static String printMaterials(Map<Material, Integer> map) {
        StringBuilder sb = new StringBuilder();

        Iterator<Entry<Material, Integer>> it = map.entrySet().iterator();

        while (it.hasNext()) {
            Entry<Material, Integer> i = it.next();
            if (i.getValue() > 0) {
                sb.append(ChatColor.GREEN).append(i.getKey()).append(":").append(ChatColor.WHITE).append(i.getValue());
                if (it.hasNext()) sb.append(", ");
            }
        }
        return sb.toString();
    }

    public static List<EmptyBuildBlock> spiralPrintLayer(int starty,
                                                         int ylayers,
                                                         EmptyBuildBlock[][][] a,
                                                         boolean reverse) {
        int i, k = 0, l = 0;

        int m = a.length;
        int n = a[0].length;
        int o = a[0][0].length;

        List<EmptyBuildBlock> out = new ArrayList<EmptyBuildBlock>();

		/*  k - starting row index
	        m - ending row index
	        l - starting column index
	        n - ending column index
	        i - iterator
		 */

        while (k < m && l < o) {
            /* Print the first row from the remaining rows */
            for (i = l; i < o; ++i) {


                if (reverse) {
                    for (int y = starty; y < starty + ylayers; y++) {
                        if (y < n) out.add(a[k][y][i]);
                    }
                } else {
                    for (int y = starty + ylayers - 1; y >= starty; y--) {
                        if (y < n) out.add(a[k][y][i]);
                    }
                }

            }
            k++;

            /* Print the last column from the remaining columns */
            for (i = k; i < m; ++i) {

                if (reverse) {
                    for (int y = starty; y < starty + ylayers; y++) {
                        if (y < n) out.add(a[i][y][o - 1]);
                    }
                } else {
                    for (int y = starty + ylayers - 1; y >= starty; y--) {
                        if (y < n) out.add(a[i][y][o - 1]);
                    }
                }

            }
            o--;

            /* Print the last row from the remaining rows */
            if (k < m) {
                for (i = o - 1; i >= l; --i) {

                    if (reverse) {
                        for (int y = starty; y < starty + ylayers; y++) {
                            if (y < n) out.add(a[m - 1][y][i]);
                        }
                    } else {
                        for (int y = starty + ylayers - 1; y >= starty; y--) {
                            if (y < n) out.add(a[m - 1][y][i]);
                        }
                    }


                }
                m--;
            }

            /* Print the first column from the remaining columns */
            if (l < o) {
                for (i = m - 1; i >= k; --i) {

                    if (reverse) {
                        for (int y = starty; y < starty + ylayers; y++) {
                            if (y < n) out.add(a[i][y][l]);
                        }
                    } else {
                        for (int y = starty + ylayers - 1; y >= starty; y--) {
                            if (y < n) out.add(a[i][y][l]);
                        }
                    }


                }
                l++;
            }
        }


        if (!reverse) Collections.reverse(out);
        return out;
    }


    public static List<EmptyBuildBlock> LinearPrintLayer(int starty,
                                                         int ylayers,
                                                         EmptyBuildBlock[][][] a,
                                                         boolean reverse) {
        int i  = 0, k = 0;
        int di = 1;
        int dk = 1;

        int m = a.length;
        int n = a[0].length;
        int o = a[0][0].length;

        List<EmptyBuildBlock> out = new ArrayList<EmptyBuildBlock>();

		/*  k - starting row index
	        m - ending row index
	        l - starting column index
	        n - ending column index
	        i - iterator
		 */

        do {

            if (!reverse) {
                for (int y = starty; y < starty + ylayers; y++) {
                    if (y < n) out.add(a[i][y][k]);
                }
            } else {
                for (int y = starty + ylayers - 1; y >= starty; y--) {
                    if (y < n) out.add(a[i][y][k]);
                }
            }

            i += di;
            if (i >= m || i < 0) {
                di *= -1;
                i += di;
                k += dk;
                if (k >= o || k < 0) {
                    k += 1;
                    if (k >= o) break;
                }
            }


        } while (true);

        if (reverse) Collections.reverse(out);
        return out;
    }

    public static boolean canStand(org.bukkit.block.Block base) {
        org.bukkit.block.Block below = base.getRelative(0, -1, 0);
        if (!below.isEmpty() && below.getBlockData().getMaterial().isSolid()) {
            return base.isEmpty() || !base.getBlockData().getMaterial().isSolid();
        }
        return false;
    }

    public static int normalizeRotations(int rotation) {return (rotation % 4 + 4) % 4;}

    public static Vector rotateVector(Vector vector, int rotation) {
        rotation = normalizeRotations(rotation);
        if (rotation == 0) {
            return vector.clone();
        } else if (rotation == 1) {
            return new Vector(-vector.getZ(), vector.getY(), vector.getX());
        } else if (rotation == 2) {
            return new Vector(-vector.getX(), vector.getY(), -vector.getZ());
        } else {
            return new Vector(vector.getZ(), vector.getY(), -vector.getX());
        }
    }
}
