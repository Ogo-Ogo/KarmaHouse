package com.masa.karma_house.controllers;

import com.masa.karma_house.dto.*;
import com.masa.karma_house.entities.Tenant;
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
@RequestMapping(value = ApplicationController.REST_URL)
public class ApplicationController {

    static final String REST_URL = "/application";

    private IKarmaHouse service;
    private JWTUtil jwtTokenUtil;

    @Autowired
    public void setInjection(JWTUtil jwtTokenUtil, IKarmaHouse service) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.service = service;
    }

    @PostMapping("/apply")
    public ResponseEntity<?> applyToAdmin(@RequestParam long userId, @RequestParam long houseId) {
        final String jwt = jwtTokenUtil.generateToken((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        ApplicationReturnDto application = service.applyToBecomeMemberOfHouse(userId, houseId);
        if (application != null) {
            return ResponseEntity.ok().header("X-Token", jwt).body(application);
        }
        else return ResponseEntity.status(HttpStatus.FORBIDDEN).header("X-Token", jwt).body("User with such name has already applied," +
                " previous application hasn't been processed yet");
    }


    @GetMapping(value = "/allApplications")
    public ResponseEntity<?> getAllApplications(@RequestParam long tenantId, @RequestParam long houseId) {
        final String jwt = jwtTokenUtil.generateToken((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        List<ApplicationReturnDto> list = service.getAllApplications(tenantId, houseId);
        if(list==null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("X-Token", jwt).body("This tenant has no rights to see applications of this house");
        }
        if (list.size() != 0) {
            return ResponseEntity.ok().header("X-Token", jwt).body(list);
        }
        else return ResponseEntity.status(HttpStatus.OK).header("X-Token", jwt).body("There are no sended applications");
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getApplication(@RequestParam long tenantId, @RequestParam long houseId, @PathVariable long id) {
        final String jwt = jwtTokenUtil.generateToken((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        ApplicationReturnDto application = service.getApplication(tenantId, houseId, id);
        if (application == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).header("X-Token", jwt).body("There are no application with such id");
        }
        else if (application.getName() == null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("X-Token", jwt).body("This tenant has no rights to see applications of this house");
        }
        else{
            return ResponseEntity.ok().header("X-Token", jwt).body(application);
        }
    }

    @PostMapping("/addTenant")
    public ResponseEntity<?> addTenant(@RequestParam long tenantId, @RequestParam long houseId, @RequestParam long applicationId, @RequestParam  String role) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String name = userDetails.getUsername();
        final String jwt = jwtTokenUtil.generateToken((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        TenantDto tenant = service.addTenant(tenantId, houseId, applicationId, name, role);
        if(tenant==null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("X-Token", jwt).body("This user has already been added to tenants of this house or application with such id didn't exist");
        }
        else if(tenant.getName()==null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("X-Token", jwt).body("No rights to add this tenant to this house");
        }
        else  {
            return ResponseEntity.ok().header("X-Token", jwt).body(tenant);
        }
    }

}
