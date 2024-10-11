package com.kidami.security.controllers;

import com.kidami.security.dto.CourDTO;
import com.kidami.security.dto.CourSaveDTO;
import com.kidami.security.dto.CourUpdateDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/*
@RestController
@RequestMapping( "api/v1/cour")
public class CourController {

    @Autowired
    private CourService courService;
    @PostMapping("/saveCour")
    public String saveCour(@RequestBody CourSaveDTO courSaveDTO){

        String id =courService.addCour(courSaveDTO);
        return id;

    }
    @GetMapping("/getAllCours")
    List<CourDTO> getAllCour(){
        List<CourDTO> allCours = courService.getAllCours();

        return allCours;
    }
    @PutMapping("/updateCour")
    public  String updateUser(@RequestBody CourUpdateDTO courUpdateDTO){
        String id = courService.updateCour(courUpdateDTO);
        return id;
    }
    @DeleteMapping("/deleteCourId/{id}")
    public  String deleteCour(@PathVariable(value="id") Long id){
        boolean deletecour = courService.deleteCour(id);
        return "deleted!!!!!!!!";
    }

}
*/