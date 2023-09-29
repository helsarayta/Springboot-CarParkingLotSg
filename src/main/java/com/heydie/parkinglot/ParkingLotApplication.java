package com.heydie.parkinglot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class ParkingLotApplication {
    @Value("${url.base.convert}")
    String urlBaseConvert;

    public static void main(String[] args) {
        SpringApplication.run(ParkingLotApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    @Bean
    public WebClient webClient() {
        return WebClient.builder().baseUrl(urlBaseConvert).build();
    }
}
