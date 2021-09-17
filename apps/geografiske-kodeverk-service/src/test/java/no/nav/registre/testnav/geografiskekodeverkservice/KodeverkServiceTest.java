package no.nav.registre.testnav.geografiskekodeverkservice;

import no.nav.registre.testnav.geografiskekodeverkservice.service.KodeverkService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

@ExtendWith(MockitoExtension.class)
class KodeverkServiceTest {

    @InjectMocks
    private KodeverkService kodeverkService;

    @Test
    @DisplayName("Should throw exception when kommunenummer is invalid")
    void whenInvalidKommunenummer_thenThrowException() {
        var parameter = "11111";
        var exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                kodeverkService.getKommuner(parameter));
        assertThat(exception.getMessage(), containsString("Ugyldig kommunenummer"));
    }

    @Test
    @DisplayName("Should throw exception when landkode is invalid")
    void whenInvalidLandkode_thenThrowException() {
        var parameter = "SVERIGE";
        var exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                kodeverkService.getLand(parameter));
        assertThat(exception.getMessage(), containsString("Ugyldig landkode"));
    }

    @Test
    @DisplayName("Should throw exception when poststed is invalid")
    void whenInvalidPoststed_thenThrowException() {
        var parameter = "Oslo";
        var exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                kodeverkService.getPostnummer(parameter));
        assertThat(exception.getMessage(), containsString("Ugyldig poststed"));
    }
}
