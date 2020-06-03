package no.nav.registre.aaregstub.config.dev;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@EnableAutoConfiguration(exclude = {

})
@Profile("dev")
@Configuration
public class DevConfig {

}
