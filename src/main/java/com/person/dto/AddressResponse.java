package com.person.dto;

import com.person.model.Address;

import java.time.LocalDateTime;

public record AddressResponse(
        Long id, String street, String numberAddress, String neighborhood,
        String city, String state, String zipCode, LocalDateTime createdAt
) {
    public static AddressResponse from(Address a) {
        return new AddressResponse(a.getId(), a.getStreet(), a.getNumberAddress(),
                a.getNeighborhood(), a.getCity(), a.getState(), a.getZipCode(), a.getCreatedAt());
    }
}