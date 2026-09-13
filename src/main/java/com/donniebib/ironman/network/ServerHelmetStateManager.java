package com.donniebib.ironman.network;

import com.donniebib.ironman.animation.HelmetAnimationState;
import com.donniebib.ironman.animation.MaskAction;
import com.donniebib.ironman.item.ModItems;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ServerHelmetStateManager {
    public static final int ANIMATION_TICKS = 16;
    private static final Map<UUID, Entry> STATES = new HashMap<>();

    public static void handleAction(ServerPlayer player, MaskAction action) {
        if (!player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.IRON_MAN_HELMET)) {
            return;
        }

        Entry current = STATES.get(player.getUUID());
        HelmetAnimationState state = current == null ? HelmetAnimationState.CLOSED : current.state();

        if (action == MaskAction.OPEN) {
            if (state != HelmetAnimationState.CLOSED) return;
            set(player, HelmetAnimationState.OPENING, player.serverLevel().getGameTime(), 0.0F);
        } else {
            if (state != HelmetAnimationState.OPEN) return;
            set(player, HelmetAnimationState.CLOSING, player.serverLevel().getGameTime(), 1.0F);
        }
    }

    public static void tick(MinecraftServer server) {
        if (STATES.isEmpty()) return;

        var iterator = STATES.entrySet().iterator();
        while (iterator.hasNext()) {
            var mapEntry = iterator.next();
            ServerPlayer player = server.getPlayerList().getPlayer(mapEntry.getKey());
            if (player == null) {
                iterator.remove();
                continue;
            }

            Entry entry = mapEntry.getValue();
            if (!player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.IRON_MAN_HELMET)) {
                broadcast(server, HelmetStatePayload.of(player.getUUID(), HelmetAnimationState.CLOSED.id(), 0.0F));
                iterator.remove();
                continue;
            }

            long elapsed = player.serverLevel().getGameTime() - entry.startTick();
            if (entry.state() == HelmetAnimationState.OPENING && elapsed >= ANIMATION_TICKS) {
                mapEntry.setValue(new Entry(HelmetAnimationState.OPEN, player.serverLevel().getGameTime()));
                broadcast(server, HelmetStatePayload.of(player.getUUID(), HelmetAnimationState.OPEN.id(), 1.0F));
            } else if (entry.state() == HelmetAnimationState.CLOSING && elapsed >= ANIMATION_TICKS) {
                broadcast(server, HelmetStatePayload.of(player.getUUID(), HelmetAnimationState.CLOSED.id(), 0.0F));
                iterator.remove();
            }
        }
    }

    public static void syncAllTo(ServerPlayer receiver) {
        MinecraftServer server = receiver.getServer();
        if (server == null) return;

        for (var entry : STATES.entrySet()) {
            ServerPlayer wearer = server.getPlayerList().getPlayer(entry.getKey());
            if (wearer == null) continue;
            float progress = progressNow(wearer, entry.getValue());
            ServerPlayNetworking.send(receiver, HelmetStatePayload.of(
                    wearer.getUUID(), entry.getValue().state().id(), progress));
        }
    }

    public static void removePlayer(UUID playerId) {
        STATES.remove(playerId);
    }

    public static void clear() {
        STATES.clear();
    }

    private static void set(ServerPlayer player, HelmetAnimationState state, long startTick, float progress) {
        STATES.put(player.getUUID(), new Entry(state, startTick));
        MinecraftServer server = player.getServer();
        if (server != null) {
            broadcast(server, HelmetStatePayload.of(player.getUUID(), state.id(), progress));
        }
    }

    private static float progressNow(ServerPlayer player, Entry entry) {
        return switch (entry.state()) {
            case CLOSED -> 0.0F;
            case OPEN -> 1.0F;
            case OPENING -> clamp((player.serverLevel().getGameTime() - entry.startTick()) / (float) ANIMATION_TICKS);
            case CLOSING -> 1.0F - clamp((player.serverLevel().getGameTime() - entry.startTick()) / (float) ANIMATION_TICKS);
        };
    }

    private static void broadcast(MinecraftServer server, HelmetStatePayload payload) {
        for (ServerPlayer recipient : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(recipient, payload);
        }
    }

    private static float clamp(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }

    private record Entry(HelmetAnimationState state, long startTick) {}
    private ServerHelmetStateManager() {}
}
