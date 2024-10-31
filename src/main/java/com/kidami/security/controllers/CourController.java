package com.kidami.security.controllers;

import com.kidami.security.dto.CourDTO;
import com.kidami.security.dto.CourSaveDTO;
import com.kidami.security.dto.CourUpdateDTO;

import com.kidami.security.services.CourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/cour")
public class CourController {

    @Autowired
    private CourService courService;
    @PostMapping("/saveCour")
    @ResponseBody
    public ResponseEntity<CourDTO> saveCour(@RequestBody CourSaveDTO courSaveDTO){

        CourDTO courDTO =courService.addCour(courSaveDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(courDTO);

    }
    @GetMapping("/getAllCours")
    @ResponseBody
    ResponseEntity<List<CourDTO>> getAllCour(){
        List<CourDTO> allCours = courService.getAllCours();

        return ResponseEntity.ok(allCours);
    }
    @PutMapping("/updateCour")
    public  ResponseEntity<CourDTO> updateUser(@RequestBody CourUpdateDTO courUpdateDTO){
        CourDTO courDTO = courService.updateCour(courUpdateDTO);
        return ResponseEntity.ok(courDTO);
    }
    @DeleteMapping("/deleteCourId/{id}")
    public  String deleteCour(@PathVariable(value="id") Integer id){
        boolean deletecour = courService.deleteCour(id);
        return "deleted!!!!!!!!";
    }

}
