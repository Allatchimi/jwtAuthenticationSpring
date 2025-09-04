package com.kidami.security.controllers;

import com.kidami.security.dto.AddRoleRequest;
import com.kidami.security.dto.authDTO.RegisterDTO;
import com.kidami.security.dto.userDTO.UserDTO;
import com.kidami.security.dto.userDTO.UserSaveDTO;
import com.kidami.security.dto.userDTO.UserUpdateDTO;
import com.kidami.security.models.Role;
import com.kidami.security.models.User;
import com.kidami.security.responses.ApiResponse;
import com.kidami.security.services.UserService;
import com.kidami.security.utils.ResponseUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final  UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/addUser")
    public ResponseEntity<ApiResponse<UserDTO>> addUser(@Valid  @RequestBody RegisterDTO registerDTO){

        UserDTO userDTO = userService.registerNewUser(registerDTO);
        return ResponseEntity.ok(ResponseUtil.created("registered user",userDTO,null));
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUser(){
        List<UserDTO> allUsers= userService.getAllUsers();
        return ResponseEntity.ok(ResponseUtil.success("User retrieved successfully",allUsers,null));
    }
    @PutMapping("/update")
    public  ResponseEntity<ApiResponse<UserDTO>> updateUser(@RequestBody UserUpdateDTO userUpdateDTO){
        UserDTO userDTO = userService.updateUser(userUpdateDTO);
        return ResponseEntity.ok(ResponseUtil.success("updated user",userDTO,null));
    }

    @DeleteMapping("/{id}")
    public  ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable(value="id") Long id){
        boolean deleteuser = userService.deleteUser(id);
        if(deleteuser){
            return ResponseEntity.ok(ResponseUtil.success("deleted user",null,null));
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.error("user not Found",null,null));
        }
    }

    @DeleteMapping("/deleteUser")
    public ResponseEntity<ApiResponse<String>> deleteUser(@RequestBody Map<String, Long> request) {
         String deleteUser = userService.deleteUsers(request);
        if(deleteUser != null){
            return ResponseEntity.ok(ResponseUtil.success("deleted user",deleteUser,null));
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseUtil.error("user not Found",null,null));
        }

    }

    @PostMapping("/addRole")
    public ResponseEntity<ApiResponse<?>> addRole(@RequestBody AddRoleRequest request) {

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
        return ResponseEntity.ok(ResponseUtil.success("attribut succes role",user,null));
        }

}
