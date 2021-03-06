package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class FluxAndMonoFactoryTest {

    List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");

    @Test
    public void fluxUsingIterable(){
        Flux<String> namesFlux = Flux.fromIterable(names).log();

        StepVerifier.create(namesFlux)
                .expectNext("adam", "anna", "jack", "jenny")
                .verifyComplete();
    }
    @Test
    public void fluxUsingArray(){
        String[] names = new String[]{ "adam", "anna", "jack", "jenny"};

        Flux<String> namesFlux =  Flux.fromArray(names);
        StepVerifier.create(namesFlux)
                .expectNext("adam", "anna", "jack", "jenny")
                .verifyComplete();
    }

    @Test
    public void fluxUsingStream(){
        Flux<String> namesFlux = Flux.fromStream(names.stream());
        StepVerifier.create(namesFlux)
                .expectNext("adam", "anna", "jack", "jenny")
                .verifyComplete();

    }

    @Test
    public void monoUsingJustOrEmpty(){
        
    }

    @Test
    public void monoUsingSupplier(){
        Supplier<String> stringSupplier = ()-> "adam";

        Mono<String> stringMono = Mono.fromSupplier(stringSupplier);

        StepVerifier.create(stringMono.log())
                .expectNext("adam")
                .verifyComplete();
    }
}
