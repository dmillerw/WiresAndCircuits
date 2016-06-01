package me.dmillerw.circuit.api.value;

/**
 * @author dmillerw
 */
public class Value {

    public static enum Type {

        VOID,
        NUMBER,
        STRING
    }

    public static final Value NULL = new Value("NULL");

    private final String value;

    protected Value(String value) {
        this.value = value;
    }

    public boolean isNull() {
        return value.equals("NULL");
    }

    public boolean isNumber() {
        return false;
    }

    public boolean isString() {
        return false;
    }

    public final double toNumber() {
        if (isNull()) {
            return Double.NaN;
        } else {
            return Double.valueOf(value);
        }
    }

    public final String toJString() {
        return value;
    }
}
