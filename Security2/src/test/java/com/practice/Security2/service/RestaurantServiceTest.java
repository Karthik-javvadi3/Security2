package com.practice.Security2.service;

import com.practice.Security2.model.Restaurant;
import com.practice.Security2.model.RestaurantOwner;
import com.practice.Security2.repository.RestaurantOwnerRepository;
import com.practice.Security2.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantOwnerRepository restaurantOwnerRepository;

    @Mock
    private S3Client s3Client;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private RestaurantService restaurantService;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String regionName;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterOrUpdateRestaurant() {
        // Arrange
        String username = "owner123";
        Restaurant restaurantDetails = new Restaurant();
        restaurantDetails.setRestaurantName("Test Restaurant");
        restaurantDetails.setRestaurantAddress("123 Street");

        RestaurantOwner owner = new RestaurantOwner();
        owner.setRestaurantId("R1");
        when(restaurantOwnerRepository.findByUsername(username)).thenReturn(Optional.of(owner));
        when(restaurantRepository.findById("R1")).thenReturn(Optional.of(new Restaurant()));

        // Act
        String result = restaurantService.registerOrUpdateRestaurant(restaurantDetails, username);

        // Assert
        assertEquals("Restaurant details updated successfully!", result);
        verify(restaurantRepository, times(1)).save(any(Restaurant.class));
    }

    @Test
    public void testGetCategories() {
        // Arrange
        String username = "owner123";
        RestaurantOwner owner = new RestaurantOwner();
        owner.setRestaurantId("R1");

        List<Restaurant.Menu.Category> categories = new ArrayList<>();
        categories.add(new Restaurant.Menu.Category("Drinks"));
        Restaurant restaurant = new Restaurant();
        restaurant.setMenu(new Restaurant.Menu(categories));

        when(restaurantOwnerRepository.findByUsername(username)).thenReturn(Optional.of(owner));
        when(restaurantRepository.findById("R1")).thenReturn(Optional.of(restaurant));

        // Act
        List<String> result = restaurantService.getCategories(username);

        // Assert
        assertEquals(Collections.singletonList("Drinks"), result);
    }

    @Test
    public void testGetMenu() {
        // Arrange
        String username = "owner123";
        RestaurantOwner owner = new RestaurantOwner();
        owner.setRestaurantId("R1");

        Restaurant.Menu menu = new Restaurant.Menu(new ArrayList<>());
        Restaurant restaurant = new Restaurant();
        restaurant.setMenu(menu);

        when(restaurantOwnerRepository.findByUsername(username)).thenReturn(Optional.of(owner));
        when(restaurantRepository.findById("R1")).thenReturn(Optional.of(restaurant));

        // Act
        Restaurant.Menu result = restaurantService.getMenu(username);

        // Assert
        assertEquals(menu, result);
    }

    @Test
    public void testAddMenuItem() {
        // Arrange
        String username = "owner123";
        RestaurantOwner owner = new RestaurantOwner();
        owner.setRestaurantId("R1");

        Restaurant restaurant = new Restaurant();
        restaurant.setMenu(new Restaurant.Menu(new ArrayList<>()));
        restaurant.setLastUsedItemNumber(0);

        Restaurant.Menu.MenuItem menuItem = new Restaurant.Menu.MenuItem();
        menuItem.setCategoryName("New Category");
        menuItem.setItemName("Burger");

        when(restaurantOwnerRepository.findByUsername(username)).thenReturn(Optional.of(owner));
        when(restaurantRepository.findById("R1")).thenReturn(Optional.of(restaurant));

        // Act
        String result = restaurantService.addMenuItem(menuItem, username);

        // Assert
        assertEquals("Menu item added successfully with ID: R1-01", result);
        verify(restaurantRepository, times(1)).save(any(Restaurant.class));
    }

    @Test
    public void testEditMenuItem() {
        // Arrange
        String username = "owner123";
        RestaurantOwner owner = new RestaurantOwner();
        owner.setRestaurantId("R1");

        Restaurant.Menu.MenuItem item = new Restaurant.Menu.MenuItem();
        item.setItemCode("item123");
        item.setItemName("Pizza");

        Restaurant.Menu.Category category = new Restaurant.Menu.Category();
        category.setItems(Collections.singletonList(item));

        Restaurant restaurant = new Restaurant();
        restaurant.setMenu(new Restaurant.Menu(Collections.singletonList(category)));

        when(restaurantOwnerRepository.findByUsername(username)).thenReturn(Optional.of(owner));
        when(restaurantRepository.findById("R1")).thenReturn(Optional.of(restaurant));

        Restaurant.Menu.MenuItem updatedItem = new Restaurant.Menu.MenuItem();
        updatedItem.setItemName("Updated Pizza");
        updatedItem.setPrice(10.0);

        // Act
        String result = restaurantService.editMenuItem("item123", updatedItem, username);

        // Assert
        assertEquals("Menu item updated successfully!", result);
        verify(restaurantRepository, times(1)).save(restaurant);
    }

    @Test
    public void testDeleteMenuItem() {
        // Arrange
        String username = "owner123";
        RestaurantOwner owner = new RestaurantOwner();
        owner.setRestaurantId("R1");

        Restaurant.Menu.MenuItem item = new Restaurant.Menu.MenuItem();
        item.setItemCode("item123");

        Restaurant.Menu.Category category = new Restaurant.Menu.Category();
        category.setItems(new ArrayList<>(Collections.singletonList(item)));

        Restaurant restaurant = new Restaurant();
        restaurant.setMenu(new Restaurant.Menu(Collections.singletonList(category)));

        when(restaurantOwnerRepository.findByUsername(username)).thenReturn(Optional.of(owner));
        when(restaurantRepository.findById("R1")).thenReturn(Optional.of(restaurant));

        // Act
        String result = restaurantService.deleteMenuItem("item123", username);

        // Assert
        assertEquals("Menu item deleted successfully.", result);
        verify(restaurantRepository, times(1)).save(restaurant);
    }

    @Test
    public void testAddMenuItemImage() throws Exception {
        // Arrange
        String username = "owner123";
        RestaurantOwner owner = new RestaurantOwner();
        owner.setRestaurantId("R1");

        Restaurant.Menu.MenuItem item = new Restaurant.Menu.MenuItem();
        item.setItemCode("item123");
        item.setItemName("Burger");

        Restaurant.Menu.Category category = new Restaurant.Menu.Category();
        category.setItems(new ArrayList<>(Collections.singletonList(item)));

        Restaurant restaurant = new Restaurant();
        restaurant.setMenu(new Restaurant.Menu(Collections.singletonList(category)));

        when(restaurantOwnerRepository.findByUsername(username)).thenReturn(Optional.of(owner));
        when(restaurantRepository.findById("R1")).thenReturn(Optional.of(restaurant));

        // Mock S3 behavior
        S3Utilities s3Utilities = mock(S3Utilities.class);
        when(s3Client.utilities()).thenReturn(s3Utilities);
        URL url = new URL("http://example.com/burger.jpg");

        // Specify GetUrlRequest for the mock
        GetUrlRequest getUrlRequest = GetUrlRequest.builder()
                .bucket(bucketName)
                .key("R1/Burger.jpg") // Adjust the key to match your expectations
                .build();
        when(s3Utilities.getUrl(getUrlRequest)).thenReturn(url);

        // Act
        String result = restaurantService.addMenuItemImage("item123", multipartFile, username);

        // Assert
        assertEquals("Image uploaded successfully: http://example.com/burger.jpg", result);
        verify(restaurantRepository, times(1)).save(restaurant);
    }
}
