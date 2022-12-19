package no.nav.registre.testnorge.generersyntameldingservice.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import no.nav.registre.testnorge.generersyntameldingservice.consumer.SyntAmeldingConsumer;
import no.nav.registre.testnorge.generersyntameldingservice.domain.ArbeidsforholdType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.domain.dto.aareg.amelding.ArbeidsforholdPeriode;
import no.nav.registre.testnorge.generersyntameldingservice.provider.response.ArbeidsforholdDTO;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyntAmeldingService {

    private final SyntAmeldingConsumer syntAmeldingConsumer;

    public List<ArbeidsforholdDTO> generateAmeldinger(
            LocalDate startdato,
            LocalDate sluttdato,
            ArbeidsforholdType arbeidsforholdType
    ) {
        var antallMeldinger = getAntallMeldinger(startdato, sluttdato);

        var initialAmelding = syntAmeldingConsumer.getEnkeltArbeidsforhold(
                ArbeidsforholdPeriode.builder()
                        .startdato(startdato)
                        .build(),
                arbeidsforholdType);
        initialAmelding.setRapporteringsmaaned(startdato.format(DateTimeFormatter.ofPattern("yyyy-MM")));

        if (antallMeldinger > 1) {
            initialAmelding.emptyPermisjoner();
            initialAmelding.setNumEndringer(antallMeldinger - 1);

            var response = syntAmeldingConsumer.getHistorikk(initialAmelding);
            response.add(0, initialAmelding);
            return response.stream().map(ArbeidsforholdDTO::new).toList();
        }

        return Collections.singletonList(new ArbeidsforholdDTO(initialAmelding));
    }

    int getAntallMeldinger(LocalDate startdato, LocalDate sluttdato) {
        if (sluttdato.isBefore(startdato)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sluttdato kan ikke være før startdato.");
        }
        var antallMeldinger = 0;
        var lastDayInEnddateMonth = sluttdato.withDayOfMonth(sluttdato.withDayOfMonth(1).lengthOfMonth());
        while (startdato.isBefore(lastDayInEnddateMonth.plusDays(1))) {
            antallMeldinger += 1;
            startdato = startdato.plusMonths(1);
        }
        return antallMeldinger;
    }

}
