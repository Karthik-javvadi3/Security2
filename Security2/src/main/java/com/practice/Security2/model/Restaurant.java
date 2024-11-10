package com.practice.Security2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "restaurants")
public class Restaurant {
    @Id
    private String id;                 // Unique restaurant ID
    private String restaurantName;     // Name of the restaurant
    private String restaurantAddress;  // Address of the restaurant
    private Menu menu;                 // Menu associated with the restaurant
    private int lastUsedItemNumber;    // Last used item number for menu items

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Menu {
        private List<Category> categories;

        // List of categories (e.g., Starters, Main Course, etc.)

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Category {
            private String name;            // Category name (e.g., Starters, Main Course)
            private List<MenuItem> items;

            public Category(String name) {
                this.name = name;
            }
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class MenuItem {
            private String itemCode;
            private String itemName;          // Name of the item
            private double price;             // Price of the item
            private boolean isVeg;            // Vegetarian flag
            private boolean isAvailable;      // Availability flag
            private String description;       // Description of the item
            private String categoryName;      // Name of the category this item belongs to
            private String imageUrl;          // URL for the item’s image
        }
    }
}
