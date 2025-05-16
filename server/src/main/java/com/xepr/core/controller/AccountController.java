package com.xepr.core.controller;

import com.xepr.core.dto.request.CreateUserRequestDTO;
import com.xepr.core.dto.request.UserAuthRequestDTO;
import com.xepr.core.dto.request.UpdateUserRequestDTO;
import com.xepr.core.dto.request.AuthCodeRequestDTO;
import com.xepr.core.dto.response.CreateUserResponseDTO;
import com.xepr.core.dto.response.UserAuthResponseDTO;
import com.xepr.core.dto.response.UpdateUserResponseDTO;
import com.xepr.core.exception.InvalidAttributeException;
import com.xepr.core.exception.DuplicateUserException;
import com.xepr.core.exception.UserNotFoundException;
import com.xepr.core.response.ErrorResponse;
import com.xepr.core.response.SuccessResponse;
import com.xepr.core.response.Response;
import com.xepr.core.response.Status;
import com.xepr.core.service.AccountService;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/account")
@CrossOrigin(origins = "http://localhost:3000")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/register")
    public ResponseEntity<? extends Response> registerUser(@RequestBody CreateUserRequestDTO request, HttpServletRequest hsr) {
        Response response;

        try {
            CreateUserResponseDTO data = this.accountService.createUser(request, hsr);
            response = new SuccessResponse<>(
                    Status.SUCCESS,
                    "User successfully created",
                    data);
        } catch (InvalidAttributeException | DuplicateUserException e) {
            response = new ErrorResponse(Status.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<? extends Response> loginUser(@RequestBody UserAuthRequestDTO request, HttpServletRequest hsr) {
        Response response;

        try {
            UserAuthResponseDTO data = this.accountService.authenticateUser(request, hsr);
            response = new SuccessResponse<>(
                    Status.SUCCESS,
                    "User authentication successful",
                    data);
        } catch (UserNotFoundException | InvalidAttributeException | MessagingException e) {
            response = new ErrorResponse(Status.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<? extends Response> updateUser(@PathVariable long id, @RequestBody UpdateUserRequestDTO request) {
        Response response;

        try {
            UpdateUserResponseDTO data = this.accountService.updateUserInfo(id, request);
            response = new SuccessResponse<>(
                    Status.SUCCESS,
                    "User successfully updated",
                    data);
        } catch (UserNotFoundException | InvalidAttributeException e) {
            response = new ErrorResponse(Status.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<? extends Response> deleteUser(@PathVariable long id) {
        Response response;

        try {
            this.accountService.deleteUser(id);
            response = new SuccessResponse<>(
                    Status.SUCCESS,
                    "User successfully deleted");
        } catch (UserNotFoundException e) {
            response = new ErrorResponse(Status.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/register/auth/{id}")
    public ResponseEntity<? extends Response> sendAuth(@PathVariable long id) {
        Response response;

        try {
            this.accountService.sendAuthCode(id);
            response = new SuccessResponse<>(
                    Status.SUCCESS,
                    "User authentication code successfully sent");
        } catch (MessagingException | UserNotFoundException e) {
            response = new ErrorResponse(Status.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/register/auth/verify")
    public ResponseEntity<? extends Response> verifyUser(@RequestBody AuthCodeRequestDTO request) {
        Response response;

        try {
            this.accountService.verifyAuthCode(request);
            response = new SuccessResponse<>(
                    Status.SUCCESS,
                    "Authentication code verified");
        } catch (UserNotFoundException | AuthenticationFailedException e) {
            response = new ErrorResponse(Status.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}
