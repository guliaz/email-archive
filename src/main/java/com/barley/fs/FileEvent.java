package com.barley.fs;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FileEvent implements ApplicationListener<ApplicationContextEvent> {

    private static boolean isRunning = false;
    private static FileWatcher fileWatcher;

    @Override
    public void onApplicationEvent(ApplicationContextEvent event) {
        if (event instanceof ContextStartedEvent || event instanceof ContextRefreshedEvent) {
            Path dir = Paths.get(System.getProperty("email.directory.name", "/Users/vikram.gulia/Documents/emailArchive"));
            try {
                if (!isRunning) {
                    fileWatcher = new FileWatcher();
                    fileWatcher.setUp(dir, true);
                    fileWatcher.start();
                    isRunning = true;
                    System.out.println(" ########## File watcher started ########## ");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (event instanceof ContextClosedEvent || event instanceof ContextStoppedEvent) {
            if (isRunning && fileWatcher.isAlive())
                fileWatcher.setKeepRunning(false);
        }
    }
}
