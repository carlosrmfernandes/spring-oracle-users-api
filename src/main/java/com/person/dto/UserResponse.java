package com.person.dto;

import com.person.model.User;

import java.time.LocalDateTime;
import java.util.List;

public record UserResponse(
        Long id,
        String name,
        String email,
        LocalDateTime createdAt,
        List<AddressResponse> addresses
) {
    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getCreatedAt(),
                u.getAddresses().stream().map(AddressResponse::from).toList());
    }
}
