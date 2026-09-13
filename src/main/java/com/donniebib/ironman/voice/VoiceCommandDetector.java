package com.donniebib.ironman.voice;

import com.donniebib.ironman.animation.MaskAction;
import com.donniebib.ironman.config.IronManConfig;

import java.util.Locale;
import java.util.Optional;

public final class VoiceCommandDetector {
    private final IronManConfig config;
    private long lastAcceptedNanos;

    public VoiceCommandDetector(IronManConfig config) {
        this.config = config;
    }

    public synchronized Optional<MaskAction> detect(String rawText) {
        if (!config.enableVoiceCommands || rawText == null || rawText.isBlank()) {
            return Optional.empty();
        }

        String heard = normalize(rawText);
        String open = normalize(config.openPhrase);
        String close = normalize(config.closePhrase);

        double openScore = similarity(heard, open);
        double closeScore = similarity(heard, close);

        MaskAction action;
        double best;
        double other;
        if (openScore >= closeScore) {
            action = MaskAction.OPEN;
            best = openScore;
            other = closeScore;
        } else {
            action = MaskAction.CLOSE;
            best = closeScore;
            other = openScore;
        }

        if (best < config.recognitionThreshold || best - other < 0.08D) {
            return Optional.empty();
        }

        long now = System.nanoTime();
        long cooldownNanos = config.voiceCommandCooldownMs * 1_000_000L;
        if (now - lastAcceptedNanos < cooldownNanos) return Optional.empty();

        lastAcceptedNanos = now;
        return Optional.of(action);
    }

    static String normalize(String text) {
        return text.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s]", " ")
                .trim()
                .replaceAll("\\s+", " ");
    }

    static double similarity(String a, String b) {
        if (a.equals(b)) return 1.0D;
        int max = Math.max(a.length(), b.length());
        if (max == 0) return 1.0D;
        return 1.0D - (levenshtein(a, b) / (double) max);
    }

    private static int levenshtein(String a, String b) {
        int[] prev = new int[b.length() + 1];
        int[] cur = new int[b.length() + 1];
        for (int j = 0; j <= b.length(); j++) prev[j] = j;

        for (int i = 1; i <= a.length(); i++) {
            cur[0] = i;
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                cur[j] = Math.min(Math.min(cur[j - 1] + 1, prev[j] + 1), prev[j - 1] + cost);
            }
            int[] swap = prev; prev = cur; cur = swap;
        }
        return prev[b.length()];
    }
}
