package no.nav.dolly.service;

import ma.glasnost.orika.MapperFacade;
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
import org.apache.http.entity.ContentType;
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
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TestgruppeServiceTest {

    private final static String BRUKERID = "123";
    private final static String BRUKERNAVN = "BRUKER";
    private final static String EPOST = "@@@@";

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

    @Test
    public void opprettTestgruppe_HappyPath() {
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
    public void fetchTestgruppeById_KasterExceptionHvisGruppeIkkeErFunnet() throws Exception {
        Optional<Testgruppe> op = Optional.empty();
        when(testgruppeRepository.findById(any())).thenReturn(op);

        Assertions.assertThrows(NotFoundException.class, () ->
                testgruppeService.fetchTestgruppeById(1L));
    }

    @Test
    public void fetchTestgruppeById_ReturnererGruppeHvisGruppeMedIdFinnes() throws Exception {
        Testgruppe g = mock(Testgruppe.class);
        Optional<Testgruppe> op = Optional.of(g);
        when(testgruppeRepository.findById(any())).thenReturn(op);

        Testgruppe hentetGruppe = testgruppeService.fetchTestgruppeById(1L);

        assertThat(g, is(hentetGruppe));
    }

    @Test
    public void fetchTestgrupperByTFavoritterOfBruker() {
        Testgruppe tg1 = Testgruppe.builder().id(1L).navn("test1").build();
        Testgruppe tg2 = Testgruppe.builder().id(2L).navn("test2").build();
        Testgruppe tg3 = Testgruppe.builder().id(3L).navn("test3").build();

        Bruker bruker = Bruker.builder()
                .favoritter(new HashSet<>(asList(tg1, tg2, tg3)))
                .navIdent(BRUKERID)
                .build();

        when(brukerService.fetchBruker(any())).thenReturn(bruker);

        List<Testgruppe> grupper = testgruppeService.fetchTestgrupperByBrukerId(BRUKERID);

        assertThat(grupper, hasItem(hasProperty("id", equalTo(1L))));
        assertThat(grupper, hasItem(hasProperty("id", equalTo(2L))));
        assertThat(grupper, hasItem(hasProperty("id", equalTo(3L))));
    }

    @Test
    public void saveGruppeTilDB_returnererTestgruppeHvisTestgruppeFinnes() {
        Testgruppe g = new Testgruppe();
        when(testgruppeRepository.save(any())).thenReturn(g);

        Testgruppe res = testgruppeService.saveGruppeTilDB(new Testgruppe());
        assertThat(res, is(notNullValue()));
    }

    @Test
    public void slettGruppeById_deleteBlirKaltMotRepoMedGittId() {
        when(testgruppeRepository.findById(GROUP_ID)).thenReturn(Optional.of(testGruppe));
        testgruppeService.deleteGruppeById(GROUP_ID);
        verify(brukerService).sletteBrukerFavoritterByGroupId(GROUP_ID);
        verify(testgruppeRepository).deleteTestgruppeById(GROUP_ID);
    }

    @Test
    public void saveGruppeTilDB_kasterExceptionHvisDBConstraintErBrutt() {
        when(testgruppeRepository.save(any())).thenThrow(DataIntegrityViolationException.class);
        Assertions.assertThrows(ConstraintViolationException.class, () ->
                testgruppeService.saveGruppeTilDB(new Testgruppe()));
    }

    @Test
    public void saveGruppeTilDB_kasterDollyExceptionHvisDBConstraintErBrutt() {
        when(testgruppeRepository.save(any())).thenThrow(nonTransientDataAccessException);
        Assertions.assertThrows(DollyFunctionalException.class, () ->
                testgruppeService.saveGruppeTilDB(new Testgruppe()));
    }

    @Test
    public void saveGrupper_kasterExceptionHvisDBConstraintErBrutt() {
        when(testgruppeRepository.save(any(Testgruppe.class))).thenThrow(DataIntegrityViolationException.class);
        Assertions.assertThrows(ConstraintViolationException.class, () ->
                testgruppeService.saveGrupper(new HashSet<>(singletonList(new Testgruppe()))));
    }

    @Test
    public void saveGrupper_kasterDollyExceptionHvisDBConstraintErBrutt() {
        when(testgruppeRepository.save(any(Testgruppe.class))).thenThrow(nonTransientDataAccessException);
        Assertions.assertThrows(DollyFunctionalException.class, () ->
                testgruppeService.saveGrupper(new HashSet<>(singletonList(new Testgruppe()))));
    }

    @Test
    public void fetchGrupperByIdsIn_kasterExceptionOmGruppeIkkeFinnes() {
        Assertions.assertThrows(NotFoundException.class, () ->
                testgruppeService.fetchGrupperByIdsIn(singletonList(anyLong())));
    }

    @Test
    public void oppdaterTestgruppe_sjekkAtDBKalles() {

        RsOpprettEndreTestgruppe rsOpprettEndreTestgruppe = RsOpprettEndreTestgruppe.builder().hensikt("test").navn("navn").build();

        when(testgruppeRepository.findById(anyLong())).thenReturn(Optional.of(testGruppe));
        when(brukerService.fetchBruker(anyString())).thenReturn(Bruker.builder().navIdent("brukerId").build());
        testgruppeService.oppdaterTestgruppe(GROUP_ID, rsOpprettEndreTestgruppe);
        verify(testgruppeRepository).save(testGruppe);
    }

    @Test
    public void getTestgrupper() {
        testgruppeService.getTestgruppeByBrukerId(null);
        verify(testgruppeRepository).findAllByOrderByNavn();
    }

    @BeforeEach
    public void establishSecurity() {
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(Jwt.withTokenValue("test")
                .claim("oid", BRUKERID)
                .claim("name", BRUKERNAVN)
                .claim("epost", EPOST)
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON)
                .build()));
    }
}