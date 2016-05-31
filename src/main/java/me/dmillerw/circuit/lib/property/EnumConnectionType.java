package me.dmillerw.circuit.lib.property;

import net.minecraft.util.IStringSerializable;

/**
 * @author dmillerw
 */
public enum EnumConnectionType implements IStringSerializable {

    NONE,
    CABLE,
    BLOCK;

    private static EnumConnectionType[] values;

    public static EnumConnectionType[] getValues() {
        if (values == null)
            values = values();
        return values;
    }

    public boolean renderCable() {
        return this == CABLE || this == BLOCK;
    }

    public boolean renderConnector() {
        return this == BLOCK;
    }

    @Override
    public String getName() {
        return this.name();
    }
}
