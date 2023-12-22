package com.kidami.security.service.Impl;
import com.kidami.security.dto.SignUpRequest;
import com.kidami.security.dto.UserDto;
import com.kidami.security.models.User;
import com.kidami.security.repository.UserRepository;
import com.kidami.security.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDto createUser(SignUpRequest signUpRequest) {
        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(new BCryptPasswordEncoder().encode(signUpRequest.getPassword()));
        user.setFirstname(signUpRequest.getFirstname());
        user.setLastname(signUpRequest.getLastname());
        user.setPhone(signUpRequest.getPhone());
        user.setRole(signUpRequest.getRole());
        User createdUser = userRepository.save(user);
        UserDto userDto = new UserDto();
        userDto.setId(createdUser.getId());
        userDto.setEmail(createdUser.getEmail());
        userDto.setPhone(createdUser.getPhone());
        userDto.setFirstname(createdUser.getFirstname());
        userDto.setLastname(createdUser.getLastname());
        userDto.setRole(createdUser.getRole());
        return userDto;

    }
}
