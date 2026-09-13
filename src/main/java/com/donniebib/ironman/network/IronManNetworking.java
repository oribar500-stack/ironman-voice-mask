package com.donniebib.ironman.network;

import com.donniebib.ironman.animation.MaskAction;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public final class IronManNetworking {
    public static void initialize() {
        // Fabric API 0.141.6+1.21.11 uses playC2S()/playS2C().
        PayloadTypeRegistry.playC2S().register(MaskCommandPayload.TYPE, MaskCommandPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(HelmetStatePayload.TYPE, HelmetStatePayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(MaskCommandPayload.TYPE, (payload, context) ->
                ServerHelmetStateManager.handleAction(context.player(), MaskAction.fromId(payload.actionId())));
    }

    private IronManNetworking() {}
}
