package com.heydie.parkinglot;

import com.heydie.parkinglot.repository.CarParkRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ParkingLotApplicationTests {

    @LocalServerPort
    private int port;

    private String baseUrl = "http://localhost";
    @Autowired
    private CarParkRepository carParkRepository;
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
    @DisplayName("GET NEAREST CAR PARKING LOT API")
    void getNearestAPI() {
        List<Map<String,Object>> result = restTemplate.getForObject(baseUrl+"/?latitude=0.346927&longitude=0.262826&per_page=3&page=0", List.class);
        assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals(3, result.size())
        );

    }

}
