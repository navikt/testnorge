package no.nav.pdl.forvalter.service;

import no.nav.testnav.libs.dto.pdlforvalter.v1.DeltBostedDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.List;

import static no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle.BARN;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle.FORELDER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class DeltBostedServiceTest {

    @InjectMocks
    private DeltBostedService deltBostedService;

    @Test
    void whenAmbiguosAdresserExist_thenThrowExecption() {

        var request = DeltBostedDTO.builder()
                .vegadresse(new VegadresseDTO())
                .matrikkeladresse(new MatrikkeladresseDTO())
                .startdatoForKontrakt(LocalDateTime.now())
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                deltBostedService.validate(request, PersonDTO.builder()
                        .forelderBarnRelasjon(List.of(ForelderBarnRelasjonDTO.builder()
                                .minRolleForPerson(FORELDER)
                                .relatertPersonsRolle(BARN)
                                .build()))
                        .build()));

        assertThat(exception.getMessage(), containsString("Delt bosted: kun én adresse skal være satt (vegadresse, ukjentBosted, matrikkeladresse)"));
    }
}