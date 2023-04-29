package fr.weefle.constructor.NMS;

import fr.weefle.constructor.block.EmptyBuildBlock;
import fr.weefle.constructor.nbt.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.*;
import java.util.Map.Entry;


public class NMSUtil implements fr.weefle.constructor.api.Util {

    public String printList(Map<Material, Integer> map) {
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

    public List<EmptyBuildBlock> spiralPrintLayer(int starty, int ylayers, EmptyBuildBlock[][][] a, boolean reverse) {
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


    public List<EmptyBuildBlock> LinearPrintLayer(int starty, int ylayers, EmptyBuildBlock[][][] a, boolean reverse) {
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

    public Map<Material, Integer> MaterialsList(Queue<EmptyBuildBlock> Q) {

        Map<Material, Integer> out = new HashMap<Material, Integer>();

        do {

            EmptyBuildBlock b = Q.poll();

            if (b == null) break;
            Material material = b.getMat().getMaterial();

            if (material == org.bukkit.Material.AIR || !material.isItem())
                continue;

            if (out.containsKey(material)) {
                int amt = out.get(material);
                out.put(material, amt + 1);
            } else {
                out.put(material, 1);
            }

        } while (true);

        return out;
    }


    public boolean canStand(org.bukkit.block.Block base) {
        org.bukkit.block.Block below = base.getRelative(0, -1, 0);
        if (!below.isEmpty() && below.getBlockData().getMaterial().isSolid()) {
            return base.isEmpty() || !base.getBlockData().getMaterial().isSolid();
        }
        return false;
    }

    public static Object fromNative(Tag foreign) {
        if (foreign == null) {
            return null;
        }
        if (foreign instanceof CompoundTag) {
            Object tag = NMS.getInstance().getNMSProvider().newNBTTagCompound();
            for (Entry<String, Tag> entry : ((CompoundTag) foreign).getValue().entrySet()) {
                NMS.getInstance().getNMSProvider().nbtTagCompound_put(tag, entry.getKey(), fromNative(entry.getValue()));
            }
            return tag;
        } else if (foreign instanceof ByteTag) {
            return NMS.getInstance().getNMSProvider().nbtTagByte_valueOf(((ByteTag) foreign).getValue());
        } else if (foreign instanceof ByteArrayTag) {
            return NMS.getInstance().getNMSProvider().newNBTTagByteArray(((ByteArrayTag) foreign).getValue());
        } else if (foreign instanceof DoubleTag) {
            return NMS.getInstance().getNMSProvider().nbtTagDouble_valueOf(((DoubleTag) foreign).getValue());
        } else if (foreign instanceof FloatTag) {
            return NMS.getInstance().getNMSProvider().nbtTagFloat_valueOf(((FloatTag) foreign).getValue());
        } else if (foreign instanceof IntTag) {
            return NMS.getInstance().getNMSProvider().nbtTagInt_valueOf(((IntTag) foreign).getValue());
        } else if (foreign instanceof IntArrayTag) {
            return NMS.getInstance().getNMSProvider().newNBTTagIntArray(((IntArrayTag) foreign).getValue());
        } else if (foreign instanceof ListTag) {
            AbstractList<Object> tag         = NMS.getInstance().getNMSProvider().newNBTTagList();
            ListTag              foreignList = (ListTag) foreign;
            for (Tag t : foreignList.getValue()) {
                tag.add(fromNative(t));
            }
            return tag;
        } else if (foreign instanceof LongTag) {
            return NMS.getInstance().getNMSProvider().nbtTagLong_valueOf(((LongTag) foreign).getValue());
        } else if (foreign instanceof ShortTag) {
            return NMS.getInstance().getNMSProvider().nbtTagShort_valueOf(((ShortTag) foreign).getValue());
        } else if (foreign instanceof StringTag) {
            return NMS.getInstance().getNMSProvider().nbtTagString_valueOf(((StringTag) foreign).getValue());
        } else if (foreign instanceof EndTag) {
            throw new IllegalArgumentException("Cant make EndTag: "
                    + foreign.getValue().toString());
        } else {
            throw new IllegalArgumentException("Don't know how to make NMS "
                    + foreign.getClass().getCanonicalName());
        }
    }
}
