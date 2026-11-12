package com.example.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.concurrent.TimeUnit;
@Configuration
public class WebConfig implements WebMvcConfigurer {
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
