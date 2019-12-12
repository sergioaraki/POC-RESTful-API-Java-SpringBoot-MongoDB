package ar.com.sergioaraki.poc.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

import ar.com.sergioaraki.poc.model.Product;

public interface ProductRepository extends MongoRepository<Product, String> {

}
