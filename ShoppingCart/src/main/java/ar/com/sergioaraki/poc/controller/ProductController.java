package ar.com.sergioaraki.poc.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ar.com.sergioaraki.poc.model.Product;
import ar.com.sergioaraki.poc.persistence.ProductRepository;

@Component
@RestController
public class ProductController {
	
	Logger logger = LoggerFactory.getLogger(ProductController.class);

	@Autowired
	private ProductRepository productRepository;

	@RequestMapping(value = "/products", method = RequestMethod.POST)
	public Product create(@RequestBody Product product) {
		Product new_product = null;
		try {
			new_product = productRepository.save(product);
		} catch (Error e) {
			logger.error("Error saving new product: "+ e.getMessage());
		}
		return new_product;
	}

	@RequestMapping(value = "/products/{id}", method = RequestMethod.GET)
	public Product get(@PathVariable String id) {
		Product product = null;
		try {
			product = productRepository.findOne(id);
		} catch (Error e) {
			logger.error("Error getting a product: "+ e.getMessage());
		}
		return product;
	}
	
	@RequestMapping(value = "/products", method = RequestMethod.GET)
	public List<Product> getAll() {
		List<Product> products = new ArrayList<Product>();
		try {
			products = productRepository.findAll();
		} catch (Error e) {
			logger.error("Error getting all products: "+ e.getMessage());
		}
		return products;
	}
}