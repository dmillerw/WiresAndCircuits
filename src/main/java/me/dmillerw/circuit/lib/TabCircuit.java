package me.dmillerw.circuit.lib;

import me.dmillerw.circuit.item.ItemRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

/**
 * @author dmillerw
 */
public class TabCircuit extends CreativeTabs {

    public static final TabCircuit INSTANCE = new TabCircuit();

    public TabCircuit() {
        super(ModInfo.ID);
    }

    @Override
    public Item getTabIconItem() {
        return ItemRegistry.confGun;
    }
}
