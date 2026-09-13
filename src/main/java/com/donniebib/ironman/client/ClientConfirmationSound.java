package com.donniebib.ironman.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;

public final class ClientConfirmationSound {
    public static void play(boolean opening) {
        Minecraft mc = Minecraft.getInstance();
        mc.getSoundManager().play(SimpleSoundInstance.forUI(
                SoundEvents.UI_BUTTON_CLICK, opening ? 1.25F : 0.85F));
    }

    private ClientConfirmationSound() {}
}
