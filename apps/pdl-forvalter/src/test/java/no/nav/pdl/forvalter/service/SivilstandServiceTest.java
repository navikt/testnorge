package no.nav.pdl.forvalter.service;

import lombok.val;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselsdatoDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO.Sivilstand.GIFT;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO.Sivilstand.SKILT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SivilstandServiceTest {

    private static final String IDENT = "12315678901";

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private SivilstandService sivilstandService;

    @Test
    void whenRelatertPersonDontExist_thenThrowExecption() {

        var request = SivilstandDTO.builder()
                .type(GIFT)
                .sivilstandsdato(LocalDateTime.now())
                .relatertVedSivilstand(IDENT)
                .isNew(true)
                .build();

        when(personRepository.existsByIdent(IDENT)).thenReturn(Mono.just(false));

        StepVerifier.create(sivilstandService.validate(request, PersonDTO.builder()
                        .ident(IDENT)
                        .build()))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Sivilstand: Relatert person finnes ikke")));
    }

    @Test
    void whenSivilstandDatoHasInvalidOrdering_fixIt() {

        val skiltDato = LocalDateTime.now();
        val giftDato = LocalDateTime.now().minusYears(3);

        val request = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(IDENT)
                        .foedselsdato(List.of(FoedselsdatoDTO.builder()
                                .foedselsdato(LocalDate.of(1956, 12, 31).atStartOfDay())
                                .build()))
                        .sivilstand(List.of(SivilstandDTO.builder()
                                        .type(GIFT)
                                        .sivilstandsdato(giftDato)
                                        .build(),
                                SivilstandDTO.builder()
                                        .type(SKILT)
                                        .sivilstandsdato(skiltDato)
                                        .build()))
                        .build())
                .build();

        StepVerifier.create(sivilstandService.convert(request))
                .assertNext(target -> {
                    assertThat(target.getPerson().getSivilstand().get(0).getSivilstandsdato(), is(equalTo(skiltDato)));
                    assertThat(target.getPerson().getSivilstand().get(1).getSivilstandsdato(), is(equalTo(giftDato)));
                })
                .verifyComplete();
    }
}