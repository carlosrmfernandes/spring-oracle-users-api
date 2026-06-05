package com.person.service;

import com.person.dto.AddressRequest;
import com.person.dto.UserRequest;
import com.person.dto.UserResponse;
import com.person.model.Address;
import com.person.model.User;
import com.person.model.UserAddressView;
import com.person.repository.AddressProcedureRepository;
import com.person.repository.UserAddressViewRepository;
import com.person.repository.UserProcedureRepository;
import com.person.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserAddressViewRepository userAddressViewRepository;
    private final UserProcedureRepository userProcedureRepository;
    private final AddressProcedureRepository addressProcedureRepository;

    public UserService(
            UserRepository userRepository,
            UserAddressViewRepository userAddressViewRepository,
            UserProcedureRepository userProcedureRepository,
            AddressProcedureRepository addressProcedureRepository
    ) {
        this.userRepository = userRepository;
        this.userAddressViewRepository = userAddressViewRepository;
        this.userProcedureRepository = userProcedureRepository;
        this.addressProcedureRepository = addressProcedureRepository;

    }

    @Transactional
    public UserResponse create(UserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email já cadastrado: " + request.email());
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());

        if (request.addresses() != null) {
            for (AddressRequest a : request.addresses()) {
                Address address = new Address();
                address.setStreet(a.street());
                address.setNumberAddress(a.numberAddress());
                address.setNeighborhood(a.neighborhood());
                address.setCity(a.city());
                address.setState(a.state());
                address.setZipCode(a.zipCode());
                user.addAddress(address);
            }
        }

        User saved = userRepository.save(user);
        return UserResponse.from(saved);
    }

    @Transactional
    public UserResponse createUserProcedure(UserRequest request) {
        Long userId = userProcedureRepository.createUser(request.name(), request.email());

        if (request.addresses() != null) {
            for (AddressRequest a : request.addresses()) {
                addressProcedureRepository.addAddress(userId, a.street(), a.numberAddress(),
                        a.neighborhood(), a.city(), a.state(), a.zipCode());
            }
        }
        return findById(userId);
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + id));
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }

    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("Usuário não encontrado: " + id);
        }
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<UserAddressView> listFromView(String city) {
        return (city == null)
                ? userAddressViewRepository.findAll()
                : userAddressViewRepository.findByCityIgnoreCase(city);
    }
}