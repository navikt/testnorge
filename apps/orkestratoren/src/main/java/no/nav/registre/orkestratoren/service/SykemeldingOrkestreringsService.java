package no.nav.registre.orkestratoren.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.orkestratoren.consumer.HendelseConsumer;
import no.nav.registre.orkestratoren.consumer.PopulasjonerConsumer;
import no.nav.registre.orkestratoren.consumer.StatistikkConsumer;
import no.nav.registre.testnorge.dto.hendelse.v1.HendelseDTO;

@Slf4j
@Service
@RequiredArgsConstructor
public class SykemeldingOrkestreringsService {

    private final PopulasjonerConsumer populasjonerConsumer;
    private final HendelseConsumer hendelseConsumer;
    private final StatistikkConsumer statistikkConsumer;
    private final SykemeldingSytentiseringsService sykemeldingSytentiseringsService;

    public void orkistrer() {

        LocalDate startDate = LocalDate.now();

        Set<String> populasjon = populasjonerConsumer.getPopulasjon();

        Set<String> aktiveArbeidsforhold = getIdenterIPopulasjonenFra(
                hendelseConsumer.getArbeidsforholdAt(startDate),
                populasjon
        );

        Set<String> aktiveSykemeldinger = getIdenterIPopulasjonenFra(
                hendelseConsumer.getSykemeldingerAt(startDate),
                populasjon
        );

        double maalAntallSykemeldtIProsent = statistikkConsumer.getAntallSykemeldtIProsent();

        if (aktiveArbeidsforhold.isEmpty()) {
            log.warn("Ingen akrive arbeidsforhold. Avslutter syntentisering av sykemeldinger.");
            return;
        }
        double antallSykemeldtIprosent = (double) aktiveSykemeldinger.size() / (double) aktiveArbeidsforhold.size();

        log.info("Det er {}% sykemeldt", antallSykemeldtIprosent * 100);

        if (antallSykemeldtIprosent >= maalAntallSykemeldtIProsent) {
            log.info(
                    "Oppretter ingen flere sykemeldinger siden det er {}% aktive og målet er {}%",
                    antallSykemeldtIprosent * 100,
                    maalAntallSykemeldtIProsent * 100
            );
            return;
        }

        int antallSykemeldingerAOpprette = (int) Math.round(
                (maalAntallSykemeldtIProsent - antallSykemeldtIprosent) * aktiveArbeidsforhold.size()
        );

        Set<String> identerMedArbeidsforholdUtenSykemelding = aktiveArbeidsforhold
                .stream()
                .filter(value -> !aktiveSykemeldinger.contains(value))
                .limit(antallSykemeldingerAOpprette)
                .collect(Collectors.toSet());

        sykemeldingSytentiseringsService.syntentiser(identerMedArbeidsforholdUtenSykemelding, startDate);
    }

    private Set<String> getIdenterIPopulasjonenFra(List<HendelseDTO> hendelseer, Set<String> populasjon) {
        return hendelseer.stream()
                .filter(value -> populasjon.contains(value.getIdent()))
                .map(HendelseDTO::getIdent)
                .collect(Collectors.toSet());
    }
}
