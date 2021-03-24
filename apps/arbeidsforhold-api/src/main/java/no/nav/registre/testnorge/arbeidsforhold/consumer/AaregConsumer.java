package no.nav.registre.testnorge.arbeidsforhold.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arbeidsforhold.consumer.commnad.GetArbeidstakerArbeidsforholdCommand;
import no.nav.registre.testnorge.arbeidsforhold.domain.Arbeidsforhold;
import no.nav.registre.testnorge.arbeidsforhold.service.StsOidcTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AaregConsumer {

    private final StsOidcTokenService tokenService;
    private final RestTemplate restTemplate;
    private final String url;

    public AaregConsumer(StsOidcTokenService tokenService, RestTemplateBuilder restTemplateBuilder, @Value("${aareg.url}") String url) {
        this.tokenService = tokenService;
        this.restTemplate = restTemplateBuilder.build();
        this.url = url;
    }

    private List<Arbeidsforhold> getArbeidsforholds(String ident) {
        return new GetArbeidstakerArbeidsforholdCommand(restTemplate, url, tokenService.getToken(), ident).call();
    }

    private List<Arbeidsforhold> getArbeidsforholds(String ident, String orgnummer) {
        return getArbeidsforholds(ident)
                .stream()
                .filter(value -> value.getOrgnummer().equals(orgnummer))
                .collect(Collectors.toList());
    }

    public Arbeidsforhold getArbeidsforhold(String ident, String orgnummer, String arbeidsforholdId) {
        var arbeidsforholds = getArbeidsforholds(ident, orgnummer);
        return arbeidsforholds
                .stream()
                .filter(value -> value.getArbeidsforholdId().equals(arbeidsforholdId))
                .findFirst()
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Klarer ikke aa finne arbeidsforhold for " + ident));
    }
}