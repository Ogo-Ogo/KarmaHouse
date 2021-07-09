package com.masa.karma_house.controllers;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import com.masa.karma_house.dto.AuthRequest;
import com.masa.karma_house.dto.UserDto;
import com.masa.karma_house.dto.UserRegisterDto;
import com.masa.karma_house.security.JWTUtil;
import com.masa.karma_house.services.IKarmaHouse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = AuthenticationController.REST_URL)
public class AuthenticationController {

    static final String REST_URL = "/account";
    Authentication authentication;

    private AuthenticationManager authenticationManager;
    private JWTUtil jwtTokenUtil;
    private IKarmaHouse service;


    @Autowired
    public void setInjection(JWTUtil jwtTokenUtil, IKarmaHouse service, AuthenticationManager authenticationManager) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.service = service;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    @PostMapping("/register")
    ResponseEntity<?> register(@RequestBody UserRegisterDto userRegisterDto, HttpServletResponse response) {
        UserDto user;
        try {
            user = service.addUser(userRegisterDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        AuthRequest request = new AuthRequest();
        System.out.println("request" + request);
        request.setName(userRegisterDto.getName());
        request.setPassword(userRegisterDto.getPassword());
        ResponseEntity<?> res = createAuthenticationToken(request, response);
        System.out.println("result" + res);
        return ResponseEntity.ok().headers(res.getHeaders()).body(user);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<UserDto> createAuthenticationToken(@RequestBody AuthRequest authRequest,
                                                             HttpServletResponse response) {
        System.out.println("authRequest in authenticate" + authRequest.getName() + authRequest.getPassword());
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getName(), authRequest.getPassword()));
            System.out.println("authetication " + authentication);
            System.out.println("I m before catch");
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User login or password not correct", e);
        }

        try {
            System.out.println("I generate jwt");
            final String jwt = jwtTokenUtil.generateToken((UserDetails) authentication.getPrincipal());
            System.out.println("created jwt " + jwt);
            return ResponseEntity.ok().header("X-Token", jwt)
                    .body(service.getUserData(((UserDetails) authentication.getPrincipal()).getUsername(), authRequest.getName()));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @GetMapping("/{login}/info")
    public ResponseEntity<?> getUserInfo(@PathVariable("login") String name, HttpServletResponse response)
            throws IOException {
        System.out.println("authentication " + authentication);
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserDto user = service.getUserData(userDetails.getUsername(), name);
            System.out.println("user in get User " + user);
            System.out.println(response);
            if (user != null) {
                System.out.println("I begin generate");
                System.out.println(authentication);
                System.out.println(authentication.getPrincipal() + " principal");
                final String jwt = jwtTokenUtil.generateToken((UserDetails) authentication.getPrincipal());
                System.out.println("finished generate");
                return ResponseEntity.ok().header("X-Token", jwt).body(user);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.notFound().build();
    }


  /*  @GetMapping("/token/validation")
    ResponseEntity<?> tokenValidation(@RequestHeader("X-Token") String token, HttpServletResponse response)
            throws IOException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String login = userDetails.getUsername();
        UserDto user = service.getUserData((UserDetails) authentication.getPrincipal(), login, response);
        String userName = user.getLogin();
        if (jwtTokenUtil.validateToken(token, userDetails)) {
            System.out.println("I begin generate");
            final String jwt = jwtTokenUtil.generateToken((UserDetails) authentication.getPrincipal());
            System.out.println("finished generate");
            return ResponseEntity.ok().header("X-Token", jwt).body(new AuthResponse(login, userName));
        }
        return ResponseEntity.status(401).build();
    }*/

    @DeleteMapping("/{login}")
    ResponseEntity<?> removeUser(@PathVariable String login, @RequestHeader("X-Token") String token,
                                 HttpServletResponse response) throws IOException {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            UserDto userDto = service.removeUser(login, userDetails.getUsername());
            if (userDto != null) {
                final String jwt = jwtTokenUtil.generateToken((UserDetails) authentication.getPrincipal());
                return ResponseEntity.ok().header("X-Token", jwt).body(userDto);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.noContent().build();
    }


}