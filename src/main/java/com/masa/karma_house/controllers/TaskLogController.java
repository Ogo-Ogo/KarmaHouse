package com.masa.karma_house.controllers;

import com.masa.karma_house.dto.*;
import com.masa.karma_house.security.JWTUtil;
import com.masa.karma_house.services.IKarmaHouse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import sun.java2d.DisposerTarget;
import sun.security.util.BitArray;

import javax.swing.*;
import java.util.List;


@RestController
@RequestMapping(value = TaskLogController.REST_URL)
public class TaskLogController {

    static final String REST_URL = "/tasklog";

    private IKarmaHouse service;
    private JWTUtil jwtTokenUtil;

    @Autowired
    public void setInjection(IKarmaHouse service, JWTUtil jwtTokenUtil) {
        this.service = service;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/addTaskLog")
    public ResponseEntity<?> addTaskLog(@RequestBody TaskLogDto taskLogDto) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String authenticationName = userDetails.getUsername();
        final String jwt = jwtTokenUtil.generateToken((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        TaskLogReturnDto taskLog = service.addTaskLog(taskLogDto, authenticationName);
        if (taskLog == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("X-Token", jwt).body("Cannot add taskLog for such tenant and house");
        } else if (taskLog.getTenantDto() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("X-Token", jwt).body("This tenant cannot add task log for such house");
        }
        else return ResponseEntity.status(HttpStatus.OK).header("X-Token", jwt).body(taskLog);
    }


    @GetMapping(value = "/allTaskLogs/{id}")
    public ResponseEntity<?> getAllTaskLogs(@PathVariable long id) {
        final String jwt = jwtTokenUtil.generateToken((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<TaskLogReturnDto> list = service.getAllTaskLogs(id, userDetails.getUsername());
        if (list == null) {
            return ResponseEntity.status(HttpStatus.OK).body("There are no created Task logs in this house");
        } else if (list.size() == 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("X-Token", jwt).body("This user cannot see task logs in this house");

        } else return ResponseEntity.status(HttpStatus.OK).header("X-Token", jwt).body(list);
    }

    @GetMapping(value = "/{id}/{houseId}")
    public ResponseEntity<?> getTaskLog(@PathVariable long id, @PathVariable long houseId) {
        final String jwt = jwtTokenUtil.generateToken((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TaskLogReturnDto taskLog = service.getTaskLog(id, houseId, userDetails.getUsername());
        if (taskLog == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There are no task log with such id");
        } else if (taskLog.getTimestamp() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("X-Token", jwt).body("This user cannot see task logs in this house");
        } else return ResponseEntity.status(HttpStatus.OK).header("X-Token", jwt).body(taskLog);
    }

    @PutMapping("/approveTaskLog/{taskLogId}")
    public ResponseEntity<?> approveTaskLog(@PathVariable long taskLogId) {
        final String jwt = jwtTokenUtil.generateToken((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TaskLogReturnDto taskLog = service.approveTaskLog(taskLogId, userDetails.getUsername());
        if (taskLog == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There are no task log with such id");
        } else if (taskLog.getTimestamp() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("X-Token", jwt).body("This user cannot approve task logs in this house");
        } else return ResponseEntity.status(HttpStatus.OK).header("X-Token", jwt).body(taskLog);
    }


    @DeleteMapping(value = "/deleteTaskLog/{id}")
    public ResponseEntity<?> deleteTaskLog(@PathVariable long id) {
        final String jwt = jwtTokenUtil.generateToken((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TaskLogReturnDto taskLog = service.deleteTaskLog(id, userDetails.getUsername());
        if (taskLog == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There are no task log with such id");
        }
        else if (taskLog.getTimestamp() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("X-Token", jwt).body("This user cannot delete task logs in this house");
        }
        else return  ResponseEntity.status(HttpStatus.OK).header("X-Token", jwt).body(taskLog);
    }


}
