package io.erosemberg.pkgo.util;

import POGOProtos.Inventory.Item.ItemAwardOuterClass;

import java.util.List;

/**
 * @author Erik Rosemberg
 * @since 8/10/16
 */
public final class ArrayUtil {

    public static boolean contains(String[] args, String argument) {
        for (String a : args) {
            if (a.equals(argument)) {
                return true;
            }
        }

        return false;
    }

    public static String prettyPrint(List<ItemAwardOuterClass.ItemAward> awardList) {
        StringBuilder builder = new StringBuilder("[");
        for (ItemAwardOuterClass.ItemAward award : awardList) {
            builder.append(award.getItemCount()).append("x ").append(award.getItemId()).append(",");
        }

        String s = builder.toString().trim();
        if (s.endsWith(",")) {
            s = s.substring(0, s.length() - 1);
        }

        s += "]";
        return s;
    }

}
