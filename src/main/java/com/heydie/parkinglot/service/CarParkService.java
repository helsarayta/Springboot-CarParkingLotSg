package com.heydie.parkinglot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heydie.parkinglot.entity.CarPark;
import com.heydie.parkinglot.repository.CarParkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CarParkService {
    @Value("${url.carpark-availability}")
    String urlCarParkAvailability;

    private final CarParkRepository carParkRepository;
    private final RestTemplate restTemplate;
    private final WebClient webClient;
    @Async
    public CompletableFuture<List<CarPark>> saveCarPark(MultipartFile file) throws Exception {
        List<CarPark> carParkList = parseFromCSV(file).get();
        return CompletableFuture.completedFuture(carParkRepository.saveAll(carParkList));
    }

    @Async
    public CompletableFuture<List<CarPark>> parseFromCSV(MultipartFile file) throws Exception {
        final List<CarPark> listCarPark = new ArrayList<>();
        try {
            try (final BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line;
                boolean skipHeader = true;
                while ((line = br.readLine()) != null) {
                    if (skipHeader) {
                        skipHeader = false;
                        continue; // Skip the header line
                    }
                    Pattern pattern = Pattern.compile(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                    String[] csvData = pattern.split(line);

                    final CarPark cp = new CarPark();
                    cp.setCarParkNo(csvData[0]);
                    cp.setAddress(csvData[1].replace("\"", "").replace("-",""));
                    String convertGeo = convert("/commonapi/convert/3857to4326?Y=" + csvData[3] + "&X=" + csvData[2]);
                    Map<String, Object> req = new ObjectMapper().readValue(convertGeo, Map.class);
                    log.info("CLIENT ==> {}", req.get("latitude")+" "+req.get("longitude"));
                    cp.setX_coord(Float.valueOf(req.get("latitude").toString()));
                    cp.setY_coord(Float.valueOf(req.get("longitude").toString()));
                    cp.setCar_park_type(csvData[4]);
                    cp.setType_of_parking_system(csvData[5]);
                    cp.setShort_term_parking(csvData[6]);
                    cp.setFree_parking(csvData[7]);
                    cp.setNight_parking(csvData[8]);
                    cp.setCar_park_decks(Integer.valueOf(csvData[9]));
                    cp.setGantry_height(Float.valueOf(csvData[10]));
                    cp.setCar_park_basement(csvData[11]);

                    listCarPark.add(cp);
                }
                return CompletableFuture.completedFuture(listCarPark);
            }
        }catch (final IOException e) {
            log.error("Failed to parse CSV file {}", e);
            throw new Exception("Failed to parse CSV file {}", e);
        }
    }

    private String convert(String Uri) {
        return webClient.get().uri(Uri)
                .retrieve()
                .bodyToMono(String.class).block();
    }

    private Map<String, Object> getAvalaibleCarLot(String no) {
        Map<String, Object> responseEntity = restTemplate.getForEntity(urlCarParkAvailability, Map.class).getBody();
        List<Map<String, Object>> items = (List<Map<String, Object>>) responseEntity.get("items");
        List<Map<String, Object>> listCarparkData = (List<Map<String, Object>>) items.get(0).get("carpark_data");

        Map<String, Object> resp = new HashMap<>();
        for (Map<String, Object> carparkData : listCarparkData) {
            if(carparkData.get("carpark_number").toString().equalsIgnoreCase(no)) {
                resp.put("carpark_number", carparkData.get("carpark_number"));
                List<Map<String, Object>> listCarparkInfo = (List<Map<String, Object>>) carparkData.get("carpark_info");
                for (Map<String, Object> carParkInfo : listCarparkInfo) {
                    resp.put("total_lots", carParkInfo.get("total_lots"));
                    resp.put("lots_available", carParkInfo.get("lots_available"));
                }
            }
        }
        return resp;
    }

    public List<Map<String, Object>> getNearestCarPark(String latitude, String longitude, int size, int page) {
        List<Map<String, Object>> response = new ArrayList<>();

        Pageable pageable = PageRequest.of(page, size);
        List<CarPark> nearestCarPark = carParkRepository.findNearestCarPark(Float.valueOf(latitude), Float.valueOf(longitude), pageable);


        for (CarPark carPark : nearestCarPark) {
            Map<String, Object> resp = new HashMap<>();
            Map<String, Object> avalaibleCarLot = getAvalaibleCarLot(carPark.getCarParkNo());
            if(avalaibleCarLot.isEmpty() || Integer.valueOf(avalaibleCarLot.get("lots_available").toString()) == 0) {
               continue;
            }

            resp.put("address",carPark.getAddress());
            resp.put("latitude", carPark.getX_coord());
            resp.put("longitude", carPark.getY_coord());
            resp.put("total_lots", Integer.valueOf(avalaibleCarLot.get("total_lots").toString()));
            resp.put("avalaible_lots", Integer.valueOf(avalaibleCarLot.get("lots_available").toString()));
            response.add(resp);
        }
        return response;
    }
}
