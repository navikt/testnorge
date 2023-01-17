package no.nav.dolly.web.config;

import no.nav.testnav.libs.reactivesessionsecurity.exchange.AzureAdTokenExchange;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.TokenXExchange;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;


@Configuration
@Profile({ "local", "test" })
@Import({
        AzureAdTokenExchange.class,
        TokenXExchange.class,
})
public class LocalConfig {
}
