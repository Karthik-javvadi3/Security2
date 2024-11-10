package com.practice.Security2.repository;

import com.practice.Security2.model.RestaurantOwner;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface RestaurantOwnerRepository extends MongoRepository<RestaurantOwner, String> {
    Optional<RestaurantOwner> findByUsername(String username);
}
