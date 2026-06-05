package com.person.controller;

import com.person.dto.UserRequest;
import com.person.dto.UserResponse;
import com.person.model.UserAddressView;
import com.person.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponse create(@Valid @RequestBody UserRequest request) {
        return userService.create(request);
    }

    @PostMapping("/save-procedure")
    public UserResponse createUserProcedure(@Valid @RequestBody UserRequest request) {
        return userService.createUserProcedure(request);
    }

    @GetMapping
    public List<UserResponse> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

    //VC pode ter um DTo ou não
    @GetMapping("/view")
    public List<UserAddressView> listFromView(@RequestParam(required = false) String city) {
        return userService.listFromView(city);
    }
}
