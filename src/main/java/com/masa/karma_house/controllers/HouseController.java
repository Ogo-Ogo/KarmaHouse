package com.masa.karma_house.controllers;

import com.masa.karma_house.dto.HouseChangeDto;
import com.masa.karma_house.dto.HouseDto;
import com.masa.karma_house.dto.HouseReturnDto;
import com.masa.karma_house.security.JWTUtil;
import com.masa.karma_house.services.IKarmaHouse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = HouseController.REST_URL)
public class HouseController {

    static final String REST_URL = "/house";

    private IKarmaHouse service;
    private JWTUtil jwtTokenUtil;


    @Autowired
    public void setInjection(JWTUtil jwtTokenUtil, IKarmaHouse service) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.service = service;
    }

    @PostMapping("/addHouse")
    public ResponseEntity<?> addHouse(@RequestBody HouseDto houseDto) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final String jwt = jwtTokenUtil.generateToken((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        HouseReturnDto house = service.addHouse(houseDto, userDetails.getUsername());
        if (house != null) {
            return ResponseEntity.ok().header("X-Token", jwt).body(house);
        } else
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("X-Token", jwt).body("House with such name is already exists");
    }

    @PutMapping("/editHouse/{houseId}")
    public ResponseEntity<?> editHouse(@PathVariable long houseId, @RequestBody HouseChangeDto houseChangeDto, ServletRequest req) {
        HttpServletRequest request = (HttpServletRequest) req;
        String userName = (String) request.getAttribute("name");
        final String jwt = jwtTokenUtil.generateToken((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        try {
            HouseReturnDto house = service.editHouse(houseId, houseChangeDto, userName);
            if (house != null) {
                return ResponseEntity.ok().header("X-Token", jwt).body(house);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().header(jwt).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).header("X-Token", jwt).body("House with such id is not found");
    }

    @DeleteMapping(value = "/deleteHouse/{houseId}")
    public ResponseEntity<?> deleteHouse(@PathVariable long houseId, ServletRequest req) {
        HttpServletRequest request = (HttpServletRequest) req;
        String userName = (String) request.getAttribute("name");
        final String jwt = jwtTokenUtil.generateToken((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        try {
            HouseReturnDto house = service.deleteHouse(houseId, userName);
            if (house != null) {
                return ResponseEntity.status(HttpStatus.OK).header("X-Token", jwt).build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().header(jwt).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).header("X-Token", jwt).body("House with such id is not exists");
    }

    @GetMapping(value = "/karma/{houseId}")
    public ResponseEntity<?> getHouseKarma(@PathVariable long houseId) {
        final String jwt = jwtTokenUtil.generateToken((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        long karma = service.getHouseKarma(houseId);
        if (karma != 0) {
            return ResponseEntity.ok().header("X-Token", jwt).body(karma);
        } else return ResponseEntity.status(HttpStatus.OK).header("X-Token", jwt).body("No karma in this house yet");
    }

    @GetMapping(value = "/houses")
    public ResponseEntity<?> getAllHouses() {
        final String jwt = jwtTokenUtil.generateToken((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        List<HouseReturnDto> houses = service.getAllHouses();
        if (houses != null) {
            return ResponseEntity.ok().header("X-Token", jwt).body(houses);
        } else return ResponseEntity.status(HttpStatus.OK).header("X-Token", jwt).body("No houses were created yet");
    }

    @GetMapping(value = "/house/{houseId}")
    public ResponseEntity<?> getHouse(@PathVariable long houseId) {
        final String jwt = jwtTokenUtil.generateToken((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        HouseReturnDto house = service.getHouse(houseId);
        if (house != null) {
            return ResponseEntity.ok().header("X-Token", jwt).body(house);
        } else return ResponseEntity.status(HttpStatus.OK).header("X-Token", jwt).body("No house with such id");
    }


}
