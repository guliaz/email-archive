package com.barley;

import com.barley.fs.FileWatcher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
@ComponentScan(basePackages = "com.barley")
public class Application {
    public static void main(String[] args) throws IOException {
        System.out.println("$$$$$$$$$ Arguments received $$$$$$$$$$$$");
        args = new String[]{"/Users/vikram.gulia/Documents/emailArchive"};
        for (String arg : args) {
            System.out.println(" Argument : " + arg);
        }
        if (args.length > 0) {
            System.setProperty("email.directory.name", args[0]);
        }
        System.out.println("######## Starting the application now ########");
        ConfigurableApplicationContext cac = SpringApplication.run(Application.class, args);
        System.out.println("######## Application load finished ########");
        cac.start();
    }

   /* @Bean
    public MultipartResolver multipartResolver() {
        final CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
        commonsMultipartResolver.setMaxUploadSize(-1);
        return commonsMultipartResolver;
    }*/
}
