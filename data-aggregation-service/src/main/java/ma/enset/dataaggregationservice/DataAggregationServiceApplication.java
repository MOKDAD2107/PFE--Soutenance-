package ma.enset.dataaggregationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class DataAggregationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataAggregationServiceApplication.class, args);
    }

}
