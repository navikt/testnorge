package no.nav.identpool;

import static no.nav.identpool.util.LastInnFiktiveNavnUtil.loadListFromCsvFile;

import java.io.IOException;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FiktiveNavnConfig {

    @Bean
    public List<String> validFornavn() throws IOException {
        return loadListFromCsvFile("navnepool/adjektiv (fornavn).csv");
    }

    @Bean
    public List<String> validEtternavn() throws IOException {
        return loadListFromCsvFile("navnepool/substantiv (etternavn).csv");
    }
}
