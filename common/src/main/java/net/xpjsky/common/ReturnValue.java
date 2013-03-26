package net.xpjsky.sandglass.common;

import java.io.Serializable;

/**
 * Description Here
 *
 * @author paddy.xie
 * @version 10/19/12 1:54 PM
 */
public final class ReturnValue implements Serializable {

    /** multiple return value */
    private Serializable[] values;

    public ReturnValue(Serializable... values) {
        if (values == null) {
            values = new Serializable[]{null};
        }

        this.values = new Serializable[]{values};
    }

    public Serializable[] get() {
        return values;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(int index) {
        if(index >= values.length) {
            return null;
        }
        return (T) values[index];
    }

}
