package no.nav.registre.bisys.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import lombok.RequiredArgsConstructor;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import no.nav.bidrag.ui.bisys.kodeverk.KodeSoknGrKomConstants;
import no.nav.bidrag.ui.bisys.soknad.Soknad;
import no.nav.registre.bisys.consumer.rs.SyntBisysConsumer;
import no.nav.registre.bisys.consumer.rs.responses.SyntetisertBidragsmelding;
import no.nav.registre.bisys.exception.SyntetisertBidragsmeldingException;
import no.nav.registre.bisys.provider.requests.SyntetiserBisysRequest;
import no.nav.registre.bisys.service.utils.Barn;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.registre.testnorge.consumers.hodejegeren.responses.Relasjon;
import no.nav.registre.testnorge.consumers.hodejegeren.responses.RelasjonsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyntetiseringService {

    private static final String KIBANA_X_STATUS = "x_status";

    public static final String RELASJON_MOR = "MORA";
    public static final String RELASJON_FAR = "FARA";

    public static final LocalDate MIN_MOTTATT_DATO = new LocalDate(2007, 1, 1);
    public static final int MAX_AGE_BARN_AT_MOTTATTDATO = 20;

    private final HodejegerenConsumer hodejegerenConsumer;
    private final SyntBisysConsumer syntBisysConsumer;

    @Value("${USE_HISTORICAL_MOTTATTDATO}")
    private boolean useHistoricalMottattdato;

    public List<SyntetisertBidragsmelding> generateBidragsmeldinger(
            SyntetiserBisysRequest syntetiserBisysRequest) throws SyntetisertBidragsmeldingException {

        List<String> identerMedFoedselsmelding = finnFoedteIdenter(syntetiserBisysRequest.getAvspillergruppeId());
        List<Barn> utvalgteIdenter = selectValidUids(
                syntetiserBisysRequest.getAntallNyeIdenter(),
                identerMedFoedselsmelding,
                syntetiserBisysRequest.getMiljoe());

        if (utvalgteIdenter.size() < syntetiserBisysRequest.getAntallNyeIdenter()) {
            log.warn(
                    "Fant ikke nok identer registrert med mor og far. Oppretter {} bidragsmelding(er).",
                    utvalgteIdenter.size());
        }

        if (!utvalgteIdenter.isEmpty()) {

            List<SyntetisertBidragsmelding> bidragsmeldinger = syntBisysConsumer.getSyntetiserteBidragsmeldinger(utvalgteIdenter.size());

            setRelationsInBidragsmeldinger(utvalgteIdenter, bidragsmeldinger);
            return bidragsmeldinger;

        } else {
            throw new SyntetisertBidragsmeldingException("Ingen identer funnet!");
        }
    }

    private List<Barn> selectValidUids(
            int antallIdenter, List<String> identerMedFoedselsmelding, String miljoe) {
        List<Barn> utvalgteIdenter = new ArrayList<>();

        for (String ident : identerMedFoedselsmelding) {

            if (isBarnTooOld(ident)) {
                continue;
            }

            RelasjonsResponse relasjonsResponse = finnRelasjonerTilIdent(ident, miljoe);
            List<Relasjon> relasjoner = relasjonsResponse.getRelasjoner();

            String morFnr = "";
            String farFnr = "";

            for (Relasjon relasjon : relasjoner) {
                if (RELASJON_MOR.equals(relasjon.getTypeRelasjon())) {
                    morFnr = relasjon.getFnrRelasjon();
                } else if (RELASJON_FAR.equals(relasjon.getTypeRelasjon())) {
                    farFnr = relasjon.getFnrRelasjon();
                }
            }

            if (!morFnr.isEmpty() && !farFnr.isEmpty()) {
                utvalgteIdenter.add(Barn.builder().fnr(ident).morFnr(morFnr).farFnr(farFnr).build());
            }

            if (utvalgteIdenter.size() >= antallIdenter) {
                break;
            }
        }

        return utvalgteIdenter;
    }

    private void setRelationsInBidragsmeldinger(
            List<Barn> utvalgteIdenter, List<SyntetisertBidragsmelding> bidragsmeldinger) {
        for (SyntetisertBidragsmelding bidragsmelding : bidragsmeldinger) {
            finalizeBidragsmeldinger(utvalgteIdenter, bidragsmelding);
        }
    }

    private boolean isBarnTooOld(String baFnr) {
        LocalDate birthdate = getBirthdate(baFnr);
        if (useHistoricalMottattdato) {
            return birthdate.isBefore(MIN_MOTTATT_DATO.minusYears(MAX_AGE_BARN_AT_MOTTATTDATO));
        } else {
            return birthdate.isBefore(LocalDate.now().minusYears(MAX_AGE_BARN_AT_MOTTATTDATO));
        }
    }

    private void finalizeBidragsmeldinger(List<Barn> utvalgteIdenter, SyntetisertBidragsmelding bidragsmelding) {
        Barn barn = utvalgteIdenter.get(0);

        String originalBarnFnr = barn.getFnr();

        LocalDate mottattdato = LocalDate.now();
        mottattdato = useHistoricalMottattdato ? getHistoricalMottattdato(bidragsmelding, barn) : mottattdato;

        Optional<Barn> youngAdult = findYoungAdultIfRequired(bidragsmelding, barn.getFnr(), utvalgteIdenter, mottattdato);
        Optional<Barn> barnInBidragAgeRange = findBarnInBidragAgeRangeIfRequired(
                bidragsmelding, barn.getFnr(), utvalgteIdenter, mottattdato);

        if (youngAdult.isPresent()) {
            barn = youngAdult.get();
            mottattdato = useHistoricalMottattdato ? getHistoricalMottattdato(bidragsmelding, barn) : mottattdato;
        } else if (barnInBidragAgeRange.isPresent()) {
            barn = barnInBidragAgeRange.get();
            mottattdato = useHistoricalMottattdato ? getHistoricalMottattdato(bidragsmelding, barn) : mottattdato;
        }

        alignBaAlderWithMottattdato(bidragsmelding, getBirthdate(barn.getFnr()), mottattdato);

        adjustSoktFra(bidragsmelding, getBirthdate(barn.getFnr()), mottattdato);

        bidragsmelding.setBarn(barn.getFnr());
        bidragsmelding.setBidragsmottaker(barn.getMorFnr());
        bidragsmelding.setBidragspliktig(barn.getFarFnr());

        bidragsmelding.setMottattDato(mottattdato.toString(DateTimeFormat.forPattern(Soknad.STANDARD_DATE_FORMAT_TESTNORGEBISYS_REQUEST)));

        if (originalBarnFnr.equals(bidragsmelding.getBarn()) && !utvalgteIdenter.isEmpty()) {
            utvalgteIdenter.remove(0);
        }
    }

    private LocalDate getHistoricalMottattdato(SyntetisertBidragsmelding bidragsmelding, Barn barn) {
        LocalDate mottattdato = getBirthdate(barn.getFnr()).plusMonths(bidragsmelding.getBarnAlderIMnd());
        mottattdato = mottattdato.isAfter(LocalDate.now()) ? LocalDate.now() : mottattdato;
        mottattdato = mottattdato.isBefore(MIN_MOTTATT_DATO)
                ? getRandomizedMottattdatoInValidRange(barn.getFnr(), MIN_MOTTATT_DATO)
                : mottattdato;
        return mottattdato;
    }

    private void alignBaAlderWithMottattdato(SyntetisertBidragsmelding bidragsmelding, LocalDate fodselsdato, LocalDate mottattdato) {
        int adjustedBaAlder = Months.monthsBetween(fodselsdato, mottattdato).getMonths();
        bidragsmelding.setBarnAlderIMnd(adjustedBaAlder);
    }

    /**
     * Adjust soktFra for soktOm 18 or II to the first month after barn turns 18. For other types, ensure barn is born at
     * soktFra date.
     */
    private void adjustSoktFra(SyntetisertBidragsmelding bidragsmelding, LocalDate birthdate, LocalDate mottattdato) {

        int ageInMonthsAtMottattdato = Months.monthsBetween(birthdate.dayOfMonth().withMinimumValue(),
                mottattdato.dayOfMonth().withMinimumValue()).getMonths();

        if (isSoktOm18(bidragsmelding.getSoktOm())) {
            int age18Plus1MonthInMonths = Months.monthsBetween(birthdate,
                    birthdate.plusYears(18).plusMonths(1)).getMonths();
            int adjustedSoktFra = ageInMonthsAtMottattdato - age18Plus1MonthInMonths;
            bidragsmelding.setSoktFra(Integer.toString(adjustedSoktFra));

        } else {
            adjustLowerAndUpperBound(bidragsmelding, ageInMonthsAtMottattdato);
        }
    }

    private void adjustLowerAndUpperBound(SyntetisertBidragsmelding bidragsmelding, int ageInMonthsAtMottattdato) {
        int soktFra = parseSoktFra(bidragsmelding);

        if (ageInMonthsAtMottattdato - soktFra < 0) {
            soktFra = ageInMonthsAtMottattdato;
            bidragsmelding.setSoktFra(Integer.toString(soktFra));
        } else if (ageInMonthsAtMottattdato - soktFra > 18 * 12 - 1) {
            Random rand = new Random();
            int monthsAbove18 = ageInMonthsAtMottattdato - 18 * 12;
            int randomizedSoktFra = monthsAbove18 + (int) (rand.nextFloat() * 24 + 1);
            bidragsmelding.setSoktFra(Integer.toString(randomizedSoktFra));
        }
    }

    private LocalDate getRandomizedMottattdatoInValidRange(String baFnr, LocalDate minMottattdato) {
        int minAge = getAgeInMonths(baFnr, minMottattdato);
        int maxAge = 17 * 12 + 6;

        Random rand = new Random();
        int newAge = minAge + (int) (rand.nextFloat() * (maxAge - minAge + 1));
        return getBirthdate(baFnr).plusMonths(newAge);
    }

    private int parseSoktFra(SyntetisertBidragsmelding bidragsmelding) {
        int soktFra = 0;
        try {
            soktFra = Integer.parseInt(bidragsmelding.getSoktFra());
        } catch (NumberFormatException e) {
            log.warn("soktFra incorrectly formatted as {} in SyntetisertBidragsmelding, attempting to parse string as floating number", bidragsmelding.getSoktFra());
            return (int) Float.parseFloat(bidragsmelding.getSoktFra());
        }

        return soktFra;
    }

    private boolean isSoktOm18(String soktOm) {
        return KodeSoknGrKomConstants.BIDRAG_18_AAR.equals(soktOm) || KodeSoknGrKomConstants.BIDRAG_18_AAR_INNKREVING.equals(soktOm);
    }

    private Optional<Barn> findBarnInBidragAgeRangeIfRequired(
            SyntetisertBidragsmelding bidragsmelding, String baFnr, List<Barn> barnSelection, LocalDate mottattdato) {
        int soktFra = parseSoktFra(bidragsmelding);

        LocalDate soktFraDate = mottattdato.minusMonths(soktFra);
        soktFraDate = soktFraDate.isBefore(getBirthdate(baFnr)) ? getBirthdate(baFnr) : soktFraDate;

        if (!isSoktOm18(bidragsmelding.getSoktOm()) && !isAgeInRange(baFnr, soktFraDate, 0, 17 * 12 + 6)) {
            int age17AndOneHalfInMonths = 17 * 12 + 6;
            int lowerBound = Months.monthsBetween(MIN_MOTTATT_DATO.minusMonths(age17AndOneHalfInMonths), LocalDate.now()).getMonths() * (-1);
            int upperBound = age17AndOneHalfInMonths;

            return getBarnInValidAgeRange(barnSelection, soktFraDate, lowerBound, upperBound);
        }

        return Optional.empty();
    }

    /**
     * Ensures that barn is old enough for 18-årsbidrag.
     */
    private Optional<Barn> findYoungAdultIfRequired(SyntetisertBidragsmelding bidragsmelding, String baFnr, List<Barn> barnSelection, LocalDate mottattdato) {

        int soktFra = parseSoktFra(bidragsmelding);

        if (isSoktOm18(bidragsmelding.getSoktOm())
                && isAgeInRange(baFnr, mottattdato.minusMonths(soktFra), 0, 18 * 12)) {

            if (useHistoricalMottattdato) {
                int lowerBound = 17 * 12 + 6;
                int upperBound = 17 * 12 + 6 + Months.monthsBetween(MIN_MOTTATT_DATO, LocalDate.now()).getMonths();
                // Look for barn in the age range of currently 17.5 years to max 17.5 years at MIN_MOTTAT_DATO
                return getBarnInValidAgeRange(barnSelection, LocalDate.now().minusMonths(soktFra), lowerBound, upperBound);
            } else {
                int lowerBound = 17 * 12 + 6;
                int upperBound = lowerBound + (2 * 12);
                // Look for barn between 17.5 and 19.5 years of age
                return getBarnInValidAgeRange(barnSelection, LocalDate.now().minusMonths(soktFra), lowerBound, upperBound);
            }

        }

        return Optional.empty();
    }

    private int getAgeInMonths(String baFnr, LocalDate dateMeasured) {
        LocalDate fodselsdato = getBirthdate(baFnr);

        return Months.monthsBetween(fodselsdato.dayOfMonth().withMinimumValue(), dateMeasured.dayOfMonth().withMinimumValue()).getMonths();
    }

    private LocalDate getBirthdate(String baFnr) {
        String birthdateStr = baFnr.substring(0, 6);
        LocalDate birthdate = LocalDate.parse(birthdateStr, DateTimeFormat.forPattern("ddMMyy"));
        log.trace("Barn {} was born on {}", baFnr, birthdate);
        return birthdate;
    }

    /**
     * Look for BA that is valid age range
     *
     * @param barnSelection
     * @return
     */
    private Optional<Barn> getBarnInValidAgeRange(List<Barn> barnSelection, LocalDate soktFraDato, int lowerBound, int upperBound) {

        Random rand = new Random();
        int randomIdx = (int) (rand.nextFloat() * barnSelection.size());

        for (int i = randomIdx; i < barnSelection.size(); i++) {
            Barn barn = barnSelection.get(i);
            if (isAgeInRange(barn.getFnr(), soktFraDato, lowerBound, upperBound)) {
                Barn newBarn = barnSelection.remove(i);
                return Optional.of(newBarn);
            }
        }

        for (int i = randomIdx - 1; i > -1; i--) {
            Barn barn = barnSelection.get(i);
            if (isAgeInRange(barn.getFnr(), soktFraDato, lowerBound, upperBound)) {
                Barn newBarn = barnSelection.remove(i);
                return Optional.of(newBarn);
            }
        }

        return Optional.empty();
    }

    private boolean isAgeInRange(String fnr, LocalDate soktFraDato, int lowerBound, int upperBound) {
        int ageAtSoktFra = getAgeInMonths(fnr, soktFraDato);

        boolean withinUpperBound = ageAtSoktFra < upperBound;
        boolean withinLowerBound = ageAtSoktFra > lowerBound - 1;

        return withinUpperBound && withinLowerBound;
    }

    @Timed(value = "bisys.resource.latency", extraTags = {"operation", "hodejegeren"})
    private List<String> finnFoedteIdenter(Long avspillergruppeId) {
        return hodejegerenConsumer.getFoedte(avspillergruppeId);
    }

    @Timed(value = "bisys.resource.latency", extraTags = {"operation", "hodejegeren"})
    public RelasjonsResponse finnRelasjonerTilIdent(String ident, String miljoe) {
        return hodejegerenConsumer.getRelasjoner(ident, miljoe);
    }
}
