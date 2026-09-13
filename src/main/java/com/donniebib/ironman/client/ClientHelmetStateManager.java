package com.donniebib.ironman.client;

import com.donniebib.ironman.animation.HelmetAnimationState;
import com.donniebib.ironman.network.ServerHelmetStateManager;
import net.minecraft.client.Minecraft;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ClientHelmetStateManager {
    private static final Map<UUID, Entry> STATES = new ConcurrentHashMap<>();

    public static void update(UUID playerId, HelmetAnimationState state, float initialProgress) {
        long now = clientTick();
        if (state == HelmetAnimationState.CLOSED) {
            STATES.remove(playerId);
            return;
        }
        STATES.put(playerId, new Entry(state, clamp(initialProgress), now));
    }

    public static float getOpenProgress(UUID playerId, float partialTick) {
        Entry e = STATES.get(playerId);
        if (e == null) return 0.0F;

        float elapsed = (clientTick() - e.localStartTick() + partialTick) /
                (float) ServerHelmetStateManager.ANIMATION_TICKS;

        return switch (e.state()) {
            case CLOSED -> 0.0F;
            case OPEN -> 1.0F;
            case OPENING -> clamp(e.initialProgress() + elapsed);
            case CLOSING -> clamp(e.initialProgress() - elapsed);
        };
    }

    public static void clear() {
        STATES.clear();
    }

    private static long clientTick() {
        Minecraft mc = Minecraft.getInstance();
        return mc.level == null ? 0L : mc.level.getGameTime();
    }

    private static float clamp(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }

    private record Entry(HelmetAnimationState state, float initialProgress, long localStartTick) {}
    private ClientHelmetStateManager() {}
}
