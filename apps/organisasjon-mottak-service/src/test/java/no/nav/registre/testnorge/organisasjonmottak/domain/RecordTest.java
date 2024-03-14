package no.nav.registre.testnorge.organisasjonmottak.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class RecordTest {

    @InjectMocks
    Record record;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreate() {
        // Create mock objects
        Line line = mock(Line.class);
        String orgnummer = "123456789";
        String enhetstype = "AS";
        Date regDato = Date.from(LocalDate.of(2022, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC));
        boolean update = false;

        // Call method under test
        Record result = Record.create(Collections.singletonList(line), orgnummer, enhetstype, regDato, update);

        // Verify that the StringBuilder contains the correct date
        String expectedDate = "20220101";
        assertTrue(result.build().contains(expectedDate));
    }
}