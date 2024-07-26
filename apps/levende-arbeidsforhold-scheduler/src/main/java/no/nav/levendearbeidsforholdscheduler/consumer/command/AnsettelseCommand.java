package no.nav.levendearbeidsforholdscheduler.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.levendearbeidsforholdscheduler.config.Consumers;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@RequiredArgsConstructor
@Service
public class AnsettelseCommand {

    private final Consumers consumers;
    private final TokenExchange tokenExchange;

    @EventListener(ApplicationReadyEvent.class)
    public void aktiverAnsettelseService(){

        log.info("Kjørrrr");

        var accessToken = hentToken();

        WebClient webClient = WebClient.builder().baseUrl("https://testnav-levende-arbeidsforhold-ansettelse-v2.intern.dev.nav.no/api/ansettelse-jobb").build();
        String response = webClient.get()
                .header("Authorization", "Bearer " + accessToken)
                .retrieve().bodyToMono(String.class).block();
        if(response != null){
            log.info("Response: {}", response);
        }
//        try {
//
//        } catch (Exception e){
//            log.info("Feilet å sende spørring til levende-arbeidsforhold-ansettelse");
//        }
    }

    public String hentToken(){

        var serverProperties = consumers.getTestnavLevendeArbeidsforholdAnsettelsev2();
        var accessToken = tokenExchange.exchange(serverProperties).block();
        if(accessToken != null){
            return accessToken.getTokenValue();
        } else {
            log.warn("Kunne ikke hente access token");
            return "";
        }
    }
}
