package com.heydie.parkinglot.controller;

import com.heydie.parkinglot.service.CarParkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/carparks")
@RequiredArgsConstructor
@Slf4j
public class CarParkController {

    public final CarParkService carParkService;

    @PostMapping(value = "/uploadCSV", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> saveCarParkData(@RequestParam(value = "files")MultipartFile[] files) throws Exception {
        for (MultipartFile file : files) {
            carParkService.saveCarPark(file);
        }
        return ResponseEntity.ok("Success save data !!");
    }

    @GetMapping("/nearest")
    public ResponseEntity<List<Map<String, Object>>> getNearest(@RequestParam("latitude")  String latitude,
                                                                @RequestParam("longitude") String longitude,
                                                                @RequestParam("per_page") int perPage,
                                                                @RequestParam("page") int page) {

        List<Map<String, Object>> response = new ArrayList<>();
        if(latitude.isEmpty() || latitude.isBlank() || longitude.isEmpty() || longitude.isBlank()) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("message","Please input Latitude and longitude");
            response.add(resp);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(carParkService.getNearestCarPark(latitude, longitude,perPage,page));
    }
}
