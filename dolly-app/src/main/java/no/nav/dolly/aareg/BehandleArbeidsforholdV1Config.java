package no.nav.dolly.aareg;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BehandleArbeidsforholdV1Config {

    @Bean
    public BehandleArbeidsforholdV1Proxy behandleArbeidsforholdV1Proxy() {
        return new BehandleArbeidsforholdV1Proxy();
    }
}