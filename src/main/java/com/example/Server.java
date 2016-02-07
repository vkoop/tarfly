package com.example;


import lombok.extern.slf4j.Slf4j;
import org.jooq.lambda.Seq;
import org.jooq.lambda.function.Function1;
import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Lazy
@Component
@Slf4j
public class Server {

    @Value("${port}")
    int port;

    @Value("${sourceFolder}")
    String sf;

    public static <I,O> Observable<O> doInparalla(I item, Function1<I,O> f){

        return Observable.just(f.apply(item));

    }

    public Observable<Path> doStuffWithPath(Path path, TarOutputStream tarOut){
        return Observable.defer(()-> {


            return Observable.just(path)
                    ;
        }).subscribeOn(Schedulers.io());
    }

    public void start() {

        log.info("Server port:{} sourceFolder:{}", port, sf);

        RxFileLoader rxFileLoader = new RxFileLoader();

        try (
            ServerSocket serverSocket = new ServerSocket(port);
            Socket clientSocket = serverSocket.accept();

            TarOutputStream tarOut = new TarOutputStream(new BufferedOutputStream(
                    clientSocket.getOutputStream()));


        ) {
            Observable<Path> fileObservable = rxFileLoader.walkPath(Paths.get(sf));





            fileObservable
                    .subscribe(path -> {
                        try {
                            log.info("start adding file: {}", path);
                            TarEntry entry = new TarEntry(path.toFile(), path.toString());
                            tarOut.putNextEntry(entry);

                            Files.copy(path, tarOut);
                        } catch (IOException e) {
                            e.printStackTrace();
                            log.error("Skipping file because of error", path);
                        }
                    }, (e)->{
                log.error("Some error...");
            });



        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
