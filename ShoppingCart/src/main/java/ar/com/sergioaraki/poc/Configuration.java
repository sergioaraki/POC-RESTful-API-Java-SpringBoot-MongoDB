package ar.com.sergioaraki.poc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class Configuration {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Configuration.class, args);
	}

}