package com.example.security;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private static final long ASYNC_TIMEOUT_MS = 30L * 60L * 1000L;

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setDefaultTimeout(ASYNC_TIMEOUT_MS);
    }

    @Bean
    public static BeanPostProcessor asyncRequestTimeoutEnforcer() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) {
                if (bean instanceof RequestMappingHandlerAdapter) {
                    ((RequestMappingHandlerAdapter) bean).setAsyncRequestTimeout(ASYNC_TIMEOUT_MS);
                }
                return bean;
            }
        };
    }

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatAsyncTimeout() {
        return factory -> factory.addConnectorCustomizers(connector -> connector.setAsyncTimeout(ASYNC_TIMEOUT_MS));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. ДЛЯ INDEX.HTML: Полный запрет на кэширование
        registry.addResourceHandler("/index.html")
                .addResourceLocations("classpath:/static/index.html")
                .setCacheControl(CacheControl.noCache().mustRevalidate());

        // 2. ДЛЯ СТАТИКИ (JS, CSS, картинки): Можно кэшировать, у них уникальные хэши в именах
        registry.addResourceHandler("/static/**", "/assets/**")
                .addResourceLocations("classpath:/static/static/", "classpath:/static/assets/")
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic());
    }
}
