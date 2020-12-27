package com.masa.karma_house.controllers;
import com.masa.karma_house.dto.HouseDto;
import com.masa.karma_house.dto.HouseReturnDto;
import com.masa.karma_house.dto.TaskLogReturnDto;
import com.masa.karma_house.services.IKarmaHouse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = HouseController.REST_URL)
public class HouseController {

    static final String REST_URL = "/house";

    private IKarmaHouse service;

    @Autowired
    public void setInjection(IKarmaHouse service) {
        this.service = service;
    }
    
    @PostMapping("/addHouse")
    public ResponseEntity<?> addHouse(@RequestBody HouseDto houseDto) {
        HouseReturnDto house = service.addHouse(houseDto);
        if (house != null) {
            return new ResponseEntity<>(house, null, HttpStatus.OK);
        }
        else return ResponseEntity.status(HttpStatus.FORBIDDEN).body("House with such name is already exists");
    }

    @PutMapping("/editHouse{houseId}")
    public ResponseEntity<?> editHouse(@PathVariable long houseId, @RequestBody HouseDto houseDto) {
        HouseReturnDto house = service.editHouse(houseId, houseDto);
        if (house != null) {
            return new ResponseEntity<>(house, null, HttpStatus.OK);
        }
        else return ResponseEntity.status(HttpStatus.FORBIDDEN).body("House with such id is not found");
    }

    @DeleteMapping(value = "/deleteHouse/{houseId}")
    public ResponseEntity<?> deleteHouse(@PathVariable long houseId) {
        HouseReturnDto house = service.deleteHouse(houseId);
        if (house != null) {
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
        else return ResponseEntity.status(HttpStatus.FORBIDDEN).body("House with such id is not exists");
    }

    @GetMapping(value = "/karma{houseId}")
    public ResponseEntity<?> getHouseKarma(@PathVariable long houseId) {
        long karma = service.getHouseKarma(houseId);
        if (karma != 0) {
            return new ResponseEntity<>(karma, null, HttpStatus.OK);
        }
        else return ResponseEntity.status(HttpStatus.OK).body("No karma in this house yet");
    }

    @GetMapping(value = "/houses")
    public ResponseEntity<?> getAllHouses() {
        List<HouseReturnDto> houses = service.getAllHouses();
        if (houses != null) {
            return new ResponseEntity<>(houses, null, HttpStatus.OK);
        }
        else return ResponseEntity.status(HttpStatus.OK).body("No houses were created yet");
    }

    @GetMapping(value = "/house{houseId}")
    public ResponseEntity<?> getHouse(@PathVariable long houseId) {
        HouseReturnDto house = service.getHouse(houseId);
        if (house != null) {
            return new ResponseEntity<>(house, null, HttpStatus.OK);
        }
        else return ResponseEntity.status(HttpStatus.OK).body("No karma in this house yet");
    }



}
