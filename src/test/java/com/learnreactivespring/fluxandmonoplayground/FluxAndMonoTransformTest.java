package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoTransformTest {

    List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");

    @Test
    public void transformUsingMap(){
        Flux<String> namesFlux = Flux.fromIterable(names)
                .map(s -> s.toUpperCase())
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("ADAM", "ANNA", "JACK", "JENNY"  )
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_Length(){
        Flux<Integer> namesFlux = Flux.fromIterable(names)
                .map(s -> s.length())
                .log();

        StepVerifier.create(namesFlux)
                .expectNext(4,4,4,5)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap(){
        Flux<String> StringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F")) //A , B , C D, E, F
                .flatMap(s-> {
                    return Flux.fromIterable(converToList(s));
                        })
                .log();

        StepVerifier.create(StringFlux)
                .expectNextCount(12)
                .verifyComplete();

    }

    private List<String> converToList(String s) {
        try {
            Thread.sleep(10001);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s, "newValue");

    }
}
