package com.learnreactivespring.controller.v1;


import com.learnreactivespring.constants.ItemConstants;
import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.print.attribute.standard.Media;
import java.util.Arrays;
import java.util.List;

import static com.learnreactivespring.constants.ItemConstants.ITEM_END_POINT_V1;
import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    public List<Item> data(){
        return  Arrays.asList(new Item(null, "Samsung TV", 399.99),
                new Item(null, "LG TV", 329.99),
                new Item(null, "Apple Watch", 349.99),
                new Item("ABC", "Beats Headphones", 19.99));
    }
    @Before
    public void setUp(){
       itemReactiveRepository.deleteAll()
               .thenMany(Flux.fromIterable(data()))
               .flatMap(itemReactiveRepository::save)
               .doOnNext(item -> {
                   System.out.println("Inserted item is:"+item);
               })
               .blockLast();
    }

    @Test
    public void getAllItems(){
        webTestClient.get().uri(ITEM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4);
    }

    @Test
    public void getAllItems_approach2 (){
        webTestClient.get().uri(ITEM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4)
        .consumeWith((response) -> {
            List<Item> items = response.getResponseBody();
            items.forEach(item -> {
                assertTrue(item.getId()!=null);
            });
        });
    }

    @Test
    public void getAllItems_approach3 (){
       Flux<Item> itemFlux = webTestClient.get().uri(ITEM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(itemFlux.log("value from network"))
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void getOneItem(){
        webTestClient.get().uri(ITEM_END_POINT_V1.concat("/{id}"),"ABC")
                .exchange()
                .expectStatus().isOk() // Status is OK
                .expectBody() // return the body
                .jsonPath("$.price", 149.99); //check for specific vlaue
    }

    @Test
    public void createItem(){
        Item item = new Item(null, "iPhone X", 999.99);
        webTestClient.post().uri(ITEM_END_POINT_V1)
                .contentType(MediaType.APPLICATION_JSON) //Format is JSON
                .body(Mono.just(item), Item.class)// this is the value to be posted
                .exchange()
                .expectStatus().isCreated() // Status returned is created
                .expectBody() //Get the body
                .jsonPath("$.id").isNotEmpty() //Look for specific vlaue
                .jsonPath("$.description").isEqualTo("Iphone X");
    }
    @Test
    public void DeleteItem(){
        webTestClient.delete().uri(ITEM_END_POINT_V1.concat("/{id}"),"ABC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }

    @Test
    public void updateItem(){
        double newPrice = 129.99;
        Item item = new Item(null, "Beats headphones", newPrice);
        webTestClient.put().uri(ITEM_END_POINT_V1.concat("/{id}"),"ABC")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody() //Get the body
                .jsonPath("$.price", newPrice);
    }

    @Test
    public void updateItem_notFound(){
        double newPrice = 129.99;
        Item item = new Item(null, "Beats headphones", newPrice);
        webTestClient.put().uri(ITEM_END_POINT_V1.concat("/{id}"),"DEF")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void runTimeException(){
        webTestClient.get().uri(ITEM_END_POINT_V1+"/runtimeException")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("RuntimeException ocurred");
    }





}
