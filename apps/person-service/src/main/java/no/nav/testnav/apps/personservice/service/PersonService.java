package no.nav.testnav.apps.personservice.service;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.personservice.adapter.TpsPersonAdapter;
import no.nav.testnav.apps.personservice.consumer.PdlApiConsumer;
import no.nav.testnav.apps.personservice.consumer.PdlTestdataConsumer;
import no.nav.testnav.apps.personservice.consumer.dto.pdl.graphql.PdlAktoer.AktoerIdent;
import no.nav.testnav.apps.personservice.domain.Person;
import no.nav.testnav.libs.dto.personservice.v1.Persondatasystem;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class PersonService {

    private static final long TIME_TO_LIVE_S = 60;

    private final PdlApiConsumer pdlApiConsumer;
    private final TpsPersonAdapter tpsPersonAdapter;
    private final PdlTestdataConsumer pdlTestdataConsumer;
    private Map<String, IdentStatus> identerStatus = new ConcurrentHashMap<>();

    public String createPerson(Person person, String kilde) {
        return pdlTestdataConsumer.createPerson(person, kilde);
    }

    public Mono<Optional<Person>> getPerson(String ident, String miljoe, Persondatasystem persondatasystem) {
        if (persondatasystem.equals(Persondatasystem.PDL)) {
            return pdlApiConsumer.getPerson(ident);
        } else {
            return tpsPersonAdapter.getPerson(ident, miljoe);
        }
    }

    public Mono<Optional<AktoerIdent>> getAktoerId(String ident) {
        return pdlApiConsumer.getAktoer(ident);
    }

    public Mono<Boolean> isPerson(String ident) {
        return pdlApiConsumer.isPerson(ident);
    }

    public Mono<Boolean> syncPdlPerson(String ident) {

        if (identerStatus.containsKey(ident)) {
            if (nonNull(identerStatus.get(ident).availStartTime) && ChronoUnit.SECONDS.between(LocalDateTime.now(),
                    identerStatus.get(ident).availStartTime) < TIME_TO_LIVE_S) {

                return Mono.just(true);
            } else if () {}

        }

        var isPerson = pdlApiConsumer.isPerson(ident);
        pdlIdenter.remove(ident);

        try {
            Thread.sleep(100);

        } catch (InterruptedException e) {

            // ingenting
        }

        if (personServiceConsumer.isPerson(ident)) {

            pdlIdenter.put(ident, System.currentTimeMillis());
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    private static class IdentStatus {

        private LocalDateTime requestStartTime;
        private LocalDateTime availStartTime;
    }
}
