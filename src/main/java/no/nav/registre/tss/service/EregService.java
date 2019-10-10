package no.nav.registre.tss.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import no.nav.registre.tss.consumer.rs.EregConsumer;
import no.nav.registre.tss.consumer.rs.EregMapperConsumer;
import no.nav.registre.tss.consumer.rs.request.EregMapperRequest;
import no.nav.registre.tss.consumer.rs.request.Knytning;
import no.nav.registre.tss.consumer.rs.request.Navn;
import no.nav.registre.tss.domain.TssType;
import no.nav.registre.tss.domain.TssTypeGruppe;

@Service
@RequiredArgsConstructor
@Slf4j
public class EregService {

    private final EregMapperConsumer eregMapperConsumer;
    private final EregConsumer eregConsumer;

    @Value("${testnorge.ereg.enhet.as}")
    private String ASEnhet;

    public Map<TssType, List<String>> opprettEregEnheter(Map<TssType, Integer> tssTypeAntallMap) {

        Integer totalAntallOrganissasjoner = tssTypeAntallMap.entrySet().stream()
                .filter(entry -> TssTypeGruppe.skalHaOrgnummer(TssTypeGruppe.getGruppe(entry.getKey())))
                .map(Map.Entry::getValue)
                .reduce(0, Integer::sum);

        List<String> orgnr = eregMapperConsumer.hentNyttOrgnr(totalAntallOrganissasjoner);

        Map<TssType, List<String>> typeMedOrgnr = getTssTypeListMap(orgnr);
        boolean sendtTilJenkins = eregMapperConsumer.opprett(typeMedOrgnr.entrySet().stream()
                .map(entry -> entry.getValue().stream()
                        .map(org -> opprettEregRequest(entry.getKey(), org))
                        .collect(Collectors.toList()))
                .flatMap(List::stream).collect(Collectors.toList()));

        if (!sendtTilJenkins)
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Kunne ikke opprette bedrifter i EREG");

        Map<String, Boolean> enhetStatus = typeMedOrgnr.values().stream()
                .map(orgnummere -> orgnummere.stream().collect(Collectors.toMap(Function.identity(), eregConsumer::verifiserEnhet)))
                .flatMap(m -> m.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        for (var entry : typeMedOrgnr.entrySet()) {
            entry.setValue(entry.getValue().stream().filter(enhetStatus::get).collect(Collectors.toList()));
        }

        return typeMedOrgnr;
    }

    private Map<TssType, List<String>> getTssTypeListMap(List<String> orgnr) {
        int chunkSize = (orgnr.size() / TssType.values().length) - 1;
        log.info("Lik fordeling blant samhandlere på størrelse " + chunkSize);

        Map<TssType, List<String>> typeMedOrgnr = Stream.of(TssType.values()).collect(Collectors.toMap(Function.identity(), e -> new ArrayList<>(chunkSize)));

        int counter = 0;
        for (var entry : typeMedOrgnr.entrySet()) {
            entry.getValue().addAll(orgnr.subList(counter, counter + chunkSize));
            counter += chunkSize;
        }
        return typeMedOrgnr;
    }

    private EregMapperRequest opprettEregRequest(TssType type, String orgnr) {
        return EregMapperRequest.builder()
                .orgnr(orgnr)
                .knytninger(Collections.singletonList(Knytning.builder().orgnr(ASEnhet).build()))
                .navn(Navn.builder()
                        .navneListe(Collections.singletonList(type.beskrivelse))
                        .build())
                .build();
    }

}
