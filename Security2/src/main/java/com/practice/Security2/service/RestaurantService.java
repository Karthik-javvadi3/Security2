package com.practice.Security2.service;

import com.practice.Security2.model.Restaurant;
import com.practice.Security2.repository.RestaurantOwnerRepository;
import com.practice.Security2.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantOwnerRepository restaurantOwnerRepository;
    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String regionName;

    @Autowired
    public RestaurantService(RestaurantRepository restaurantRepository, RestaurantOwnerRepository restaurantOwnerRepository,
                             @Value("${aws.access-key-id}") String accessKeyId,
                             @Value("${aws.secret-access-key}") String secretAccessKey,
                             @Value("${aws.s3.region}") String regionName) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantOwnerRepository = restaurantOwnerRepository;

        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        this.s3Client = S3Client.builder()
                .region(Region.of(regionName))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }


    public String registerOrUpdateRestaurant(Restaurant restaurantDetails, String username) {
        String restaurantId = restaurantOwnerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Owner not found"))
                .getRestaurantId();

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElse(new Restaurant());

        restaurant.setId(restaurantId);
        restaurant.setRestaurantName(restaurantDetails.getRestaurantName());
        restaurant.setRestaurantAddress(restaurantDetails.getRestaurantAddress());

        restaurantRepository.save(restaurant);

        return "Restaurant details updated successfully!";
    }

    public List<String> getCategories(String username) {
        String restaurantId = restaurantOwnerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Owner not found"))
                .getRestaurantId();

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        return restaurant.getMenu().getCategories().stream()
                .map(Restaurant.Menu.Category::getName)
                .collect(Collectors.toList());
    }

    public Restaurant.Menu getMenu(String username) {
        String restaurantId = restaurantOwnerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Owner not found"))
                .getRestaurantId();

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        return restaurant.getMenu();
    }

    public String addMenuItem(Restaurant.Menu.MenuItem menuItem, String username) {
        String restaurantId = restaurantOwnerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Owner not found"))
                .getRestaurantId();

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        Optional<Restaurant.Menu.Category> categoryOpt = restaurant.getMenu().getCategories().stream()
                .filter(category -> category.getName().equalsIgnoreCase(menuItem.getCategoryName()))
                .findFirst();

        Restaurant.Menu.Category category;
        if (categoryOpt.isPresent()) {
            category = categoryOpt.get();
        } else {
            category = new Restaurant.Menu.Category();
            category.setName(menuItem.getCategoryName());
            category.setItems(new ArrayList<>());
            restaurant.getMenu().getCategories().add(category);
        }

        String nextItemId = generateNextItemId(restaurantId, restaurant);
        menuItem.setItemCode(nextItemId);
        category.getItems().add(menuItem);

        restaurant.setLastUsedItemNumber(restaurant.getLastUsedItemNumber() + 1);
        restaurantRepository.save(restaurant);

        return "Menu item added successfully with ID: " + nextItemId;
    }

    public String editMenuItem(String itemId, Restaurant.Menu.MenuItem updatedItem, String username) {
        String restaurantId = restaurantOwnerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Owner not found"))
                .getRestaurantId();

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        for (Restaurant.Menu.Category category : restaurant.getMenu().getCategories()) {
            for (Restaurant.Menu.MenuItem item : category.getItems()) {
                if (item.getItemCode().equals(itemId)) {
                    item.setItemName(updatedItem.getItemName());
                    item.setPrice(updatedItem.getPrice());
                    item.setDescription(updatedItem.getDescription());
                    item.setVeg(updatedItem.isVeg());
                    item.setAvailable(updatedItem.isAvailable());
                    restaurantRepository.save(restaurant);
                    return "Menu item updated successfully!";
                }
            }
        }

        return "Menu item not found.";
    }

    public String deleteMenuItem(String itemId, String username) {
        String restaurantId = restaurantOwnerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Owner not found"))
                .getRestaurantId();

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        for (Restaurant.Menu.Category category : restaurant.getMenu().getCategories()) {
            Restaurant.Menu.MenuItem itemToRemove = category.getItems().stream()
                    .filter(item -> item.getItemCode().equals(itemId))
                    .findFirst()
                    .orElse(null);
            if (itemToRemove != null) {
                category.getItems().remove(itemToRemove);
                restaurantRepository.save(restaurant);
                return "Menu item deleted successfully.";
            }
        }

        return "Menu item not found.";
    }

    private String generateNextItemId(String restaurantId, Restaurant restaurant) {
        int nextItemNumber = restaurant.getLastUsedItemNumber() + 1;
        return String.format("%s-%02d", restaurantId, nextItemNumber);
    }

    public String addMenuItemImage(String itemId, MultipartFile image, String username) {
        String restaurantId = restaurantOwnerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Owner not found"))
                .getRestaurantId();

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        Optional<Restaurant.Menu.MenuItem> menuItemOpt = restaurant.getMenu().getCategories().stream()
                .flatMap(category -> category.getItems().stream())
                .filter(item -> item.getItemCode().equals(itemId))
                .findFirst();

        if (!menuItemOpt.isPresent()) {
            throw new RuntimeException("Menu item not found");
        }

        Restaurant.Menu.MenuItem menuItem = menuItemOpt.get();
        String imageName = menuItem.getItemName().replaceAll("\\s+", "_") + ".jpg"; // Sanitize filename
        String s3Path = restaurantId + "/" + imageName;

        try {
            // Upload the image to S3
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Path)
                  //  .acl("public-read")
                    .contentType("image/jpeg")  // Set the appropriate content type
                    .build();


            // Transfer the file content to S3
            s3Client.putObject(putRequest, RequestBody.fromInputStream(image.getInputStream(), image.getSize()));

            // Generate the URL using S3Utilities
            S3Utilities s3Utilities = s3Client.utilities();
            URL imageUrl = s3Utilities.getUrl(builder -> builder.bucket(bucketName).key(s3Path));

            // Set the image URL in the menu item and save the restaurant entity
            menuItem.setImageUrl(imageUrl.toString());
            restaurantRepository.save(restaurant);

            return "Image uploaded successfully: " + imageUrl.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error while uploading image to S3", e);
        }
    }


}
