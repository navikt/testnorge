package no.nav.testnorge.apps.levendearbeidsforholdansettelse.consumers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class TenorConsumer {
    private static final String TENOR_DOMAIN = "https://vg.no";//"https://testnav-tenor-search-service.intern.dev.nav.no";

    @EventListener(ApplicationReadyEvent.class)
    public void buildRequest(){
        WebClient request = WebClient.builder().baseUrl(TENOR_DOMAIN).build();
        String response = request.get().retrieve().bodyToMono(String.class).block();
        log.info(response);
    }

}
