package no.nav.testnav.apps.syntvedtakshistorikkservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.PdlPersonConsumer;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.PersonSearchServiceConsumer;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.personSearch.AlderSearch;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.personSearch.BarnSearch;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.personSearch.PersonSearchRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.personSearch.PersonstatusSearch;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.KontoinfoResponse;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.PdlPerson;
import no.nav.testnav.libs.dto.personsearchservice.v1.PersonDTO;
import no.nav.testnav.libs.servletcore.util.IdentUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentService {

    private final PersonSearchServiceConsumer personSearchServiceConsumer;
    private final PdlPersonConsumer pdlPersonConsumer;
    private final Random rand = new Random();
    private static final int MAX_SEARCH_REQUESTS = 20;
    private static final int PAGE_SIZE = 10;

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
            var request = getSearchRequest(randomSeed, page, minimumAlder, maksimumAlder, "bosatt", null);
            var response = personSearchServiceConsumer.search(request);
            int numberOfPages = response.getNumberOfItems() / 10;

            for (PersonDTO person : response.getItems()) {
                if (validIdent(person, tidligsteDatoBosatt)) utvalgteIdenter.add(person.getIdent());
                if (utvalgteIdenter.size() >= antallNyeIdenter) break;
            }

            page++;
            if (page > numberOfPages) break;
        }

        return utvalgteIdenter;
    }

    public List<String> getUtvalgteIdenterIAldersgruppeMedBarnUnder18(
            int antallNyeIdenter,
            int minimumAlder,
            int maksimumAlder,
            LocalDate tidligsteDatoBosatt,
            LocalDate tidligsteDatoBarnetillegg
    ) {
        List<String> utvalgteIdenter = new ArrayList<>(antallNyeIdenter);

        var randomSeed = rand.nextFloat() + "";
        var page = 1;
        while (page < MAX_SEARCH_REQUESTS) {
            var request = getSearchRequest(randomSeed, page, minimumAlder, maksimumAlder, "bosatt", true);
            var response = personSearchServiceConsumer.search(request);
            int numberOfPages = response.getNumberOfItems() / 10;

            for (PersonDTO person : response.getItems()) {
                if (validIdent(person, tidligsteDatoBosatt) && validBarn(person, tidligsteDatoBarnetillegg)) utvalgteIdenter.add(person.getIdent());
                if (utvalgteIdenter.size() >= antallNyeIdenter) break;
            }

            page++;
            if (page > numberOfPages) break;
        }

        return utvalgteIdenter;
    }

    private boolean validIdent(PersonDTO person, LocalDate tidligsteDatoBosatt) {
        var personData = pdlPersonConsumer.getPdlPerson(person.getIdent());
        var bosattTidspunkt = personData.getData().getHentPerson()
                .getFolkeregisterpersonstatus().stream()
                .filter(status -> status.getStatus().equals("bosatt"))
                .filter(status -> !status.getMetadata().isHistorisk())
                .map(status -> status.getFolkeregistermetadata().getGyldighetstidspunkt())
                .toList();
        return true;
    }

    private boolean validBarn(PersonDTO person, LocalDate tidligsteDatoBarnetillegg) {
        var personData = pdlPersonConsumer.getPdlPerson(person.getIdent());
        var barnIdenter = personData.getData().getHentPerson().getForelderBarnRelasjon()
                .stream()
                .filter(relasjon -> relasjon.getRelatertPersonsRolle().equals("BARN"))
                .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
                .filter(ident -> under18VedTidspunkt(ident, tidligsteDatoBarnetillegg))
                .toList();

        return !barnIdenter.isEmpty();
    }

    private boolean under18VedTidspunkt(String ident, LocalDate tidspunkt){
        var month = Integer.parseInt(ident.substring(2,4)) - 80;
        var oppdatertFnr = ident.substring(0,2) + month + ident.substring(4);
        var foedselsdato = IdentUtil.getFoedselsdatoFraIdent(oppdatertFnr);

        var alder = Math.toIntExact(ChronoUnit.YEARS.between(foedselsdato, tidspunkt));
        return alder > -1 && alder < 18;
    }


    public List<KontoinfoResponse> getIdenterMedKontoinformasjon(
            int antall
    ) {
        return Collections.emptyList();
    }

    private PersonSearchRequest getSearchRequest(
            String randomSeed,
            int page,
            int minimumAlder,
            int maksimumAlder,
            String personstatus,
            Boolean harBarn
    ) {
        var request = PersonSearchRequest.builder()
                .randomSeed(randomSeed)
                .page(page)
                .pageSize(PAGE_SIZE)
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
