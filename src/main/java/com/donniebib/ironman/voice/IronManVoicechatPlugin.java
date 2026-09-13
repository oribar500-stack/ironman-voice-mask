package com.donniebib.ironman.voice;

import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.ClientSoundEvent;
import de.maxhenkel.voicechat.api.events.EventRegistration;

public final class IronManVoicechatPlugin implements VoicechatPlugin {
    @Override
    public String getPluginId() {
        return "ironman_voice_mask";
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(ClientSoundEvent.class, this::onClientSound);
    }

    private void onClientSound(ClientSoundEvent event) {
        // Listen only. Never call event.setRawAudio(...), so Simple Voice Chat is untouched.
        VoiceAudioBus.publish(event.getRawAudio());
    }
}
