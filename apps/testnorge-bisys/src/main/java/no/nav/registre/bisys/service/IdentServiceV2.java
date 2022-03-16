package no.nav.registre.bisys.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.bisys.adapter.PersonSearchAdapter;
import no.nav.registre.bisys.adapter.model.Response;
import no.nav.registre.bisys.domain.search.FoedselSearch;
import no.nav.registre.bisys.domain.search.PersonSearch;
import no.nav.registre.bisys.domain.search.RelasjonSearch;
import no.nav.registre.bisys.consumer.response.SyntetisertBidragsmelding;
import no.nav.registre.bisys.domain.Barn;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Random;

import static no.nav.registre.bisys.service.utils.BidragUtils.isSoktOm18;
import static no.nav.registre.bisys.service.utils.BidragUtils.parseSoktFra;
import static no.nav.registre.bisys.service.utils.DateUtilsV2.getMonthsBetween;


@Slf4j
@Service
@RequiredArgsConstructor
public class IdentServiceV2 {

    private static final String RELASJON_MOR = "MOR";
    private static final String RELASJON_FAR = "FAR";
    private static final LocalDate MIN_MOTTATT_DATO = LocalDate.of(2007, 1, 1);

    private final Random rand = new Random();
    private final PersonSearchAdapter personSearchAdapter;

    @Value("${USE_HISTORICAL_MOTTATTDATO}")
    private boolean useHistoricalMottattdato;

    public Barn getValidId(SyntetisertBidragsmelding bidragsmelding) {
        boolean needYoungAdult = isSoktOm18(bidragsmelding.getSoktOm());
        if (needYoungAdult) {
            return getYoungAdultWithForeldre(bidragsmelding);
        } else {
            return getBarnWithForeldre(bidragsmelding);
        }
    }

    private Barn getBarnWithForeldre(SyntetisertBidragsmelding bidragsmelding) {
        int soktFra = parseSoktFra(bidragsmelding);
        var soktFraDate = LocalDate.now().minusMonths(soktFra);
        if (useHistoricalMottattdato) {
            int lowerBound = 0;
            int upperBound = 17 * 12 + 6 + getMonthsBetween(MIN_MOTTATT_DATO, LocalDate.now());
            // Look for barn in the age range of currently 17.5 years to max 17.5 years at MIN_MOTTATT_DATO
            return getBarnInValidAgeRange(soktFraDate.minusMonths(upperBound).plusDays(1), soktFraDate.minusMonths(lowerBound).minusDays(1));
        } else {
            int lowerBound = 0;
            int upperBound = 17 * 12 + 6;
            // Look for barn between 0 and 17.5 of age at soktFraDate
            return getBarnInValidAgeRange(soktFraDate.minusMonths(upperBound).plusDays(1), soktFraDate.minusMonths(lowerBound).minusDays(1));
        }
    }

    private Barn getYoungAdultWithForeldre(SyntetisertBidragsmelding bidragsmelding) {
        int soktFra = parseSoktFra(bidragsmelding);
        var soktFraDate = LocalDate.now().minusMonths(soktFra);
        if (useHistoricalMottattdato) {
            int lowerBound = 17 * 12 + 6;
            int upperBound = getMonthsBetween(MIN_MOTTATT_DATO.minusMonths(lowerBound), LocalDate.now());
            // Look for barn in the age range of currently 17.5 years at soktFraDate to max 17.5 years at MIN_MOTTATT_DATO
            return getBarnInValidAgeRange(soktFraDate.minusMonths(upperBound).plusDays(1), soktFraDate.minusMonths(lowerBound).minusDays(1));
        } else {
            int lowerBound = 17 * 12 + 6;
            int upperBound = lowerBound + (2 * 12);
            // Look for barn between 17.5 and 19.5 years of age at soktFraDate
            return getBarnInValidAgeRange(soktFraDate.minusMonths(upperBound).plusDays(1), soktFraDate.minusMonths(lowerBound).minusDays(1));
        }
    }

    private Barn getBarnInValidAgeRange(LocalDate bornFom, LocalDate bornTom) {
        //TODO sjekk om det trengs norsk statsborgerskap eller foedt Norge
        //TODO sjekk om det ikke kan sendes inn på samme barn flere ganger
        //TODO sjekk at man ikke returnerer samme barn som tidligere
        var searchRequest = PersonSearch.builder()
                .page(1)
                .pageSize(10)
                .randomSeed(rand.nextFloat() + "")
                .foedsel(FoedselSearch.builder()
                        .fom(bornFom)
                        .tom(bornTom)
                        .build())
                .relasjoner(RelasjonSearch.builder()
                        .mor(true)
                        .far(true)
                        .build())
                .build();

        var response = personSearchAdapter.search(searchRequest);
        if (response.isEmpty()){
            return null;
        } else{
            return convert(response.get(0));
        }
    }

    private Barn convert(Response response) {
        try {
            return Barn.builder()
                    .fnr(getIdent(response))
                    .foedselsdato(getFoedselsdato(response))
                    .farFnr(getForelder(response, RELASJON_FAR))
                    .morFnr(getForelder(response, RELASJON_MOR))
                    .build();
        } catch (Exception e) {
            log.error("Feil i konvertering av søke resultat");
            return null;
        }
    }

    private String getIdent(Response response) {
        return response
                .getHentIdenter()
                .getIdenter()
                .stream()
                .filter(identer -> identer.getGruppe().equals("FOLKEREGISTERIDENT"))
                .findFirst()
                .orElseThrow()
                .getIdent();
    }

    private LocalDate getFoedselsdato(Response response) {
        return response
                .getHentPerson()
                .getFoedsel()
                .stream().filter(foedsel -> !foedsel.getMetadata().getHistorisk())
                .findFirst()
                .orElseThrow()
                .getFoedselsdato();
    }

    private String getForelder(Response response, String forelder) {
        return response
                .getHentPerson()
                .getForelderBarnRelasjon()
                .stream()
                .filter(relasjon -> relasjon.getRelatertPersonsRolle().equals(forelder))
                .findFirst()
                .orElseThrow()
                .getRelatertPersonsIdent();
    }

}
