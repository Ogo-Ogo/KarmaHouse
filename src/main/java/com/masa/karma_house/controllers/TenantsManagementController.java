package com.masa.karma_house.controllers;

import com.masa.karma_house.dto.TenantDto;
import com.masa.karma_house.dto.TenantEditDto;
import com.masa.karma_house.security.JWTUtil;
import com.masa.karma_house.services.IKarmaHouse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping(value = TenantsManagementController.REST_URL)
public class TenantsManagementController {

    static final String REST_URL = "/tenant";

    private IKarmaHouse service;
    private JWTUtil jwtTokenUtil;

    @Autowired
    public void setInjection(IKarmaHouse service, JWTUtil jwtTokenUtil) {
        this.service = service;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @GetMapping(value = "/allTenants/{houseid}")
    public ResponseEntity<?> getAllHouseTenants(@PathVariable long houseid)  {
        final String jwt = jwtTokenUtil.generateToken((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        List<TenantDto> list = service.getAllTenants(houseid);
        if (list.size() != 0) {
            return ResponseEntity.ok().header("X-Token", jwt).body(list);
        }
        else return ResponseEntity.status(HttpStatus.OK).header("X-Token", jwt).body("There are no tenants in this house");
    }

    @PutMapping("/editProfile/{tenantId}")
    public ResponseEntity<?> editProfile(@PathVariable long tenantId,  @RequestBody TenantEditDto tenantDto) {
        final String jwt = jwtTokenUtil.generateToken((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        TenantDto tenant = service.editProfile(tenantId, tenantDto);
        if (tenant != null) {
            return ResponseEntity.ok().header("X-Token", jwt).body(tenant);
        }
        else return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Tenant with such id is not found");
    }

    @PutMapping("/changeRole")
    public ResponseEntity<?> changeRole(@RequestParam long tenantId, @RequestParam long adminId, @RequestParam  String role) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String authenticationName = userDetails.getUsername();
        final String jwt = jwtTokenUtil.generateToken((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        TenantDto tenant = service.changeRole(tenantId, adminId, authenticationName, role);
        if(tenant==null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("X-Token", jwt).body("Tenant with such id is not found");
        }
        else if(tenant.getName()==null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("X-Token", jwt).body("user don't have rights to change role of this tenant");
        }
        else  {
            return ResponseEntity.ok().header("X-Token", jwt).body(tenant);
        }

    }


    @DeleteMapping(value = "/deleteProfile/{tenantId}")
    public ResponseEntity<?> deleteTenantProfile(@PathVariable long tenantId) {
        final String jwt = jwtTokenUtil.generateToken((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String name = userDetails.getUsername();
        TenantDto tenant = service.deleteTenantProfile(tenantId, name);
        if (tenant != null) {
            return ResponseEntity.ok().header("X-Token", jwt).build();
        }
        else return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Tenant with such id is not exists" +
                "or user don't have rights to delete this tenant");
    }

}
