package com.practice.Security2.controller;

import com.practice.Security2.model.Restaurant;
import com.practice.Security2.repository.RestaurantRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Api(tags = "Menu")
@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final RestaurantRepository restaurantRepository;

    @Autowired
    public MenuController(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @ApiOperation(value = "Get restaurant menu", notes = "Retrieves the menu of the restaurant by restaurant ID.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the menu"),
            @ApiResponse(code = 404, message = "Restaurant not found with the provided ID")
    })
    @GetMapping("/{restaurantId}")
    public Restaurant getRestaurantMenu(@PathVariable String restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Restaurant not found with ID: " + restaurantId
                ));
    }
}
