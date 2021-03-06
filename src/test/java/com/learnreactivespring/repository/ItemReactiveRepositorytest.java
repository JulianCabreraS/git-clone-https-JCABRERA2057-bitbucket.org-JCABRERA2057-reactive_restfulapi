package com.learnreactivespring.repository;

import com.learnreactivespring.document.Item;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@RunWith(SpringRunner.class)
public class ItemReactiveRepositorytest {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    List<Item> itemList = Arrays.asList(
            new Item(null,"Samsung TV", 400.0),
            new Item(null, "LG TV", 420.0),
            new Item(null, "Apple watch", 299.99),
            new Item(null, "Beats headsphone", 149.99),
            new Item("ABC", "Bose headphones", 149.99));

    @Before
    public void setUp(){
        itemReactiveRepository.deleteAll()
        .thenMany(Flux.fromIterable(itemList))
        .flatMap(itemReactiveRepository::save)
        .doOnNext((item -> {
            System.out.println("Inserted item is: "+ item);
        }))
        .blockLast();
    }

    @Test
    public void getAllItems(){
        StepVerifier.create(itemReactiveRepository.findAll())
        .expectSubscription()
        .expectNextCount(5)
        .verifyComplete();
    }

    @Test
    public void getItemByID(){
        StepVerifier.create(itemReactiveRepository.findById("ABC"))
                .expectSubscription()
                .expectNextMatches((item -> item.getDescription().equals("Bose headphones")))
                .verifyComplete();
    }
    @Test
    public void findByDescription(){
        StepVerifier.create(itemReactiveRepository.findByDescription("Bose headphones"))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }
    @Test
    public void saveItem(){
        Item item = new Item(null, "Google Home Mini", 30.00);
        Mono<Item> savedItem = itemReactiveRepository.save(item);

        StepVerifier.create(savedItem.log("saveitem: "))
                .expectSubscription()
                .expectNextMatches(item1 -> (item1.getId()!=null && item1.getDescription().equals("Google Home Mini")))
                .verifyComplete();
    }
    @Test
    public void updateItem(){
        double newPrice = 520.00;
        Flux<Item> updatedItem =  itemReactiveRepository.findByDescription("LG TV")
                .map(item -> {
                    item.setPrice(newPrice);
                    return item;
                })
                .flatMap(item -> {
                    return  itemReactiveRepository.save(item); //saving the item with the new price;
                });

        StepVerifier.create(updatedItem)
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice() ==520.00)
                .verifyComplete();
    }
    @Test
    public void deleteItemById(){
        Mono<Void> deleteItem = itemReactiveRepository.findById("ABC")
                .map(Item::getId)
                .flatMap((id)-> {
                    return itemReactiveRepository.deleteById(id);
                });

        StepVerifier.create(deleteItem.log())
                .expectSubscription()
                .verifyComplete();

        
    }
}
