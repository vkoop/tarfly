package de.vkoop.tarfly;


import lombok.extern.slf4j.Slf4j;
import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarInputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.*;

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

                try{
                    Files.createFile(newFile);
                    Files.copy(tarIn, newFile, StandardCopyOption.REPLACE_EXISTING);

                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(newFile.toFile()));
                    byte data[] = new byte[2048];
                    int count;

                    while((count = tarIn.read(data)) != -1) {
                        bufferedOutputStream.write(data, 0, count);
                    }

                    bufferedOutputStream.flush();
                    bufferedOutputStream.close();

                    log.info("Copied file: {}" , newFile);
                } catch(FileAlreadyExistsException ex){
                    log.info("File already exists: {}", newFile.toString());
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
