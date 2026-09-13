package com.donniebib.ironman.animation;

public enum MaskAction {
    OPEN((byte) 0),
    CLOSE((byte) 1);

    private final byte id;

    MaskAction(byte id) {
        this.id = id;
    }

    public byte id() {
        return id;
    }

    public static MaskAction fromId(byte id) {
        return id == 1 ? CLOSE : OPEN;
    }
}
