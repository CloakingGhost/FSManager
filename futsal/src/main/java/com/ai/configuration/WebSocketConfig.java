package com.ai.configuration;

import com.ai.websocket.HttpHandshakeInterceptor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.ArrayList;
import java.util.List;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic","/queue"); //prefix가 /topic인것만 찾아서 greeting클래스의 content를 클라이언트에게 되돌려주는 역할
        config.setApplicationDestinationPrefixes("/app"); // /app이 컨트롤러의 @MessagesMapping의 주소의 prefix역할을 함
//        config.setUserDestinationPrefix("/user"); // 특정유저에게
    }
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/gs-guide-websocket")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new HttpHandshakeInterceptor())
                .withSockJS();
        // gs-guide-websocket 이걸 주소로 가지고 있는 엔드포인트를 등록함
    }

//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(new ChannelInterceptor() {
//            @Override
//            public Message<?> preSend(Message<?> message, MessageChannel channel) {
//                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//                if(StompCommand.CONNECT.equals(accessor.getCommand())){
//                    String user = accessor.getFirstNativeHeader("user");
//                    if(user != null){
//                        List<GrantedAuthority> authorities = new ArrayList<>();
//                        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
//                        Authentication auth = new UsernamePasswordAuthenticationToken(user, user, authorities);
//                        SecurityContextHolder.getContext().setAuthentication(auth);
//                        accessor.setUser(auth);
//                    }
//                }
//                return message;
//            }
//        });
//    }
}