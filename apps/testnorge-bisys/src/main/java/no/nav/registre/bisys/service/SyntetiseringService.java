package no.nav.registre.bisys.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import no.nav.bidrag.ui.bisys.soknad.Soknad;
import no.nav.registre.bisys.consumer.SyntBisysConsumer;
import no.nav.registre.bisys.consumer.response.SyntetisertBidragsmelding;

import no.nav.registre.bisys.domain.Barn;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static no.nav.registre.bisys.service.utils.DateUtils.getAgeInMonths;
import static no.nav.registre.bisys.service.utils.DateUtils.getMonthsBetween;
import static no.nav.registre.bisys.service.utils.BidragUtils.isSoktOm18;
import static no.nav.registre.bisys.service.utils.BidragUtils.parseSoktFra;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyntetiseringService {

    private final SyntBisysConsumer syntBisysConsumer;
    private final IdentService identService;
    private final Random rand = new Random();

    @Value("${USE_HISTORICAL_MOTTATTDATO}")
    private boolean useHistoricalMottattdato;

    private static final LocalDate MIN_MOTTATT_DATO = LocalDate.of(2007, 1, 1);

    public List<SyntetisertBidragsmelding> generateBidragsmeldinger(int antallIdenter) {
        var bidragsmeldinger = syntBisysConsumer.getSyntetiserteBidragsmeldinger(antallIdenter);

        for (var melding : bidragsmeldinger) {
            var barn = identService.getValidId(melding);
            if (barn != null) {
                finalizeBidragsmelding(barn, melding);
            }
        }

        var oppdaterteBidragsmeldinger = bidragsmeldinger.stream()
                .filter(bidragsmelding -> bidragsmelding.getBarn() != null && !bidragsmelding.getBarn().equals(""))
                .toList();

        if (oppdaterteBidragsmeldinger.size() < antallIdenter) {
            log.warn("Oppretter {} av {} bidragsmelding(er).", oppdaterteBidragsmeldinger.size(), antallIdenter);
        }

        return oppdaterteBidragsmeldinger;
    }

    private void finalizeBidragsmelding(Barn barn, SyntetisertBidragsmelding bidragsmelding) {
        var mottattdato = useHistoricalMottattdato ? getHistoricalMottattdato(bidragsmelding, barn) : LocalDate.now();

        alignBaAlderWithMottattdato(bidragsmelding, barn.getFoedselsdato(), mottattdato);
        adjustSoktFra(bidragsmelding, barn.getFoedselsdato(), mottattdato);

        bidragsmelding.setBarn(barn.getFnr());
        //TODO sjekke om mor alltid skal være bidragsmottaker
        bidragsmelding.setBidragsmottaker(barn.getMorFnr());
        bidragsmelding.setBidragspliktig(barn.getFarFnr());
        bidragsmelding.setMottattDato(mottattdato.format(DateTimeFormatter.ofPattern(Soknad.STANDARD_DATE_FORMAT_TESTNORGEBISYS_REQUEST)));
    }

    private LocalDate getHistoricalMottattdato(SyntetisertBidragsmelding bidragsmelding, Barn barn) {
        LocalDate mottattdato = barn.getFoedselsdato().plusMonths(bidragsmelding.getBarnAlderIMnd());
        mottattdato = mottattdato.isAfter(LocalDate.now()) ? LocalDate.now() : mottattdato;
        mottattdato = mottattdato.isBefore(MIN_MOTTATT_DATO)
                ? getRandomizedMottattdatoInValidRange(barn.getFoedselsdato(), MIN_MOTTATT_DATO)
                : mottattdato;
        return mottattdato;
    }

    private LocalDate getRandomizedMottattdatoInValidRange(LocalDate foedselsdato, LocalDate minMottattdato) {
        int minAge = getAgeInMonths(foedselsdato, minMottattdato);
        int maxAge = 17 * 12 + 6;
        int newAge = minAge + (rand.nextInt() * (maxAge - minAge + 1));
        return foedselsdato.plusMonths(newAge);
    }

    private void alignBaAlderWithMottattdato(SyntetisertBidragsmelding bidragsmelding, LocalDate fodselsdato, LocalDate mottattdato) {
        int adjustedBaAlder = getMonthsBetween(fodselsdato, mottattdato);
        bidragsmelding.setBarnAlderIMnd(adjustedBaAlder);
    }

    /**
     * Adjust soktFra for soktOm 18 or II to the first month after barn turns 18. For other types, ensure barn is born at
     * soktFra date.
     */
    private void adjustSoktFra(SyntetisertBidragsmelding bidragsmelding, LocalDate birthdate, LocalDate mottattdato) {
        int ageInMonthsAtMottattdato = getMonthsBetween(birthdate.withDayOfMonth(1), mottattdato.withDayOfMonth(1));

        if (isSoktOm18(bidragsmelding.getSoktOm())) {
            int age18Plus1MonthInMonths = getMonthsBetween(birthdate, birthdate.plusYears(18).plusMonths(1));
            int adjustedSoktFra = ageInMonthsAtMottattdato - age18Plus1MonthInMonths;
            bidragsmelding.setSoktFra(Integer.toString(adjustedSoktFra));
        } else {
            adjustLowerAndUpperBound(bidragsmelding, ageInMonthsAtMottattdato);
        }
    }

    private void adjustLowerAndUpperBound(SyntetisertBidragsmelding bidragsmelding, int ageInMonthsAtMottattdato) {
        int soktFra = parseSoktFra(bidragsmelding);

        if (ageInMonthsAtMottattdato - soktFra < 0) {
            bidragsmelding.setSoktFra(Integer.toString(ageInMonthsAtMottattdato));
        } else if (ageInMonthsAtMottattdato - soktFra > 18 * 12 - 1) {
            int monthsAbove18 = ageInMonthsAtMottattdato - 18 * 12;
            int randomizedSoktFra = monthsAbove18 + (rand.nextInt() * 24 + 1);
            bidragsmelding.setSoktFra(Integer.toString(randomizedSoktFra));
        }
    }

}
