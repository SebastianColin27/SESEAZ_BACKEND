package com.scspd.backend.user;

import com.scspd.backend.models.Personal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class UserService {
    @Autowired
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

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


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }



    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(String id, User userDetails) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) return null;

        User existingUser = optionalUser.get();
        existingUser.setUsername(userDetails.getUsername());
        existingUser.setFirstName(userDetails.getFirstName());
        existingUser.setLastName(userDetails.getLastName());


        if (userDetails.getPassword() != null && !userDetails.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        existingUser.setRole(userDetails.getRole());
        return userRepository.save(existingUser);
    }


    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
}




