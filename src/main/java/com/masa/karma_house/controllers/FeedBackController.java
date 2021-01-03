package com.masa.karma_house.controllers;

import com.masa.karma_house.dto.FeedBackDto;
import com.masa.karma_house.dto.FeedBackReturnDto;
import com.masa.karma_house.dto.TaskLogDto;
import com.masa.karma_house.dto.TaskLogReturnDto;
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
@RequestMapping(value = FeedBackController.REST_URL)
public class FeedBackController {

    static final String REST_URL = "/comments";

    private IKarmaHouse service;
    private JWTUtil jwtTokenUtil;

    @Autowired
    public void setInjection(IKarmaHouse service, JWTUtil jwtTokenUtil) {
        this.service = service;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/addComment")
    public ResponseEntity<?> addComment(@RequestBody FeedBackDto dto) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String authenticationName = userDetails.getUsername();
        final String jwt = jwtTokenUtil.generateToken((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        TaskLogReturnDto taskLog = service.addComment(dto, authenticationName);
        if (taskLog == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("X-Token", jwt).body("No task log with such id");
        } else if (taskLog.getTenantDto() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("X-Token", jwt).body("This tenant cannot add task log for such house");
        } else return ResponseEntity.status(HttpStatus.OK).header("X-Token", jwt).body(taskLog);
    }

    @DeleteMapping(value = "/deleteComment/{id}")
    public ResponseEntity<?> deleteTaskLog(@PathVariable long id) {
        final String jwt = jwtTokenUtil.generateToken((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TaskLogReturnDto taskLog = service.deleteComment(id, userDetails.getUsername());
        if (taskLog == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There are no task log with such id");
        }
        else if (taskLog.getTimestamp() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("X-Token", jwt).body("This user cannot delete comments for this task log");
        }
        else return  ResponseEntity.status(HttpStatus.OK).header("X-Token", jwt).body(taskLog);
    }

    @GetMapping(value = "/allComments/{taskLogId}")
    public ResponseEntity<?> getAllComments(@PathVariable long taskLogId) {
        final String jwt = jwtTokenUtil.generateToken((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<FeedBackReturnDto> list = service.getAllComments(taskLogId, userDetails.getUsername());
        if (list == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("X-Token", jwt).body("There no comments for this task log");
        }
        else if (list.size() == 0) {
            return ResponseEntity.status(HttpStatus.OK).body("This user cannot see comments for this task log");
        }
        else return ResponseEntity.status(HttpStatus.OK).header("X-Token", jwt).body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getComment(@PathVariable long id) {
        final String jwt = jwtTokenUtil.generateToken((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        FeedBackReturnDto feedback = service.getComment(id, userDetails.getUsername());
        if (feedback == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There are no comment with such id");
        }
        else if (feedback.getTimestamp() == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).header("X-Token", jwt).body("This user cannot see comments for this task log");
        }
        else return ResponseEntity.status(HttpStatus.OK).header("X-Token", jwt).body(feedback);
    }


}
