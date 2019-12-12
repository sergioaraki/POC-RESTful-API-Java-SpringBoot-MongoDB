package ar.com.sergioaraki.poc;

import ar.com.sergioaraki.poc.service.AsynchronousService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {

    @Autowired
    private AsynchronousService checkAsyncService;

    @Scheduled(fixedDelay = 60000)
    public void checkTheScedule() {
        checkAsyncService.checkCarts();
    }

}
