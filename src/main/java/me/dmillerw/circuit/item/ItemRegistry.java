package me.dmillerw.circuit.item;

import me.dmillerw.circuit.item.tool.ItemConfGun;
import net.minecraft.item.Item;

/**
 * @author dmillerw
 */
public class ItemRegistry {

    public static Item confGun;

    public static void initialize() {
        confGun = new ItemConfGun();
    }
}
