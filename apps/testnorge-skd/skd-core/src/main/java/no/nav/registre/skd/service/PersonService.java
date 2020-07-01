package no.nav.registre.skd.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.skd.consumer.PersonConsumer;
import no.nav.registre.testnorge.dto.person.v1.PersonDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonConsumer personConsumer;

    public void leggTilIdenterIPdl(List<String> identer) {
        if (!identer.isEmpty()) {
            log.info("Legger til {} ident(er) i PDL", identer.size());

            for (var ident : identer) {
                try {
                    personConsumer.leggTilIdentIPdl(PersonDTO.builder().ident(ident).build());
                } catch (HttpStatusCodeException e) {
                    log.error("Kunne ikke legge følgende ident til i PDL: {}", ident, e);
                }
            }
        }
    }

}
