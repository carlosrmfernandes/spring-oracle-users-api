package com.person.controller;

import com.person.dto.AddressRequest;
import com.person.dto.AddressResponse;
import com.person.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{userId}/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AddressResponse addToUser(@PathVariable Long userId,
                                     @Valid @RequestBody AddressRequest request) {
        return addressService.addToUser(userId, request);
    }
}