package com.masa.karma_house.controllers;

import com.masa.karma_house.dto.FeedBackDto;
import com.masa.karma_house.dto.FeedBackReturnDto;
import com.masa.karma_house.dto.TaskLogDto;
import com.masa.karma_house.dto.TaskLogReturnDto;
import com.masa.karma_house.services.IKarmaHouse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = FeedBackController.REST_URL)
public class FeedBackController {

    static final String REST_URL = "/comments";

    private IKarmaHouse service;

    @Autowired
    public void setInjection(IKarmaHouse service) {
        this.service = service;
    }

    @PostMapping("/addComment")
    public ResponseEntity<?> addComment(@RequestBody FeedBackDto dto) {
        try {
            TaskLogReturnDto taskLog = service.addComment(dto);
            if (taskLog != null) {
                return new ResponseEntity<>(taskLog, null, HttpStatus.OK);
            }
        } catch (Exception e) {
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cannot add comment");
    }

    @DeleteMapping(value = "/deleteComment/{id}")
    public ResponseEntity<?> deleteTaskLog(@PathVariable long id) {
        TaskLogReturnDto taskLog = service.deleteComment(id);
        if (taskLog != null) {
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
        else return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Comment with such id is not exists");
    }

    @GetMapping(value = "/allComments{taskLogId}")
    public ResponseEntity<?> getAllComments(@PathVariable long taskLogId) {
        List<FeedBackReturnDto> list = service.getAllComments(taskLogId);
        if (list.size() != 0) {
            return new ResponseEntity<>(list, null, HttpStatus.OK);
        }
        else return ResponseEntity.status(HttpStatus.OK).body("There are no created comments in this taskLog");
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getComment(@PathVariable long id) {
        FeedBackReturnDto feedback = service.getComment(id);
        if (feedback != null) {
            return new ResponseEntity<>(feedback, null, HttpStatus.OK);
        }
        else return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There are no task log with such id");
    }


}
