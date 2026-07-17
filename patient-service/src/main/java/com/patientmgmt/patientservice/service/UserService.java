package com.patientmgmt.patientservice.service;

import com.patientmgmt.patientservice.dto.PatientResponseDTO;
import com.patientmgmt.patientservice.model.User;
import com.patientmgmt.patientservice.repository.UserServiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserServiceRepository userServiceRepository;

    public UserService(UserServiceRepository userServiceRepository) {
        this.userServiceRepository = userServiceRepository;
    }

    public List<User> getAllUsers() {
        return userServiceRepository.findAll();
    }

    public User saveUser(User user) {
        return userServiceRepository.save(user);
    }

    public User findUserById(String id) {
        return userServiceRepository.findById(id);
    }

    public void deleteUserById(String id) {
        userServiceRepository.deleteById(id);
    }


}
