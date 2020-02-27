package no.nav.registre.spion.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.spion.domain.Vedtak;
import no.nav.registre.spion.provider.rs.request.SyntetiserSpionRequest;


@Service
@Slf4j
@RequiredArgsConstructor
public class SyntetiseringService {

    private final Random rand;

    public List<Vedtak> syntetiserVedtak(SyntetiserSpionRequest request) {
        LocalDate endDate = request.getEndDate();
        LocalDate startDate = request.getStartDate();
        Integer numPeriods = request.getNumPeriods();

        LocalDate sluttDato = endDate!=null ? endDate : startDate==null ? LocalDate.now() : startDate.plusMonths(18);
        LocalDate startDato = startDate!=null ? startDate : sluttDato.minusMonths(18);

        int antallPerioder = numPeriods != null ? numPeriods : rand.nextInt(15) + 1 ;

        List<Vedtak> resultat = lagListeMedVedtak(startDato, sluttDato, antallPerioder);

        int antallVedtak = resultat.size();
        String vedtakMsg = "Vedtak for {} periode(r) ble lagd.";
        String filterMsg = "Antall perioder ble for mange for valgt tidsrom. " + vedtakMsg;

        log.info(antallVedtak<antallPerioder ? filterMsg : vedtakMsg , antallVedtak);

        return resultat;
    }

    private List<Vedtak> lagListeMedVedtak(
            LocalDate startDato,
            LocalDate sluttDato,
            int antallPerioder
    ){
        List<Vedtak> vedtaksliste = new ArrayList<>();

        int dagerFoerFoerstePeriode = rand.nextInt(91);
        LocalDate lastSluttDato = startDato.plusDays(dagerFoerFoerstePeriode);

        for(int i=0; i<antallPerioder; i++){

            Vedtak vedtak = new Vedtak(lastSluttDato, i==0 ? true : false);
            lastSluttDato = vedtak.getTom();

            vedtaksliste.add(vedtak);
        }

        return vedtaksliste.stream().filter(vedtak ->
                !vedtak.getFom().isAfter(sluttDato) && !vedtak.getFom().isAfter(LocalDate.now()))
                .collect(Collectors.toList());
    }

}
