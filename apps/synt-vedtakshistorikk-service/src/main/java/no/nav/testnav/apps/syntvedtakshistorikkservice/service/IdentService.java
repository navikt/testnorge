package no.nav.testnav.apps.syntvedtakshistorikkservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.PdlProxyConsumer;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.DollySearchServiceConsumer;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.pdl.PdlPerson;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.pdl.PdlPersonBolk;
import no.nav.testnav.apps.syntvedtakshistorikkservice.domain.IdentMedKontonr;
import no.nav.testnav.apps.syntvedtakshistorikkservice.domain.Kontoinfo;
import no.nav.testnav.libs.data.dollysearchservice.v1.legacy.PersonDTO;
import no.nav.testnav.libs.data.dollysearchservice.v1.legacy.PersonSearch;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentService {

    private final DollySearchServiceConsumer dollySearchServiceConsumer;
    private final ArenaForvalterService arenaForvalterService;
    private final PdlProxyConsumer pdlProxyConsumer;
    private final Random RANDOM = new SecureRandom();
    private static final int MAX_SEARCH_REQUESTS = 20;
    private static final int PAGE_SIZE = 10;
    private static final String BOSATT_STATUS = "BOSATT";
    private static final List<IdentMedKontonr> IDENTER_MED_KONTONR;

    static {
        ObjectMapper objectMapper = new ObjectMapper();
        URL resourceIdenterMedKontonr = Resources.getResource("files/identer_med_kontonr.json");
        IDENTER_MED_KONTONR = new ArrayList<>();
        try {
            Collection<IdentMedKontonr> identCollection = objectMapper.readValue(resourceIdenterMedKontonr, new TypeReference<>() {
            });
            IDENTER_MED_KONTONR.addAll(identCollection);

        } catch (IOException e) {
            log.error("Kunne ikke laste inn identer med kontonr.", e);
        }
    }

    public List<PersonDTO> getUtvalgteIdenterIAldersgruppe(
            int antallNyeIdenter,
            int minimumAlder,
            int maksimumAlder,
            boolean maaVaereBosatt

    ) {
        List<PersonDTO> utvalgteIdenter = new ArrayList<>(antallNyeIdenter);

        var randomSeed = RANDOM.nextInt();
        var page = 0;
        while (page < MAX_SEARCH_REQUESTS && utvalgteIdenter.size() < antallNyeIdenter) {
            var request = getSearchRequest(randomSeed, page, minimumAlder, maksimumAlder, maaVaereBosatt ? BOSATT_STATUS : null, null);
            var response = dollySearchServiceConsumer.search(request);

            var numberOfPages = 0;
            if (nonNull(response)) {
                numberOfPages = response.getNumberOfItems() / PAGE_SIZE;
                for (PersonDTO person : response.getItems()) {
                    if (arenaForvalterService.arbeidssoekerIkkeOpprettetIArena(person.getIdent())){
                        utvalgteIdenter.add(person);
                        if (utvalgteIdenter.size() >= antallNyeIdenter) break;
                    }
                }
            }

            page++;
            if (page > numberOfPages) page = MAX_SEARCH_REQUESTS;
        }

        return utvalgteIdenter;
    }

    public List<PersonDTO> getUtvalgteIdenterIAldersgruppeMedBarnUnder18(
            int antallNyeIdenter,
            int minimumAlder,
            int maksimumAlder,
            LocalDate tidligsteDatoBarnetillegg,
            boolean maaVaereBosatt
    ) {
        List<PersonDTO> utvalgteIdenter = new ArrayList<>();

        var randomSeed = RANDOM.nextInt();
        var page = 0;
        while (page < MAX_SEARCH_REQUESTS && utvalgteIdenter.size() < antallNyeIdenter) {
            var request = getSearchRequest(randomSeed, page, minimumAlder, maksimumAlder, maaVaereBosatt ? BOSATT_STATUS:  null, true);
            var response = dollySearchServiceConsumer.search(request);

            var numberOfPages = 0;
            if (nonNull(response)){
                numberOfPages = response.getNumberOfItems() / PAGE_SIZE;
                for (PersonDTO person : response.getItems()) {
                    if (validBarn(person.getForelderBarnRelasjoner().getBarn(), tidligsteDatoBarnetillegg)){
                        utvalgteIdenter.add(person);
                        if (utvalgteIdenter.size() >= antallNyeIdenter) break;
                    }
                }
            }

            page++;
            if (page > numberOfPages) page = MAX_SEARCH_REQUESTS;
        }

        return utvalgteIdenter;
    }

    private boolean validBarn(List<String> barn, LocalDate tidligsteDatoBarnetillegg) {
        if (isNull(barn) || barn.isEmpty()) return false;
        var pdlBolk = pdlProxyConsumer.getPdlPersoner(barn);

        List<PdlPersonBolk.PersonBolk> gyldigeBarn = new ArrayList<>();
        if(nonNull(pdlBolk) && nonNull(pdlBolk.getData())){
            gyldigeBarn = pdlBolk.getData().getHentPersonBolk().stream()
                    .filter(personBolk -> under18VedTidspunkt(personBolk, tidligsteDatoBarnetillegg))
                    .toList();
        }

        return !gyldigeBarn.isEmpty();
    }

    private boolean under18VedTidspunkt(PdlPersonBolk.PersonBolk personBolk, LocalDate tidspunkt) {
        var person = personBolk.getPerson();
        if (nonNull(person) && nonNull(person.getFoedselsdato()) && !person.getFoedselsdato().isEmpty()){
            var foedselsdato = person.getFoedselsdato().getFirst().getFoedselsdato();

            if(nonNull(person.getDoedsfall()) && !person.getDoedsfall().isEmpty()){
                var doedsdato = person.getDoedsfall().getFirst().getDoedsdato();
                if (doedsdato.isBefore(tidspunkt.plusDays(1))) return false;
            }
            var alder = Math.toIntExact(ChronoUnit.YEARS.between(foedselsdato, tidspunkt));
            return alder > -1 && alder < 18;
        }

        return false;
    }

    public Kontoinfo getIdentMedKontoinformasjon() {
        var ident = IDENTER_MED_KONTONR.get(RANDOM.nextInt(IDENTER_MED_KONTONR.size()));
        var pdlPerson = pdlProxyConsumer.getPdlPerson(ident.getIdent());
        if (isNull(pdlPerson) || isNull(pdlPerson.getData())) return null;
        var navnInfo = pdlPerson.getData().getHentPerson().getNavn();
        var boadresseInfo = pdlPerson.getData().getHentPerson().getBostedsadresse();

        return Kontoinfo.builder()
                .fnr(ident.getIdent())
                .fornavn(navnInfo.isEmpty() ? "" : navnInfo.getFirst().getFornavn())
                .mellomnavn(navnInfo.isEmpty() || isNull(navnInfo.getFirst().getMellomnavn()) ? "" : navnInfo.getFirst().getMellomnavn())
                .etternavn(navnInfo.isEmpty() ? "" : navnInfo.getFirst().getEtternavn())
                .kontonummer(ident.getKontonummer())
                .adresseLinje1(getAdresseLinje(boadresseInfo))
                .postnr(boadresseInfo.isEmpty() ? "" : boadresseInfo.getFirst().getVegadresse().getPostnummer())
                .landkode("NO")
                .build();
    }

    private String getAdresseLinje(List<PdlPerson.Boadresse> boadresse) {
        if (boadresse.isEmpty() || isNull(boadresse.getFirst().getVegadresse())) return "";
        var vegadresse = boadresse.getFirst().getVegadresse();
        var husbokstav = isNull(vegadresse.getHusbokstav()) ? "" : vegadresse.getHusbokstav();
        return vegadresse.getAdressenavn() + " " + vegadresse.getHusnummer() + husbokstav;
    }

    private PersonSearch getSearchRequest(
            int randomSeed,
            int page,
            int minimumAlder,
            int maksimumAlder,
            String personstatus,
            Boolean harBarn
    ) {
        var request = PersonSearch.builder()
                .kunLevende(true)
                .randomSeed(randomSeed)
                .page(page)
                .pageSize(PAGE_SIZE)
                .alder(PersonSearch.AlderSearch.builder()
                        .fra(minimumAlder)
                        .til(maksimumAlder)
                        .build())
                .build();

        if (nonNull(personstatus) && !personstatus.isEmpty()) {
            request.setPersonstatus(PersonSearch.PersonstatusSearch.builder()
                    .status(personstatus)
                    .build());
        }

        if (nonNull(harBarn) && Boolean.TRUE.equals(harBarn)) {
            request.setRelasjoner(PersonSearch.RelasjonSearch.builder()
                    .harBarn(true)
                    .build());
        }

        return request;
    }
}
