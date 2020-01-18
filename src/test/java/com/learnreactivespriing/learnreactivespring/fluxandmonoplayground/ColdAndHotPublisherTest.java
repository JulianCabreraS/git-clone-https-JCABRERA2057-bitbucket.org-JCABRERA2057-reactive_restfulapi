package com.learnreactivespriing.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class ColdAndHotPublisherTest {

    @Test//Example of cold Publisher
    public void coldPublisherTest() throws InterruptedException {
        Flux<String> stringFlux =  Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1));

        stringFlux.subscribe(s -> System.out.println("susbscriber 1: " +s));
        Thread.sleep(2000);

        stringFlux.subscribe(s -> System.out.println("susbscriber 2: " +s));
        Thread.sleep(4000);
    }

    @Test//Example of Hot Publisher
    public void hotdPublisherTest() throws InterruptedException {
        Flux<String> stringFlux =  Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1));

        ConnectableFlux<String> connectableFlux = stringFlux.publish();
        connectableFlux.connect();
        connectableFlux.subscribe(s -> System.out.println("susbscriber 1: " +s));
        Thread.sleep(2000);

        connectableFlux.subscribe(s -> System.out.println("susbscriber 2: " +s));
        Thread.sleep(4000);
    }
}
