package me.dmillerw.circuit.lib.property;

import net.minecraft.util.IStringSerializable;

/**
 * @author dmillerw
 */
public enum EnumCPUState implements IStringSerializable {

    OFF,
    RUNNING,
    ERROR;

    private static EnumCPUState[] values;

    public static EnumCPUState[] getValues() {
        if (values == null)
            values = values();
        return values;
    }

    @Override
    public String getName() {
        return this.name().toLowerCase();
    }
}
