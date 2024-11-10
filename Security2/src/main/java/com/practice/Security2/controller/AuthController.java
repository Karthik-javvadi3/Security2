package com.practice.Security2.controller;

import com.practice.Security2.commons.AuthRequest;
import com.practice.Security2.commons.AuthResponse;
import com.practice.Security2.model.RestaurantOwner;
import com.practice.Security2.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Authentication")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @ApiOperation(value = "Register a new restaurant owner", notes = "Registers a new restaurant owner and assigns a unique restaurant ID.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Restaurant owner registered successfully"),
            @ApiResponse(code = 400, message = "Invalid input provided")
    })
    @PostMapping("/signup")
    public String signup(@RequestBody RestaurantOwner restaurantOwner) {
        return authService.signup(restaurantOwner);
    }

    @ApiOperation(value = "Authenticate user", notes = "Authenticates a user and returns a JWT token if successful.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully authenticated"),
            @ApiResponse(code = 401, message = "Unauthorized")
    })
    @PostMapping("/signin")
    public AuthResponse signin(@RequestBody AuthRequest request) {
        return authService.signin(request);
    }
}
