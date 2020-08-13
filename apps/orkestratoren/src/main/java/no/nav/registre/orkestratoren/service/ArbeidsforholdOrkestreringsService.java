package no.nav.registre.orkestratoren.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.orkestratoren.consumer.HendelseConsumer;
import no.nav.registre.orkestratoren.consumer.PersonConsumer;
import no.nav.registre.orkestratoren.consumer.PopulasjonerConsumer;
import no.nav.registre.orkestratoren.consumer.StatistikkConsumer;
import no.nav.registre.testnorge.dto.hendelse.v1.HendelseDTO;
import no.nav.registre.testnorge.dto.person.v1.PersonDTO;
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
    private final DecimalFormat format = new DecimalFormat("##.0");

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

        log.info("Fant {} aktive arbeidsforhold på {}.", aktiveArbeidsforhold.size(), startDate);
        reporting.info("Fant {} aktive arbeidsforhold på {}.", aktiveArbeidsforhold.size(), startDate);

        Set<PersonDTO> personer = personConsumer.getPersoner(populasjon);

        if (personer.size() < populasjon.size()) {
            log.warn("Klarte ikke å hente ut alle personer {}/{}.", personer.size(), populasjon.size());
            reporting.warn("Klarte ikke å hente ut alle personer {}/{}.", personer.size(), populasjon.size());
        }

        if (personer.isEmpty()) {
            log.warn("Fant ingen personer, avslutter orkestrering");
            reporting.warn("Fant ingen personer, avslutter orkestrering");
            return;
        }

        log.info("Henter ut personer mellom 15-74 år");
        reporting.info("Henter ut personer mellom 15-74 år");
        Set<PersonDTO> personerSomKanVaereIArbeidstyrken = personer.stream()
                .filter(person -> person.getFoedselsdato().plusYears(15).isBefore(LocalDate.now()))
                .filter(person -> person.getFoedselsdato().plusYears(74).isAfter(LocalDate.now()))
                .collect(Collectors.toSet());

        if (personerSomKanVaereIArbeidstyrken.isEmpty()) {
            log.warn("Fant ingen som kan være i arbeidsstyrken, avslutter orkestrering.");
            reporting.warn("Fant ingen som kan være i arbeidsstyrken, avslutter orkestrering.");
            return;
        }

        double antallArbeidstakereSomErIArbeidsstyrkenIProsent = statistikkConsumer
                .getAntallArbeidstakereSomErIArbeidsstyrkenIProsent();

        double antallPersonIArbeidsstyrkenIProsent =
                (double) aktiveArbeidsforhold.size() / (double) personerSomKanVaereIArbeidstyrken.size();

        if (antallPersonIArbeidsstyrkenIProsent >= antallArbeidstakereSomErIArbeidsstyrkenIProsent) {
            log.info("Oppretter ingen flere arbeidsforhold da antall i arbeidstyrken er {}% og målet er {}%.",
                    format.format(antallPersonIArbeidsstyrkenIProsent * 100),
                    format.format(antallArbeidstakereSomErIArbeidsstyrkenIProsent * 100)
            );
            reporting.info("Oppretter ingen flere arbeidsforhold da antall i arbeidstyrken er {}% og målet er {}%.",
                    format.format(antallPersonIArbeidsstyrkenIProsent * 100),
                    format.format(antallArbeidstakereSomErIArbeidsstyrkenIProsent * 100)
            );
            return;
        }


        long antallSykemeldingerAOpprette = (long) Math.ceil(
                (antallArbeidstakereSomErIArbeidsstyrkenIProsent - antallPersonIArbeidsstyrkenIProsent) * personerSomKanVaereIArbeidstyrken.size()
        );

        reporting.info(
                "Antall personer i arbeidstyrken er {}% og målet er {}%.",
                format.format(antallPersonIArbeidsstyrkenIProsent * 100),
                format.format(antallArbeidstakereSomErIArbeidsstyrkenIProsent * 100)
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
