package com.kidami.security.controllers;

import com.kidami.security.dto.UserDTO;
import com.kidami.security.dto.UserSaveDTO;
import com.kidami.security.dto.UserUpdateDTO;
import com.kidami.security.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping( "api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;
    @PostMapping("/save")
    public  String saveUser(@RequestBody  UserSaveDTO userSaveDTO){
        String id = userService.addUser(userSaveDTO);
        return id;
    }
    @GetMapping("/getAllUsers")
    public List<UserDTO> getAllUser(){
        List<UserDTO> allUsers= userService.getAllUsers();
        return allUsers;
    }
    @PutMapping("/update")
    public  String updateUser(@RequestBody UserUpdateDTO userUpdateDTO){
        String id = userService.updateUser(userUpdateDTO);
        return id;
    }
    @DeleteMapping("/deleteUserId/{id}")
    public  String deleteUser(@PathVariable(value="id") int id){
        boolean deleteuser = userService.deleteUser(id);
        return "deleted!!!!!!!!";
    }

}
