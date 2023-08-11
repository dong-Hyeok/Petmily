package com.pjt.petmily.global.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pjt.petmily.domain.chat.dto.ChatMessageDto;
import com.pjt.petmily.domain.chat.dto.ChatRoomDto;
import com.pjt.petmily.domain.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final ChatService chatService;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("{}", payload);
        ChatMessageDto chatMessage = objectMapper.readValue(payload, ChatMessageDto.class);

        ChatRoomDto chatRoom = chatService.findRoomById(chatMessage.getRoomId());
        chatRoom.handlerActions(session, chatMessage, chatService);
    }
}