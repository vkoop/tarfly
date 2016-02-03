package com.example;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Lazy
@Component
@Slf4j
public class Server {

    @Value("${port}")
    int port;

    @Value("${sourceFolder}")
    String sf;

    public void start() {

        log.info("Server port:{} sourceFolder:{}", port, sf);


        try (
            ServerSocket serverSocket = new ServerSocket(port);
            Socket clientSocket = serverSocket.accept();

            TarOutputStream tarOut = new TarOutputStream(new BufferedOutputStream(
                    clientSocket.getOutputStream()));


        ) {
            Path sourceFolder = Paths.get(sf);
            Files.walk(sourceFolder)
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            log.info("start adding file: {}", path);
                            TarEntry entry = new TarEntry(path.toFile(), path.toString());
                            tarOut.putNextEntry(entry);
                            InputStream inputStream = Files.newInputStream(path);
                            IOUtils.copy(inputStream, tarOut);
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });



        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
