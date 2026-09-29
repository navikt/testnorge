package no.nav.pdl.forvalter.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.database.repository.RelasjonRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetadataTidspunkterServiceTest {

    private static final String IDENT = "01019012345";
    private static final LocalDateTime GYLDIGHET = LocalDateTime.of(2020, 1, 1, 0, 0);

    @Mock
    private PersonRepository personRepository;

    @Mock
    private RelasjonRepository relasjonRepository;

    @Mock
    private MapperFacade mapperFacade;

    @InjectMocks
    private MetadataTidspunkterService metadataTidspunkterService;

    @Test
    void shouldClearOpphoerstidspunktWhenItIsNotAfterGyldighetstidspunkt() {

        var foreldreansvar = ForeldreansvarDTO.builder()
                .gyldigFraOgMed(GYLDIGHET)
                .gyldigTilOgMed(GYLDIGHET)
                .build();
        var person = PersonDTO.builder()
                .foreldreansvar(List.of(foreldreansvar))
                .build();
        var dbPerson = DbPerson.builder()
                .id(1L)
                .ident(IDENT)
                .person(person)
                .build();
        stubPerson(dbPerson);

        StepVerifier.create(metadataTidspunkterService.updateMetadata(IDENT))
                .verifyComplete();

        assertThat(foreldreansvar.getFolkeregistermetadata().getOpphoerstidspunkt()).isNull();
    }

    @Test
    void shouldClearOpphoerstidspunktWhenItIsBeforeGyldighetstidspunkt() {

        var foreldreansvar = ForeldreansvarDTO.builder()
                .gyldigFraOgMed(GYLDIGHET)
                .gyldigTilOgMed(GYLDIGHET.minusDays(1))
                .build();
        var person = PersonDTO.builder()
                .foreldreansvar(List.of(foreldreansvar))
                .build();
        var dbPerson = DbPerson.builder()
                .id(1L)
                .ident(IDENT)
                .person(person)
                .build();
        stubPerson(dbPerson);

        StepVerifier.create(metadataTidspunkterService.updateMetadata(IDENT))
                .verifyComplete();

        assertThat(foreldreansvar.getFolkeregistermetadata().getOpphoerstidspunkt()).isNull();
    }

    @Test
    void shouldKeepOpphoerstidspunktWhenItIsAfterGyldighetstidspunkt() {

        var opphoer = GYLDIGHET.plusDays(1);
        var foreldreansvar = ForeldreansvarDTO.builder()
                .gyldigFraOgMed(GYLDIGHET)
                .gyldigTilOgMed(opphoer)
                .build();
        var person = PersonDTO.builder()
                .foreldreansvar(List.of(foreldreansvar))
                .build();
        var dbPerson = DbPerson.builder()
                .id(1L)
                .ident(IDENT)
                .person(person)
                .build();
        stubPerson(dbPerson);

        StepVerifier.create(metadataTidspunkterService.updateMetadata(IDENT))
                .verifyComplete();

        assertThat(foreldreansvar.getFolkeregistermetadata().getOpphoerstidspunkt()).isEqualTo(opphoer);
    }

    private void stubPerson(DbPerson dbPerson) {
        when(personRepository.findByIdent(IDENT)).thenReturn(Mono.just(dbPerson));
        when(relasjonRepository.findByPersonId(dbPerson.getId())).thenReturn(Flux.empty());
        when(personRepository.findByIdIn(any())).thenReturn(Flux.empty());
        when(personRepository.save(any(DbPerson.class))).thenReturn(Mono.just(dbPerson));
    }
}
