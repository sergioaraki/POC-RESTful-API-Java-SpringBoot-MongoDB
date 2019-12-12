package ar.com.sergioaraki.poc.service;

import ar.com.sergioaraki.poc.MyThread;
import ar.com.sergioaraki.poc.model.Cart;
import ar.com.sergioaraki.poc.model.CartItem;
import ar.com.sergioaraki.poc.model.Product;
import ar.com.sergioaraki.poc.persistence.CartRepository;
import ar.com.sergioaraki.poc.persistence.ProductRepository;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsynchronousService {
	
	Logger logger = LoggerFactory.getLogger(AsynchronousService.class);

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
	private CartRepository cartRepository;
    
    @Autowired
	private ProductRepository productRepository;
    
    @Async
    public void checkCarts() {
    	List<Cart> carts = new ArrayList<Cart>();
    	try {
    		carts = cartRepository.findCartsByStatus("READY");
    	} catch (Error e) {
    		logger.error("Process Carts service: error getting Carts in status READY "+ e.getMessage());
    	}
    	for (int i = 0; i < carts.size(); i++) {
    		Cart cart = carts.get(i);
    		List<CartItem> cartItems = new ArrayList<CartItem>();
    		try {
    			cartItems = cart.getCartItems();
    		} catch (Error e) {
        		logger.error("Process Carts service: error getting Cart items "+ e.getMessage());
        	}
			if (cartItems.isEmpty()) {
				cart.setStatus("PROCESSED");
				try {
					cartRepository.save(cart);
				} catch (Error e) {
	        		logger.error("Process Carts service: error saving Cart status "+ e.getMessage());
	        	}
			} else {
				boolean outStock = false;
				List<Product> products = new ArrayList<Product>();
				for (int j = 0; j < cartItems.size(); j++) {
					CartItem cartItem = cartItems.get(j);
					Product product = null;
					try {
						product = productRepository.findOne(cartItem.getId_product());
					} catch (Error e) {
		        		logger.error("Process Carts service: error getting product "+ e.getMessage());
		        	}
					if (product!=null) {
						int stock = product.getStock() - cartItem.getQuantity();
						if (stock>=0) {
							product.setStock(stock);
							products.add(product);
						}
						else
							outStock = true;
					}
				}
				if (!outStock) {
					try {
						productRepository.save(products);
						cart.setStatus("PROCESSED");
						cartRepository.save(cart);
					} catch (Error e) {
						logger.error("Process Carts service: error saving Products or Cart status "+ e.getMessage());
		        	}
				}
				else {
					logger.info("Process Carts service: Cart "+cart.getId()+" FAILED because a product is out of stock.");
					cart.setStatus("FAILED");
					try {
						cartRepository.save(cart);
					} catch (Error e) {
						logger.error("Process Carts service: error saving Cart status "+ e.getMessage());
		        	}
				}
			}
		}
    }

    public void executeAsynchronously() {

        MyThread myThread = applicationContext.getBean(MyThread.class);
        taskExecutor.execute(myThread);
    }

}
