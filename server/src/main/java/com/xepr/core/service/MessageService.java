package com.xepr.core.service;

import com.xepr.core.exception.ChatNotFoundException;
import com.xepr.core.exception.UserNotFoundException;
import com.xepr.core.exception.MessageNotFoundException;
import com.xepr.core.exception.EditTimeLimitedExceededException;
import com.xepr.core.exception.InvalidAttributeException;
import com.xepr.core.model.User;
import com.xepr.core.model.Chat;
import com.xepr.core.model.TextMessage;
import com.xepr.core.model.MediaMessage;
import com.xepr.core.model.Message;
import com.xepr.core.repository.AccountRepository;
import com.xepr.core.repository.ChatRepository;
import com.xepr.core.repository.TextMessageRepository;
import com.xepr.core.repository.MediaMessageRepository;
import com.xepr.core.util.DateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.io.IOException;
import java.util.UUID;

@Service
public class MessageService {

    private final TextMessageRepository textMessageRepository;

    private final MediaMessageRepository mediaMessageRepository;

    private final ChatRepository chatRepository;

    private final AccountRepository accountRepository;

    @Autowired
    public MessageService(
            TextMessageRepository textMessageRepository,
            MediaMessageRepository mediaMessageRepository,
            ChatRepository chatRepository,
            AccountRepository accountRepository) {
        this.textMessageRepository = textMessageRepository;
        this.mediaMessageRepository = mediaMessageRepository;
        this.chatRepository = chatRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public TextMessage sendTextMessage(long chatId, long senderId, String data)
            throws ChatNotFoundException, UserNotFoundException {

        Chat chat = this.chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found"));
        User user = this.accountRepository.findById(senderId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        this.userPresentInChat(chat, user);

        TextMessage messageToSend = new TextMessage();
        messageToSend.setSenderId(senderId);
        messageToSend.setText(data);

        TextMessage sentMessage = this.textMessageRepository.save(messageToSend);
        chat.getMessages().add(sentMessage.getId());
        this.chatRepository.save(chat);

        return sentMessage;
    }

    @Transactional
    public MediaMessage sendMediaMessage(long chatId, long senderId, byte[] data)
            throws ChatNotFoundException, UserNotFoundException, IOException {

        Chat chat = this.chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found"));
        User user = this.accountRepository.findById(senderId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        this.userPresentInChat(chat, user);

        MediaMessage messageToSend = new MediaMessage();
        messageToSend.setSenderId(senderId);
        messageToSend.setMedia(messageToSend.compress(data, 1.0, 0.7));

        MediaMessage sentMessage = this.mediaMessageRepository.save(messageToSend);
        chat.getMessages().add(sentMessage.getId());
        this.chatRepository.save(chat);

        return sentMessage;
    }

    public TextMessage editTextMessage(long chatId, UUID messageId, String data)
            throws ChatNotFoundException, MessageNotFoundException, EditTimeLimitedExceededException, InvalidAttributeException {

        Chat chat = this.chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found"));
        TextMessage messageToEdit = this.textMessageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("Message not found"));

        this.messagePresentInChat(chat, messageToEdit);
        this.checkEditTimeLimitExceeded(messageToEdit.getTimestamp());

        if (messageToEdit.getText().equals(data)) {
            throw new InvalidAttributeException("New message text is identical to current");
        }
        messageToEdit.setText(data);

        return this.textMessageRepository.save(messageToEdit);
    }

    @Transactional
    public void deleteTextMessage(long chatId, UUID messageId) throws ChatNotFoundException, MessageNotFoundException {
        Chat chat = this.chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found"));
        TextMessage messageToDelete = this.textMessageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("Message not found"));

        this.messagePresentInChat(chat, messageToDelete);
        chat.getMessages().remove(messageToDelete.getId());

        this.chatRepository.save(chat);
        this.textMessageRepository.deleteById(messageToDelete.getId());
    }

    @Transactional
    public void deleteMediaMessage(long chatId, UUID messageId) throws ChatNotFoundException, MessageNotFoundException {
        Chat chat = this.chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found"));
        MediaMessage messageToDelete = this.mediaMessageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("Message not found"));

        this.messagePresentInChat(chat, messageToDelete);
        chat.getMessages().remove(messageToDelete.getId());

        this.chatRepository.save(chat);
        this.mediaMessageRepository.deleteById(messageToDelete.getId());
    }

    private void userPresentInChat(Chat chat, User user) throws UserNotFoundException {
        if (!chat.getUsers().contains(user.getId())) {
            throw new UserNotFoundException("User not in chat");
        }
    }

    private void messagePresentInChat(Chat chat, Message message) throws MessageNotFoundException {
        if (!chat.getMessages().contains(message.getId())) {
            throw new MessageNotFoundException("Message not in chat");
        }
    }

    private void checkEditTimeLimitExceeded(String dateTime) throws EditTimeLimitedExceededException {
        if ((DateUtil.getUnixTimestamp() - DateUtil.getTimestampFromDateTime(dateTime)) > 300) {
            throw new EditTimeLimitedExceededException("Message edit time limit exceeded");
        }
    }
}
