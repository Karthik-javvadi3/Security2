package com.practice.Security2.service;

import com.practice.Security2.commons.AuthRequest;
import com.practice.Security2.commons.AuthResponse;
import com.practice.Security2.jwt.JwtUtil;
import com.practice.Security2.model.Restaurant;
import com.practice.Security2.model.RestaurantOwner;
import com.practice.Security2.repository.RestaurantOwnerRepository;
import com.practice.Security2.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

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
    private AuthService authService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSignup() {
        // Given
        RestaurantOwner restaurantOwner = new RestaurantOwner();
        restaurantOwner.setUsername("testuser");
        restaurantOwner.setPassword("testpassword");

        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode("testpassword")).thenReturn(encodedPassword);
        when(sequenceGeneratorService.generateSequence("restaurant_sequence")).thenReturn(1L);

        // When
        String result = authService.signup(restaurantOwner);

        // Then
        verify(passwordEncoder, times(1)).encode("testpassword");
        verify(sequenceGeneratorService, times(1)).generateSequence("restaurant_sequence");
        verify(restaurantOwnerRepository, times(1)).save(any(RestaurantOwner.class));
        verify(restaurantRepository, times(1)).save(any(Restaurant.class));

        assertEquals("Restaurant owner registered successfully with Restaurant ID: R1", result);
        assertEquals("R1", restaurantOwner.getRestaurantId());
    }

    @Test
    public void testSignin() {
        // Given
        AuthRequest request = new AuthRequest();
        request.setUsername("testuser");
        request.setPassword("testpassword");

        String token = "generatedToken";
        when(jwtUtil.generateToken("testuser")).thenReturn(token);

        // Mock authenticationManager.authenticate to simulate successful authentication
        doNothing().when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        // When
        AuthResponse response = authService.signin(request);

        // Then
        verify(authenticationManager, times(1))
                .authenticate(new UsernamePasswordAuthenticationToken("testuser", "testpassword"));
        verify(jwtUtil, times(1)).generateToken("testuser");

        assertEquals(token, response.getToken());
    }
}
