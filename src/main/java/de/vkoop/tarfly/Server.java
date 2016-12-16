package de.vkoop.tarfly;


import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class Server {
    private final Logger log = LoggerFactory.getLogger(Server.class);

    @Value("${sourceFolder}")
    private String sourceFolderName;

    @Value("${port}")
    private int port;

    public void start() {


        log.info("Server port:{} sourceFolder:{}", port, sourceFolderName);

        final int maxRetryCount = 3;

        //setup resources
        try (
                ServerSocket serverSocket = new ServerSocket(port);
                Socket clientSocket = serverSocket.accept();

                TarOutputStream tarOut = new TarOutputStream(
                        new BufferedOutputStream(clientSocket.getOutputStream()))

        ) {
            final Path sourcePath = Paths.get(sourceFolderName);

            Files.walk(sourcePath)
                    .filter(Files::isRegularFile)
                    .forEach(filePath -> {
                        int currentRetryCount = 0;
                        boolean shouldRetry = true;

                        while (shouldRetry) {
                            if (currentRetryCount < maxRetryCount) {
                                final String filename = filePath.toString();
                                final TarEntry entry = new TarEntry(filePath.toFile(), filename);
                                try {
                                    log.info("Add file {}", filename);
                                    tarOut.putNextEntry(entry);
                                    Files.copy(filePath, tarOut);
                                    shouldRetry = false;
                                } catch (final IOException e) {
                                    log.error("Failed to add file to tar: {} .Will retry to write.", filePath, e);

                                    currentRetryCount++;
                                }
                            } else {
                                final SocketClosedException socketClosedException = new SocketClosedException("Socket closed?");
                                log.error("Problems with the connection occured.", socketClosedException);
                                throw socketClosedException;
                            }
                        }
                    });

            log.info("Finished copy process");
        } catch (final IOException e) {
            log.error("Some kind of socket error", e);
        } catch (final SocketClosedException e) {
            log.error("Socket closed?", e);
        }
    }
}
