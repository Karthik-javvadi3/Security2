package com.practice.Security2.controller;

import com.practice.Security2.model.Restaurant;
import com.practice.Security2.service.RestaurantService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Api(tags = "Restaurant Management")
@RestController
@RequestMapping("/api/restaurant")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @Autowired
    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @ApiOperation(value = "Register or update restaurant", notes = "Register a new restaurant or update details of an existing one.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully registered or updated the restaurant"),
            @ApiResponse(code = 400, message = "Invalid input data")
    })
    @PostMapping("/registerRestaurant")
    public String registerOrUpdateRestaurant(@RequestBody Restaurant restaurantDetails, Authentication authentication) {
        return restaurantService.registerOrUpdateRestaurant(restaurantDetails, authentication.getName());
    }

    @ApiOperation(value = "Get menu categories", notes = "Retrieve a list of menu categories for the authenticated restaurant.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the categories")
    })
    @GetMapping("/menu/categories")
    public List<String> getCategories(Authentication authentication) {
        return restaurantService.getCategories(authentication.getName());
    }

    @ApiOperation(value = "Get full menu", notes = "Retrieve the entire menu of the authenticated restaurant.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the menu")
    })
    @GetMapping("/menu")
    public Restaurant.Menu getMenu(Authentication authentication) {
        return restaurantService.getMenu(authentication.getName());
    }

    @ApiOperation(value = "Add menu item", notes = "Add a new item to the restaurant's menu.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully added menu item"),
            @ApiResponse(code = 400, message = "Invalid input data")
    })
    @PostMapping("/menu/addItem")
    public String addMenuItem(@RequestBody Restaurant.Menu.MenuItem menuItem, Authentication authentication) {
        return restaurantService.addMenuItem(menuItem, authentication.getName());
    }

    @ApiOperation(value = "Edit menu item", notes = "Edit an existing item in the restaurant's menu.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully edited menu item"),
            @ApiResponse(code = 404, message = "Item not found")
    })
    @PutMapping("/menu/editItem/{itemId}")
    public String editMenuItem(@PathVariable String itemId, @RequestBody Restaurant.Menu.MenuItem updatedItem, Authentication authentication) {
        return restaurantService.editMenuItem(itemId, updatedItem, authentication.getName());
    }

    @ApiOperation(value = "Delete menu item", notes = "Delete an existing item from the restaurant's menu.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully deleted menu item"),
            @ApiResponse(code = 404, message = "Item not found")
    })
    @DeleteMapping("/menu/deleteItem/{itemId}")
    public String deleteMenuItem(@PathVariable String itemId, Authentication authentication) {
        return restaurantService.deleteMenuItem(itemId, authentication.getName());
    }

    @ApiOperation(value = "Add image to menu item", notes = "Upload and attach an image to a specific menu item.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully added image to menu item"),
            @ApiResponse(code = 404, message = "Item not found"),
            @ApiResponse(code = 400, message = "Invalid image file")
    })
    @ApiImplicitParam(name = "image", value = "Image file to upload", required = true, dataType = "file", paramType = "form")
    @PostMapping("/menu/addItemImage/{itemId}")
    public String addMenuItemImage(@PathVariable String itemId, @RequestParam("image") MultipartFile image, Authentication authentication) {
        return restaurantService.addMenuItemImage(itemId, image, authentication.getName());
    }
}
