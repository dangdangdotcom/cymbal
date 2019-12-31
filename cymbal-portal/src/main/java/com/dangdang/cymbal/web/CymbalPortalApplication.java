package com.dangdang.cymbal.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main class when run as a jar.
 *
 * @author GeZhen
 */
@EnableAsync
@SpringBootApplication(scanBasePackages = {"com.dangdang.cymbal"})
public class CymbalPortalApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(CymbalPortalApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(CymbalPortalApplication.class);
    }
}
