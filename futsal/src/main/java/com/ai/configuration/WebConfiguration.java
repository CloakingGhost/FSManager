package com.ai.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Configuration // Configuration 어노테이션 추가
@ComponentScan
public class WebConfiguration implements WebMvcConfigurer { // WebMvcConfigurer 인터페이스 구현(implements)
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**")
				.addResourceLocations("classpath:/static/");
		registry.addResourceHandler("/js/**")
				.addResourceLocations("classpath:/static/js/");
		registry.addResourceHandler("/css/**")
				.addResourceLocations("classpath:/static/css/");
		registry.addResourceHandler("/img/**")
				.addResourceLocations("classpath:/static/img/");
		registry.addResourceHandler("/fonts/**")
				.addResourceLocations("classpath:/static/fonts/");
		registry.addResourceHandler("/icon-fonts/**")
				.addResourceLocations("classpath:/static/icon-fonts/");
		registry.addResourceHandler("/scss/**")
				.addResourceLocations("classpath:/static/scss/");
		registry.addResourceHandler("/*.html")
				.addResourceLocations("classpath:/static/");
		registry.addResourceHandler("/swagger-ui.html")
				.addResourceLocations("classpath:/META-INF/resources/");
	}
	// ModelAndView 객체를 json으로 변환
	@Bean
	MappingJackson2JsonView jsonView(){
		return new MappingJackson2JsonView();
	}
}