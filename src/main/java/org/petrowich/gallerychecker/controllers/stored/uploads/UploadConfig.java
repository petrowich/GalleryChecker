package org.petrowich.gallerychecker.controllers.stored.uploads;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;

@Configuration
public class UploadConfig {
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory multipartConfigFactory = new MultipartConfigFactory();
        multipartConfigFactory.setMaxFileSize(DataSize.parse("10240KB"));
        multipartConfigFactory.setMaxRequestSize(DataSize.parse("102400KB"));
        return multipartConfigFactory.createMultipartConfig();
    }
}
