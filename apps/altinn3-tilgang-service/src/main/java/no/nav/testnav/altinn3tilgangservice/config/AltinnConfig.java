package no.nav.testnav.altinn3tilgangservice.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@NoArgsConstructor
public class AltinnConfig {

    @Value("${altinn.api.url}")
    private String url;

    @Value("${altinn.api.key}")
    private String apiKey;

    @Value("${altinn.service.code}")
    private String code;

    @Value("${altinn.service.edition}")
    private String edition;
}
