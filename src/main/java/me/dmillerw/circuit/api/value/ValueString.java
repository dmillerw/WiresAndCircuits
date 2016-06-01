package me.dmillerw.circuit.api.value;

/**
 * @author dmillerw
 */
public class ValueString extends Value {

    public ValueString(String value) {
        super(value);
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public boolean isString() {
        return true;
    }
}
