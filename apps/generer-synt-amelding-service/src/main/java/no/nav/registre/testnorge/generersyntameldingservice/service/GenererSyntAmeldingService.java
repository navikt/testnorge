package no.nav.registre.testnorge.generersyntameldingservice.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.generersyntameldingservice.consumer.SyntAmeldingConsumer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.domain.dto.aareg.amelding.ArbeidsforholdPeriode;
import no.nav.registre.testnorge.generersyntameldingservice.provider.request.SyntAmeldingRequest;
import no.nav.registre.testnorge.generersyntameldingservice.provider.response.ArbeidsforholdDTO;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenererSyntAmeldingService {

    private final SyntAmeldingConsumer syntAmeldingConsumer;

    public List<ArbeidsforholdDTO> generateAmeldinger(SyntAmeldingRequest request) {
        var antallMeldinger = getAntallMeldinger(request.getStartdato(), request.getSluttdato());

        var initialAmelding = syntAmeldingConsumer.getEnkeltArbeidsforhold(
                ArbeidsforholdPeriode.builder()
                        .startdato(request.getStartdato())
                        .build(),
                request.getArbeidsforholdType());
        initialAmelding.setRapporteringsmaaned(request.getStartdato().format(DateTimeFormatter.ofPattern("yyyy-MM")));

        if (antallMeldinger > 1) {
            initialAmelding.emptyPermisjoner();
            initialAmelding.setNumEndringer(antallMeldinger - 1);

            var response = syntAmeldingConsumer.getHistorikk(initialAmelding);
            response.add(0, initialAmelding);
            return response.stream().map(ArbeidsforholdDTO::new).collect(Collectors.toList());
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
