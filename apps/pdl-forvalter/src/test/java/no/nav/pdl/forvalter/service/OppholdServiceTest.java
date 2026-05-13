package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdDTO.OppholdType.MIDLERTIDIG;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdDTO.OppholdType.OPPLYSNING_MANGLER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@ExtendWith(MockitoExtension.class)
class OppholdServiceTest {

    private static final String IDENT = "12345678901";

    @InjectMocks
    private OppholdService oppholdService;

    @Test
    void whenInvalidDateInterval_thenThrowExecption() {

        var request = OppholdDTO.builder()
                .oppholdFra(LocalDate.of(2020, 1, 1).atStartOfDay())
                .oppholdTil(LocalDate.of(2018, 1, 1).atStartOfDay())
                .type(MIDLERTIDIG)
                .isNew(true)
                .build();

        StepVerifier.create(
                        oppholdService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Ugyldig datointervall: oppholdFra må være før oppholdTil")));
    }

    @Test
    void whenTypeIsEmpty_thenThrowExecption() {

        var request = OppholdDTO.builder()
                .oppholdFra(LocalDate.of(2020, 1, 1).atStartOfDay())
                .isNew(true)
                .build();

        StepVerifier.create(oppholdService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Type av opphold må angis")));
    }

    @Test
    void whenOverlappingDateIntervalsInInput_thenThrowExecption() {

        var request = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(IDENT)
                        .opphold(
                                List.of(OppholdDTO.builder()
                                                .oppholdFra(LocalDate.of(2020, 1, 2).atStartOfDay())
                                                .type(OPPLYSNING_MANGLER)
                                                .isNew(true)
                                                .build(),
                                        OppholdDTO.builder()
                                                .oppholdFra(LocalDate.of(2020, 1, 1).atStartOfDay())
                                                .type(MIDLERTIDIG)
                                                .isNew(true)
                                                .build()))
                        .build())
                .build();

        StepVerifier.create(oppholdService.convert(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Feil: Overlappende opphold er detektert")));
    }

    @Test
    void whenOverlappingDateIntervalsInInput2_thenThrowExecption() {

        var request = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(IDENT)
                        .opphold(List.of(OppholdDTO.builder()
                                        .oppholdFra(LocalDate.of(2020, 2, 3).atStartOfDay())
                                        .type(OPPLYSNING_MANGLER)
                                        .isNew(true)
                                        .build(),
                                OppholdDTO.builder()
                                        .oppholdFra(LocalDate.of(2020, 1, 1).atStartOfDay())
                                        .oppholdTil(LocalDate.of(2020, 2, 3).atStartOfDay())
                                        .type(MIDLERTIDIG)
                                        .isNew(true)
                                        .build()))
                        .build())
                .build();

        StepVerifier.create(oppholdService.convert(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Feil: Overlappende opphold er detektert")));
    }

    @Test
    void whenFraDatoAndEmptyTilDato_thenAcceptRequest() {

        StepVerifier.create(oppholdService.convert(DbPerson.builder()
                        .person(PersonDTO.builder()
                                .ident(IDENT)
                                .opphold(List.of(OppholdDTO.builder()
                                        .oppholdFra(LocalDate.of(2020, 1, 1).atStartOfDay())
                                        .type(OPPLYSNING_MANGLER)
                                        .isNew(true)
                                        .build()))
                                .build())
                        .build()))
                .assertNext(target -> assertThat(target.getPerson().getOpphold().getFirst().getOppholdFra(),
                        is(equalTo(LocalDate.of(2020, 1, 1).atStartOfDay()))))
                .verifyComplete();
    }

    @Test
    void whenPreviousOppholdHasEmptyTilDato_thenFixPreviousOppholdTilDato() {

        StepVerifier.create(oppholdService.convert(DbPerson.builder()
                        .person(PersonDTO.builder()
                                .ident(IDENT)
                                .opphold(List.of(OppholdDTO.builder()
                                                .oppholdFra(LocalDate.of(2020, 2, 4).atStartOfDay())
                                                .type(OPPLYSNING_MANGLER)
                                                .isNew(true)
                                                .build(),
                                        OppholdDTO.builder()
                                                .oppholdFra(LocalDate.of(2020, 1, 1).atStartOfDay())
                                                .type(MIDLERTIDIG)
                                                .isNew(true)
                                                .build()))
                                .build())
                        .build()))
                .assertNext(target -> {
                    assertThat(target.getPerson().getOpphold().getFirst().getOppholdFra(),
                            is(equalTo(LocalDate.of(2020, 2, 4).atStartOfDay())));
                    assertThat(target.getPerson().getOpphold().get(1).getOppholdTil(),
                            is(equalTo(LocalDate.of(2020, 2, 3).atStartOfDay())));
                })
                .verifyComplete();
    }

    @Test
    void shouldValidateSuccessfullyWhenRequestIsValid() {

        var request = OppholdDTO.builder()
                .oppholdFra(LocalDate.of(2020, 1, 1).atStartOfDay())
                .oppholdTil(LocalDate.of(2020, 6, 1).atStartOfDay())
                .type(MIDLERTIDIG)
                .isNew(true)
                .build();

        StepVerifier.create(oppholdService.validate(request))
                .verifyComplete();
    }

    @Test
    void shouldThrowWhenOppholdFraEqualsOppholdTil() {

        var request = OppholdDTO.builder()
                .oppholdFra(LocalDate.of(2020, 1, 1).atStartOfDay())
                .oppholdTil(LocalDate.of(2020, 1, 1).atStartOfDay())
                .type(MIDLERTIDIG)
                .isNew(true)
                .build();

        StepVerifier.create(oppholdService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Ugyldig datointervall")));
    }

    @Test
    void shouldValidateSuccessfullyWhenOnlyOppholdFraIsProvided() {

        var request = OppholdDTO.builder()
                .oppholdFra(LocalDate.of(2020, 1, 1).atStartOfDay())
                .type(MIDLERTIDIG)
                .isNew(true)
                .build();

        StepVerifier.create(oppholdService.validate(request))
                .verifyComplete();
    }

    @Test
    void shouldConvertSuccessfullyWhenOppholdListIsEmpty() {

        var request = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(IDENT)
                        .opphold(List.of())
                        .build())
                .build();

        StepVerifier.create(oppholdService.convert(request))
                .assertNext(target -> assertThat(target.getPerson().getOpphold().isEmpty(), is(true)))
                .verifyComplete();
    }

    @Test
    void shouldSkipItemsNotMarkedAsNew() {

        var request = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(IDENT)
                        .opphold(List.of(OppholdDTO.builder()
                                .oppholdFra(LocalDate.of(2020, 1, 1).atStartOfDay())
                                .type(MIDLERTIDIG)
                                .isNew(false)
                                .build()))
                        .build())
                .build();

        StepVerifier.create(oppholdService.convert(request))
                .assertNext(target -> {
                    var opphold = target.getPerson().getOpphold().getFirst();
                    assertThat(opphold.getKilde(), is(nullValue()));
                    assertThat(opphold.getMaster(), is(nullValue()));
                })
                .verifyComplete();
    }

    @Test
    void shouldSetKildeAndMasterOnNewItems() {

        var request = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(IDENT)
                        .opphold(List.of(OppholdDTO.builder()
                                .oppholdFra(LocalDate.of(2020, 1, 1).atStartOfDay())
                                .type(MIDLERTIDIG)
                                .isNew(true)
                                .build()))
                        .build())
                .build();

        StepVerifier.create(oppholdService.convert(request))
                .assertNext(target -> {
                    var opphold = target.getPerson().getOpphold().getFirst();
                    assertThat(opphold.getKilde(), is(equalTo("Dolly")));
                    assertThat(opphold.getMaster(), is(equalTo(DbVersjonDTO.Master.PDL)));
                })
                .verifyComplete();
    }

    @Test
    void shouldSkipOverlapCheckWhenCurrentOppholdFraIsNull() {

        var request = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(IDENT)
                        .opphold(List.of(
                                OppholdDTO.builder()
                                        .type(OPPLYSNING_MANGLER)
                                        .isNew(true)
                                        .build(),
                                OppholdDTO.builder()
                                        .oppholdFra(LocalDate.of(2020, 1, 1).atStartOfDay())
                                        .type(MIDLERTIDIG)
                                        .isNew(true)
                                        .build()))
                        .build())
                .build();

        StepVerifier.create(oppholdService.convert(request))
                .assertNext(target -> assertThat(target.getPerson().getOpphold().size(), is(equalTo(2))))
                .verifyComplete();
    }

    @Test
    void shouldAcceptNonOverlappingEntries() {

        var request = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(IDENT)
                        .opphold(List.of(
                                OppholdDTO.builder()
                                        .oppholdFra(LocalDate.of(2021, 1, 1).atStartOfDay())
                                        .type(OPPLYSNING_MANGLER)
                                        .isNew(true)
                                        .build(),
                                OppholdDTO.builder()
                                        .oppholdFra(LocalDate.of(2020, 1, 1).atStartOfDay())
                                        .oppholdTil(LocalDate.of(2020, 6, 1).atStartOfDay())
                                        .type(MIDLERTIDIG)
                                        .isNew(true)
                                        .build()))
                        .build())
                .build();

        StepVerifier.create(oppholdService.convert(request))
                .assertNext(target -> assertThat(target.getPerson().getOpphold().size(), is(equalTo(2))))
                .verifyComplete();
    }
}