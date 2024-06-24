package store.buzzbook.front;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BbfrontserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(BbfrontserverApplication.class, args);
    }

}
