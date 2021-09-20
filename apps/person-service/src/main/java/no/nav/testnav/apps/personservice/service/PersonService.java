package no.nav.testnav.apps.personservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

import no.nav.testnav.apps.personservice.adapter.PdlPersonAdapter;
import no.nav.testnav.apps.personservice.adapter.TpsPersonAdapter;
import no.nav.testnav.apps.personservice.domain.Person;
import no.nav.testnav.libs.dto.personservice.v1.Persondatasystem;

@Service
@Slf4j
@RequiredArgsConstructor
public class PersonService {

    private final PdlPersonAdapter pdlPersonAdapter;
    private final TpsPersonAdapter tpsPersonAdapter;

    public Mono<Optional<Person>> getPerson(String ident, String miljoe, Persondatasystem persondatasystem) {
        if (persondatasystem.equals(Persondatasystem.PDL)) {
            return pdlPersonAdapter.getPerson(ident, miljoe);
        } else {
            return tpsPersonAdapter.getPerson(ident, miljoe);
        }
    }

    public Mono<Optional<Person>> getAktoerId(String ident, String miljoe) {
            return pdlPersonAdapter.getPerson(ident, miljoe);
    }
}
