package com.masa.karma_house.controllers;

import com.masa.karma_house.dto.*;
import com.masa.karma_house.entities.Tenant;
import com.masa.karma_house.services.IKarmaHouse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = ApplicationController.REST_URL)
public class ApplicationController {

    static final String REST_URL = "/application";

    private IKarmaHouse service;

    @Autowired
    public void setInjection(IKarmaHouse service) {
        this.service = service;
    }

    @PostMapping("/apply")
    public ResponseEntity<?> applyToAdmin(@RequestBody ApplicationDto dto) {
        ApplicationReturnDto application = service.applyToBecomeMemberOfHouse(dto);
        if (application != null) {
            return new ResponseEntity<>(application, null, HttpStatus.OK);
        }
        else return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User with such name has already applied," +
                " previous application hasn't been processed yet");
    }


    @GetMapping(value = "/allApplications{houseId}")
    public ResponseEntity<?> getAllApplications(@PathVariable long houseId) {
        List<ApplicationReturnDto> list = service.getAllApplications(houseId);
        if (list.size() != 0) {
            return new ResponseEntity<>(list, null, HttpStatus.OK);
        }
        else return ResponseEntity.status(HttpStatus.OK).body("There are no sended applications");
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getApplication(@PathVariable long id) {
        ApplicationReturnDto application = service.getApplication(id);
        if (application != null) {
            return new ResponseEntity<>(application, null, HttpStatus.OK);
        }
        else return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There are no application with such id");
    }

    @PostMapping("/addTenant")
    public ResponseEntity<?> addTenant(@RequestParam long applicationId, @RequestParam  String role) {
        TenantDto tenant = service.addTenant(applicationId, role);
        if (tenant != null) {
            return new ResponseEntity<>(tenant, null, HttpStatus.OK);
        }
        else return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This user has already been added to tenants of this house");
    }

}
