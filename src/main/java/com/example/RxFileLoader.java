package com.example;


import lombok.extern.slf4j.Slf4j;
import org.jooq.lambda.Seq;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class RxFileLoader {


    public Observable<Path> walkPath(Path start)  {

        if(Files.isDirectory(start)){
                try {
                    return Observable
                            .from(Seq.seq(Files.list(start)))
                            .flatMap(this::walkPath);
                } catch (IOException e) {
                    return Observable.error(e);
                }
        } else {
            return Observable.just(start);
        }

    }




}
