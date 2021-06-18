package no.nav.pdl.forvalter.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.AdresseServiceConsumer;
import no.nav.pdl.forvalter.domain.KontaktadresseDTO;
import no.nav.pdl.forvalter.domain.KontaktadresseDTO.PostboksadresseDTO;
import no.nav.pdl.forvalter.domain.UtenlandskAdresseDTO;
import no.nav.pdl.forvalter.domain.VegadresseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static no.nav.pdl.forvalter.domain.AdresseDTO.Master.PDL;
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
    private AdresseServiceConsumer adresseServiceConsumer;

    @Mock
    private MapperFacade mapperFacade;

    @InjectMocks
    private KontaktAdresseService kontaktAdresseService;

    @Test
    void whenTooFewDigitsInPostnummer_thenThrowExecption() {

        var request = List.of(KontaktadresseDTO.builder()
                .postboksadresse(PostboksadresseDTO.builder()
                        .postboks("123")
                        .build())
                .isNew(true)
                .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                kontaktAdresseService.convert((List<KontaktadresseDTO>) request));

        assertThat(exception.getMessage(), containsString("Postnummer består av fire sifre"));
    }

    @Test
    void whenPostboksadresseAndPostboksIsOmitted_thenThrowExecption() {

        var request = List.of(KontaktadresseDTO.builder()
                .postboksadresse(PostboksadresseDTO.builder()
                        .build())
                .isNew(true)
                .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                kontaktAdresseService.convert((List<KontaktadresseDTO>) request));

        assertThat(exception.getMessage(), containsString("Kan ikke være tom"));
    }

    @Test
    void whenMultipleAdressesProvided_thenThrowExecption() {

        var request = List.of(KontaktadresseDTO.builder()
                .vegadresse(new VegadresseDTO())
                .utenlandskAdresse(new UtenlandskAdresseDTO())
                .isNew(true)
                .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                kontaktAdresseService.convert((List<KontaktadresseDTO>) request));

        assertThat(exception.getMessage(), containsString("Kun én adresse skal være satt"));
    }

    @Test
    void whenMasterPDLWithoutGyldighet_thenThrowExecption() {

        var request = List.of(KontaktadresseDTO.builder()
                .master(PDL)
                .isNew(true)
                .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                kontaktAdresseService.convert((List<KontaktadresseDTO>) request));

        assertThat(exception.getMessage(), containsString(
                "Feltene gyldigFraOgMed og gyldigTilOgMed må ha verdi hvis master er PDL"));
    }

    @Test
    void whenPDLAdresseWithoutGyldighet_thenThrowExecption() {

        var request = List.of(KontaktadresseDTO.builder()
                .vegadresse(VegadresseDTO.builder()
                        .adressenavn("Denne veien")
                        .build())
                .isNew(true)
                .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                kontaktAdresseService.convert((List<KontaktadresseDTO>) request));

        assertThat(exception.getMessage(), containsString(
                "Feltene gyldigFraOgMed og gyldigTilOgMed må ha verdi for vegadresse uten matrikkelId"));
    }

    @Test
    void whenAdresseHasUgyldigBruksenhetsnummer_thenThrowExecption() {

        var request = List.of(KontaktadresseDTO.builder()
                .vegadresse(VegadresseDTO.builder()
                        .bruksenhetsnummer("W12345")
                        .build())
                .isNew(true)
                .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                kontaktAdresseService.convert((List<KontaktadresseDTO>) request));

        assertThat(exception.getMessage(), containsString(
                "Gyldig format er Bokstaven H, L, U eller K etterfulgt av fire sifre"));
    }

    @Test
    void whenVegadresseWithOKParam_thenLookupAdresse() {

        var vegadresse = no.nav.registre.testnorge.libs.dto.adresseservice.v1.VegadresseDTO.builder()
                .adressenavn("Veien")
                .husnummer(1)
                .postnummer("1234")
                .kommunenummer("5678")
                .matrikkelId("111111111")
                .build();
        when(adresseServiceConsumer.getAdresse(any(VegadresseDTO.class), nullable(String.class))).thenReturn(vegadresse);
        doNothing().when(mapperFacade).map(eq(vegadresse), any(VegadresseDTO.class));

        var kontaktadresse =
                kontaktAdresseService.convert(List.of(KontaktadresseDTO.builder()
                        .vegadresse(VegadresseDTO.builder()
                                .postnummer("1234")
                                .build())
                        .isNew(true)
                        .build())).get(0);

        verify(adresseServiceConsumer).getAdresse(any(VegadresseDTO.class), nullable(String.class));
        verify(mapperFacade).map(eq(vegadresse), any(VegadresseDTO.class));
        assertThat(kontaktadresse.getAdresseIdentifikatorFraMatrikkelen(), is(equalTo(vegadresse.getMatrikkelId())));
        assertThat(kontaktadresse.getKilde(), is(equalTo("Dolly")));
    }
}