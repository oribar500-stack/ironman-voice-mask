package com.donniebib.ironman.voice;

import com.donniebib.ironman.IronManMod;
import com.donniebib.ironman.animation.MaskAction;
import com.donniebib.ironman.config.IronManConfig;
import com.donniebib.ironman.item.ModItems;
import com.donniebib.ironman.network.MaskCommandPayload;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public final class SpeechRecognitionManager implements AutoCloseable {
    private static final int VOICE_CHAT_RATE = 48_000;
    private static final int SPHINX_RATE = 16_000;

    private final IronManConfig config;
    private final VoiceCommandDetector detector;
    private final Pcm16Resampler resampler = new Pcm16Resampler(VOICE_CHAT_RATE, SPHINX_RATE);
    private final QueueAudioInputStream stream = new QueueAudioInputStream();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread worker;

    public SpeechRecognitionManager(IronManConfig config) {
        this.config = config;
        this.detector = new VoiceCommandDetector(config);
    }

    public void start() {
        if (!running.compareAndSet(false, true)) return;
        worker = new Thread(this::runRecognizer, "IronMan-SpeechRecognition");
        worker.setDaemon(true);
        worker.start();
    }

    public void acceptRawAudio(short[] rawAudio) {
        if (!running.get() || !config.enableVoiceCommands || rawAudio == null || rawAudio.length == 0) return;

        short[] downsampled = resampler.process(rawAudio);
        byte[] pcm16le = new byte[downsampled.length * 2];
        for (int i = 0; i < downsampled.length; i++) {
            int s = downsampled[i];
            pcm16le[i * 2] = (byte) (s & 0xFF);
            pcm16le[i * 2 + 1] = (byte) ((s >>> 8) & 0xFF);
        }
        stream.offer(pcm16le);
    }

    private void runRecognizer() {
        StreamSpeechRecognizer recognizer = null;
        try {
            Configuration sphinx = new Configuration();
            sphinx.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
            sphinx.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
            sphinx.setGrammarPath("resource:/assets/ironman/grammar");
            sphinx.setGrammarName("jarvis");
            sphinx.setUseGrammar(true);
            sphinx.setSampleRate(SPHINX_RATE);

            recognizer = new StreamSpeechRecognizer(sphinx);
            recognizer.startRecognition(stream);

            while (running.get()) {
                SpeechResult result = recognizer.getResult();
                if (result == null) {
                    if (!running.get()) break;
                    continue;
                }

                String heard = result.getHypothesis();
                if (config.debugVoiceRecognition) {
                    IronManMod.LOGGER.info("[IronMan] Heard: \"{}\"", heard);
                }

                detector.detect(heard).ifPresent(this::scheduleAction);
            }
        } catch (IOException | RuntimeException e) {
            if (running.get()) {
                IronManMod.LOGGER.error("Offline speech recognizer failed to start/run", e);
            }
        } finally {
            if (recognizer != null) recognizer.stopRecognition();
        }
    }

    private void scheduleAction(MaskAction action) {
        Minecraft mc = Minecraft.getInstance();
        mc.execute(() -> {
            if (mc.player == null || mc.getConnection() == null) return;
            if (!mc.player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.IRON_MAN_HELMET)) return;

            if (config.debugVoiceRecognition) {
                IronManMod.LOGGER.info("[IronMan] {} command detected", action);
            }
            ClientPlayNetworking.send(new MaskCommandPayload(action.id()));
        });
    }

    @Override
    public void close() {
        if (!running.compareAndSet(true, false)) return;
        stream.close();
        if (worker != null) worker.interrupt();
    }
}
