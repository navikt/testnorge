package no.nav.testnav.oppdragservice.service;

import no.nav.testnav.libs.dto.oppdragservice.v1.OppdragKodeverk;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class KodeverkService {

    public List<String> getKodeverk(OppdragKodeverk oppdragKodeverk) {

        return Stream.of(oppdragKodeverk.getImplementasjon().getEnumConstants())
                .map(Object::toString)
                .sorted()
                .toList();
    }
}