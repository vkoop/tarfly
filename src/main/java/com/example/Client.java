package com.example;


import lombok.extern.slf4j.Slf4j;
import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarInputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Lazy
@Component
@Slf4j
public class Client {


    @Value("${port}")
    public int port;

    @Value("${host}")
    public String host;

    @Value("${targetFolder}")
    String destFolder;

    public void start() {

        log.info("Client port:{} host:{} targetFolder:{}", port, host, destFolder);

        try (
                Socket socket = new Socket(host, port);
                TarInputStream tarIn = new TarInputStream(socket.getInputStream());
        ) {

            TarEntry entry;
            while ((entry = tarIn.getNextEntry()) != null) {
                Path newFile = Paths.get(destFolder, entry.getName());
                Path parentFile = newFile.getParent();

                if(parentFile != null){
                    Files.createDirectories(parentFile);
                }

                Files.createFile(newFile);
                Files.copy(tarIn, parentFile);

                log.info("Copied file: {}" , newFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
