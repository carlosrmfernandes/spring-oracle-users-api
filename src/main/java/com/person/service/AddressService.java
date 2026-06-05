package com.person.service;

import com.person.dto.AddressRequest;
import com.person.dto.AddressResponse;
import com.person.repository.AddressProcedureRepository;
import com.person.repository.AddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddressService {

    private final AddressProcedureRepository addressProcedureRepository;
    private final AddressRepository addressRepository;

    public AddressService(
            AddressProcedureRepository addressProcedureRepository,
            AddressRepository addressRepository
    ) {
        this.addressProcedureRepository = addressProcedureRepository;
        this.addressRepository = addressRepository;
    }

    @Transactional
    public AddressResponse addToUser(
            Long userId,
            AddressRequest request
    ) {
        Long id = addressProcedureRepository.addAddress(
                userId, request.street(), request.numberAddress(), request.neighborhood(),
                request.city(), request.state(), request.zipCode());

        return addressRepository.findById(id)
                .map(AddressResponse::from)
                .orElseThrow(() -> new IllegalStateException("Endereço não foi salvo"));
    }
}