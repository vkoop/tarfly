package de.vkoop.tarfly;


import lombok.extern.slf4j.Slf4j;
import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.BufferedOutputStream;
import java.io.IOException;
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

        int retryCount = 3;



        try (
                ServerSocket serverSocket = new ServerSocket(port);
                Socket clientSocket = serverSocket.accept();

                TarOutputStream tarOut = new TarOutputStream(
                        new BufferedOutputStream(
                            clientSocket.getOutputStream()));


        ) {
            Path sourcePath = Paths.get(sf);

            Files.walk(sourcePath)
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        int currentRetry = 0;
                        boolean retry = true;

                        while(retry){
                            if(currentRetry < retryCount){
                                String filename = path.toString();
                                TarEntry entry = new TarEntry(path.toFile(), filename);
                                try {
                                    log.info("Add file {}",filename);
                                    tarOut.putNextEntry(entry);
                                    Files.copy(path, tarOut);
                                    retry = false;
                                } catch (IOException e) {
                                    log.error("Failed to add file to tar: {} .Will retry to write.", path ,e);

                                    currentRetry++;
                                }
                            } else {
                                throw new SocketClosedException("Socket closed?");
                            }
                        }
                    });

            log.info("Finished copy process");
        } catch (IOException e) {

            log.error("Some kind of socket error",e);
        } catch (SocketClosedException e){
            log.error("Socket closed?", e);
        }
    }


}
