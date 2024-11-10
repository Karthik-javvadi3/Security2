package com.practice.Security2.service;

import com.practice.Security2.commons.AuthRequest;
import com.practice.Security2.commons.AuthResponse;
import com.practice.Security2.jwt.JwtUtil;
import com.practice.Security2.model.Restaurant;
import com.practice.Security2.model.RestaurantOwner;
import com.practice.Security2.repository.RestaurantOwnerRepository;
import com.practice.Security2.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final RestaurantOwnerRepository restaurantOwnerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RestaurantRepository restaurantRepository;
    private final SequenceGeneratorService sequenceGeneratorService;

    @Autowired
    public AuthService(RestaurantOwnerRepository restaurantOwnerRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                       RestaurantRepository restaurantRepository, SequenceGeneratorService sequenceGeneratorService) {
        this.restaurantOwnerRepository = restaurantOwnerRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.restaurantRepository = restaurantRepository;
        this.sequenceGeneratorService = sequenceGeneratorService;
    }

    public String signup(RestaurantOwner restaurantOwner) {
        // Encrypt the password
        restaurantOwner.setPassword(passwordEncoder.encode(restaurantOwner.getPassword()));

        // Generate the next restaurant ID
        String restaurantId = "R" + sequenceGeneratorService.generateSequence("restaurant_sequence");

        // Set the restaurant ID for the owner
        restaurantOwner.setRestaurantId(restaurantId);

        // Save the owner
        restaurantOwnerRepository.save(restaurantOwner);

        // Also create a Restaurant document for this owner
        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurantRepository.save(restaurant);

        return "Restaurant owner registered successfully with Restaurant ID: " + restaurantId;
    }

    public AuthResponse signin(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        String token = jwtUtil.generateToken(request.getUsername());
        return new AuthResponse(token);
    }
}
