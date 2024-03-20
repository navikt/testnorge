package no.nav.dolly.service;

import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.repository.TransaksjonMappingRepository;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
@ComponentScan("no.nav.dolly")
@Testcontainers
class TransaksjonsMappingServiceTest {

    @Autowired
    private TransaksjonMappingRepository transaksjonMappingRepository;

    @Autowired
    private Flyway flyway;

    @SpyBean
    private TransaksjonMappingService transaksjonsMappingService;

    @BeforeEach
    public void beforeEach() {
        flyway.migrate();
    }

    @AfterEach
    public void afterEach() {
        transaksjonMappingRepository.deleteAll();
    }

    @Test
    void testDuplicateLogged() {
        transaksjonMappingRepository.save(
                TransaksjonMapping
                        .builder()
                        .ident("12345678901")
                        .datoEndret(LocalDateTime.now())
                        .system("SYSTEM")
                        .miljoe("q1")
                        .transaksjonId("123")
                        .build());
        transaksjonsMappingService.save(
                TransaksjonMapping
                        .builder()
                        .ident("12345678901")
                        .datoEndret(LocalDateTime.now())
                        .system("SYSTEM")
                        .miljoe("q1")
                        .transaksjonId("123")
                        .build());

        verify(transaksjonsMappingService, times(1)).logExistingEntriesExist(any(), any());
    }

    @Test
    void testNonDuplicateNotLogged() {
        transaksjonsMappingService.save(
                TransaksjonMapping
                        .builder()
                        .ident("12345678901")
                        .datoEndret(LocalDateTime.now())
                        .system("SYSTEM")
                        .miljoe("q1")
                        .transaksjonId("123")
                        .build());

        verify(transaksjonsMappingService, times(0)).logExistingEntriesExist(any(), any());
    }

    @Test
    void testNullEnvironmentNotLogged() {
        transaksjonMappingRepository.save(
                TransaksjonMapping
                        .builder()
                        .ident("12345678901")
                        .datoEndret(LocalDateTime.now())
                        .system("SYSTEM")
                        .miljoe(null)
                        .transaksjonId("123")
                        .build());
        transaksjonsMappingService.save(
                TransaksjonMapping
                        .builder()
                        .ident("12345678901")
                        .datoEndret(LocalDateTime.now())
                        .system("SYSTEM")
                        .miljoe(null)
                        .transaksjonId("123")
                        .build());

        verify(transaksjonsMappingService, times(0)).logExistingEntriesExist(any(), any());
    }

}
