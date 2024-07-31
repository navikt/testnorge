package no.nav.levendearbeidsforholdscheduler.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.levendearbeidsforholdscheduler.config.Consumers;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@RequiredArgsConstructor
@Service
public class AnsettelseCommand {

    private final Consumers consumers;
    private final TokenExchange tokenExchange;

    /**
     * Funksjon som sender http spørring til levende-arbeidsforhold-ansettelse appen for å aktivere en ansettelse-jobb
     */
    public void aktiverAnsettelseService(){

        try {
            var accessToken = hentToken();

            WebClient webClient = WebClient.builder().baseUrl("https://testnav-levende-arbeidsforhold-ansettelse.intern.dev.nav.no/api/ansettelse-jobb").build();
            String response = webClient.get()
                    //.uri(builder -> builder.path("/api/ansettelse-jobb").build())
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve().bodyToMono(String.class).block();
            if(response != null){
                log.info("Response: {}", response);
            }
        } catch (Exception e) {
            log.warn("Kunne ikke aktivere ansettelse service pga spørring feilet {}", e.getMessage());
        }
    }

    /**
     * Henter ut accesstoken for å autorisere mot levende-arbeidsforhold-ansettelse appen sitt API
     * @return access-tokenen dersom forespørselen var vellykket
     */
    public String hentToken(){

        var serverProperties = consumers.getTestnavLevendeArbeidsforholdAnsettelse();
        var accessToken = tokenExchange.exchange(serverProperties).block();
        if(accessToken != null){
            return accessToken.getTokenValue();
        } else {
            log.warn("Kunne ikke hente access token");
            return "";
        }
    }
}
