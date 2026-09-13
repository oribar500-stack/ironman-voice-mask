package com.donniebib.ironman.voice;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public final class QueueAudioInputStream extends InputStream {
    private static final byte[] END = new byte[0];
    private final BlockingQueue<byte[]> queue = new ArrayBlockingQueue<>(96);
    private volatile boolean closed;
    private byte[] current;
    private int position;

    public void offer(byte[] bytes) {
        if (closed || bytes.length == 0) return;
        if (!queue.offer(bytes)) {
            queue.poll();
            queue.offer(bytes);
        }
    }

    @Override
    public int read() throws IOException {
        byte[] one = new byte[1];
        int read = read(one, 0, 1);
        return read < 0 ? -1 : one[0] & 0xFF;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (len == 0) return 0;

        while (current == null || position >= current.length) {
            if (closed && queue.isEmpty()) return -1;
            try {
                current = queue.take();
                position = 0;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Interrupted while waiting for microphone audio", e);
            }
            if (current == END || current.length == 0) return -1;
        }

        int n = Math.min(len, current.length - position);
        System.arraycopy(current, position, b, off, n);
        position += n;
        return n;
    }

    @Override
    public void close() {
        closed = true;
        queue.clear();
        queue.offer(END);
    }
}
