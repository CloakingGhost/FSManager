package com.ai.domain;

import com.google.firebase.messaging.*;
import com.google.firebase.messaging.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
public class Firebase {

    public String sendToToken(String token, String title, String contents) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setAndroidConfig(AndroidConfig.builder()
                        .setTtl(3600*1000)
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setNotification(AndroidNotification.builder()
                                .setTitle(title)
                                .setBody(contents)
                                .setIcon("@mipmap/fs_launcher")
                                .build()
                        )
                        .build())
                .setToken(token)
                .putData("title", title)
                .putData("contents", contents)
                .build();
        String response = FirebaseMessaging.getInstance().send(message);
        return response;
    }

    public String multipleSendToToken(ArrayList<String> tokenList, String title, String contents) throws FirebaseMessagingException {
        String response = null;
        if(tokenList.size() > 0){
            log.info("multipleSendToToken :: 토큰 갯수 : " + tokenList.size() + "개");
            for(String token : tokenList){
                log.info("토큰 정보 : " + token.toString());
                Message message = Message.builder()
                        .setAndroidConfig(AndroidConfig.builder()
                                .setTtl(3600*1000)
                                .setPriority(AndroidConfig.Priority.HIGH)
                                .setNotification(AndroidNotification.builder()
                                        .setTitle(title)
                                        .setBody(contents)
                                        .setIcon("@mipmap/fs_launcher")
                                        .build()
                                )
                                .build())
                        .setToken(token)
                        .putData("title", title)
                        .putData("contents", contents)
                        .build();
                response = FirebaseMessaging.getInstance().send(message);
            }
        }
        return response;
    }
}