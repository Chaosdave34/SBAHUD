package io.github.chaosdave34.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility methods for Skyblock Items
 */
public class ItemUtils {
    public static final int NBT_INTEGER = 3;
    public static final int NBT_LONG = 4;
    public static final int NBT_STRING = 8;
    public static final int NBT_LIST = 9;

    /**
     * Returns a string list containing the NBT lore of an {@code ItemStack}, or
     * an empty list if this item doesn't have a lore tag.
     * The itemStack argument must not be {@code null}. The returned lore list is unmodifiable since it has been
     * converted from an {@code NBTTagList}.
     *
     * @param itemStack the ItemStack to get the lore from
     * @return the lore of an ItemStack as a string list
     */
    public static List<String> getItemLore(ItemStack itemStack) {
        if (itemStack != null) {
            if (itemStack.hasTagCompound()) {
                NBTTagCompound display = itemStack.getSubCompound("display", false);

                if (display != null && display.hasKey("Lore", ItemUtils.NBT_LIST)) {
                    NBTTagList lore = display.getTagList("Lore", ItemUtils.NBT_STRING);

                    List<String> loreAsList = new ArrayList<>();
                    for (int lineNumber = 0; lineNumber < lore.tagCount(); lineNumber++) {
                        loreAsList.add(lore.getStringTagAt(lineNumber));
                    }

                    return Collections.unmodifiableList(loreAsList);
                }
            }

            return Collections.emptyList();
        } else {
            throw new NullPointerException("Cannot get lore from null item!");
        }
    }

    public static NBTTagCompound getExtraAttributes(ItemStack item) {
        if (item == null) {
            throw new NullPointerException("The item cannot be null!");
        }
        if (!item.hasTagCompound()) {
            return null;
        }

        return item.getSubCompound("ExtraAttributes", false);
    }
}
