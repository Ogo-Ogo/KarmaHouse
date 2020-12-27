package com.masa.karma_house.controllers;

import com.masa.karma_house.dto.*;
import com.masa.karma_house.services.IKarmaHouse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = TaskController.REST_URL)
public class TaskController {

    static final String REST_URL = "/task";

    private IKarmaHouse service;

    @Autowired
    public void setInjection(IKarmaHouse service) {
        this.service = service;
    }

    @PostMapping("/addTask")
    public ResponseEntity<?> addTask(@RequestBody TaskDto taskDto) {
        try {
            TaskReturnDto task = service.addTask(taskDto);
            if (task != null) {
                return new ResponseEntity<>(task, null, HttpStatus.OK);
            }
        } catch (Exception e) {
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Choose correct category for the task" +
                "or task has already created");
    }

    @GetMapping(value = "/allTasks{id}")
    public ResponseEntity<?> getAllTasks(@PathVariable long id) {
        List<TaskReturnDto> list = service.getAllTasks(id);
        if (list.size() != 0) {
            return new ResponseEntity<>(list, null, HttpStatus.OK);
        } else return ResponseEntity.status(HttpStatus.OK).body("There are no created tasks");
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getTask(@PathVariable long id) {
        TaskReturnDto task = service.getTask(id);
        if (task != null) {
            return new ResponseEntity<>(task, null, HttpStatus.OK);
        } else return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There are no task with such id");
    }

    @PutMapping("/editTask{taskId}")
    public ResponseEntity<?> editTask(@PathVariable long taskId, @RequestBody TaskEditDto taskDto) {
        TaskReturnDto task = service.editTask(taskId, taskDto);
        if (task != null) {
            return new ResponseEntity<>(task, null, HttpStatus.OK);
        } else return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Task with such id is not found");
    }

    @DeleteMapping(value = "/deleteTaskLog/{id}")
    public ResponseEntity<?> deleteTaskLog(@PathVariable long id) {
        TaskReturnDto task = service.deleteTask(id);
        if (task != null) {
            return new ResponseEntity<Void>(HttpStatus.OK);
        } else return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Task with such id is not exists");
    }

}
