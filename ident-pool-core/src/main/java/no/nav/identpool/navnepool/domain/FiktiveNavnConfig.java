package no.nav.identpool.navnepool.domain;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.io.Resources;

import lombok.Getter;
import lombok.Setter;

@Configuration
public class FiktiveNavnConfig {

    @Setter
    @Getter
    static class TempNavn implements Serializable {

        String fiktivnavn;
    }

    public List<String> loadListFromCsvFile(String fileName) throws IOException {
        CsvMapper mapper = new CsvMapper();
        CsvSchema bootstrapSchema = mapper.typedSchemaFor(TempNavn.class).withoutHeader();
        URL resource = Resources.getResource(fileName);
        MappingIterator<TempNavn> readValues =
                mapper.readerFor(TempNavn.class).with(bootstrapSchema).readValues(resource);
        return readValues.readAll().stream().flatMap(tempNavn -> Stream.of(tempNavn.getFiktivnavn())).collect(Collectors.toList());
    }

    @Bean
    public List<String> validFornavn() throws IOException {
        return loadListFromCsvFile("__files/navnepool/adjektiv (fornavn).csv");
    }

    @Bean
    public List<String> validEtternavn() throws IOException {
        return loadListFromCsvFile("__files/navnepool/substantiv (etternavn).csv");
    }
}
