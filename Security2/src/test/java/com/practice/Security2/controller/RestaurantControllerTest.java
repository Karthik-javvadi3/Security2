package com.practice.Security2.controller;

import com.practice.Security2.model.Restaurant;
import com.practice.Security2.service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class RestaurantControllerTest {

    @Mock
    private RestaurantService restaurantService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private RestaurantController restaurantController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterOrUpdateRestaurant() {
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantName("Test Restaurant");

        when(authentication.getName()).thenReturn("testuser");
        when(restaurantService.registerOrUpdateRestaurant(any(Restaurant.class), eq("testuser")))
                .thenReturn("Restaurant registered successfully");

        String response = restaurantController.registerOrUpdateRestaurant(restaurant, authentication);
        assertEquals("Restaurant registered successfully", response);
        verify(restaurantService, times(1)).registerOrUpdateRestaurant(restaurant, "testuser");
    }

    @Test
    public void testGetCategories() {
        when(authentication.getName()).thenReturn("testuser");
        when(restaurantService.getCategories("testuser")).thenReturn(Arrays.asList("Starters", "Main Course"));

        List<String> categories = restaurantController.getCategories(authentication);
        assertEquals(Arrays.asList("Starters", "Main Course"), categories);
        verify(restaurantService, times(1)).getCategories("testuser");
    }

    @Test
    public void testGetMenu() {
        Restaurant.Menu menu = new Restaurant.Menu();
        when(authentication.getName()).thenReturn("testuser");
        when(restaurantService.getMenu("testuser")).thenReturn(menu);

        Restaurant.Menu result = restaurantController.getMenu(authentication);
        assertEquals(menu, result);
        verify(restaurantService, times(1)).getMenu("testuser");
    }

    @Test
    public void testAddMenuItem() {
        Restaurant.Menu.MenuItem menuItem = new Restaurant.Menu.MenuItem();
        menuItem.setItemName("Pizza");

        when(authentication.getName()).thenReturn("testuser");
        when(restaurantService.addMenuItem(any(Restaurant.Menu.MenuItem.class), eq("testuser")))
                .thenReturn("Item added successfully");

        String response = restaurantController.addMenuItem(menuItem, authentication);
        assertEquals("Item added successfully", response);
        verify(restaurantService, times(1)).addMenuItem(menuItem, "testuser");
    }

    @Test
    public void testEditMenuItem() {
        Restaurant.Menu.MenuItem menuItem = new Restaurant.Menu.MenuItem();
        menuItem.setItemName("Burger");

        when(authentication.getName()).thenReturn("testuser");
        when(restaurantService.editMenuItem(eq("itemId"), any(Restaurant.Menu.MenuItem.class), eq("testuser")))
                .thenReturn("Item updated successfully");

        String response = restaurantController.editMenuItem("itemId", menuItem, authentication);
        assertEquals("Item updated successfully", response);
        verify(restaurantService, times(1)).editMenuItem("itemId", menuItem, "testuser");
    }

    @Test
    public void testDeleteMenuItem() {
        when(authentication.getName()).thenReturn("testuser");
        when(restaurantService.deleteMenuItem("itemId", "testuser"))
                .thenReturn("Item deleted successfully");

        String response = restaurantController.deleteMenuItem("itemId", authentication);
        assertEquals("Item deleted successfully", response);
        verify(restaurantService, times(1)).deleteMenuItem("itemId", "testuser");
    }

    @Test
    public void testAddMenuItemImage() {
        MultipartFile image = mock(MultipartFile.class);
        when(authentication.getName()).thenReturn("testuser");
        when(restaurantService.addMenuItemImage(eq("itemId"), eq(image), eq("testuser")))
                .thenReturn("Image uploaded successfully");

        String response = restaurantController.addMenuItemImage("itemId", image, authentication);
        assertEquals("Image uploaded successfully", response);
        verify(restaurantService, times(1)).addMenuItemImage("itemId", image, "testuser");
    }
}
