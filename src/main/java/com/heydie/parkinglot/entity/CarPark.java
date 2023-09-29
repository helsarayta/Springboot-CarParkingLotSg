package com.heydie.parkinglot.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarPark {
    @Id
    @GeneratedValue
    private int id;
    @JsonProperty("car_park_no")
    private String carParkNo;
    private String address;
    private Float x_coord;
    private Float y_coord;
    private String car_park_type;
    private String type_of_parking_system;
    private String short_term_parking;
    private String free_parking;
    private String night_parking;
    private Integer car_park_decks;
    private Float gantry_height;
    private String car_park_basement;
}
