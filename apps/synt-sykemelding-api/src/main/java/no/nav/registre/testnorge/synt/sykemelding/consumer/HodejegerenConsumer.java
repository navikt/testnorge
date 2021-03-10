package no.nav.registre.testnorge.synt.sykemelding.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import no.nav.registre.testnorge.synt.sykemelding.consumer.command.GetPersondataCommand;
import no.nav.registre.testnorge.synt.sykemelding.domain.Person;

@Component
public class HodejegerenConsumer {
    private static final String MILJOE = "q1";
    private final String url;
    private final RestTemplate restTemplate;

    public HodejegerenConsumer(
            @Value("${consumers.hodejegeren.url}") String url,
            RestTemplateBuilder restTemplateBuilder
    ) {
        this.url = url;
        this.restTemplate = restTemplateBuilder.build();
    }

    public Person getPersondata(String ident) {
        return new Person(new GetPersondataCommand(url, ident, MILJOE, restTemplate).call());
    }
}
