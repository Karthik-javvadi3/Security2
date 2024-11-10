package com.practice.Security2.controller;

import com.practice.Security2.commons.AuthRequest;
import com.practice.Security2.commons.AuthResponse;
import com.practice.Security2.model.RestaurantOwner;
import com.practice.Security2.repository.RestaurantOwnerRepository;
import com.practice.Security2.repository.RestaurantRepository;
import com.practice.Security2.service.SequenceGeneratorService;
import com.practice.Security2.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    @Mock
    private RestaurantOwnerRepository restaurantOwnerRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private SequenceGeneratorService sequenceGeneratorService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSignup_Success() {
        RestaurantOwner owner = new RestaurantOwner();
        owner.setUsername("testuser");
        owner.setPassword("password");

        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        when(sequenceGeneratorService.generateSequence("restaurant_sequence")).thenReturn(1L);
        when(restaurantOwnerRepository.save(any(RestaurantOwner.class))).thenReturn(owner);

        String result = authController.signup(owner);

        assertEquals("Restaurant owner registered successfully with Restaurant ID: R1", result);
        verify(restaurantOwnerRepository, times(1)).save(any(RestaurantOwner.class));
        verify(restaurantRepository, times(1)).save(any());
    }

    @Test
    public void testSignin_Success() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null); // Successful authentication does not throw an exception
        when(jwtUtil.generateToken("testuser")).thenReturn("jwt-token");

        AuthResponse response = authController.signin(authRequest);

        assertEquals("jwt-token", response.getToken());
        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, times(1)).generateToken("testuser");
    }

    @Test
    public void testSignin_Failure() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        assertThrows(RuntimeException.class, () -> authController.signin(authRequest));
        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
