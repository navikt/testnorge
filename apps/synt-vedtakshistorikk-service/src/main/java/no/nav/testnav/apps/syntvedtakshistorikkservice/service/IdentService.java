package no.nav.testnav.apps.syntvedtakshistorikkservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.PdlProxyConsumer;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.PersonSearchConsumer;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.pdl.PdlPerson;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.pdl.PdlPersonBolk;
import no.nav.testnav.apps.syntvedtakshistorikkservice.domain.IdentMedKontonr;
import no.nav.testnav.apps.syntvedtakshistorikkservice.domain.Kontoinfo;
import no.nav.testnav.libs.dto.personsearchservice.v1.FolkeregisterpersonstatusDTO;
import no.nav.testnav.libs.dto.personsearchservice.v1.PersonDTO;
import no.nav.testnav.libs.dto.personsearchservice.v1.search.AlderSearch;
import no.nav.testnav.libs.dto.personsearchservice.v1.search.PersonSearch;
import no.nav.testnav.libs.dto.personsearchservice.v1.search.PersonstatusSearch;
import no.nav.testnav.libs.dto.personsearchservice.v1.search.RelasjonSearch;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentService {

    private final PersonSearchConsumer personSearchConsumer;
    private final ArenaForvalterService arenaForvalterService;
    private final PdlProxyConsumer pdlProxyConsumer;
    private final Random rand = new Random();
    private static final int MAX_SEARCH_REQUESTS = 20;
    private static final int PAGE_SIZE = 10;
    private static final String BOSATT_STATUS = "bosatt";
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
            LocalDate tidligsteDatoBosatt

    ) {
        List<PersonDTO> utvalgteIdenter = new ArrayList<>(antallNyeIdenter);

        var randomSeed = rand.nextFloat() + "";
        var page = 1;
        while (page < MAX_SEARCH_REQUESTS && utvalgteIdenter.size() < antallNyeIdenter) {
            var request = getSearchRequest(randomSeed, page, minimumAlder, maksimumAlder, BOSATT_STATUS, null);
            var response = personSearchConsumer.search(request);

            var numberOfPages = 0;
            if (nonNull(response)) {
                numberOfPages = response.getNumberOfItems() / PAGE_SIZE;
                for (PersonDTO person : response.getItems()) {
                    //TODO: legg til validering bosatt når data kvalitet på gyldighetsdato er bedre
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
            LocalDate tidligsteDatoBosatt,
            LocalDate tidligsteDatoBarnetillegg
    ) {
        List<PersonDTO> utvalgteIdenter = new ArrayList<>();

        var randomSeed = rand.nextFloat() + "";
        var page = 1;
        while (page < MAX_SEARCH_REQUESTS && utvalgteIdenter.size() < antallNyeIdenter) {
            var request = getSearchRequest(randomSeed, page, minimumAlder, maksimumAlder, BOSATT_STATUS, true);
            var response = personSearchConsumer.search(request);

            var numberOfPages = 0;
            if (nonNull(response)){
                numberOfPages = response.getNumberOfItems() / PAGE_SIZE;
                for (PersonDTO person : response.getItems()) {
                    //TODO: legg til validering bosatt når data kvalitet på gyldighetsdato er bedre
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

    private boolean validBosattdato(PersonDTO person, LocalDate tidligsteDatoBosatt) {
        var gyldigeBosattstatuser = person.getFolkeregisterpersonstatus().stream()
                .filter(status -> status.getStatus().equals(BOSATT_STATUS))
                .map(FolkeregisterpersonstatusDTO::getGyldighetstidspunkt)
                .filter(bosattdato -> bosattdato.isBefore(tidligsteDatoBosatt) || bosattdato.equals(tidligsteDatoBosatt))
                .toList();
        return !gyldigeBosattstatuser.isEmpty();
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
        if (nonNull(person) && nonNull(person.getFoedsel()) && !person.getFoedsel().isEmpty()){
            var foedselsdato = person.getFoedsel().get(0).getFoedselsdato();

            if(nonNull(person.getDoedsfall()) && !person.getDoedsfall().isEmpty()){
                var doedsdato = person.getDoedsfall().get(0).getDoedsdato();
                if (doedsdato.isBefore(tidspunkt.plusDays(1))) return false;
            }
            var alder = Math.toIntExact(ChronoUnit.YEARS.between(foedselsdato, tidspunkt));
            return alder > -1 && alder < 18;
        }

        return false;
    }

    public Kontoinfo getIdentMedKontoinformasjon() {
        var ident = IDENTER_MED_KONTONR.get(rand.nextInt(IDENTER_MED_KONTONR.size()));
        var pdlPerson = pdlProxyConsumer.getPdlPerson(ident.getIdent());
        if (isNull(pdlPerson) || isNull(pdlPerson.getData())) return null;
        var navnInfo = pdlPerson.getData().getHentPerson().getNavn();
        var boadresseInfo = pdlPerson.getData().getHentPerson().getBostedsadresse();

        return Kontoinfo.builder()
                .fnr(ident.getIdent())
                .fornavn(navnInfo.isEmpty() ? "" : navnInfo.get(0).getFornavn())
                .mellomnavn(navnInfo.isEmpty() || isNull(navnInfo.get(0).getMellomnavn()) ? "" : navnInfo.get(0).getMellomnavn())
                .etternavn(navnInfo.isEmpty() ? "" : navnInfo.get(0).getEtternavn())
                .kontonummer(ident.getKontonummer())
                .adresseLinje1(getAdresseLinje(boadresseInfo))
                .postnr(boadresseInfo.isEmpty() ? "" : boadresseInfo.get(0).getVegadresse().getPostnummer())
                .landkode("NO")
                .build();
    }

    private String getAdresseLinje(List<PdlPerson.Boadresse> boadresse) {
        if (boadresse.isEmpty() || isNull(boadresse.get(0).getVegadresse())) return "";
        var vegadresse = boadresse.get(0).getVegadresse();
        var husbokstav = isNull(vegadresse.getHusbokstav()) ? "" : vegadresse.getHusbokstav();
        return vegadresse.getAdressenavn() + " " + vegadresse.getHusnummer() + husbokstav;
    }

    private PersonSearch getSearchRequest(
            String randomSeed,
            int page,
            int minimumAlder,
            int maksimumAlder,
            String personstatus,
            Boolean harBarn
    ) {
        var request = PersonSearch.builder()
                .tag("TESTNORGE")
                .excludeTags(Arrays.asList("DOLLY", "ARENASYNT"))
                .kunLevende(true)
                .randomSeed(randomSeed)
                .page(page)
                .pageSize(PAGE_SIZE)
                .alder(AlderSearch.builder()
                        .fra((short) minimumAlder)
                        .til((short) maksimumAlder)
                        .build())
                .build();

        if (nonNull(personstatus) && !personstatus.isEmpty()) {
            request.setPersonstatus(PersonstatusSearch.builder()
                    .status(personstatus)
                    .build());
        }

        if (nonNull(harBarn) && Boolean.TRUE.equals(harBarn)) {
            request.setRelasjoner(RelasjonSearch.builder()
                    .harBarn("Y")
                    .build());
        }

        return request;

    }
}
