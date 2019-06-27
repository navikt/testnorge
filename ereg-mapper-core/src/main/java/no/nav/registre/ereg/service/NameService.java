package no.nav.registre.ereg.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import no.nav.registre.ereg.consumer.rs.IdentPoolConsumer;
import no.nav.registre.ereg.csv.CsvReader;
import no.nav.registre.ereg.csv.NaeringskodeRecord;

@Slf4j
@Service
@RequiredArgsConstructor
public class NameService {

    private final IdentPoolConsumer identPoolConsumer;
    @Value("${naeringskoder.path}")
    private final String filePath;
    private Map<String, NaeringskodeRecord> naeringskodeRecords;

    @PostConstruct
    public void init() {
        try {
            naeringskodeRecords = CsvReader.read(filePath).stream().collect(Collectors.toMap(NaeringskodeRecord::getCode, n -> n));
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    private String getFullName(String naeringskode, String type) {
        if (type.equals("AS")) {
            return naeringskodeRecords.getOrDefault(naeringskode, null).getShortName() + " " + type;
        }
        return naeringskodeRecords.getOrDefault(naeringskode, null).getShortName();
    }

    public List<String> getFullNames(List<String> naeringsKoder, String type) {
        List<String> companyNames = new ArrayList<>();
        List<String> fakeNames = identPoolConsumer.getFakeNames(naeringsKoder.size());
        if ("AS".equals(type)) {
            if (fakeNames.size() != naeringsKoder.size()) {
                log.warn("Kunne ikke hente riktig antall navn fra ident-pool");
            }
            for (int i = 0; i < naeringsKoder.size(); i++) {
                String fullName = fakeNames.get(i) + " " + getFullName(naeringsKoder.get(i), type);
                companyNames.add(fullName);
            }
        } else {
            companyNames = naeringsKoder.parallelStream().map(n -> getFullName(n, type)).collect(Collectors.toList());
        }
        return companyNames;
    }

}
