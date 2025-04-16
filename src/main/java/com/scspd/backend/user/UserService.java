package com.scspd.backend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO getUserById(String id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            UserDTO userDTO = new UserDTO();
            userDTO.id = user.getId();
            userDTO.username = user.getUsername();
            userDTO.firstName = user.getFirstName();
            userDTO.lastName = user.getLastName();
            return userDTO;
        }
        return null;
    }
}
