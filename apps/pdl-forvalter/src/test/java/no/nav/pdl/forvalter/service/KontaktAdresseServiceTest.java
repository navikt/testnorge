package no.nav.pdl.forvalter.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.artifact.VegadresseService;
import no.nav.pdl.forvalter.domain.PdlUtenlandskAdresse;
import no.nav.pdl.forvalter.domain.PdlVegadresse;
import no.nav.pdl.forvalter.dto.PdlAdresseResponse.Vegadresse;
import no.nav.pdl.forvalter.dto.RsKontaktadresse;
import no.nav.pdl.forvalter.dto.RsKontaktadresse.Postboksadresse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static no.nav.pdl.forvalter.domain.PdlAdresse.Master.PDL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KontaktAdresseServiceTest {

    @Mock
    private VegadresseService vegadresseService;

    @Mock
    private MapperFacade mapperFacade;

    @InjectMocks
    private KontaktAdresseService kontaktAdresseService;

    @Test
    void whenTooFewDigitsInPostnummer_thenThrowExecption() {

        Exception exception = assertThrows(HttpClientErrorException.class, () ->
                kontaktAdresseService.resolve(List.of(RsKontaktadresse.builder()
                        .postboksadresse(Postboksadresse.builder()
                                .postboks("123")
                                .build())
                        .build())));

        assertThat(exception.getMessage(), containsString("Postnummer består av fire sifre"));
    }

    @Test
    void whenPostboksadresseAndPostboksIsOmitted_thenThrowExecption() {

        Exception exception = assertThrows(HttpClientErrorException.class, () ->
                kontaktAdresseService.resolve(List.of(RsKontaktadresse.builder()
                        .postboksadresse(Postboksadresse.builder()
                                .build())
                        .build())));

        assertThat(exception.getMessage(), containsString("Kan ikke være tom"));
    }

    @Test
    void whenMultipleAdressesProvided_thenThrowExecption() {

        Exception exception = assertThrows(HttpClientErrorException.class, () ->
                kontaktAdresseService.resolve(List.of(RsKontaktadresse.builder()
                        .vegadresse(new PdlVegadresse())
                        .utenlandskAdresse(new PdlUtenlandskAdresse())
                        .build())));

        assertThat(exception.getMessage(), containsString("Kun én adresse skal være satt"));
    }

    @Test
    void whenMasterPDLWithoutGyldighet_thenThrowExecption() {

        var kontaktAdresse = new RsKontaktadresse();
        kontaktAdresse.setMaster(PDL);

        Exception exception = assertThrows(HttpClientErrorException.class, () ->
                kontaktAdresseService.resolve(List.of(kontaktAdresse)));

        assertThat(exception.getMessage(), containsString(
                "Feltene gyldigFraOgMed og gyldigTilOgMed må ha verdi hvis master er PDL"));
    }

    @Test
    void whenPDLAdresseWithoutGyldighet_thenThrowExecption() {

        Exception exception = assertThrows(HttpClientErrorException.class, () ->
                kontaktAdresseService.resolve(List.of(RsKontaktadresse.builder()
                        .vegadresse(PdlVegadresse.builder()
                                .adressenavn("Denne veien")
                                .build())
                        .build())));

        assertThat(exception.getMessage(), containsString(
                "Feltene gyldigFraOgMed og gyldigTilOgMed må ha verdi for vegadresse uten matrikkelId"));
    }

    @Test
    void whenAdresseHasUgyldigBruksenhetsnummer_thenThrowExecption() {

        Exception exception = assertThrows(HttpClientErrorException.class, () ->
                kontaktAdresseService.resolve(List.of(RsKontaktadresse.builder()
                        .vegadresse(PdlVegadresse.builder()
                                .bruksenhetsnummer("W12345")
                                .build())
                        .build())));

        assertThat(exception.getMessage(), containsString(
                "Gyldig format er Bokstaven H, L, U eller K etterfulgt av fire sifre"));
    }

    @Test
    void whenVegadresseWithOKParam_thenLookupAdresse() {

        var vegadresse = Vegadresse.builder()
                .adressenavn("Veien")
                .husnummer(1)
                .postnummer("1234")
                .kommunenummer("5678")
                .matrikkelId("111111111")
                .build();
        when(vegadresseService.get(any(PdlVegadresse.class), nullable(String.class))).thenReturn(vegadresse);
        doNothing().when(mapperFacade).map(eq(vegadresse), any(PdlVegadresse.class));

        var kontaktadresse =
                kontaktAdresseService.resolve(List.of(RsKontaktadresse.builder()
                        .vegadresse(PdlVegadresse.builder()
                                .postnummer("1234")
                                .build())
                        .build())).get(0);

        verify(vegadresseService).get(any(PdlVegadresse.class), nullable(String.class));
        verify(mapperFacade).map(eq(vegadresse), any(PdlVegadresse.class));
        assertThat(kontaktadresse.getAdresseIdentifikatorFraMatrikkelen(), is(equalTo(vegadresse.getMatrikkelId())));
        assertThat(kontaktadresse.getKilde(), is(equalTo("Dolly")));
    }
}