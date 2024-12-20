package com.ai.websocket;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class EchoHandler extends TextWebSocketHandler {
//    //연결된 모든 sessions 저장
//    List<WebSocketSession> sessions = new ArrayList<>();
//    //userId의 webSession을 저장한다
//    Map<String, WebSocketSession> userSessions = new HashMap<>();
//    //클라이언트 접속 성공 시 연결 성공시
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception{
//        log.info("afterConnectionEstablished :" + session);
//
//        //userId 알아내기
//        Map<String, Object> sessionVal =  session.getAttributes();
//        String email = (String) sessionVal.get("userId");
//        log.info("유저이메일 : " + email);
//
//        if(userSessions.get(userId) != null) {
//            //userId에 원래 웹세션값이 저장되어 있다면 update
//            userSessions.replace(userId, session);
//        } else {
//            //userId에 웹세션값이 없다면 put
//            userSessions.put(userId, session);
//        }


    private static final Logger LOGGER = LoggerFactory.getLogger(EchoHandler.class);
    private Map<String, WebSocketSession> users = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(
            WebSocketSession session) throws Exception {
        log(session.getId() + " 연결 됨");
        users.put(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(
            WebSocketSession session, CloseStatus status) throws Exception {
        log(session.getId() + " 연결 종료됨");
        users.remove(session.getId());
    }

    @Override
    protected void handleTextMessage(
            WebSocketSession session, TextMessage message) throws Exception {
        log(session.getId() + "로부터 메시지 수신: " + message.getPayload());
        for (WebSocketSession s : users.values()) {
            s.sendMessage(message);
            log(s.getId() + "에 메시지 발송: " + message.getPayload());
        }
    }

    @Override
    public void handleTransportError(
            WebSocketSession session, Throwable exception) throws Exception {
        log(session.getId() + " 익셉션 발생: " + exception.getMessage());
    }

    private void log(String logmsg) {
        LOGGER.info(new Date() + " : " + logmsg);
    }

}