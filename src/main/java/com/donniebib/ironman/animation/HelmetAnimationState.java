package com.donniebib.ironman.animation;

public enum HelmetAnimationState {
    CLOSED((byte) 0),
    OPENING((byte) 1),
    OPEN((byte) 2),
    CLOSING((byte) 3);

    private final byte id;

    HelmetAnimationState(byte id) {
        this.id = id;
    }

    public byte id() {
        return id;
    }

    public static HelmetAnimationState fromId(byte id) {
        for (HelmetAnimationState state : values()) {
            if (state.id == id) return state;
        }
        return CLOSED;
    }
}
