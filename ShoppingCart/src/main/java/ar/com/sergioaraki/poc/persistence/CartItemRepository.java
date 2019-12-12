package ar.com.sergioaraki.poc.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

import ar.com.sergioaraki.poc.model.CartItem;

public interface CartItemRepository extends MongoRepository<CartItem, String> {

}