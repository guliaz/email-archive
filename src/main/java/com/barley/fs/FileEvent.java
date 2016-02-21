package com.barley.fs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.*;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Configuration
public class FileEvent implements ApplicationListener<ApplicationContextEvent> {

    private static boolean isRunning = false;
    private static FileWatcher fileWatcher;

    @Value("${email.directory.name:/Users/vikram.gulia/Documents/emailArchive}")
    private String emailDirectory;

    @Override
    public void onApplicationEvent(ApplicationContextEvent event) {
        if (event instanceof ContextStartedEvent || event instanceof ContextRefreshedEvent) {
            Path dir = Paths.get(getEmailDirectory());
            try {
                if (!isRunning) {
                    fileWatcher = new FileWatcher();
                    fileWatcher.setUp(dir, true);
                    fileWatcher.start();
                    isRunning = true;
                    System.out.println(" ########## File watcher started ########## ");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (event instanceof ContextClosedEvent || event instanceof ContextStoppedEvent) {
            if (isRunning && fileWatcher.isAlive())
                fileWatcher.setKeepRunning(false);
        }
    }

    public String getEmailDirectory() {
        return emailDirectory;
    }
}
