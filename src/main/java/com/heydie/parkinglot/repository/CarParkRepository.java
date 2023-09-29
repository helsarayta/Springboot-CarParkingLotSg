package com.heydie.parkinglot.repository;

import com.heydie.parkinglot.entity.CarPark;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface CarParkRepository extends JpaRepository<CarPark, Integer> {
    @Query(nativeQuery = true,
            value = "SELECT *," +
                    " (6371 * acos(cos(radians(:targetLat)) * cos(radians(cp.x_coord)) * cos(radians(cp.y_coord) - radians(:targetLong)) + sin(radians(:targetLat)) * sin(radians(cp.x_coord)))) AS distance" +
                    " FROM car_park cp" +
                    " ORDER BY distance")
    List<CarPark> findNearestCarPark(@Param("targetLat") Float targetLat, @Param("targetLong") Float targetLong, Pageable pageable);
}
