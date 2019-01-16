package no.nav.dolly.config;

import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Component
public class DollyObjectMapper extends ObjectMapper {
    public DollyObjectMapper(){
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);    //ISO 8601 er default format for java.time
        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        findAndRegisterModules();
    }
}
