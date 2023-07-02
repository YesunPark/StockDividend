package com.zerobase.StockDividend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class StockDividendApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockDividendApplication.class, args);

    }

}
