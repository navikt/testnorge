package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.database.repository.RelasjonRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnhookEksternePersonerServiceTest {

    private static final String IDENT_HOVEDPERSON = "12345678901";
    private static final String IDENT_STANDALONE = "98765432109";
    private static final String IDENT_BARN = "01020312345";
    private static final String IDENT_EXTERNAL = "55566677788";

    @Mock
    private PersonRepository personRepository;

    @Mock
    private RelasjonRepository relasjonRepository;

    @InjectMocks
    private UnhookEksternePersonerService unhookEksternePersonerService;

    private static DbPerson buildDbPerson(Long id, String ident, Boolean standalone) {

        var person = PersonDTO.builder()
                .ident(ident)
                .standalone(standalone)
                .build();

        return DbPerson.builder()
                .id(id)
                .ident(ident)
                .person(person)
                .build();
    }

    private static ForelderBarnRelasjonDTO buildForelderBarnRelasjon(String relatertPerson) {

        return ForelderBarnRelasjonDTO.builder()
                .relatertPerson(relatertPerson)
                .minRolleForPerson(Rolle.MOR)
                .relatertPersonsRolle(Rolle.BARN)
                .build();
    }

    @Test
    void shouldCompleteWhenNoRelatedPersons() {

        var hovedperson = buildDbPerson(1L, IDENT_HOVEDPERSON, false);

        when(relasjonRepository.findByPersonId(1L)).thenReturn(Flux.empty());

        StepVerifier.create(unhookEksternePersonerService.unhook(hovedperson))
                .verifyComplete();

        verify(personRepository, never()).save(any());
        verify(relasjonRepository, never()).deleteByPersonIdOrRelatertPersonId(any());
    }

    @Test
    void shouldNotProcessNonStandalonePersons() {

        var hovedperson = buildDbPerson(1L, IDENT_HOVEDPERSON, false);
        var relatedPerson = buildDbPerson(2L, IDENT_STANDALONE, false);

        when(relasjonRepository.findByPersonId(1L)).thenReturn(Flux.just(
                DbRelasjon.builder().relatertPersonId(2L).build()));
        when(personRepository.findById(2L)).thenReturn(Mono.just(relatedPerson));

        StepVerifier.create(unhookEksternePersonerService.unhook(hovedperson))
                .verifyComplete();

        verify(personRepository, never()).save(any());
        verify(relasjonRepository, never()).deleteByPersonIdOrRelatertPersonId(any());
    }

    @Test
    void shouldKeepMatchedRelasjonOnStandalone() {

        var hovedperson = buildDbPerson(1L, IDENT_HOVEDPERSON, false);
        hovedperson.getPerson().getForelderBarnRelasjon().add(buildForelderBarnRelasjon(IDENT_BARN));

        var standalonePerson = buildDbPerson(2L, IDENT_STANDALONE, true);
        standalonePerson.getPerson().getForelderBarnRelasjon().add(buildForelderBarnRelasjon(IDENT_BARN));

        when(relasjonRepository.findByPersonId(1L)).thenReturn(Flux.just(
                DbRelasjon.builder().relatertPersonId(2L).build()));
        when(personRepository.findById(2L)).thenReturn(Mono.just(standalonePerson));
        when(personRepository.save(any(DbPerson.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(relasjonRepository.deleteByPersonIdOrRelatertPersonId(2L))
                .thenReturn(Mono.empty());

        StepVerifier.create(unhookEksternePersonerService.unhook(hovedperson))
                .verifyComplete();

        assertThat(standalonePerson.getPerson().getForelderBarnRelasjon(), hasSize(1));
        assertThat(standalonePerson.getPerson().getForelderBarnRelasjon().get(0).getRelatertPerson(),
                is(IDENT_BARN));

        verify(personRepository).save(standalonePerson);
        verify(relasjonRepository).deleteByPersonIdOrRelatertPersonId(2L);
    }

    @Test
    void shouldRemoveUnmatchedRelasjonFromStandalone() {

        var hovedperson = buildDbPerson(1L, IDENT_HOVEDPERSON, false);
        hovedperson.getPerson().getForelderBarnRelasjon().add(buildForelderBarnRelasjon(IDENT_BARN));

        var standalonePerson = buildDbPerson(2L, IDENT_STANDALONE, true);
        standalonePerson.getPerson().getForelderBarnRelasjon().add(buildForelderBarnRelasjon(IDENT_EXTERNAL));

        when(relasjonRepository.findByPersonId(1L)).thenReturn(Flux.just(
                DbRelasjon.builder().relatertPersonId(2L).build()));
        when(personRepository.findById(2L)).thenReturn(Mono.just(standalonePerson));
        when(personRepository.save(any(DbPerson.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(relasjonRepository.deleteByPersonIdOrRelatertPersonId(2L))
                .thenReturn(Mono.empty());

        StepVerifier.create(unhookEksternePersonerService.unhook(hovedperson))
                .verifyComplete();

        assertThat(standalonePerson.getPerson().getForelderBarnRelasjon(), hasSize(0));
    }

    @Test
    void shouldHandleMixedMatchingAndNonMatchingRelations() {

        var hovedperson = buildDbPerson(1L, IDENT_HOVEDPERSON, false);
        hovedperson.getPerson().getForelderBarnRelasjon().add(buildForelderBarnRelasjon(IDENT_BARN));

        var standalonePerson = buildDbPerson(2L, IDENT_STANDALONE, true);
        standalonePerson.getPerson().getForelderBarnRelasjon().add(buildForelderBarnRelasjon(IDENT_BARN));
        standalonePerson.getPerson().getForelderBarnRelasjon().add(buildForelderBarnRelasjon(IDENT_EXTERNAL));

        when(relasjonRepository.findByPersonId(1L)).thenReturn(Flux.just(
                DbRelasjon.builder().relatertPersonId(2L).build()));
        when(personRepository.findById(2L)).thenReturn(Mono.just(standalonePerson));
        when(personRepository.save(any(DbPerson.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(relasjonRepository.deleteByPersonIdOrRelatertPersonId(2L))
                .thenReturn(Mono.empty());

        StepVerifier.create(unhookEksternePersonerService.unhook(hovedperson))
                .verifyComplete();

        assertThat(standalonePerson.getPerson().getForelderBarnRelasjon(), hasSize(1));
        assertThat(standalonePerson.getPerson().getForelderBarnRelasjon().get(0).getRelatertPerson(),
                is(IDENT_BARN));
    }

    @Test
    void shouldKeepNonRelationalOpplysningerWhenBothHaveEntries() {

        var hovedperson = buildDbPerson(1L, IDENT_HOVEDPERSON, false);
        hovedperson.getPerson().getKjoenn().add(KjoennDTO.builder()
                .kjoenn(KjoennDTO.Kjoenn.KVINNE)
                .build());

        var standalonePerson = buildDbPerson(2L, IDENT_STANDALONE, true);
        standalonePerson.getPerson().getKjoenn().add(KjoennDTO.builder()
                .kjoenn(KjoennDTO.Kjoenn.MANN)
                .build());

        when(relasjonRepository.findByPersonId(1L)).thenReturn(Flux.just(
                DbRelasjon.builder().relatertPersonId(2L).build()));
        when(personRepository.findById(2L)).thenReturn(Mono.just(standalonePerson));
        when(personRepository.save(any(DbPerson.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(relasjonRepository.deleteByPersonIdOrRelatertPersonId(2L))
                .thenReturn(Mono.empty());

        StepVerifier.create(unhookEksternePersonerService.unhook(hovedperson))
                .verifyComplete();

        assertThat(standalonePerson.getPerson().getKjoenn(), hasSize(1));
        assertThat(standalonePerson.getPerson().getKjoenn().get(0).getKjoenn(),
                is(KjoennDTO.Kjoenn.MANN));
    }

    @Test
    void shouldHandleMultipleStandalonePersons() {

        var hovedperson = buildDbPerson(1L, IDENT_HOVEDPERSON, false);
        hovedperson.getPerson().getForelderBarnRelasjon().add(buildForelderBarnRelasjon(IDENT_BARN));

        var standalone1 = buildDbPerson(2L, IDENT_STANDALONE, true);
        standalone1.getPerson().getForelderBarnRelasjon().add(buildForelderBarnRelasjon(IDENT_BARN));

        var standalone2 = buildDbPerson(3L, "33344455566", true);
        standalone2.getPerson().getForelderBarnRelasjon().add(buildForelderBarnRelasjon(IDENT_EXTERNAL));

        when(relasjonRepository.findByPersonId(1L)).thenReturn(Flux.just(
                DbRelasjon.builder().relatertPersonId(2L).build(),
                DbRelasjon.builder().relatertPersonId(3L).build()));
        when(personRepository.findById(2L)).thenReturn(Mono.just(standalone1));
        when(personRepository.findById(3L)).thenReturn(Mono.just(standalone2));
        when(personRepository.save(any(DbPerson.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(relasjonRepository.deleteByPersonIdOrRelatertPersonId(any()))
                .thenReturn(Mono.empty());

        StepVerifier.create(unhookEksternePersonerService.unhook(hovedperson))
                .verifyComplete();

        assertThat(standalone1.getPerson().getForelderBarnRelasjon(), hasSize(1));
        assertThat(standalone2.getPerson().getForelderBarnRelasjon(), hasSize(0));
    }

    @Test
    void shouldDeleteRelasjonerForAllStandalonePersons() {

        var hovedperson = buildDbPerson(1L, IDENT_HOVEDPERSON, false);

        var standalone1 = buildDbPerson(2L, IDENT_STANDALONE, true);
        var standalone2 = buildDbPerson(3L, "33344455566", true);

        when(relasjonRepository.findByPersonId(1L)).thenReturn(Flux.just(
                DbRelasjon.builder().relatertPersonId(2L).build(),
                DbRelasjon.builder().relatertPersonId(3L).build()));
        when(personRepository.findById(2L)).thenReturn(Mono.just(standalone1));
        when(personRepository.findById(3L)).thenReturn(Mono.just(standalone2));
        when(personRepository.save(any(DbPerson.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(relasjonRepository.deleteByPersonIdOrRelatertPersonId(any()))
                .thenReturn(Mono.empty());

        StepVerifier.create(unhookEksternePersonerService.unhook(hovedperson))
                .verifyComplete();

        verify(relasjonRepository).deleteByPersonIdOrRelatertPersonId(2L);
        verify(relasjonRepository).deleteByPersonIdOrRelatertPersonId(3L);
    }
}
