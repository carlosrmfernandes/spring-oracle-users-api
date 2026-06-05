package com.person.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressRequest(

        @NotBlank(message = "Rua é obrigatória")
        String street,

        String numberAddress,

        String neighborhood,

        @NotBlank(message = "Cidade é obrigatória")
        String city,

        @NotBlank(message = "Estado é obrigatório")
        String state,

        String zipCode
) {}