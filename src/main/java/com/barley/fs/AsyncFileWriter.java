package com.barley.fs;

import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class AsyncFileWriter implements Runnable {
    private final File file;
    private final Writer out;
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private volatile boolean started = false;
    private volatile boolean stopped = false;
    FileWriter fw;
    BufferedWriter bw;

    public AsyncFileWriter(File file) throws IOException {
        this.file = file;
        this.fw = new FileWriter(file.getAbsoluteFile());
        this.bw = new BufferedWriter(fw);
        this.out = new BufferedWriter(new java.io.FileWriter(file));
    }

    public void append(String seq) {
        if (!started) {
            throw new IllegalStateException("open() call expected before append()");
        }
        try {
            queue.put(seq);
        } catch (InterruptedException ignored) {
        }
    }

    public void open() {
        this.started = true;
        new Thread(this).start();
    }

    public void run() {
        while (!stopped) {
            try {
                String item = queue.poll(100, TimeUnit.MICROSECONDS);
                if (item != null) {
                    try {
                        // if file doesnt exists, then create it
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        bw.write(item);
                        bw.flush();
                    } catch (IOException logme) {
                    }
                }
            } catch (InterruptedException e) {
                // TODO
            }
        }
        try {
            out.close();
        } catch (IOException ignore) {
            // TODO
        }
    }

    public void close() throws Exception {
        this.bw.close();
        this.stopped = true;
    }
}
