package com.monteBravo.be.Config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class CloudinaryConfig {
     @Value("${CLOUDINARY_URL}")
    private String cloudinaryUrl;
    
    @Bean
    public Cloudinary cloudinary() {
       return new Cloudinary(cloudinaryUrl);
    }
}
