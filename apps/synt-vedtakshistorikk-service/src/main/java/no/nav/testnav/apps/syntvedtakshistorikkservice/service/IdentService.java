package no.nav.testnav.apps.syntvedtakshistorikkservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.PersonSearchServiceConsumer;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.personSearch.AlderSearch;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.personSearch.PersonSearchRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.personSearch.PersonstatusSearch;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.KontoinfoResponse;
import no.nav.testnav.libs.dto.personsearchservice.v1.PersonDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentService {

    private final PersonSearchServiceConsumer personSearchServiceConsumer;

    public List<String> getUtvalgteIdenterIAldersgruppe(
            int antallNyeIdenter,
            int minimumAlder,
            int maksimumAlder,
            LocalDate tidligsteDatoBosatt

    ) {
        var request = PersonSearchRequest.builder()
                .randomSeed("test")
                .alder(AlderSearch.builder()
                        .fra((short) minimumAlder)
                        .til((short) maksimumAlder)
                        .build())
                .personstatus(PersonstatusSearch.builder()
                        .status("bosatt")
                        .build())
                .build();

        var identer = personSearchServiceConsumer.search(request).stream().map(PersonDTO::getIdent).toList();
        return Collections.emptyList();
    }

    public List<String> getUtvalgteIdenterIAldersgruppeMedBarnUnder18(
            int antallNyeIdenter,
            int minimumAlder,
            int maksimumAlder,
            LocalDate tidligsteDatoBosatt,
            LocalDate tidligsteDatoBarnetillegg
    ) {
        return Collections.emptyList();
    }

    public List<KontoinfoResponse> getIdenterMedKontoinformasjon(
            int antall
    ) {
        return Collections.emptyList();
    }
}
