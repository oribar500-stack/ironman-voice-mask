package com.donniebib.ironman.client;

import com.donniebib.ironman.animation.HelmetAnimationState;
import com.donniebib.ironman.config.IronManConfig;
import com.donniebib.ironman.network.HelmetStatePayload;
import com.donniebib.ironman.voice.SpeechRecognitionManager;
import com.donniebib.ironman.voice.VoiceAudioBus;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;

public final class IronManClient implements ClientModInitializer {
    private SpeechRecognitionManager speech;
    private IronManConfig config;

    @Override
    public void onInitializeClient() {
        config = IronManConfig.load();
        speech = new SpeechRecognitionManager(config);
        VoiceAudioBus.setListener(speech::acceptRawAudio);
        speech.start();

        ClientPlayNetworking.registerGlobalReceiver(HelmetStatePayload.TYPE, (payload, context) ->
            context.client().execute(() -> {
                HelmetAnimationState state = HelmetAnimationState.fromId(payload.stateId());
                ClientHelmetStateManager.update(payload.playerId(), state, payload.progress());

                Minecraft mc = Minecraft.getInstance();
                if (config.enableConfirmationSound && mc.player != null &&
                        mc.player.getUUID().equals(payload.playerId()) &&
                        (state == HelmetAnimationState.OPENING || state == HelmetAnimationState.CLOSING)) {
                    ClientConfirmationSound.play(state == HelmetAnimationState.OPENING);
                }
            })
        );

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ClientHelmetStateManager.clear());

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            VoiceAudioBus.clearListener();
            if (speech != null) speech.close();
            ClientHelmetStateManager.clear();
        });
    }
}
