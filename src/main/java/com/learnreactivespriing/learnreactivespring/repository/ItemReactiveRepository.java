package com.learnreactivespriing.learnreactivespring.repository;

import com.learnreactivespriing.learnreactivespring.document.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ItemReactiveRepository extends ReactiveMongoRepository<Item, String> {
}
