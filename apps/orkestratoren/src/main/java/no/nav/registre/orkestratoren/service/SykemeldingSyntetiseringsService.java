package no.nav.registre.orkestratoren.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import no.nav.registre.orkestratoren.consumer.ArbeidsforholdConsumer;
import no.nav.registre.orkestratoren.consumer.SyntSykemeldingConsumer;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v1.ArbeidsforholdDTO;
import no.nav.registre.testnorge.libs.reporting.Reporting;

@Slf4j
@Service
@RequiredArgsConstructor
public class SykemeldingSyntetiseringsService {

    private final ArbeidsforholdConsumer arbeidsforholdConsumer;
    private final SyntSykemeldingConsumer syntSykemeldingConsumer;

    public void syntentiser(Set<String> identer, LocalDate startDate, Reporting reporting) {
        log.info("Syntentiserer {} nye sykemeldinger med start dato {}.", identer.size(), startDate.toString());
        reporting.info(
                "Syntentiserer {} nye sykemeldinger med start dato {}.",
                identer.size(), startDate.toString()
        );

        Map<String, CompletableFuture<ArbeidsforholdDTO>> arbeidsforholdMap = identer
                .stream()
                .collect(Collectors.toMap(
                        value -> value,
                        value -> arbeidsforholdConsumer.getArbeidsforholdAt(value, startDate)
                ));

        Map<String, CompletableFuture<Void>> syntSykemeldingMap = new HashMap<>();

        arbeidsforholdMap.forEach((ident, future) -> {
            try {
                ArbeidsforholdDTO arbeidsforhold = future.get();
                syntSykemeldingMap.put(
                        ident,
                        syntSykemeldingConsumer.createSyntSykemldinger(ident, arbeidsforhold, startDate)
                );
            } catch (Exception e) {
                log.error("Klarte ikke å hente arbeidsforhold for {}.", ident, e);
                reporting.error("Klarte ikke å hente arbeidsforhold for {}. Exception: {}", ident, e.getMessage());
            }
        });

        var count = new AtomicInteger();
        syntSykemeldingMap.forEach((ident, future) -> {
            try {
                future.get();
                count.incrementAndGet();
            } catch (Exception e) {
                log.error("Klarte ikke å opprette synt sykemelding for {}.", ident, e);
                reporting.error(
                        "Klarte ikke å opprette synt sykemelding for {}. Exception: {}",
                        ident, e.getMessage()
                );
            }
        });

        if (count.get() < identer.size()) {
            log.warn("Klarte bare å opprette {}/{} sykemeldinger.", count.get(), identer.size());
            reporting.warn("Klarte bare å opprette {}/{} sykemeldinger.", count.get(), identer.size());
        }

        if (count.get() == identer.size()) {
            log.info("Alle {} sykemeldinger opprettet.", count.get());
            reporting.info("Alle {} sykemeldinger opprettet.", count.get());
        }
    }
}
