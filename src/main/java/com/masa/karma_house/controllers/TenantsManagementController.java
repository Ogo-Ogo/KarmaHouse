package com.masa.karma_house.controllers;

import com.masa.karma_house.dto.HouseDto;
import com.masa.karma_house.dto.HouseReturnDto;
import com.masa.karma_house.dto.TenantDto;
import com.masa.karma_house.dto.TenantEditDto;
import com.masa.karma_house.services.IKarmaHouse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = TenantsManagementController.REST_URL)
public class TenantsManagementController {

    static final String REST_URL = "/tenant";

    private IKarmaHouse service;

    @Autowired
    public void setInjection(IKarmaHouse service) {
        this.service = service;
    }

    @GetMapping(value = "/allTenants{houseid}")
    public ResponseEntity<?> getAllHouseTenants(@PathVariable long houseid)  {
        List<TenantDto> list = service.getAllTenants(houseid);
        if (list.size() != 0) {
            return new ResponseEntity<>(list, null, HttpStatus.OK);
        }
        else return ResponseEntity.status(HttpStatus.OK).body("There are no tenants in this house");
    }


    @PutMapping("/editProfile{tenantId}")
    public ResponseEntity<?> editProfile(@PathVariable long tenantId, @RequestBody TenantEditDto tenantDto) {
        TenantDto tenant = service.editProfile(tenantId, tenantDto);
        if (tenant != null) {
            return new ResponseEntity<>(tenant, null, HttpStatus.OK);
        }
        else return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Tenant with such id is not found");
    }

    @PutMapping("/changeRole")
    public ResponseEntity<?> changeRole(@RequestParam long tenantId, @RequestParam  String role) {
        TenantDto tenant = service.changeRole(tenantId, role);
        if (tenant != null) {
            return new ResponseEntity<>(tenant, null, HttpStatus.OK);
        }
        else return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Tenant with such id is not found");
    }

    @DeleteMapping(value = "/deleteTenant/{tenantId}")
    public ResponseEntity<?> deleteTenantByAdminHouse(@PathVariable long tenantId) {
        TenantDto tenant = service.deleteTenantByAdminHouse(tenantId);
        if (tenant != null) {
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
        else return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Tenant with such id is not exists");
    }

    @DeleteMapping(value = "/deleteProfile/{tenantId}")
    public ResponseEntity<?> deleteTenantProfile(@PathVariable long tenantId) {
        TenantDto tenant = service.deleteTenantProfile(tenantId);
        if (tenant != null) {
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
        else return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Tenant with such id is not exists");
    }

}
