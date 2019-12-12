package ar.com.sergioaraki.poc;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class MyThread implements Runnable {

    @Override
    public void run() {
    	
    }
}
