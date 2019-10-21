package no.nav.registre.tss.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import no.nav.registre.tss.consumer.rs.EregConsumer;
import no.nav.registre.tss.consumer.rs.EregMapperConsumer;
import no.nav.registre.tss.consumer.rs.request.EregMapperRequest;
import no.nav.registre.tss.consumer.rs.request.Knytning;
import no.nav.registre.tss.consumer.rs.request.Navn;
import no.nav.registre.tss.domain.TssType;

@Service
@RequiredArgsConstructor
@Slf4j
public class EregService {

    private final EregMapperConsumer eregMapperConsumer;
    private final EregConsumer eregConsumer;
    private final CsvFileService csvFileService;

    @Value("${testnorge.ereg.enhet.as}")
    private String ASEnhet;

    public Map<TssType, List<String>> opprettEregEnheter(Map<TssType, Integer> tssTypeAntallMap) {
        var eksiterendeEnheter = csvFileService.findExistingFromFile();

        Map<TssType, Integer> oppdatertAntall = tssTypeAntallMap.entrySet().stream()
                .peek(entry -> entry.setValue(entry.getValue() - eksiterendeEnheter.getOrDefault(entry.getKey(), new ArrayList<>()).size()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Integer totalAntallOrganissasjoner = oppdatertAntall.values().stream().reduce(0, Integer::sum);

        List<String> orgnr = eregMapperConsumer.hentNyttOrgnr(totalAntallOrganissasjoner);
        Map<TssType, List<String>> typeMedOrgnr = setOrgnrForType(orgnr, oppdatertAntall);

        typeMedOrgnr.entrySet().stream().peek(entry -> log.info(entry.getKey().name() + " : " + entry.getValue().toString()));

        boolean sendtTilJenkins = eregMapperConsumer.opprett(typeMedOrgnr.entrySet().stream()
                .map(entry -> entry.getValue().stream()
                        .map(org -> opprettEregRequest(entry.getKey(), org))
                        .collect(Collectors.toList()))
                .flatMap(List::stream).collect(Collectors.toList())
        );

        if (!sendtTilJenkins)
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Kunne ikke opprette bedrifter i EREG");

        Map<String, Boolean> enhetStatus = typeMedOrgnr.values().stream()
                .map(orgnummere -> orgnummere.stream().collect(Collectors.toMap(Function.identity(), eregConsumer::verifiserEnhet)))
                .flatMap(m -> m.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


        for (var entry : typeMedOrgnr.entrySet()) {
            entry.setValue(entry.getValue().stream().filter(enhetStatus::get).collect(Collectors.toList()));
        }

        Map<TssType, List<String>> opprettedeEnheter = typeMedOrgnr.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> entry.getValue().stream().filter(enhetStatus::get).collect(Collectors.toList())));

        csvFileService.writeIfNotExist(opprettedeEnheter);

        return typeMedOrgnr;
    }

    private Map<TssType, List<String>> setOrgnrForType(List<String> orgnr, Map<TssType, Integer> antallPrType) {
        int counter = 0;
        Map<TssType, List<String>> result = new HashMap<>();
        for (Map.Entry<TssType, Integer> entry : antallPrType.entrySet()) {
            result.put(entry.getKey(), orgnr.subList(counter, counter + entry.getValue()));
            counter += entry.getValue();
        }
        return result;
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
