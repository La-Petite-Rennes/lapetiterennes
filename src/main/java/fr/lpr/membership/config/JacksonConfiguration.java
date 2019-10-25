package fr.lpr.membership.config;

import com.fasterxml.jackson.datatype.guava.GuavaModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {

    @Bean
    public GuavaModule guavaModule() {
    	return new GuavaModule();
    }
}
