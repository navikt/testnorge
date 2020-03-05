package no.nav.registre.spion.service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import no.nav.registre.spion.consumer.rs.AaregConsumer;
import no.nav.registre.spion.consumer.rs.HodejegerenConsumer;
import no.nav.registre.spion.consumer.rs.response.aareg.AaregResponse;
import static no.nav.registre.spion.utils.RandomUtils.getRandomBoundedNumber;

import no.nav.registre.spion.consumer.rs.response.hodejegeren.HodejegerenResponse;
import no.nav.registre.spion.provider.rs.response.SyntetiserVedtakResponse;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.spion.domain.Vedtak;

@Service
@Slf4j
@RequiredArgsConstructor
public class SyntetiseringService {

    private final AaregConsumer aaregConsumer;
    private final HodejegerenConsumer hodejegerenConsumer;

    public List<SyntetiserVedtakResponse> syntetiserVedtak(
            long groupId,
            String environment,
            Integer numPersons,
            LocalDate startDate,
            LocalDate endDate,
            Integer numPeriods) {

        LocalDate sluttDato = endDate!=null ? endDate : startDate==null ? LocalDate.now() : startDate.plusMonths(18);
        LocalDate startDato = startDate!=null ? startDate : sluttDato.minusMonths(18);

        List<String> utvalgteIdenter = finnIdenterMedArbeidsforhold(
                groupId,
                environment,
                numPersons!=null ? numPersons : 1);

        List<SyntetiserVedtakResponse> resultat = new ArrayList<>();

        for(int i=0; i< utvalgteIdenter.size(); i++){
            List<Vedtak> vedtaksliste = lagListeMedVedtak(
                    environment,
                    utvalgteIdenter.get(i),
                    startDato,
                    sluttDato,
                    numPeriods != null ? numPeriods : getRandomBoundedNumber(1, 15) );
            resultat.add(new SyntetiserVedtakResponse(utvalgteIdenter.get(i), vedtaksliste));
        }

        return resultat;
    }

    private List<Vedtak> lagListeMedVedtak(
            String miljoe,
            String utvalgtIdent,
            LocalDate startDato,
            LocalDate sluttDato,
            int antallPerioder
    ){
        List<Vedtak> vedtaksliste = new ArrayList<>();


        List<AaregResponse> aaregResponse = aaregConsumer.hentArbeidsforholdTilIdent(utvalgtIdent, miljoe);
        AaregResponse arbeidsforhold = aaregResponse.get(new Random().nextInt(aaregResponse.size()));

        HodejegerenResponse persondata = hodejegerenConsumer.hentPersondataTilIdent(utvalgtIdent, miljoe);

        LocalDate lastSluttDato = startDato.plusDays(getRandomBoundedNumber(0,90));

        for(int i=0; i<antallPerioder; i++){

            Vedtak vedtak = new Vedtak(persondata, arbeidsforhold, lastSluttDato, i==0);
            lastSluttDato = vedtak.getTom();

            vedtaksliste.add(vedtak);
        }

        return vedtaksliste.stream().filter(vedtak ->
                !vedtak.getFom().isAfter(sluttDato))
                .collect(Collectors.toList());
    }

    private List<String> finnIdenterMedArbeidsforhold(
            Long avspillergruppeId,
            String miljoe,
            int antallIdenterOensket
    ) {

        Set<String> identerMedArbeidsforhold =
                new HashSet<>(aaregConsumer.hentAlleIdenterMedArbeidsforhold(avspillergruppeId, miljoe));

        var identerMedArbeidsforholdListe = new ArrayList<>(identerMedArbeidsforhold);
        Collections.shuffle(identerMedArbeidsforholdListe);

        int antallIdenterFunnet = identerMedArbeidsforholdListe.size();
        if(antallIdenterFunnet<antallIdenterOensket){
            log.warn("Fant ikke nok identer med arbeidsforhold. Lager vedtak for {} identer",
                    antallIdenterFunnet);
            return identerMedArbeidsforholdListe.subList(0, antallIdenterFunnet);
        }else{
            return identerMedArbeidsforholdListe.subList(0, antallIdenterOensket);
        }
    }

}