package com.practice.Security2.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "restaurant_owners")
public class RestaurantOwner {
    @Id
    private String id;            // Unique owner ID
    private String username;      // Username of the owner
    private String password;      // Password for login
    private String restaurantId;  // Reference to the restaurant owned by this owner
}
