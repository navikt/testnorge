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
import no.nav.registre.orkestratoren.consumer.PopulasjonerConsumer;
import no.nav.registre.orkestratoren.consumer.StatistikkConsumer;
import no.nav.registre.testnorge.libs.dto.hendelse.v1.HendelseDTO;
import no.nav.registre.testnorge.libs.reporting.Reporting;

@Slf4j
@Service
@RequiredArgsConstructor
public class SykemeldingOrkestreringsService {

    private final PopulasjonerConsumer populasjonerConsumer;
    private final HendelseConsumer hendelseConsumer;
    private final StatistikkConsumer statistikkConsumer;
    private final SykemeldingSyntetiseringsService sykemeldingSyntetiseringsService;
    private final DecimalFormat format = new DecimalFormat("##.0");

    public void orkistrer(Reporting reporting) {

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
            log.warn("Ingen aktive arbeidsforhold. Avslutter syntetisering av sykemeldinger.");
            reporting.warn("Ingen aktive arbeidsforhold. Avslutter syntetisering av sykemeldinger.");
            return;
        }
        double antallSykemeldtIprosent = (double) aktiveSykemeldinger.size() / (double) aktiveArbeidsforhold.size();

        log.info("Det er {}% sykemeldt", format.format(antallSykemeldtIprosent * 100));
        reporting.info("Det er {}% sykemeldt", format.format(antallSykemeldtIprosent * 100));

        if (antallSykemeldtIprosent >= maalAntallSykemeldtIProsent) {
            log.info(
                    "Oppretter ingen nye sykemeldinger siden det er {}% aktive og målet er {}%",
                    format.format(antallSykemeldtIprosent * 100), format.format(maalAntallSykemeldtIProsent * 100)
            );
            reporting.info(
                    "Oppretter ingen nye sykemeldinger siden det er {}% aktive og målet er {}%",
                    format.format(antallSykemeldtIprosent * 100), format.format(maalAntallSykemeldtIProsent * 100)
            );
            return;
        }

        reporting.info(
                "Det er {}% aktive og målet er {}% sykemeldinger.",
                format.format(antallSykemeldtIprosent * 100), format.format(maalAntallSykemeldtIProsent * 100)
        );


        long antallSykemeldingerAOpprette = (long) Math.ceil(
                (maalAntallSykemeldtIProsent - antallSykemeldtIprosent) * aktiveArbeidsforhold.size()
        );

        Set<String> identerMedArbeidsforholdUtenSykemelding = aktiveArbeidsforhold
                .stream()
                .filter(value -> !aktiveSykemeldinger.contains(value))
                .limit(antallSykemeldingerAOpprette)
                .collect(Collectors.toSet());

        sykemeldingSyntetiseringsService.syntentiser(identerMedArbeidsforholdUtenSykemelding, startDate, reporting);
    }

    private Set<String> getIdenterIPopulasjonenFra(List<HendelseDTO> hendelseer, Set<String> populasjon) {
        return hendelseer.stream()
                .filter(value -> populasjon.contains(value.getIdent()))
                .map(HendelseDTO::getIdent)
                .collect(Collectors.toSet());
    }
}
