package com.kidami.security.services.impl;

import com.kidami.security.dto.UserDTO;
import com.kidami.security.dto.UserSaveDTO;
import com.kidami.security.dto.UserUpdateDTO;
import com.kidami.security.models.User;
import com.kidami.security.repository.UserRepository;
import com.kidami.security.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServicesImpl implements UserServices {
    @Autowired
    private UserRepository userRepository;
    @Override
    public String addUser(UserSaveDTO userSaveDTO) {

        User user = new User(
                userSaveDTO.getFirstname(),
                userSaveDTO.getLastname(),
                userSaveDTO.getEmail(),
                userSaveDTO.getRole()

        );

    userRepository.save(user);

        return user.getFirstname();
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> getUsers = userRepository.findAll();
        List<UserDTO> userDTOList = new ArrayList<>();

       for(User s:getUsers){
           UserDTO userDTO = new UserDTO(
                   s.getId(),
                   s.getFirstname(),
                   s.getLastname(),
                   s.getEmail(),
                   s.getPassword(),
                   s.getRole()
           );
           userDTOList.add(userDTO);
       }
        return userDTOList;
    }

    @Override
    public String updateUser(UserUpdateDTO userUpdateDTO) {

        if(userRepository.existsById(userUpdateDTO.getId())){

            User user  = userRepository.(userUpdateDTO.getId());
            user.setFirstname(userUpdateDTO.getFirstname());
            user.setLastname(userUpdateDTO.getLastname());
            user.setEmail(userUpdateDTO.getEmail());
            user.setRole(userUpdateDTO.getRole());
            user.setPassword(userUpdateDTO.getPassword());

            userRepository.save(user);
        }else {
            System.out.println("User ID not Exists");
        }
        return null;
    }

    @Override
    public boolean deleteUser(int id) {

        if(userRepository.existsById(id)){
            userRepository.deleteById(id);
        }
        else{
            System.out.println("User ID not Exists");
        }
        return false;
    }

}
