package com.practice.Security2.controller;

import com.practice.Security2.model.Restaurant;
import com.practice.Security2.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class MenuControllerTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private MenuController menuController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetRestaurantMenu() {
        String restaurantId = "R1";
        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setRestaurantName("Test Restaurant");

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        Restaurant result = menuController.getRestaurantMenu(restaurantId);
        assertEquals(restaurant, result);
        verify(restaurantRepository, times(1)).findById(restaurantId);
    }
}
