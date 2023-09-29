package com.heydie.parkinglot;

import com.heydie.parkinglot.repository.CarParkRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
class ParkingLotApplicationTests {

    @LocalServerPort
    private int port;

    private String baseUrl = "http://localhost";
    private static RestTemplate restTemplate;

    @BeforeAll
    static void beforeAll() {
        restTemplate = new RestTemplate();
    }

    @BeforeEach
    void setUp() {
        baseUrl = baseUrl.concat(":").concat(port + "").concat("/carparks/nearest");
    }

    @Test
    @DisplayName("Get Nearest ParkingLot API")
    void getNearestAPI() {
        List<Map<String,Object>> result = restTemplate.getForObject(baseUrl+"/?latitude=0.346927&longitude=0.262826&per_page=3&page=0", List.class);
        assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals(3, result.size())
        );

    }

    @Test
    @DisplayName("Error Test API")
    void errorTestGetNearestAPI() {
        try {
            List<Map<String,Object>> result = restTemplate.getForObject(baseUrl+"/?latitude=&longitude=0.262826&per_page=3&page=0", List.class);
        } catch (HttpClientErrorException e) {
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        }



    }

}
