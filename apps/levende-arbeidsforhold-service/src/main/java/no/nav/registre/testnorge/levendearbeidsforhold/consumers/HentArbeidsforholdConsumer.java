package no.nav.registre.testnorge.levendearbeidsforhold.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.registre.testnorge.levendearbeidsforhold.consumers.command.HentArbeidsforholdCommand;
import no.nav.testnav.libs.dto.ameldingservice.v1.ArbeidsforholdDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import no.nav.registre.testnorge.levendearbeidsforhold.config.Consumers;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

/**
 * Consumer for å hente arbeidsforhold fra AAREG
 * WebClient for å hente data fra AAREG
 * TokenExchange for å hente token fra STS
 * ObjectMapper for å mappe json til objekter
 */
@Component
public class HentArbeidsforholdConsumer {
    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;

    /**
     * Konstruktør
     * @param consumers er ett objekt som inneholder serverProperties som finnes i application.yml
     * @param tokenExchange er ett objekt som henter token fra STS
     * @param objectMapper er ett objekt som mapper json til objekter
     */
    public HentArbeidsforholdConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            ObjectMapper objectMapper) {
        // Henter serverProperties fra consumers
        serverProperties = consumers.getLevendeArbeidsforholdService();
        this.tokenExchange = tokenExchange;
        //Bygger en ExchangeStratefies for å mappe json til objekter
        ExchangeStrategies exchangeStrategies = ExchangeStrategies
                .builder()
                .codecs(configurer -> {
                    configurer.defaultCodecs()
                            .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                    configurer.defaultCodecs()
                            .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                })
                .build();
        //Bygger en WebClient for å hente data fra AAREG
        //Legger inn echangestrategies og baseurl
        this.webClient = WebClient
                .builder()
                .exchangeStrategies(exchangeStrategies)
                .baseUrl("serverProperties.getUrl()") //For lokal kjøring bytt ut med https://aareg-services-q2.intern.nav.no
                .build();
    }

    /**
     * Henter arbeidsforhold for en arbeidstaker
     * @param ident FNR/DNR/aktør id til arbeidstakeren
     * @return Hvis arbeidsforhold finnes for identen vil en liste med ArbeidsforholdDTO bli returnert, ellers en tom liste
     */
    public List<ArbeidsforholdDTO> getArbeidsforhold(String ident) {
        var token = tokenExchange.exchange(serverProperties).block();
        if(nonNull(token)) {
            return new HentArbeidsforholdCommand(webClient, token.getTokenValue(), ident).call();
        }
        return new ArrayList<>();
    }

}

