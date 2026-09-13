package com.donniebib.ironman.config;

import com.donniebib.ironman.IronManMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class IronManConfig {
    private static final Path PATH = FabricLoader.getInstance().getConfigDir()
            .resolve("ironman-voice-mask.properties");

    public boolean enableVoiceCommands = true;
    public String openPhrase = "jarvis open mask";
    public String closePhrase = "jarvis close mask";
    public double recognitionThreshold = 0.82D;
    public long voiceCommandCooldownMs = 1750L;
    public boolean debugVoiceRecognition = false;
    public boolean enableConfirmationSound = false;

    public static IronManConfig load() {
        IronManConfig config = new IronManConfig();
        Properties p = new Properties();

        if (Files.exists(PATH)) {
            try (InputStream in = Files.newInputStream(PATH)) {
                p.load(in);
            } catch (IOException e) {
                IronManMod.LOGGER.warn("Could not read {}", PATH, e);
            }
        }

        config.enableVoiceCommands = bool(p, "enableVoiceCommands", config.enableVoiceCommands);
        config.openPhrase = p.getProperty("openPhrase", config.openPhrase).trim();
        config.closePhrase = p.getProperty("closePhrase", config.closePhrase).trim();
        config.recognitionThreshold = dbl(p, "recognitionThreshold", config.recognitionThreshold);
        config.voiceCommandCooldownMs = lng(p, "voiceCommandCooldownMs", config.voiceCommandCooldownMs);
        config.debugVoiceRecognition = bool(p, "debugVoiceRecognition", config.debugVoiceRecognition);
        config.enableConfirmationSound = bool(p, "enableConfirmationSound", config.enableConfirmationSound);

        config.saveDefaultsMerged(p);
        return config;
    }

    private void saveDefaultsMerged(Properties p) {
        p.setProperty("enableVoiceCommands", Boolean.toString(enableVoiceCommands));
        p.setProperty("openPhrase", openPhrase);
        p.setProperty("closePhrase", closePhrase);
        p.setProperty("recognitionThreshold", Double.toString(recognitionThreshold));
        p.setProperty("voiceCommandCooldownMs", Long.toString(voiceCommandCooldownMs));
        p.setProperty("debugVoiceRecognition", Boolean.toString(debugVoiceRecognition));
        p.setProperty("enableConfirmationSound", Boolean.toString(enableConfirmationSound));

        try {
            Files.createDirectories(PATH.getParent());
            try (OutputStream out = Files.newOutputStream(PATH)) {
                p.store(out, "Iron Man Voice Mask");
            }
        } catch (IOException e) {
            IronManMod.LOGGER.warn("Could not write {}", PATH, e);
        }
    }

    private static boolean bool(Properties p, String key, boolean fallback) {
        return Boolean.parseBoolean(p.getProperty(key, Boolean.toString(fallback)));
    }

    private static long lng(Properties p, String key, long fallback) {
        try { return Long.parseLong(p.getProperty(key, Long.toString(fallback))); }
        catch (NumberFormatException ignored) { return fallback; }
    }

    private static double dbl(Properties p, String key, double fallback) {
        try { return Double.parseDouble(p.getProperty(key, Double.toString(fallback))); }
        catch (NumberFormatException ignored) { return fallback; }
    }
}
