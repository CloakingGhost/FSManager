package com.ai.controller;

import com.ai.domain.CheckDevenv;
import com.ai.repository.*;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

//import com.ai.repository.ReserveRepository;

import javax.annotation.PostConstruct;
import javax.servlet.*;
import java.io.FileInputStream;
import java.util.Collections;

@SpringBootApplication

@ComponentScan(basePackages = {"com.ai.controller", "com.ai.domain", "com.ai.repository", "com.ai.service", "com.ai.configuration"})
@EnableMongoRepositories(basePackageClasses = { FieldRepository.class, TeamRepository.class, CommunityRepository.class,
		MemberRepository.class, ReserveRepository.class, ReserveListRepository.class})
@EnableScheduling
@EnableSwagger2
public class FutsalApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(FutsalApplication.class, args);
	}
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		super.onStartup(servletContext);
		servletContext.setSessionTrackingModes(Collections.singleton(SessionTrackingMode.COOKIE));
		SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();
		sessionCookieConfig.setHttpOnly(true);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(FutsalApplication.class);
	}

}

