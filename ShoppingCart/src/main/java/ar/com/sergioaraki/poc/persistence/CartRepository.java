package ar.com.sergioaraki.poc.persistence;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import ar.com.sergioaraki.poc.model.Cart;

public interface CartRepository extends MongoRepository<Cart, String> {
	
	@Query("{ 'status' : ?0 }")
	List<Cart> findCartsByStatus(String status);

}