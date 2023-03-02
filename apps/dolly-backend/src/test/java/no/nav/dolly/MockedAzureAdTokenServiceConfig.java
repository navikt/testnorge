package no.nav.dolly;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.AzureAdTokenService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Configuration
@Slf4j
public class MockedAzureAdTokenServiceConfig {

    @MockBean
    private AzureAdTokenService azureAdTokenService;

    @Primary
    @Bean
    public AzureAdTokenService azureAdTokenService() {
        log.info("Using a mocked AzureAdTokenService");
        when(azureAdTokenService.exchange(any(ServerProperties.class)))
                .thenReturn(Mono.just(new AccessToken("{}")));
        return azureAdTokenService;
    }

}
