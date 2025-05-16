package com.xepr.core.controller;

import com.xepr.core.dto.request.CreateChatRequestDTO;
import com.xepr.core.dto.request.JoinChatRequestDTO;
import com.xepr.core.dto.request.LeaveChatRequestDTO;
import com.xepr.core.dto.request.UpdateChatRequestDTO;
import com.xepr.core.dto.request.SendTextMessageRequestDTO;
import com.xepr.core.dto.request.SendMediaMessageRequestDTO;
import com.xepr.core.dto.request.DeleteTextMessageRequestDTO;
import com.xepr.core.dto.request.DeleteMediaMessageRequestDTO;
import com.xepr.core.dto.request.EditTextMessageRequestDTO;
import com.xepr.core.dto.response.CreateChatResponseDTO;
import com.xepr.core.dto.response.JoinChatResponseDTO;
import com.xepr.core.dto.response.LeaveChatResponseDTO;
import com.xepr.core.dto.response.UpdateChatResponseDTO;
import com.xepr.core.dto.response.SendTextMessageResponseDTO;
import com.xepr.core.dto.response.SendMediaMessageResponseDTO;
import com.xepr.core.dto.response.EditTextMessageResponseDTO;
import com.xepr.core.exception.ChatNotFoundException;
import com.xepr.core.exception.DuplicateChatException;
import com.xepr.core.exception.InvalidAttributeException;
import com.xepr.core.exception.UserNotFoundException;
import com.xepr.core.exception.DuplicateUserException;
import com.xepr.core.exception.InvalidCodeException;
import com.xepr.core.exception.InvalidMessageTypeException;
import com.xepr.core.exception.MessageNotFoundException;
import com.xepr.core.exception.EditTimeLimitedExceededException;
import com.xepr.core.model.Chat;
import com.xepr.core.response.ErrorResponse;
import com.xepr.core.response.SuccessResponse;
import com.xepr.core.response.Response;
import com.xepr.core.response.Status;
import com.xepr.core.service.ChatService;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/create/{id}")
    public ResponseEntity<? extends Response> createChat(@PathVariable long id, @RequestBody CreateChatRequestDTO request) {
        Response response;

        try {
            CreateChatResponseDTO data = this.chatService.createChat(id, request);
            response = new SuccessResponse<>(
                    Status.SUCCESS,
                    "Chat successfully created",
                    data);
        } catch (InvalidAttributeException | DuplicateChatException | UserNotFoundException e) {
            response = new ErrorResponse(Status.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/join/{id}")
    public ResponseEntity<? extends Response> joinChat(@PathVariable long id, @RequestBody JoinChatRequestDTO request) {
        Response response;

        try {
            JoinChatResponseDTO data = this.chatService.joinChat(id, request);
            response = new SuccessResponse<>(
                    Status.SUCCESS,
                    "User successfully joined",
                    data);
        } catch (ChatNotFoundException | UserNotFoundException | DuplicateUserException | InvalidCodeException e) {
            response = new ErrorResponse(Status.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PatchMapping("/leave/{id}")
    public ResponseEntity<? extends Response> leaveChat(@PathVariable long id, @RequestBody LeaveChatRequestDTO request) {
        Response response;

        try {
            LeaveChatResponseDTO data = this.chatService.leaveChat(id, request);
            response = new SuccessResponse<>(
                    Status.SUCCESS,
                    "User successfully removed",
                    data);
        } catch (ChatNotFoundException | UserNotFoundException | MessageNotFoundException e) {
            response = new ErrorResponse(Status.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/message/send/text/{id}")
    public ResponseEntity<? extends Response> sendTextMessage(
            @PathVariable long id, @RequestBody SendTextMessageRequestDTO request) {
        Response response;

        try {
            SendTextMessageResponseDTO data = (SendTextMessageResponseDTO) this.chatService.sendMessage(id, request);
            response = new SuccessResponse<>(
                    Status.SUCCESS,
                    "Message successfully sent",
                    data);
        } catch (InvalidMessageTypeException | ChatNotFoundException | UserNotFoundException |
                 InvalidAttributeException | IOException e) {
            response = new ErrorResponse(Status.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/message/send/media/{id}")
    public ResponseEntity<? extends Response> sendMediaMessage(
            @PathVariable long id, @RequestBody SendMediaMessageRequestDTO request) {
        Response response;

        try {
            SendMediaMessageResponseDTO data = (SendMediaMessageResponseDTO) this.chatService.sendMessage(id, request);
            response = new SuccessResponse<>(
                    Status.SUCCESS,
                    "Message successfully sent",
                    data);
        } catch (InvalidMessageTypeException | ChatNotFoundException | UserNotFoundException |
                 InvalidAttributeException | IOException e) {
            response = new ErrorResponse(Status.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/message/edit/text/{id}")
    public ResponseEntity<? extends Response> editTextMessage(
            @PathVariable long id, @RequestBody EditTextMessageRequestDTO request) {
        Response response;

        try {
            EditTextMessageResponseDTO data = this.chatService.editMessage(id, request);
            response = new SuccessResponse<>(
                    Status.SUCCESS,
                    "Message successfully edited",
                    data);
        } catch (ChatNotFoundException | MessageNotFoundException |
                 EditTimeLimitedExceededException | InvalidAttributeException e) {
            response = new ErrorResponse(Status.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/message/delete/text/{id}")
    public ResponseEntity<? extends Response> deleteTextMessage(
            @PathVariable long id, @RequestBody DeleteTextMessageRequestDTO request) {
        Response response;

        try {
            this.chatService.deleteMessage(id, request);
            response = new SuccessResponse<>(
                    Status.SUCCESS,
                    "Message successfully deleted");
        } catch (ChatNotFoundException | MessageNotFoundException | InvalidMessageTypeException e) {
            response = new ErrorResponse(Status.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/message/delete/media/{id}")
    public ResponseEntity<? extends Response> deleteMediaMessage(
            @PathVariable long id, @RequestBody DeleteMediaMessageRequestDTO request) {
        Response response;

        try {
            this.chatService.deleteMessage(id, request);
            response = new SuccessResponse<>(
                    Status.SUCCESS,
                    "Message successfully deleted");
        } catch (ChatNotFoundException | MessageNotFoundException | InvalidMessageTypeException e) {
            response = new ErrorResponse(Status.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<? extends Response> updateChat(@PathVariable long id, @RequestBody UpdateChatRequestDTO request) {
        Response response;

        try {
            UpdateChatResponseDTO data = this.chatService.updateChatName(id, request);
            response = new SuccessResponse<>(
                    Status.SUCCESS,
                    "Chat successfully updated",
                    data);
        } catch (ChatNotFoundException | InvalidAttributeException e) {
            response = new ErrorResponse(Status.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<? extends Response> deleteChat(@PathVariable long id) {
        Response response;

        try {
            this.chatService.deleteChat(id);
            response = new SuccessResponse<>(
                    Status.SUCCESS,
                    "Chat successfully deleted");
        } catch (ChatNotFoundException | MessageNotFoundException e) {
            response = new ErrorResponse(Status.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/chats/{id}")
    public ResponseEntity<? extends Response> getChats(@PathVariable long id) {
        Response response;

        try {
            List<Chat> data = this.chatService.getUserChats(id);
            response = new SuccessResponse<>(
                    Status.SUCCESS,
                    "Successfully retrieved user chats",
                    data);
        } catch (UserNotFoundException | ChatNotFoundException e) {
            response = new ErrorResponse(Status.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
