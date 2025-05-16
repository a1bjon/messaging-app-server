package com.xepr.core.service;

import com.xepr.core.dto.Sendable;
import com.xepr.core.dto.request.CreateChatRequestDTO;
import com.xepr.core.dto.request.JoinChatRequestDTO;
import com.xepr.core.dto.request.LeaveChatRequestDTO;
import com.xepr.core.dto.request.SendTextMessageRequestDTO;
import com.xepr.core.dto.request.SendMediaMessageRequestDTO;
import com.xepr.core.dto.request.DeleteTextMessageRequestDTO;
import com.xepr.core.dto.request.DeleteMediaMessageRequestDTO;
import com.xepr.core.dto.request.UpdateChatRequestDTO;
import com.xepr.core.dto.request.EditTextMessageRequestDTO;
import com.xepr.core.dto.response.CreateChatResponseDTO;
import com.xepr.core.dto.response.JoinChatResponseDTO;
import com.xepr.core.dto.response.LeaveChatResponseDTO;
import com.xepr.core.dto.response.UpdateChatResponseDTO;
import com.xepr.core.dto.response.SendTextMessageResponseDTO;
import com.xepr.core.dto.response.SendMediaMessageResponseDTO;
import com.xepr.core.dto.response.EditTextMessageResponseDTO;
import com.xepr.core.exception.InvalidAttributeException;
import com.xepr.core.exception.DuplicateChatException;
import com.xepr.core.exception.UserNotFoundException;
import com.xepr.core.exception.ChatNotFoundException;
import com.xepr.core.exception.MessageNotFoundException;
import com.xepr.core.exception.DuplicateUserException;
import com.xepr.core.exception.InvalidCodeException;
import com.xepr.core.exception.InvalidMessageTypeException;
import com.xepr.core.exception.EditTimeLimitedExceededException;
import com.xepr.core.model.Chat;
import com.xepr.core.model.Message;
import com.xepr.core.model.TextMessage;
import com.xepr.core.model.User;
import com.xepr.core.repository.AccountRepository;
import com.xepr.core.repository.ChatRepository;
import com.xepr.core.repository.TextMessageRepository;
import com.xepr.core.repository.MediaMessageRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import jakarta.transaction.Transactional;

import java.io.IOException;
import java.util.List;

@Service
public class ChatService {

    private final ChatRepository chatRepository;

    private final AccountRepository accountRepository;

    private final TextMessageRepository textMessageRepository;

    private final MediaMessageRepository mediaMessageRepository;

    private final MessageService messageService;

    private final ModelMapper mapper;

    @Autowired
    public ChatService(
            ChatRepository chatRepository,
            AccountRepository accountRepository,
            TextMessageRepository textMessageRepository,
            MediaMessageRepository mediaMessageRepository,
            MessageService messageService,
            ModelMapper mapper) {
        this.chatRepository = chatRepository;
        this.accountRepository = accountRepository;
        this.textMessageRepository = textMessageRepository;
        this.mediaMessageRepository = mediaMessageRepository;
        this.messageService = messageService;
        this.mapper = mapper;
    }

    @Transactional
    public CreateChatResponseDTO createChat(long id, CreateChatRequestDTO request)
            throws InvalidAttributeException, DuplicateChatException, UserNotFoundException {

        User user = this.accountRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (request.getName().isEmpty()) {
            throw new InvalidAttributeException("Chat missing name attribute");
        }

        if (this.chatRepository.findChatName(request.getName()) != null) {
            throw new DuplicateChatException("Chat already exists");
        }

        if (!request.getUsers().contains(id)) {
            request.getUsers().add(id);
        }

        Chat chatToCreate = new Chat();
        chatToCreate.setName(request.getName());
        chatToCreate.setUsers(request.getUsers());

        Chat createdChat = this.chatRepository.save(chatToCreate);
        user.getChats().add(createdChat.getId());
        this.accountRepository.save(user);

        return this.mapper.map(createdChat, CreateChatResponseDTO.class);
    }

    @Transactional
    public JoinChatResponseDTO joinChat(long id, JoinChatRequestDTO request)
            throws ChatNotFoundException, UserNotFoundException, DuplicateUserException, InvalidCodeException {

        Chat chatToJoin = this.chatRepository.findById(id)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found"));
        User userToAdd = this.accountRepository.findById(request.getId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (chatToJoin.getUsers().contains(request.getId())) {
            throw new DuplicateUserException("User already in chat");
        }

        if (!chatToJoin.getCode().equals(request.getCode())) {
            throw new InvalidCodeException("Invalid code");
        }

        chatToJoin.getUsers().add(request.getId());
        userToAdd.getChats().add(chatToJoin.getId());

        this.chatRepository.save(chatToJoin);
        this.accountRepository.save(userToAdd);

        return this.mapper.map(chatToJoin, JoinChatResponseDTO.class);
    }

    @Transactional
    public LeaveChatResponseDTO leaveChat(long id, LeaveChatRequestDTO request)
            throws ChatNotFoundException, UserNotFoundException, MessageNotFoundException {

        Chat chatToRemoveFrom = this.chatRepository.findById(id)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found"));
        User userToRemove = this.accountRepository.findById(request.getId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        chatToRemoveFrom.getUsers().remove(request.getId());
        userToRemove.getChats().remove(chatToRemoveFrom.getId());

        if (chatToRemoveFrom.getUsers().isEmpty()) {
            this.deleteChat(id);
            this.accountRepository.save(userToRemove);
            return this.mapper.map(chatToRemoveFrom, LeaveChatResponseDTO.class);
        }

        Chat updatedChat = this.chatRepository.save(chatToRemoveFrom);
        this.accountRepository.save(userToRemove);

        return this.mapper.map(updatedChat, LeaveChatResponseDTO.class);
    }

    public <T extends Sendable> Sendable sendMessage(long id, T request)
            throws InvalidMessageTypeException, ChatNotFoundException, UserNotFoundException, InvalidAttributeException, IOException {

        Message message;

        if (request instanceof SendTextMessageRequestDTO textMessageRequest) {
            String requestText = textMessageRequest.getText();

            if (requestText.isEmpty()) {
                throw new InvalidAttributeException("Text value is empty");
            }

            message = this.messageService.sendTextMessage(id, textMessageRequest.getSenderId(), requestText);
        } else if (request instanceof SendMediaMessageRequestDTO mediaMessageRequest) {
            byte[] requestMedia = mediaMessageRequest.getMedia();

            if (requestMedia == null || requestMedia.length == 0) {
                throw new InvalidAttributeException("Media value is empty");
            }

            message = this.messageService.sendMediaMessage(id, mediaMessageRequest.getSenderId(), requestMedia);
        } else {
            throw new InvalidMessageTypeException("Message request DTO type not valid");
        }

        return message instanceof TextMessage ?
                this.mapper.map(message, SendTextMessageResponseDTO.class) :
                this.mapper.map(message, SendMediaMessageResponseDTO.class);
    }

    public EditTextMessageResponseDTO editMessage(long id, EditTextMessageRequestDTO request)
            throws ChatNotFoundException, MessageNotFoundException, EditTimeLimitedExceededException, InvalidAttributeException {

        if (request.getNewText().isEmpty()) {
            throw new InvalidAttributeException("New message text is empty");
        }

        return this.mapper.map(this.messageService
                .editTextMessage(id, request.getId(), request.getNewText()), EditTextMessageResponseDTO.class);
    }

    public <T extends Sendable> void deleteMessage(long id, T request)
            throws ChatNotFoundException, MessageNotFoundException, InvalidMessageTypeException {

        if (request instanceof DeleteTextMessageRequestDTO textMessageRequest) {
            this.messageService.deleteTextMessage(id, textMessageRequest.getTextMessageId());
        } else if (request instanceof DeleteMediaMessageRequestDTO mediaMessageRequest) {
            this.messageService.deleteMediaMessage(id, mediaMessageRequest.getMediaMessageId());
        } else {
            throw new InvalidMessageTypeException("Message request DTO type not valid");
        }
    }

    public UpdateChatResponseDTO updateChatName(long id, UpdateChatRequestDTO request)
            throws ChatNotFoundException, InvalidAttributeException {

        Chat chatToUpdate = this.chatRepository.findById(id)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found"));

        if (request.getUpdatedName().isEmpty()) {
            throw new InvalidAttributeException("New chat name attribute is empty");
        }

        if (request.getUpdatedName().equals(chatToUpdate.getName())) {
            throw new InvalidAttributeException("New chat name identical to current name");
        }

        chatToUpdate.setName(request.getUpdatedName());
        Chat updatedChat = this.chatRepository.save(chatToUpdate);

        return this.mapper.map(updatedChat, UpdateChatResponseDTO.class);
    }

    @Transactional
    public void deleteChat(long id) throws ChatNotFoundException, MessageNotFoundException {
        Chat chatToDel = this.chatRepository.findById(id)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found"));

        List<User> users = this.accountRepository.findAllById(chatToDel.getUsers());
        users.forEach((user) -> user.getChats().remove(chatToDel.getId()));
        this.accountRepository.saveAll(users);

        this.textMessageRepository.deleteAllById(chatToDel.getMessages());
        this.mediaMessageRepository.deleteAllById(chatToDel.getMessages());
        this.chatRepository.delete(chatToDel);
    }

    public List<Chat> getUserChats(long id) throws UserNotFoundException, ChatNotFoundException {
        User user = this.accountRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return this.chatRepository.findAllById(user.getChats());
    }
}
