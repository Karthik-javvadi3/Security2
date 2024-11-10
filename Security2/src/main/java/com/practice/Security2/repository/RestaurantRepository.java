package com.practice.Security2.repository;

import com.practice.Security2.model.Restaurant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantRepository extends MongoRepository<Restaurant, String> {
    Optional<Restaurant> findByRestaurantName(String restaurantName);

}
