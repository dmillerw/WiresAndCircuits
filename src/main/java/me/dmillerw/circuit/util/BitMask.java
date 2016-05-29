package me.dmillerw.circuit.util;

/**
 * @author dmillerw
 */
public class BitMask {

    private int current;

    public BitMask() {
        this(0);
    }

    public BitMask(int current) {
        this.current = current;
    }

    public boolean isEnabled(final int flag) {
        final int f = (1 << flag);
        return (current & f) == f;
    }

    public BitMask enable(final int flag) {
        current |= (1 << flag);
        return this;
    }

    public BitMask disable(final int flag) {
        current &= ~(1 << flag);
        return this;
    }

    public int get() {
        return current;
    }
}
