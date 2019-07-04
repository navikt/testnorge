package no.nav.registre.ereg.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import no.nav.registre.ereg.consumer.rs.IdentPoolConsumer;
import no.nav.registre.ereg.csv.CsvReader;
import no.nav.registre.ereg.csv.NaeringskodeRecord;

@Slf4j
@Service
public class NameService {

    private final IdentPoolConsumer identPoolConsumer;
    private final String filePath;
    private final Random generator;

    private Map<String, NaeringskodeRecord> naeringskodeRecords;

    public NameService(
            @Value("${naeringskoder.path}")
                    String filePath,
            IdentPoolConsumer identPoolConsumer
    ) {
        this.filePath = filePath;
        this.identPoolConsumer = identPoolConsumer;
        this.generator = new Random();
    }

    @PostConstruct
    public void init() {
        try {
            naeringskodeRecords = CsvReader.read(filePath).stream().collect(Collectors.toMap(NaeringskodeRecord::getCode, n -> n));
            if (naeringskodeRecords.isEmpty()) {
                throw new IllegalStateException("Næringskoder kan ikke være tom. Det har skjed noe feil med innlesingen");
            }
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }


    public String getRandomNaeringskode() {
        Collection<NaeringskodeRecord> tmp = naeringskodeRecords.values();
        List<NaeringskodeRecord> values = new ArrayList<>(tmp);
        return values.get(generator.nextInt(values.size())).getCode();
    }

    private String getFullName(String naeringskode, String type) {
        if (type.equals("AS")) {
            return naeringskodeRecords.get(naeringskode).getShortName() + " " + type;
        }
        return naeringskodeRecords.get(naeringskode).getShortName();
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
