package ar.com.sergioaraki.poc.controller;

import java.util.ArrayList;
import java.util.Date;
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

import ar.com.sergioaraki.poc.model.Cart;
import ar.com.sergioaraki.poc.model.CartItem;
import ar.com.sergioaraki.poc.model.Product;
import ar.com.sergioaraki.poc.persistence.CartItemRepository;
import ar.com.sergioaraki.poc.persistence.CartRepository;
import ar.com.sergioaraki.poc.persistence.ProductRepository;

@Component
@RestController
public class CartController {
	
	Logger logger = LoggerFactory.getLogger(CartController.class);

	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private CartItemRepository cartItemRepository;

	@RequestMapping(value = "/carts", method = RequestMethod.POST)
	public Cart createCart(@RequestBody Cart cart) {
		Cart new_cart = null;
		cart.setCreationDate(new Date());
		cart.setTotal(0);
		cart.setStatus("NEW");
		try {
			new_cart = cartRepository.save(cart);
		} catch (Error e) {
			logger.error("Error saving new cart: "+e.getMessage());
		}
		return new_cart;
	}
	
	private void updateCartTotal(Cart cart) {
		List<CartItem> cartItems = cart.getCartItems();
		cart.setTotal(0);
		for (int i = 0; i < cart.getCartItems().size(); i++) {
			cart.setTotal(cart.getTotal()+(cartItems.get(i).getUnit_price()*cartItems.get(i).getQuantity()));
		}
	}
	
	@RequestMapping(value = "/carts/{id}/products", method = RequestMethod.POST)
	public Cart addProduct(@PathVariable String id, @RequestBody Item item) {
		Cart cart = null;
		try {
			cart = cartRepository.findOne(id);
		} catch (Error e) {
			logger.error("Error getting a cart: "+e.getMessage());
		}
		if (cart!=null) {
			Product product = null;
			try {
				product = productRepository.findOne(item.getId());
			} catch (Error e) {
				logger.error("Error getting a product: "+e.getMessage());
			}
			if (product != null) {
				CartItem cartItem = new CartItem();
				cartItem.setId_product(product.getId());
				cartItem.setQuantity(item.getQuantity());
				cartItem.setUnit_price(product.getUnit_price());
				List<CartItem> cartItems = cart.getCartItems();
				boolean sameProduct = false;
				for (int i = 0; i < cartItems.size(); i++) {
					if (cartItems.get(i).getId_product().equals(cartItem.getId_product())) {
						CartItem cartItemAux = null;
						try {
							cartItemAux = cartItemRepository.findOne(cartItems.get(i).getId());
						} catch (Error e) {
							logger.error("Error getting a cart item: "+e.getMessage());
						}
						if (cartItemAux != null) {
							cartItemAux.setQuantity(cartItemAux.getQuantity()+cartItem.getQuantity());
							cartItemAux.setUnit_price(cartItem.getUnit_price());
							try {
								cartItemRepository.save(cartItemAux);
								cartItems.set(i, cartItemAux);
								sameProduct = true;
							} catch (Error e) {
								logger.error("Error saving a cart item: "+e.getMessage());
							}
						}
					}
				}
				if (!sameProduct) {
					try {
						cart.getCartItems().add(cartItemRepository.save(cartItem));
					} catch (Error e) {
						logger.error("Error saving a cart item: "+e.getMessage());
					}
				}
				updateCartTotal(cart);
				try {
					 cart = cartRepository.save(cart);
				} catch (Error e) {
					logger.error("Error saving a cart: "+e.getMessage());
				}
				return cart;
			}
			else
				return cart;
		}
		else 
			return cart;
	}
	
	@RequestMapping(value = "/carts/{cartId}/products/{productId}", method = RequestMethod.DELETE)
	public Cart removeProduct(@PathVariable String cartId, @PathVariable String productId) {
		Cart cart = null; 
		try { 
			cart = cartRepository.findOne(cartId);
		} catch (Error e) {
			logger.error("Error getting a cart: "+e.getMessage());
		}
		if (cart != null) {
			List<CartItem> cartItems = cart.getCartItems();
			if (!cartItems.isEmpty()) {
				List<CartItem> auxCartItems = new ArrayList<CartItem>();
				for (int i = 0; i < cartItems.size(); i++) {
					if (cartItems.get(i).getId_product().equals(productId))
						auxCartItems.add(cartItems.get(i));
				}
				if (!auxCartItems.isEmpty()) {
					for (int i = 0; i < auxCartItems.size(); i++) {
						try {
							cartItemRepository.delete(auxCartItems.get(i).getId());
						} catch (Error e) {
							logger.error("Error deleting cart item: "+e.getMessage());
						}
					}
					cartItems.removeAll(auxCartItems);
					updateCartTotal(cart);
					try {
						cartRepository.save(cart);
					} catch (Error e) {
						logger.error("Error saving cart: "+e.getMessage());
					}
				}
			}
			return cart;
		}
		else
			return cart;
	}
	
	@RequestMapping(value = "/carts/{id}/products", method = RequestMethod.GET)
	public List<CartProduct> getCartItems(@PathVariable String id) {
		List<CartProduct> items = new ArrayList<CartProduct>();
		Cart cart = null;
		try {
			cart = cartRepository.findOne(id);		
		} catch (Error e) {
			logger.error("Error getting a cart: "+e.getMessage());
		}
		if (cart != null) {
			List<CartItem> cartItems = cart.getCartItems();
			if (!cartItems.isEmpty()) {
				for (int i = 0; i < cartItems.size(); i++) {
					CartItem cartItem = cartItems.get(i);
					Product product = null; 
					try {
						product = productRepository.findOne(cartItem.getId_product());
					} catch (Error e) {
						logger.error("Error getting a product: "+e.getMessage());
					}
					CartProduct item = new CartProduct();
					item.setId(cartItem.getId_product());
					item.setDescription(product.getDescription());
				    item.setQuantity(cartItem.getQuantity());
				    item.setUnit_price(cartItem.getUnit_price());
				    items.add(item);
				}
			}
			return items;
		}
		else
			return items;
	}
	
	@RequestMapping(value = "/carts/{id}/", method = RequestMethod.GET)
	public Cart getCart(@PathVariable String id) {
		Cart cart = null;
		try {
			cart = cartRepository.findOne(id);
		} catch (Error e) {
			logger.error("Error getting a cart: "+e.getMessage());
		}
		return cart;
	}
	
	@RequestMapping(value = "/carts/{id}/checkouts", method = RequestMethod.POST)
	public Cart checkout(@PathVariable String id) {
		Cart cart = null;
		try {
			cart = cartRepository.findOne(id);
		} catch (Error e) {
			logger.error("Error getting a cart: "+e.getMessage());
		}
		if (cart != null) {
			cart.setStatus("READY");
			try {
				cart =  cartRepository.save(cart);
			} catch (Error e) {
				logger.error("Error saving a cart: "+e.getMessage());
			}
			return cart;
		}
		else
			return cart;
	}
	
}

class Item {
	
	private String id;
	private int quantity;

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}

class CartProduct extends Item {
	
	private double unit_price;
	private String description;
	
	public double getUnit_price() {
		return unit_price;
	}
	
	public void setUnit_price(double unit_price) {
		this.unit_price = unit_price;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}	
}