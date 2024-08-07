package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.consumer.GenererNavnServiceConsumer;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.testnav.libs.data.pdlforvalter.v1.ForeldreansvarDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.ForeldreansvarDTO.Ansvar;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonnavnDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.RelatertBiPersonDTO;
import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ForeldreansvarServiceTest {

    private static final String IDENT_HOVEDPERSON = "23510001234";
    private static final String IDENT_ANDRE = "12435678901";
    private static final String ETTERNAVN = "TUBA";

    @Mock
    private PersonRepository personRepository;

    @Mock
    private GenererNavnServiceConsumer genererNavnServiceConsumer;

    @InjectMocks
    private ForeldreansvarService foreldreansvarService;

    @Test
    void whenAnsvarIsMissing_thenThrowExecption() {

        var request = ForeldreansvarDTO.builder()
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                foreldreansvarService.validate(request, new PersonDTO()));

        assertThat(exception.getMessage(), containsString("Forelderansvar: hvem som har ansvar må oppgis"));
    }

    @Test
    void whenAmbiguousAnsvarlig_thenThrowExecption() {

        var request = ForeldreansvarDTO.builder()
                .ansvar(Ansvar.ANDRE)
                .ansvarlig(IDENT_ANDRE)
                .ansvarligUtenIdentifikator(new RelatertBiPersonDTO())
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                foreldreansvarService.validate(request, new PersonDTO()));

        assertThat(exception.getMessage(), containsString("Forelderansvar: kun et av feltene 'ansvarlig' og " +
                "'ansvarligUtenIdentifikator' kan benyttes"));
    }

    @Test
    void whenAnsvarligPersonDontExist_thenThrowExecption() {

        when(personRepository.existsByIdent(IDENT_ANDRE)).thenReturn(false);

        var request = ForeldreansvarDTO.builder()
                .ansvar(Ansvar.ANDRE)
                .ansvarlig(IDENT_ANDRE)
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                foreldreansvarService.validate(request, new PersonDTO()));

        assertThat(exception.getMessage(), containsString(String.format("Foreldreansvar: Ansvarlig person %s finnes ikke", IDENT_ANDRE)));
    }

    @Test
    void whenNonExistingNavnStated_thenThrowExecption() {

        var personnavn = NavnDTO.builder()
                .substantiv(ETTERNAVN)
                .build();

        when(genererNavnServiceConsumer.verifyNavn(personnavn)).thenReturn(false);

        var request = ForeldreansvarDTO.builder()
                .ansvar(Ansvar.ANDRE)
                .ansvarligUtenIdentifikator(RelatertBiPersonDTO.builder()
                        .navn(PersonnavnDTO.builder()
                                .etternavn(ETTERNAVN)
                                .build())
                        .build())
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                foreldreansvarService.validate(request, new PersonDTO()));

        assertThat(exception.getMessage(),
                containsString("Foreldreansvar: Navn er ikke i liste over gyldige verdier"));
    }

    @Test
    void whenNonExistingRelatedMorStated_thenThrowExecption() {

        var personnavn = NavnDTO.builder()
                .substantiv(ETTERNAVN)
                .build();

        when(genererNavnServiceConsumer.verifyNavn(personnavn)).thenReturn(false);

        var request = ForeldreansvarDTO.builder()
                .ansvar(Ansvar.ANDRE)
                .ansvarligUtenIdentifikator(RelatertBiPersonDTO.builder()
                        .navn(PersonnavnDTO.builder()
                                .etternavn(ETTERNAVN)
                                .build())
                        .build())
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                foreldreansvarService.validate(request, new PersonDTO()));

        assertThat(exception.getMessage(),
                containsString("Foreldreansvar: Navn er ikke i liste over gyldige verdier"));
    }

    @Test
    void whenAnsvarIsMorAndForeldreBarnRelationExcludesMor_thenThrowExecption() {

        var request = ForeldreansvarDTO.builder()
                .ansvar(Ansvar.MOR)
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                foreldreansvarService.validate(request, PersonDTO.builder()
                        .ident(IDENT_HOVEDPERSON)
                        .build()
                ));

        assertThat(exception.getMessage(),
                containsString("Foreldreansvar: barn mangler / barnets foreldrerelasjon til mor ikke funnet"));
    }

    @Test
    void whenAnsvarIsFarAndForeldreBarnRelationExcludesFar_thenThrowExecption() {

        var request = ForeldreansvarDTO.builder()
                .ansvar(Ansvar.FAR)
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                foreldreansvarService.validate(request, PersonDTO.builder()
                                .ident(IDENT_HOVEDPERSON)
                                .build()
                ));

        assertThat(exception.getMessage(),
                containsString("Foreldreansvar: barn mangler / barnets foreldrerelasjon til far ikke funnet"));
    }

    @Test
    void whenAnsvarIsFellesAndForeldreBarnRelationExcludesMorOrFar_thenThrowExecption() {

        var request = ForeldreansvarDTO.builder()
                .ansvar(Ansvar.FELLES)
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                foreldreansvarService.validate(request, PersonDTO.builder()
                                .ident(IDENT_HOVEDPERSON)
                                .build()
                ));

        assertThat(exception.getMessage(),
                containsString("Foreldreansvar: barn mangler / barnets foreldrerelasjon til mor og/eller far ikke funnet"));
    }
}