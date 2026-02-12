package com.michael.AuctionV2.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class StartupRunner implements CommandLineRunner {

    @Value("${script.path}")
    private String scriptPath;

    @Override
    public void run(String... args) throws Exception {

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(scriptPath);
            processBuilder.inheritIO();  // show output in console
            Process process = processBuilder.start();
            process.waitFor();  // wait for script to finish

        } catch (IOException | InterruptedException e) {
            log.error("Something went wrong when running initialize scripts!");
        }
    }
}

