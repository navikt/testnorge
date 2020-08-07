package no.nav.registre.orkestratoren.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.orkestratoren.consumer.HendelseConsumer;
import no.nav.registre.orkestratoren.consumer.PersonConsumer;
import no.nav.registre.orkestratoren.consumer.PopulasjonerConsumer;
import no.nav.registre.orkestratoren.consumer.StatistikkConsumer;
import no.nav.registre.orkestratoren.consumer.dto.PersonDTO;
import no.nav.registre.testnorge.dto.hendelse.v1.HendelseDTO;
import no.nav.registre.testnorge.libs.reporting.Reporting;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidsforholdOrkestreringsService {

    private final PopulasjonerConsumer populasjonerConsumer;
    private final HendelseConsumer hendelseConsumer;
    private final PersonConsumer personConsumer;
    private final StatistikkConsumer statistikkConsumer;
    private final ArbeidsforholdSyntetiseringsService arbeidsforholdSyntetiseringsService;

    public void orkistrer(Reporting reporting) {
        LocalDate startDate = LocalDate.now();

        Set<String> populasjon = populasjonerConsumer.getPopulasjon();

        if (populasjon.isEmpty()) {
            log.warn("Fant ingen personer i populasjonen, avslutter");
            reporting.warn("Fant ingen personer i populasjonen, avslutter");
        }

        Set<String> aktiveArbeidsforhold = getIdenterIPopulasjonenFra(
                hendelseConsumer.getArbeidsforholdAt(startDate),
                populasjon
        );


        Set<PersonDTO> personer = personConsumer.getPersoner(populasjon);

        if (personer.size() < populasjon.size()) {
            log.warn("Klarte ikke å hente ut alle personer {}/{}.", personer.size(), populasjon.size());
            reporting.warn("Klarte ikke å hente ut alle personer {}/{}.", personer.size(), populasjon.size());
        }

        if (personer.isEmpty()) {
            log.warn("Fant ingen personer, avslutter orkestrering...");
            reporting.warn("Fant ingen personer, avslutter orkestrering...");
            return;
        }

        log.info("Henter ut personer mellom 16-73 år");
        reporting.info("Henter ut personer mellom 16-73 år");
        Set<PersonDTO> personerSomKanVaereIArbeidstyrken = personer.stream()
                .filter(person -> person.getFoedselsdato().isBefore(LocalDateTime.now().minusYears(16)))
                .filter(person -> person.getFoedselsdato().isAfter(LocalDateTime.now().plusMinutes(73)))
                .filter(person -> aktiveArbeidsforhold.contains(person.getIdent()))
                .collect(Collectors.toSet());

        double antallArbeidstakereSomErIArbeidsstyrkenIProsent = statistikkConsumer
                .getAntallArbeidstakereSomErIArbeidsstyrkenIProsent();

        double antallPersonIArbeidsstyrkenIProsent = (double) aktiveArbeidsforhold.size() / (double) personerSomKanVaereIArbeidstyrken.size();
        if (antallPersonIArbeidsstyrkenIProsent > antallArbeidstakereSomErIArbeidsstyrkenIProsent) {
            log.info("Oppretter ingen flere arbeidsforhold da antall i arbeidstyrken er {}% og målet er {}%",
                    antallPersonIArbeidsstyrkenIProsent,
                    antallArbeidstakereSomErIArbeidsstyrkenIProsent
            );
            reporting.info("Oppretter ingen flere arbeidsforhold da antall i arbeidstyrken er {}% og målet er {}%",
                    antallPersonIArbeidsstyrkenIProsent,
                    antallArbeidstakereSomErIArbeidsstyrkenIProsent
            );
            return;
        }

        long antallSykemeldingerAOpprette = Math.round(
                (antallArbeidstakereSomErIArbeidsstyrkenIProsent - antallPersonIArbeidsstyrkenIProsent) * personerSomKanVaereIArbeidstyrken.size()
        );

        Set<PersonDTO> personerSomSkalOppretteArbeidsforhold = personerSomKanVaereIArbeidstyrken
                .stream()
                .filter(person -> !aktiveArbeidsforhold.contains(person.getIdent()))
                .limit(antallSykemeldingerAOpprette)
                .collect(Collectors.toSet());

        arbeidsforholdSyntetiseringsService.syntentiser(personerSomSkalOppretteArbeidsforhold, reporting);
    }

    private Set<String> getIdenterIPopulasjonenFra(List<HendelseDTO> hendelseer, Set<String> populasjon) {
        return hendelseer.stream()
                .filter(value -> populasjon.contains(value.getIdent()))
                .map(HendelseDTO::getIdent)
                .collect(Collectors.toSet());
    }
}
