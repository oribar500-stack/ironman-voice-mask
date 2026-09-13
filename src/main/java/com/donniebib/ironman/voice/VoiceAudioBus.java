package com.donniebib.ironman.voice;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public final class VoiceAudioBus {
    private static final AtomicReference<Consumer<short[]>> LISTENER = new AtomicReference<>();

    public static void setListener(Consumer<short[]> listener) {
        LISTENER.set(listener);
    }

    public static void clearListener() {
        LISTENER.set(null);
    }

    public static void publish(short[] pcm) {
        Consumer<short[]> listener = LISTENER.get();
        if (listener != null && pcm != null && pcm.length > 0) {
            listener.accept(Arrays.copyOf(pcm, pcm.length));
        }
    }

    private VoiceAudioBus() {}
}
