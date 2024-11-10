package com.practice.Security2.service;

import com.practice.Security2.model.RestaurantOwner;
import com.practice.Security2.repository.RestaurantOwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final RestaurantOwnerRepository restaurantOwnerRepository;

    @Autowired
    public UserDetailsServiceImpl(RestaurantOwnerRepository restaurantOwnerRepository) {
        this.restaurantOwnerRepository = restaurantOwnerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        RestaurantOwner restaurantOwner = restaurantOwnerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Restaurant owner not found"));
        return new org.springframework.security.core.userdetails.User(restaurantOwner.getUsername(), restaurantOwner.getPassword(), Collections.emptyList());
    }
}
