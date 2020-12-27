package com.masa.karma_house.controllers;

import com.masa.karma_house.dto.*;
import com.masa.karma_house.services.IKarmaHouse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = TaskLogController.REST_URL)
public class TaskLogController {

    static final String REST_URL = "/tasklog";

    private IKarmaHouse service;

    @Autowired
    public void setInjection(IKarmaHouse service) {
        this.service = service;
    }

    @PostMapping("/addTaskLog")
    public ResponseEntity<?> addTaskLog(@RequestBody TaskLogDto taskLogDto) {
        try {
            TaskLogReturnDto taskLog = service.addTaskLog(taskLogDto);
            if (taskLog != null) {
                return new ResponseEntity<>(taskLog, null, HttpStatus.OK);
            }
        } catch (Exception e) {
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cannot add task");
    }

    @GetMapping(value = "/allTaskLogs{id}")
    public ResponseEntity<?> getAllTaskLogs(@PathVariable long id) {
        List<TaskLogReturnDto> list = service.getAllTaskLogs(id);
        if (list.size() != 0) {
            return new ResponseEntity<>(list, null, HttpStatus.OK);
        }
        else return ResponseEntity.status(HttpStatus.OK).body("There are no created Task logs in this house");
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getTaskLog(@PathVariable long id) {
        TaskLogReturnDto task = service.getTaskLog(id);
        if (task != null) {
            return new ResponseEntity<>(task, null, HttpStatus.OK);
        }
        else return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There are no task log with such id");
    }

    @PutMapping("/approveTaskLog{taskLogId}")
    public ResponseEntity<?> approveTaskLog(@PathVariable long taskLogId) {
        TaskLogReturnDto task = service.approveTaskLog(taskLogId);
        if (task != null) {
            return new ResponseEntity<>(task, null, HttpStatus.OK);
        }
        else return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Task with such id is not found");
    }


    @DeleteMapping(value = "/deleteTaskLog/{id}")
    public ResponseEntity<?> deleteTaskLog(@PathVariable long id) {
        TaskLogReturnDto taskLog = service.deleteTaskLog(id);
        if (taskLog != null) {
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
        else return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Task log with such id is not exists");
    }




}
