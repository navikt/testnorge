package no.nav.dolly.service;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;
import static org.assertj.core.util.Sets.newHashSet;
import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;

@RunWith(MockitoJUnitRunner.class)
public class BrukerServiceTest {

    private final static String navIdent = "BRUKER";

    @Mock
    private BrukerRepository brukerRepository;

    @Mock
    private TestgruppeRepository testgruppeRepository;

    @InjectMocks
    private BrukerService brukerService;

    @Before
    public void setup() {
        SecurityContextHolder.getContext().setAuthentication(new OidcTokenAuthentication(navIdent, null, null, null));
    }

    @Test
    public void fetchBruker_kasterIkkeExceptionOgReturnererBrukerHvisBrukerErFunnet() {
        when(brukerRepository.findBrukerByBrukerId(any())).thenReturn(new Bruker());
        Bruker b = brukerService.fetchBruker("test");
        assertThat(b, is(notNullValue()));
    }

    @Test(expected = NotFoundException.class)
    public void fetchBruker_kasterExceptionHvisIngenBrukerFunnet() {
        when(brukerRepository.findBrukerByBrukerId(any())).thenReturn(null);
        Bruker b = brukerService.fetchBruker("test");
        assertThat(b, is(notNullValue()));
    }

    @Test
    public void getBruker_KallerRepoHentBrukere() {
        brukerService.fetchBrukere();
        verify(brukerRepository).findAllByOrderByBrukerId();
    }

    @Test
    public void fetchOrCreateBruker_saveKallesVedNotFoundException() {
        brukerService.fetchOrCreateBruker("tullestring");
        verify(brukerRepository).save(any());
    }

    @Test(expected = ConstraintViolationException.class)
    public void saveBrukerTilDB_kasterExceptionNarDBConstrainBrytes() {
        when(brukerRepository.save(any(Bruker.class))).thenThrow(DataIntegrityViolationException.class);
        brukerService.saveBrukerTilDB(new Bruker());
    }

    @Test
    public void leggTilFavoritter_medGrupperIDer() {
        Long ID = 1L;
        Testgruppe testgruppe = Testgruppe.builder().navn("gruppe").hensikt("hen").build();
        Bruker bruker = Bruker.builder().brukerId(navIdent).favoritter(new HashSet<>()).build();

        when(testgruppeRepository.findById(ID)).thenReturn(ofNullable(testgruppe));
        when(brukerRepository.findBrukerByBrukerId(navIdent)).thenReturn(bruker);
        when(brukerRepository.save(bruker)).thenReturn(bruker);

        Bruker hentetBruker = brukerService.leggTilFavoritt(ID);

        verify(brukerRepository).save(bruker);

        assertThat(hentetBruker, is(bruker));
        assertThat(hentetBruker.getFavoritter().size(), is(1));

        assertThat(hentetBruker.getFavoritter(), hasItem(both(
                hasProperty("navn", equalTo("gruppe"))).and(
                hasProperty("hensikt", equalTo("hen"))
        )));
    }

    @Test
    public void fjernFavoritter_medGrupperIDer() {
        Long ID = 1L;
        Testgruppe testgruppe = Testgruppe.builder().navn("gruppe").hensikt("hen").build();
        Testgruppe testgruppe2 = Testgruppe.builder().navn("gruppe2").hensikt("hen2").build();
        Set<Testgruppe> favoritter = newHashSet(asList(testgruppe, testgruppe2));

        Bruker bruker = Bruker.builder().brukerId(navIdent).favoritter(favoritter).build();
        testgruppe.setFavorisertAv(newHashSet(singletonList(bruker)));
        testgruppe2.setFavorisertAv(newHashSet(singletonList(bruker)));

        when(testgruppeRepository.findById(ID)).thenReturn(ofNullable(testgruppe));
        when(brukerRepository.findBrukerByBrukerId(navIdent)).thenReturn(bruker);
        when(brukerRepository.save(bruker)).thenReturn(bruker);

        Bruker hentetBruker = brukerService.fjernFavoritt(ID);

        verify(brukerRepository).save(bruker);

        assertThat(hentetBruker, is(bruker));
        assertThat(hentetBruker.getFavoritter().size(), is(1));

        assertThat(hentetBruker.getFavoritter(), hasItem(both(
                hasProperty("navn", equalTo("gruppe2"))).and(
                hasProperty("hensikt", equalTo("hen2"))
        )));

        assertThat(testgruppe.getFavorisertAv().isEmpty(), is(true));
    }

    @Test
    public void fetchBrukere() {

        brukerService.fetchBrukere();

        verify(brukerRepository).findAllByOrderByBrukerId();
    }

    @Test
    public void sletteBrukerFavoritterByGroupId() {

        long groupId = 1L;

        brukerService.sletteBrukerFavoritterByGroupId(groupId);

        verify(brukerRepository).deleteBrukerFavoritterByGroupId(groupId);
    }
}