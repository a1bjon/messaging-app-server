package com.xepr.core.service;

import com.xepr.core.dto.request.CreateUserRequestDTO;
import com.xepr.core.dto.request.UserAuthRequestDTO;
import com.xepr.core.dto.request.UpdateUserRequestDTO;
import com.xepr.core.dto.request.AuthCodeRequestDTO;
import com.xepr.core.dto.response.CreateUserResponseDTO;
import com.xepr.core.dto.response.UserAuthResponseDTO;
import com.xepr.core.dto.response.UpdateUserResponseDTO;
import com.xepr.core.exception.DuplicateUserException;
import com.xepr.core.exception.InvalidAttributeException;
import com.xepr.core.exception.UserNotFoundException;
import com.xepr.core.model.Chat;
import com.xepr.core.model.User;
import com.xepr.core.repository.AccountRepository;
import com.xepr.core.repository.ChatRepository;
import com.xepr.core.util.SecurityUtil;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.modelmapper.ModelMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.AuthenticationFailedException;
import jakarta.transaction.Transactional;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    private final ChatRepository chatRepository;

    private final EmailService emailService;

    private final BCryptPasswordEncoder bcryptEncoder;

    private final ModelMapper mapper;

    @Autowired
    public AccountService(
            AccountRepository accountRepository,
            ChatRepository chatRepository,
            EmailService emailService,
            BCryptPasswordEncoder bcryptEncoder,
            ModelMapper mapper) {
        this.accountRepository = accountRepository;
        this.chatRepository = chatRepository;
        this.bcryptEncoder = bcryptEncoder;
        this.emailService = emailService;
        this.mapper = mapper;

        this.removeUnverifiedUsers();
    }

    public CreateUserResponseDTO createUser(CreateUserRequestDTO request, HttpServletRequest hsr)
            throws InvalidAttributeException, DuplicateUserException {

        if (request.getEmail().isEmpty() || request.getPassword().isEmpty() || request.getConfirmPassword().isEmpty()) {
            throw new InvalidAttributeException("User missing email or password attribute");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new InvalidAttributeException("Passwords do not match");
        }

        if (!EmailService.isValidEmail(request.getEmail())) {
            throw new InvalidAttributeException("User email is invalid");
        }

        if (this.accountRepository.findUserEmail(request.getEmail()) != null) {
            throw new DuplicateUserException("User already exists");
        }

        User userToCreate = new User();
        userToCreate.setEmail(request.getEmail());
        userToCreate.setPassword(this.bcryptEncoder.encode(request.getPassword()));
        userToCreate.getTrustedIps().add(SecurityUtil.getClientIpAddress(hsr));

        User createdUser = this.accountRepository.save(userToCreate);
        return this.mapper.map(createdUser, CreateUserResponseDTO.class);
    }

    public UserAuthResponseDTO authenticateUser(UserAuthRequestDTO request, HttpServletRequest hsr)
            throws UserNotFoundException, InvalidAttributeException, MessagingException {

        User userToAuth = this.accountRepository.findUserByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!this.bcryptEncoder.matches(request.getPassword(), userToAuth.getPassword())) {
            throw new InvalidAttributeException("User password is incorrect");
        }

        if (userToAuth.getAuthenticated() == null) {
            throw new AuthenticationFailedException("User email has not been verified");
        }

        String ip = SecurityUtil.getClientIpAddress(hsr);
        if (!userToAuth.getTrustedIps().contains(ip)) {
            this.emailService.setRecipient(userToAuth.getEmail());
            this.emailService.setSubject("New Login");
            this.emailService.setMessageHeading("A login from a new device was detected");
            this.emailService.setMessage("IP: " + ip);
            this.emailService.sendEmail();

            userToAuth.getTrustedIps().add(ip);
            this.accountRepository.save(userToAuth);
        }

        return this.mapper.map(userToAuth, UserAuthResponseDTO.class);
    }

    public UpdateUserResponseDTO updateUserInfo(long id, UpdateUserRequestDTO request)
            throws UserNotFoundException, InvalidAttributeException {

        User userToUpdate = this.accountRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Cannot update email of non-existent user"));

        if (request.getUpdatedEmail().isEmpty() || request.getUpdatedPassword().isEmpty()) {
            throw new InvalidAttributeException("New user email or password attribute is empty");
        }

        if (request.getUpdatedEmail().equals(userToUpdate.getEmail())
                && this.bcryptEncoder.matches(request.getUpdatedPassword(), userToUpdate.getPassword())) {
            throw new InvalidAttributeException("New user details identical to current details");
        }

        userToUpdate.setEmail(request.getUpdatedEmail());
        userToUpdate.setPassword(this.bcryptEncoder.encode(request.getUpdatedPassword()));
        User updatedUser = this.accountRepository.save(userToUpdate);

        return this.mapper.map(updatedUser, UpdateUserResponseDTO.class);
    }

    @Transactional
    public void deleteUser(long id) throws UserNotFoundException {
        User userToDel = this.accountRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Cannot remove non-existent user"));

        List<Chat> chatsToDelFrom = this.chatRepository.findAllById(userToDel.getChats());
        chatsToDelFrom.forEach((chat) -> chat.getUsers().remove(userToDel.getId()));

        Iterator<Chat> chatIterator = chatsToDelFrom.iterator();
        while (chatIterator.hasNext()) {
            Chat currChat = chatIterator.next();

            if (currChat.getUsers().isEmpty()) {
                chatIterator.remove();
                this.chatRepository.delete(currChat);
            }
        }

        this.chatRepository.saveAll(chatsToDelFrom);
        this.accountRepository.delete(userToDel);
    }

    public void sendAuthCode(long id) throws MessagingException, UserNotFoundException {
        User user = this.accountRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getAuthenticated() != null) {
            if (user.getAuthenticated()) {
                throw new AuthenticationFailedException("User already authenticated");
            }
        }

        if (user.getAuthenticated() == null && user.getAuthenticationCode() == null && user.getEmail() != null) {
            user.setAuthenticationCode(SecurityUtil.generateAuthCode());

            this.emailService.setRecipient(user.getEmail());
            this.emailService.setSubject("Verification Code");
            this.emailService.setMessageHeading("This is your verification code");
            this.emailService.setMessage("Code: " + user.getAuthenticationCode());
            this.emailService.sendEmail();
        }

        this.accountRepository.save(user);
    }

    public void verifyAuthCode(AuthCodeRequestDTO request) throws UserNotFoundException, AuthenticationFailedException {
        User userToAuth = this.accountRepository.findById(request.getId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (userToAuth.getAuthenticated() != null) {
            throw new AuthenticationFailedException("Authentication code already verified");
        }

        if (userToAuth.getAuthenticationCode() == null) {
            throw new AuthenticationFailedException("Authentication code not sent yet");
        }

        if (!userToAuth.getAuthenticationCode().equals(request.getCode())) {
            throw new AuthenticationFailedException("Incorrect authentication code");
        }

        userToAuth.setAuthenticationCode(null);
        userToAuth.setAuthenticated(true);

        this.accountRepository.save(userToAuth);
    }

    private void removeUnverifiedUsers() {
        Runnable task = () -> {
            List<User> unverifiedUsers = this.accountRepository.findAll()
                    .stream()
                    .filter((user) -> user.getAuthenticated() == null)
                    .toList();

            this.accountRepository.deleteAllInBatch(unverifiedUsers);
        };

        Executors.newScheduledThreadPool(1)
                .scheduleWithFixedDelay(task, 0, 1, TimeUnit.HOURS);
    }
}
