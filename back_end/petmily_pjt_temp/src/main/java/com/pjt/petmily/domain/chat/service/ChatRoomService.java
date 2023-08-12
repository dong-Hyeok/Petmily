package com.pjt.petmily.service;

import com.pjt.petmily.domain.chat.ChatMessage;
import com.pjt.petmily.domain.chat.ChatRoom;
import com.pjt.petmily.domain.chat.dto.ChatRequestDto;
import com.pjt.petmily.domain.chat.dto.ChatRoomDTO;
import com.pjt.petmily.domain.chat.repository.ChatMessageJpaRepository;
import com.pjt.petmily.domain.chat.repository.ChatRoomJpaRepository;
import com.pjt.petmily.domain.user.User;
import com.pjt.petmily.domain.user.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class ChatRoomService {

    @Autowired
    private ChatRoomJpaRepository chatRoomJpaRepository;
    @Autowired
    private ChatMessageJpaRepository chatMessageJpaRepository;
    @Autowired
    private UserRepository userRepository;

    public List<ChatRoom> getUserChatRooms(String userEmail) {
        Optional<User> userOptional = userRepository.findByUserEmail(userEmail);

        if (!userOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없음");
        }

        User user = userOptional.get();
        return user.getChatRooms();
    }
    @Transactional
    public ChatRoomDTO createChatRoom(ChatRequestDto chatRequestDto) {
        ChatRoomDTO roomDTO = ChatRoomDTO.create(chatRequestDto.getReceiver());

        Optional<User> userReceiver = userRepository.findByUserEmail(chatRequestDto.getReceiver());
        Optional<User> userSender = userRepository.findByUserEmail(chatRequestDto.getSender());

        if (!userReceiver.isPresent() || !userSender.isPresent()) {
            throw new RuntimeException("유저 정보 찾을 수 없음: " + chatRequestDto.getReceiver() + " or " + chatRequestDto.getSender());
        }

        List<ChatRoom> existingRooms = chatRoomJpaRepository.findByParticipantsIn(Arrays.asList(userReceiver.get(), userSender.get()),2);

        if (!existingRooms.isEmpty()) {
            // Assuming there is only one unique chat room for the two users
            ChatRoom existingRoom = existingRooms.get(0);
            ChatRoomDTO existingRoomDTO = new ChatRoomDTO();
            existingRoomDTO.setRoomId(existingRoom.getRoomId());
            // Setting other necessary properties if any
            return existingRoomDTO;
        }

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setRoomId(roomDTO.getRoomId());
        chatRoom.getParticipants().add(userReceiver.get());
        chatRoom.getParticipants().add(userSender.get());

        chatRoomJpaRepository.save(chatRoom);

        return roomDTO;
    }

    public List<ChatMessage> getChatHistory(ChatRequestDto chatRequestDto) {
        Optional<User> userReceiver = userRepository.findByUserEmail(chatRequestDto.getReceiver());
        Optional<User> userSender = userRepository.findByUserEmail(chatRequestDto.getSender());

        if (!userReceiver.isPresent() || !userSender.isPresent()) {
            throw new RuntimeException("유저 정보 찾을 수 없음: " + chatRequestDto.getReceiver() + " or " + chatRequestDto.getSender());
        }

        List<ChatRoom> chatRooms = chatRoomJpaRepository.findByParticipantsIn(Arrays.asList(userReceiver.get(), userSender.get()),2);
        if (chatRooms.isEmpty()) {
            throw new RuntimeException("No chat room exists between these users.");
        }

        // Assuming there's only one unique chat room for the two users
        ChatRoom chatRoom = chatRooms.get(0);
        log.info(chatMessageJpaRepository.findByChatRoom(chatRoom));
        return chatMessageJpaRepository.findByChatRoom(chatRoom);
    }
    /*
    ##### 채팅 내역 더 간결하게 나옴 ########
     */
//    public List<ChatMessage> getChatHistory(ChatRequestDto chatRequestDto) {
//        Optional<User> userReceiver = userRepository.findByUserEmail(chatRequestDto.getReceiver());
//        Optional<User> userSender = userRepository.findByUserEmail(chatRequestDto.getSender());
//
//        if (!userReceiver.isPresent() || !userSender.isPresent()) {
//            throw new RuntimeException("유저 정보 찾을 수 없음: " + chatRequestDto.getReceiver() + " or " + chatRequestDto.getSender());
//        }
//
//        List<ChatRoom> chatRooms = chatRoomJpaRepository.findByParticipantsIn(Arrays.asList(userReceiver.get(), userSender.get()),2);
//        if (chatRooms.isEmpty()) {
//            throw new RuntimeException("No chat room exists between these users.");
//        }
//
//        // Assuming there's only one unique chat room for the two users
//        ChatRoom chatRoom = chatRooms.get(0);
//
//        // Directly using the messages field of ChatRoom entity
//        return chatRoom.getMessages();
//    }

}
