package com.kidami.security.controllers;

import com.kidami.security.dto.AddRoleRequest;
import com.kidami.security.dto.UserDTO;
import com.kidami.security.dto.UserSaveDTO;
import com.kidami.security.dto.UserUpdateDTO;
import com.kidami.security.models.Role;
import com.kidami.security.models.User;
import com.kidami.security.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/getAllUsers")
    @ResponseBody
    public List<UserDTO> getAllUser(){
        List<UserDTO> allUsers= userService.getAllUsers();
        return allUsers;
    }
    @PutMapping("/update")
    @ResponseBody
    public  String updateUser(@RequestBody UserUpdateDTO userUpdateDTO){
        String id = userService.updateUser(userUpdateDTO);
        return id;
    }
    @DeleteMapping("/deleteUserId/{id}")
    @ResponseBody
    public  String deleteUser(@PathVariable(value="id") int id){
        boolean deleteuser = userService.deleteUser(id);
        return "deleted!!!!!!!!";
    }
    @DeleteMapping("/deleteUser")
    @ResponseBody
    public String deleteUser(@RequestBody Map<String, Integer> request) {
         String deleteUser = userService.deleteUsers(request);
        return deleteUser;
    }
 @PostMapping("/addRole")
 @ResponseBody
 public ResponseEntity<?> addRole(@RequestBody AddRoleRequest request) {
  String email = request.getEmail();

  // Convert strings to Role enums
  Set<Role> roles = request.getRoles().stream()
          .map(roleName -> {
           try {
            return Role.valueOf(roleName.toUpperCase()); // Convert to uppercase for matching
           } catch (IllegalArgumentException e) {
            throw new RuntimeException("Role " + roleName + " is not valid");
           }
          })
          .collect(Collectors.toSet());

  User user = userService.addRolesToUser(email, roles);
  return ResponseEntity.ok(user);
 }

}
