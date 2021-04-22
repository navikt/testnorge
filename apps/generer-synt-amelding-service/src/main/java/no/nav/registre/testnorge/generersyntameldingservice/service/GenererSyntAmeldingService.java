package no.nav.registre.testnorge.generersyntameldingservice.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.domain.dto.aareg.amelding.ArbeidsforholdPeriode;
import no.nav.registre.testnorge.generersyntameldingservice.consumer.SyntrestConsumer;
import no.nav.registre.testnorge.generersyntameldingservice.provider.request.SyntAmeldingRequest;
import no.nav.registre.testnorge.generersyntameldingservice.provider.response.ArbeidsforholdDTO;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenererSyntAmeldingService {

    private final SyntrestConsumer syntrestConsumer;

    public List<ArbeidsforholdDTO> generateAmeldinger(SyntAmeldingRequest request) {
        var antallMeldinger = getAntallMeldinger(request.getStartdato(), request.getSluttdato());

        var initialAmelding = syntrestConsumer.getEnkeltArbeidsforhold(
                ArbeidsforholdPeriode.builder()
                        .startdato(request.getStartdato())
                        .build(),
                request.getArbeidsforholdType());
        initialAmelding.setRapporteringsmaaned(request.getStartdato().format(DateTimeFormatter.ofPattern("yyyy-MM")));

        if (antallMeldinger > 1) {
            initialAmelding.emptyPermisjoner();
            initialAmelding.setNumEndringer(antallMeldinger - 1);

            var response = syntrestConsumer.getHistorikk(initialAmelding);
            response.add(0, initialAmelding);
            return response.stream().map(ArbeidsforholdDTO::new).collect(Collectors.toList());
        }

        return Collections.singletonList(new ArbeidsforholdDTO(initialAmelding));
    }

    int getAntallMeldinger(LocalDate startdato, LocalDate sluttdato) {
        var antallMeldinger = 0;
        while (startdato.isBefore(sluttdato.plusDays(1))) {
            antallMeldinger += 1;
            startdato = startdato.plusMonths(1);
        }
        return antallMeldinger;
    }

}
