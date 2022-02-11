package no.nav.testnav.apps.syntvedtakshistorikkservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.PersonSearchServiceConsumer;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.personSearch.AlderSearch;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.personSearch.BarnSearch;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.personSearch.PersonSearchRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.personSearch.PersonstatusSearch;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.KontoinfoResponse;
import no.nav.testnav.libs.dto.personsearchservice.v1.PersonDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentService {

    private final PersonSearchServiceConsumer personSearchServiceConsumer;
    private final Random rand = new Random();
    private final int MAX_SEARCH_REQUESTS = 20;

    public List<String> getUtvalgteIdenterIAldersgruppe(
            int antallNyeIdenter,
            int minimumAlder,
            int maksimumAlder,
            LocalDate tidligsteDatoBosatt

    ) {
        List<String> utvalgteIdenter = new ArrayList<>(antallNyeIdenter);

        var randomSeed = rand.nextFloat() + "";
        var page = 1;
        while (page < MAX_SEARCH_REQUESTS) {
            var request = getSearchRequest(randomSeed, page, 10, minimumAlder, maksimumAlder, "bosatt", null);
            var response = personSearchServiceConsumer.search(request);
            int numberOfPages = response.getNumberOfItems() / 10;

            for (PersonDTO person : response.getItems()) {
                if (validIdent(person)) utvalgteIdenter.add(person.getIdent());
                if (utvalgteIdenter.size() >= antallNyeIdenter) break;
            }

            page++;
            if (page >= MAX_SEARCH_REQUESTS || page > numberOfPages) break;
        }

        return Collections.emptyList();
    }

    private boolean validIdent(PersonDTO person) {
        return true;
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

    private PersonSearchRequest getSearchRequest(
            String randomSeed,
            int page,
            int pageSize,
            int minimumAlder,
            int maksimumAlder,
            String personstatus,
            Boolean harBarn
    ) {
        var request = PersonSearchRequest.builder()
                .randomSeed(randomSeed)
                .page(page)
                .pageSize(pageSize)
                .alder(AlderSearch.builder()
                        .fra((short) minimumAlder)
                        .til((short) maksimumAlder)
                        .build())
                .build();

        if (personstatus != null && !personstatus.isEmpty()) {
            request.setPersonstatus(PersonstatusSearch.builder()
                    .status(personstatus)
                    .build());
        }

        if (harBarn != null && harBarn) {
            request.setBarn(BarnSearch.builder()
                    .barn(Boolean.TRUE)
                    .build());
        }

        return request;

    }
}
