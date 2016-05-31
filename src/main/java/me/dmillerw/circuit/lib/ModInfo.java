package me.dmillerw.circuit.lib;

import net.minecraft.util.ResourceLocation;

/**
 * @author dmillerw
 */
public class ModInfo {

    public static final String ID = "circuit";
    public static final String NAME = "Wires and Circuits";
    public static final String VERSION = "%MOD_VERSION%";

    public static String prefix(String string) {
        return ID + ":" + string;
    }
}
