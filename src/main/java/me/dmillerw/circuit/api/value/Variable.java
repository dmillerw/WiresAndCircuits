package me.dmillerw.circuit.api.value;

/**
 * @author dmillerw
 */
public class Variable {

    public final int index;
    public final String name;
    public final Value.Type type;

    public Variable(int index, String name, Value.Type type) {
        this.index = index;
        this.name = name;
        this.type = type;
    }
}
