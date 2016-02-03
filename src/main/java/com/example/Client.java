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
                int count;
                byte data[] = new byte[2048];

                File f = new File(destFolder + "/" + entry.getName());
                File parentFile = f.getParentFile();
                if(parentFile != null){
                    parentFile.mkdirs();
                }
                f.createNewFile();

                FileOutputStream fos = new FileOutputStream(f);
                BufferedOutputStream dest = new BufferedOutputStream(fos);

                while ((count = tarIn.read(data)) != -1) {
                    dest.write(data, 0, count);
                }

                dest.flush();
                dest.close();

                log.info("Copied file: {}" , f);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
