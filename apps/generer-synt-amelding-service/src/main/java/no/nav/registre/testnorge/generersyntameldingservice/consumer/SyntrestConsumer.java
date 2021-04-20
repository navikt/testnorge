package no.nav.registre.testnorge.generersyntameldingservice.consumer;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.domain.dto.aareg.amelding.Arbeidsforhold;
import no.nav.registre.testnorge.domain.dto.aareg.amelding.ArbeidsforholdPeriode;
import no.nav.registre.testnorge.generersyntameldingservice.consumer.command.PostArbeidsforholdCommand;
import no.nav.registre.testnorge.generersyntameldingservice.consumer.command.PostHistorikkCommand;

@Slf4j
@Component
public class SyntrestConsumer {

    private final WebClient webClient;

    public SyntrestConsumer(
            @Value("${syntrest.rest-api.url}") String syntrestServerUrl
    ) {
        this.webClient = WebClient.builder().baseUrl(syntrestServerUrl).build();
    }

    public Arbeidsforhold getEnkeltArbeidsforhold(ArbeidsforholdPeriode periode, String arbeidsforholdType){
        return new PostArbeidsforholdCommand(periode, webClient, arbeidsforholdType).call();
    }

    public List<Arbeidsforhold> getHistorikk(Arbeidsforhold arbeidsforhold){
        return new PostHistorikkCommand(arbeidsforhold, webClient).call();
    }

}
