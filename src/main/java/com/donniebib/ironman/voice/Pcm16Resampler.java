package com.donniebib.ironman.voice;

import java.util.Arrays;

public final class Pcm16Resampler {
    private final int inputRate;
    private final int outputRate;
    private final int integerRatio;
    private short[] carry = new short[0];

    public Pcm16Resampler(int inputRate, int outputRate) {
        if (inputRate <= 0 || outputRate <= 0 || inputRate < outputRate) {
            throw new IllegalArgumentException("Expected positive down-sampling rates");
        }
        this.inputRate = inputRate;
        this.outputRate = outputRate;
        this.integerRatio = inputRate % outputRate == 0 ? inputRate / outputRate : 0;
    }

    public synchronized short[] process(short[] input) {
        if (input.length == 0) return new short[0];
        if (inputRate == outputRate) return Arrays.copyOf(input, input.length);
        if (integerRatio > 0) return averageDownsample(input);
        return linearDownsample(input);
    }

    private short[] averageDownsample(short[] input) {
        short[] joined = new short[carry.length + input.length];
        System.arraycopy(carry, 0, joined, 0, carry.length);
        System.arraycopy(input, 0, joined, carry.length, input.length);

        int outCount = joined.length / integerRatio;
        short[] out = new short[outCount];

        for (int o = 0; o < outCount; o++) {
            long sum = 0;
            int base = o * integerRatio;
            for (int i = 0; i < integerRatio; i++) sum += joined[base+i];
            out[o] = (short) (sum / integerRatio);
        }

        int consumed = outCount * integerRatio;
        carry = Arrays.copyOfRange(joined, consumed, joined.length);
        return out;
    }

    private short[] linearDownsample(short[] input) {
        // Fallback for non-integer ratios. Voice Chat's normal 48kHz -> 16kHz path
        // uses the anti-aliasing box-average branch above.
        double ratio = inputRate / (double) outputRate;
        int outCount = Math.max(1, (int) Math.floor(input.length / ratio));
        short[] out = new short[outCount];

        for (int i = 0; i < outCount; i++) {
            double source = i * ratio;
            int a = Math.min(input.length - 1, (int) source);
            int b = Math.min(input.length - 1, a + 1);
            double f = source - a;
            out[i] = (short) Math.round(input[a] * (1.0 - f) + input[b] * f);
        }
        return out;
    }
}
