package net.xpjsky.common.buffer;

import java.util.Date;

/**
 * Description Here
 *
 * @author Paddy
 * @version 12-10-17 下午10:09
 */
public class Buffer {
    private int id;
    private Date pickTime;
    private String picker;
    private byte[] buffer;

    Buffer(int id, int size) {
        this.id = id;
        this.buffer = new byte[size];
    }

    public byte[] get() {
        return buffer;
    }

    protected void set(byte[] buffer) {
        this.buffer = buffer;
    }

    protected int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    protected Date getPickTime() {
        return pickTime;
    }

    protected void setPickTime(Date pickTime) {
        this.pickTime = pickTime;
    }

    protected String getPicker() {
        return picker;
    }

    protected void setPicker(String picker) {
        this.picker = picker;
    }

    public void back() {
        BufferPool.put(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Buffer buffer = (Buffer) o;

        return id == buffer.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}