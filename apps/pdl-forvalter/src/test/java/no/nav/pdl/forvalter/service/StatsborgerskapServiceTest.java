package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.consumer.KodeverkConsumer;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.InnflyttingDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.StatsborgerskapDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsborgerskapServiceTest {

    private static final String FNR_IDENT = "12045612301";
    private static final String DNR_IDENT = "42045612301";

    @Mock
    private KodeverkConsumer kodeverkConsumer;

    @InjectMocks
    private StatsborgerskapService statsborgerskapService;

    @Test
    void whenUgyldigLandkode_thenThrowExecption() {

        var request = StatsborgerskapDTO.builder()
                .landkode("Uruguay")
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                statsborgerskapService.validate(request));

        assertThat(exception.getMessage(), containsString("Ugyldig landkode, må være i hht ISO-3 Landkoder"));
    }

    @Test
    void whenInvalidDateInterval_thenThrowExecption() {

        var request = StatsborgerskapDTO.builder()
                .gyldigFraOgMed(LocalDate.of(2020, 1, 1).atStartOfDay())
                .gyldigTilOgMed(LocalDate.of(2018, 1, 1).atStartOfDay())
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                statsborgerskapService.validate(request));

        assertThat(exception.getMessage(), containsString("Ugyldig datointervall: gyldigFom må være før gyldigTom"));
    }

    @Test
    void whenLandkodeIsEmptyAndAvailFromInnflytting_thenPickLandkodeFromInnflytting() {

        var target = statsborgerskapService.convert(PersonDTO.builder()
                        .statsborgerskap(List.of(StatsborgerskapDTO.builder()
                                .isNew(true)
                                .build()))
                        .ident(FNR_IDENT)
                        .innflytting(List.of(InnflyttingDTO.builder()
                                .fraflyttingsland("GER")
                                .build()))
                        .build())
                .getFirst();

        assertThat(target.getLandkode(), is(equalTo("GER")));
    }

    @Test
    void whenLandkodeIsEmptyAndUnavailFromInnflyttingAndIdenttypeFNR_thenSetLandkodeNorge() {

        var target = statsborgerskapService.convert(PersonDTO.builder()
                        .statsborgerskap(List.of(StatsborgerskapDTO.builder()
                                .isNew(true)
                                .build()))
                        .ident(FNR_IDENT)
                        .build())
                .getFirst();

        assertThat(target.getLandkode(), is(equalTo("NOR")));
    }

    @Test
    void whenLandkodeIsEmptyAndUnavailFromInnflyttingAndIdenttypeDNR_thenGeografiskeKodeverkConsumerIsCalled() {

        when(kodeverkConsumer.getTilfeldigLand()).thenReturn(Mono.just("CHL"));

        var target = statsborgerskapService.convert(PersonDTO.builder()
                        .statsborgerskap(List.of(StatsborgerskapDTO.builder()
                                .isNew(true)
                                .build()))
                        .ident(DNR_IDENT)
                        .build())
                .getFirst();

        verify(kodeverkConsumer).getTilfeldigLand();

        assertThat(target.getLandkode(), is(equalTo("CHL")));
    }

    @Test
    void whenGyldigFomNotProvided_thenDeriveGyldigFomFromBirthdate() {

        var target = statsborgerskapService.convert(PersonDTO.builder()
                        .statsborgerskap(List.of(StatsborgerskapDTO.builder()
                                .isNew(true)
                                .master(DbVersjonDTO.Master.PDL)
                                .build()))
                        .ident(FNR_IDENT)
                        .build())
                .getFirst();

        assertThat(target.getGyldigFraOgMed(), is(equalTo(LocalDate.of(1956, 4, 12).atStartOfDay())));
    }
}