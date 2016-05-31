package me.dmillerw.circuit.lib.property;

import net.minecraftforge.common.property.IUnlistedProperty;

/**
 * @author dmillerw
 */
public class UnlistedConnectionType implements IUnlistedProperty<EnumConnectionType> {

    private final String name;

    public UnlistedConnectionType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isValid(EnumConnectionType value) {
        return true;
    }

    @Override
    public Class<EnumConnectionType> getType() {
        return EnumConnectionType.class;
    }

    @Override
    public String valueToString(EnumConnectionType value) {
        return value.toString();
    }
}
