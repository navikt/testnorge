package no.nav.dolly;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@Configuration
@Slf4j
public class JwtDecoderConfig {

    @MockBean
    private JwtDecoder jwtDecoder;

    @Bean
    public JwtDecoder jwtDecoder() {
        log.info("Using a mocked JWTDecoder");
        return jwtDecoder;
    }

}
