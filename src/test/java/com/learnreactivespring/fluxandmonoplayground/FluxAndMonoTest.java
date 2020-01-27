package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


public class FluxAndMonoTest {
    @Test
    public void FluxTest() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring boot", "Reactor Spring")
                //.concatWith(Flux.error(new RuntimeException("Exception ocurred")))
                .log();


        stringFlux.subscribe(System.out::println,
                (e) -> System.err.println(e),
                () -> System.out.println("Completed"));
    }

    @Test
    public void fluxTestElements_WithoutError() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring boot", "Reactor Spring")
                //.concatWith(Flux.error(new RuntimeException("Exception ocurred")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring boot")
                .expectNext("Reactor Spring")
                .verifyComplete();
    }

    @Test
    public void fluxTestElements_WithError() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring boot", "Reactor Spring")
                .concatWith(Flux.error(new RuntimeException("Exception ocurred")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring boot")
                .expectNext("Reactor Spring")
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void monoTest(){
        Mono<String> stringMono = Mono.just("Spring");
        StepVerifier.create(stringMono.log())
        .expectNext("Spring")
        .verifyComplete();
    }

    @Test
    public void monoTest_Error(){

        StepVerifier.create(Mono.error(new RuntimeException("Exception Ocurred")).log())
                .expectError(RuntimeException.class)
                .verify();
    }
}
