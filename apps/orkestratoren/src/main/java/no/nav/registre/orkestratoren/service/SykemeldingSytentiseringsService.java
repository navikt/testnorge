package no.nav.registre.orkestratoren.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import no.nav.registre.orkestratoren.consumer.ArbeidsforholdConsumer;
import no.nav.registre.orkestratoren.consumer.SyntSykemeldingConsumer;
import no.nav.registre.testnorge.dto.arbeidsforhold.v1.ArbeidsforholdDTO;

@Slf4j
@Service
@RequiredArgsConstructor
public class SykemeldingSytentiseringsService {

    private final ArbeidsforholdConsumer arbeidsforholdConsumer;
    private final SyntSykemeldingConsumer syntSykemeldingConsumer;

    public void syntentiser(Set<String> identer, LocalDate startDate) {
        log.info("Syntentiserer {} ny(e) sykemelding(er) med start dato {}.", identer.size(), startDate.toString());
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
                log.error("Klarte ikke å hente å hente arbeidsforhold for {}.", ident, e);
            }
        });

        syntSykemeldingMap.forEach((ident, future) -> {
            try {
                future.get();
            } catch (Exception e) {
                log.error("Klarte ikke å opprette synt sykemelding for {}.", ident, e);
            }
        });
    }
}
