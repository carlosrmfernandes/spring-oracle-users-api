package com.person.repository;

import com.person.model.UserAddressView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAddressViewRepository extends JpaRepository<UserAddressView, Long> {

    List<UserAddressView> findByUserId(Long userId);

    List<UserAddressView> findByCityIgnoreCase(String city);
}