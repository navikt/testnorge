package no.nav.dolly.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.MockedJwtAuthenticationTokenUtils;
import no.nav.dolly.common.TestidentBuilder;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsOpprettEndreTestgruppe;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.dolly.repository.TransaksjonMappingRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TestgruppeServiceTest {

    private final static String BRUKERID = "123";

    private static final long GROUP_ID = 1L;
    private static final String IDENT_ONE = "1";
    private static final String IDENT_TWO = "2";

    @Mock
    private TestgruppeRepository testgruppeRepository;

    @Mock
    private TransaksjonMappingRepository transaksjonMappingRepository;

    @Mock
    private BrukerService brukerService;

    @Mock
    private BestillingService bestillingService;

    @Mock
    private IdentService identService;

    @Mock
    private PersonService personService;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private NonTransientDataAccessException nonTransientDataAccessException;

    @InjectMocks
    private TestgruppeService testgruppeService;

    private Testgruppe testGruppe;

    @BeforeEach
    public void setup() {

        List<Testident> gruppe =
                List.of(
                        TestidentBuilder.builder().ident(IDENT_ONE).build().convertToRealTestident(),
                        TestidentBuilder.builder().ident(IDENT_TWO).build().convertToRealTestident()
                );
        testGruppe = Testgruppe.builder().id(GROUP_ID).testidenter(gruppe).hensikt("test").build();
    }

    @BeforeEach
    public void establishSecurity() {
        MockedJwtAuthenticationTokenUtils.setJwtAuthenticationToken();
    }

    @Test
    void opprettTestgruppe_HappyPath() {
        RsOpprettEndreTestgruppe rsTestgruppe = mock(RsOpprettEndreTestgruppe.class);
        Bruker bruker = mock(Bruker.class);

        when(brukerService.fetchBruker(BRUKERID)).thenReturn(bruker);

        testgruppeService.opprettTestgruppe(rsTestgruppe);

        ArgumentCaptor<Testgruppe> cap = ArgumentCaptor.forClass(Testgruppe.class);
        verify(testgruppeRepository).save(cap.capture());

        Testgruppe res = cap.getValue();

        assertThat(res.getOpprettetAv(), is(bruker));
        assertThat(res.getSistEndretAv(), is(bruker));
    }

    @Test
    void fetchTestgruppeById_KasterExceptionHvisGruppeIkkeErFunnet() {
        Optional<Testgruppe> op = Optional.empty();
        when(testgruppeRepository.findById(any())).thenReturn(op);

        Assertions.assertThrows(NotFoundException.class, () ->
                testgruppeService.fetchTestgruppeById(1L));
    }

    @Test
    void fetchTestgruppeById_ReturnererGruppeHvisGruppeMedIdFinnes() {
        Testgruppe g = mock(Testgruppe.class);
        Optional<Testgruppe> op = Optional.of(g);
        when(testgruppeRepository.findById(any())).thenReturn(op);

        Testgruppe hentetGruppe = testgruppeService.fetchTestgruppeById(1L);

        assertThat(g, is(hentetGruppe));
    }

    @Test
    void saveGruppeTilDB_returnererTestgruppeHvisTestgruppeFinnes() {
        Testgruppe g = new Testgruppe();
        when(testgruppeRepository.save(any())).thenReturn(g);

        Testgruppe res = testgruppeService.saveGruppeTilDB(new Testgruppe());
        assertThat(res, is(notNullValue()));
    }

    @Test
    void slettGruppeById_deleteBlirKaltMotRepoMedGittId() {
        when(testgruppeRepository.findById(GROUP_ID)).thenReturn(Optional.of(testGruppe));
        testgruppeService.deleteGruppeById(GROUP_ID);
        verify(brukerService).sletteBrukerFavoritterByGroupId(GROUP_ID);
        verify(testgruppeRepository).deleteAllById(GROUP_ID);
    }

    @Test
    void saveGruppeTilDB_kasterExceptionHvisDBConstraintErBrutt() {
        when(testgruppeRepository.save(any())).thenThrow(DataIntegrityViolationException.class);
        Assertions.assertThrows(ConstraintViolationException.class, () ->
                testgruppeService.saveGruppeTilDB(new Testgruppe()));
    }

    @Test
    void saveGruppeTilDB_kasterDollyExceptionHvisDBConstraintErBrutt() {
        when(testgruppeRepository.save(any())).thenThrow(nonTransientDataAccessException);
        Assertions.assertThrows(DollyFunctionalException.class, () ->
                testgruppeService.saveGruppeTilDB(new Testgruppe()));
    }

    @Test
    void saveGrupper_kasterExceptionHvisDBConstraintErBrutt() {
        when(testgruppeRepository.save(any(Testgruppe.class))).thenThrow(DataIntegrityViolationException.class);
        Assertions.assertThrows(ConstraintViolationException.class, () ->
                testgruppeService.saveGrupper(new HashSet<>(singletonList(new Testgruppe()))));
    }

    @Test
    void saveGrupper_kasterDollyExceptionHvisDBConstraintErBrutt() {
        when(testgruppeRepository.save(any(Testgruppe.class))).thenThrow(nonTransientDataAccessException);
        Assertions.assertThrows(DollyFunctionalException.class, () ->
                testgruppeService.saveGrupper(new HashSet<>(singletonList(new Testgruppe()))));
    }

    @Test
    void oppdaterTestgruppe_sjekkAtDBKalles() {

        RsOpprettEndreTestgruppe rsOpprettEndreTestgruppe = RsOpprettEndreTestgruppe.builder().hensikt("test").navn("navn").build();

        when(testgruppeRepository.findById(anyLong())).thenReturn(Optional.of(testGruppe));
        testgruppeService.oppdaterTestgruppe(GROUP_ID, rsOpprettEndreTestgruppe);
        verify(testgruppeRepository).save(testGruppe);
    }

    @Test
    void getTestgrupper() {
        when(testgruppeRepository.findAllOrderByIdDesc(any(Pageable.class))).thenReturn(new PageImpl<>(emptyList()));
        when(brukerService.fetchOrCreateBruker(any())).thenReturn(new Bruker());
        testgruppeService.getTestgruppeByBrukerId(0, 10, null);
        verify(testgruppeRepository).findAllOrderByIdDesc(Pageable.ofSize(10));
    }
}