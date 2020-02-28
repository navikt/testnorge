package no.nav.registre.spion.service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import no.nav.registre.spion.consumer.rs.AaregConsumer;
import no.nav.registre.spion.consumer.rs.response.aareg.AaregResponse;
import static no.nav.registre.spion.utils.RandomUtils.getRandomBoundedNumber;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.spion.domain.Vedtak;


@Service
@Slf4j
@RequiredArgsConstructor
public class SyntetiseringService {

    private final AaregConsumer aaregConsumer;

    public List<Vedtak> syntetiserVedtak(
            long avspillergruppeId,
            String miljoe,
            LocalDate startDate,
            LocalDate endDate,
            Integer numPeriods) {

        LocalDate sluttDato = endDate!=null ? endDate : startDate==null ? LocalDate.now() : startDate.plusMonths(18);
        LocalDate startDato = startDate!=null ? startDate : sluttDato.minusMonths(18);

        int antallPerioder = numPeriods != null ? numPeriods : getRandomBoundedNumber(1, 15) ;

        List<Vedtak> resultat = lagListeMedVedtak(avspillergruppeId, miljoe, startDato, sluttDato, antallPerioder);

        int antallVedtak = resultat.size();
        String vedtakMsg = "Vedtak for {} periode(r) ble lagd.";
        String filterMsg = "Antall perioder ble for mange for valgt tidsrom. " + vedtakMsg;

        log.info(antallVedtak<antallPerioder ? filterMsg : vedtakMsg , antallVedtak);
        return resultat;
    }

    private List<Vedtak> lagListeMedVedtak(
            long avspillergruppeId,
            String miljoe,
            LocalDate startDato,
            LocalDate sluttDato,
            int antallPerioder
    ){
        List<Vedtak> vedtaksliste = new ArrayList<>();

        String utvalgtIdent = finnIdentMedArbeidsforhold(avspillergruppeId, miljoe);

        AaregResponse[] aaregResponse = aaregConsumer.hentArbeidsforholdTilIdent(utvalgtIdent, miljoe);
        String orgnummer = aaregResponse[new Random().nextInt(aaregResponse.length)]
                .getArbeidsgiver().getOrganisasjonsnummer();

        LocalDate lastSluttDato = startDato.plusDays(getRandomBoundedNumber(0,90));

        for(int i=0; i<antallPerioder; i++){

            Vedtak vedtak = new Vedtak(utvalgtIdent, orgnummer, lastSluttDato, i==0);
            lastSluttDato = vedtak.getTom();

            vedtaksliste.add(vedtak);
        }

        return vedtaksliste.stream().filter(vedtak ->
                !vedtak.getFom().isAfter(sluttDato) && !vedtak.getFom().isAfter(LocalDate.now()))
                .collect(Collectors.toList());
    }

    private String finnIdentMedArbeidsforhold(Long avspillergruppeId, String miljoe) {

        Set<String> identerMedArbeidsforhold =
                new HashSet<>(aaregConsumer.hentAlleIdenterMedArbeidsforhold(avspillergruppeId, miljoe));

        var identerMedArbeidsforholdListe = new ArrayList<>(identerMedArbeidsforhold);
        Collections.shuffle(identerMedArbeidsforholdListe);

        return identerMedArbeidsforholdListe.get(0);

    }

}