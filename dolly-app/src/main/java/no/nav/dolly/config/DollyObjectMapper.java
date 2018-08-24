package no.nav.dolly.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.springframework.stereotype.Component;

@Component
public class DollyObjectMapper extends ObjectMapper {
    public DollyObjectMapper(){
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); //ISO 8601 er default format for java.time
        findAndRegisterModules();
    }
}
