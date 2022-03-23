package no.nav.testnav.apps.syntvedtakshistorikkservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.PdlProxyConsumer;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.PersonSearchConsumer;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.search.AlderSearch;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.search.RelasjonSearch;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.search.PersonSearchRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.search.PersonstatusSearch;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.pdl.PdlPerson;
import no.nav.testnav.apps.syntvedtakshistorikkservice.domain.IdentMedKontonr;
import no.nav.testnav.apps.syntvedtakshistorikkservice.domain.Kontoinfo;
import no.nav.testnav.libs.dto.personsearchservice.v1.FolkeregisterpersonstatusDTO;
import no.nav.testnav.libs.dto.personsearchservice.v1.PersonDTO;
import no.nav.testnav.libs.servletcore.util.IdentUtil;
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

    public List<String> getUtvalgteIdenterIAldersgruppe(
            int antallNyeIdenter,
            int minimumAlder,
            int maksimumAlder,
            LocalDate tidligsteDatoBosatt

    ) {
        List<String> utvalgteIdenter = new ArrayList<>(antallNyeIdenter);

        var randomSeed = rand.nextFloat() + "";
        var page = 1;
        while (page < MAX_SEARCH_REQUESTS && utvalgteIdenter.size() < antallNyeIdenter) {
            var request = getSearchRequest(randomSeed, page, minimumAlder, maksimumAlder, BOSATT_STATUS, null);
            var response = personSearchConsumer.search(request);
            if (isNull(response)) break;
            int numberOfPages = response.getNumberOfItems() / PAGE_SIZE;

            for (PersonDTO person : response.getItems()) {
                //TODO: legg til validering bosatt når data kvalitet på gyldighetsdato er bedre
                if (arenaForvalterService.arbeidssoekerIkkeOpprettetIArena(person.getIdent()))
                    utvalgteIdenter.add(person.getIdent());
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
        List<String> utvalgteIdenter = new ArrayList<>();

        var randomSeed = rand.nextFloat() + "";
        var page = 1;
        while (page < MAX_SEARCH_REQUESTS && utvalgteIdenter.size() < antallNyeIdenter) {
            var request = getSearchRequest(randomSeed, page, minimumAlder, maksimumAlder, BOSATT_STATUS, true);
            var response = personSearchConsumer.search(request);
            if (isNull(response)) break;
            int numberOfPages = response.getNumberOfItems() / PAGE_SIZE;

            for (PersonDTO person : response.getItems()) {
                //TODO: legg til validering bosatt når data kvalitet på gyldighetsdato er bedre
                if (validBarn(person, tidligsteDatoBarnetillegg)) utvalgteIdenter.add(person.getIdent());
                if (utvalgteIdenter.size() >= antallNyeIdenter) break;
            }

            page++;
            if (page > numberOfPages) break;
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

    private boolean validBarn(PersonDTO person, LocalDate tidligsteDatoBarnetillegg) {
        var barnIdenter = person.getForelderBarnRelasjoner().getBarn().stream()
                .filter(ident -> under18VedTidspunkt(ident, tidligsteDatoBarnetillegg))
                .toList();

        return !barnIdenter.isEmpty();
    }

    private boolean under18VedTidspunkt(String ident, LocalDate tidspunkt) {
        var month = Integer.parseInt(ident.substring(2, 4)) - 80;
        var oppdatertFnr = ident.substring(0, 2) + month + ident.substring(4);
        var foedselsdato = IdentUtil.getFoedselsdatoFraIdent(oppdatertFnr);

        var alder = Math.toIntExact(ChronoUnit.YEARS.between(foedselsdato, tidspunkt));
        return alder > -1 && alder < 18;
    }

    public Kontoinfo getIdentMedKontoinformasjon() {
        var ident = IDENTER_MED_KONTONR.get(rand.nextInt(IDENTER_MED_KONTONR.size()));
        var pdlPerson = pdlProxyConsumer.getPdlPerson(ident.getIdent());
        if (isNull(pdlPerson)) return null;
        var navnInfo = pdlPerson.getData().getHentPerson().getNavn();
        var boadresseInfo = pdlPerson.getData().getHentPerson().getBostedsadresse();

        return Kontoinfo.builder()
                .fnr(ident.getIdent())
                .fornavn(navnInfo.isEmpty() ? "" : navnInfo.get(0).getFornavn())
                .mellomnavn(navnInfo.isEmpty() ? "" : navnInfo.get(0).getMellomnavn())
                .etternavn(navnInfo.isEmpty() || isNull(navnInfo.get(0).getMellomnavn()) ? "" : navnInfo.get(0).getEtternavn())
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

    private PersonSearchRequest getSearchRequest(
            String randomSeed,
            int page,
            int minimumAlder,
            int maksimumAlder,
            String personstatus,
            Boolean harBarn
    ) {
        var request = PersonSearchRequest.builder()
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

        if (nonNull(harBarn) && harBarn) {
            request.setRelasjoner(RelasjonSearch.builder()
                    .barn(Boolean.TRUE)
                    .build());
        }

        return request;

    }
}
