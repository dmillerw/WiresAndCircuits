package me.dmillerw.circuit.api.value;

/**
 * @author dmillerw
 */
public class ValueNumber extends Value {

    public ValueNumber(double value) {
        super(Double.toString(value));
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public boolean isNumber() {
        return true;
    }
}
