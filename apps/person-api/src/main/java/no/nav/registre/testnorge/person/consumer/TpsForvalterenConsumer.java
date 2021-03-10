package no.nav.registre.testnorge.person.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import no.nav.registre.testnorge.person.consumer.command.GetTpsPersonCommand;
import no.nav.registre.testnorge.person.consumer.dto.tpsf.PersonMiljoeResponse;
import no.nav.registre.testnorge.person.domain.Person;

@Slf4j
@Component
public class TpsForvalterenConsumer {

    private final String tpsfUrl;
    private final RestTemplate restTemplate;

    public TpsForvalterenConsumer(
            @Value("${system.tpsf.tpsForvalterenUrl}") String tpsfUrl,
            RestTemplateBuilder restTemplateBuilder
    ) {
        this.tpsfUrl = tpsfUrl;
        this.restTemplate = restTemplateBuilder.build();
    }

    public Person getPerson(String ident, String miljoe) {
        PersonMiljoeResponse tpsPerson = new GetTpsPersonCommand(restTemplate, tpsfUrl, ident, miljoe).call();
        return tpsPerson == null ? null : new Person(tpsPerson.getPerson());
    }
}
