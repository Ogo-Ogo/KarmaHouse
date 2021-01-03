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
import java.util.List;

@RestController
@RequestMapping(value = TaskController.REST_URL)
public class TaskController {

    static final String REST_URL = "/task";

    private IKarmaHouse service;
    private JWTUtil jwtTokenUtil;

    @Autowired
    public void setInjection(IKarmaHouse service, JWTUtil jwtTokenUtil) {
        this.service = service;
        this.jwtTokenUtil = jwtTokenUtil;
    }


    @PostMapping("/addTask")
    public ResponseEntity<?> addTask(@RequestBody TaskDto taskDto) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String authenticationName = userDetails.getUsername();
        final String jwt = jwtTokenUtil.generateToken((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        TaskReturnDto task;
        try {
            task = service.addTask(taskDto, authenticationName);
            if (task == null) {
                return ResponseEntity.badRequest().header("X-Token", jwt).body("House or tenant with such id not exists");
            }
            if (task.getName() == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).header("X-Token", jwt).body("This tenant cannot add task in this house");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().header("X-Token", jwt).body("Choose correct category for the task");
        }
        return ResponseEntity.status(HttpStatus.OK).header("X-Token", jwt).body(task);
    }

    @GetMapping(value = "/allTasks/{houseId}")
    public ResponseEntity<?> getAllTasks(@PathVariable long houseId) {
        final String jwt = jwtTokenUtil.generateToken((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<TaskReturnDto> list = service.getAllTasks(houseId, userDetails.getUsername());
        if(list==null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("X-Token", jwt).body("This user cannot see tasks in this house");
        }
        if (list.size() != 0) {
            return ResponseEntity.status(HttpStatus.OK).header("X-Token", jwt).body(list);
        } else return ResponseEntity.status(HttpStatus.OK).header("X-Token", jwt).body("There are no created tasks");
    }

    @GetMapping(value = "/{id}/{houseId}")
    public ResponseEntity<?> getTask(@PathVariable long id, @PathVariable long houseId) {
        final String jwt = jwtTokenUtil.generateToken((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TaskReturnDto task = service.getTask(id, houseId, userDetails.getUsername());
        if(task==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There are no task with such id");
        }
        else if(task.getName()==null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("X-Token", jwt).body("This user cannot see tasks in this house");
        }
        else  {
            return ResponseEntity.status(HttpStatus.OK).header("X-Token", jwt).body(task);
        }
    }

    @PutMapping("/editTask/{taskId}")
    public ResponseEntity<?> editTask(@PathVariable long taskId, @RequestBody TaskEditDto taskDto) {
        final String jwt = jwtTokenUtil.generateToken((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TaskReturnDto task = service.editTask(taskId, userDetails.getUsername(), taskDto);
        if (task == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("X-Token", jwt).body("Task with such id is not found");
        }
        else if (task.getName()==null){
             return ResponseEntity.status(HttpStatus.OK).header("X-Token", jwt).body("Current user cannot edit this task");
        }
        else return ResponseEntity.status(HttpStatus.OK).header("X-Token", jwt).body(task);
    }

    @DeleteMapping(value = "/deleteTask/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable long id) {
        final String jwt = jwtTokenUtil.generateToken((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TaskReturnDto task = service.deleteTask(id, userDetails.getUsername());
        if (task == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("X-Token", jwt).body("Task with such id is not found");
        }
        else if (task.getName()==null){
            return ResponseEntity.status(HttpStatus.OK).header("X-Token", jwt).body("Current user cannot delete this task");
        }
        else return ResponseEntity.status(HttpStatus.OK).header("X-Token", jwt).body(task);
    }

}
