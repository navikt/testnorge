package no.nav.dolly.service;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;
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

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BrukerServiceTest {

    private final static String BRUKERID = "123";
    private final static String BRUKERNAVN = "BRUKER";
    private final static String EPOST = "@@@@";

    @Mock
    private BrukerRepository brukerRepository;

    @Mock
    private TestgruppeRepository testgruppeRepository;

    @InjectMocks
    private BrukerService brukerService;

    @Test
    public void fetchBruker_kasterIkkeExceptionOgReturnererBrukerHvisBrukerErFunnet() {
        when(brukerRepository.findBrukerByBrukerId(any())).thenReturn(Optional.of(Bruker.builder().build()));
        Bruker b = brukerService.fetchBruker("test");
        assertThat(b, is(notNullValue()));
    }

    @Test
    public void fetchBruker_kasterExceptionHvisIngenBrukerFunnet() {
        when(brukerRepository.findBrukerByBrukerId(any())).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () ->
                brukerService.fetchBruker(BRUKERID));
    }

    @Test
    public void getBruker_KallerRepoHentBrukere() {
        brukerService.fetchBrukere();
        verify(brukerRepository).findAllByOrderById();
    }

    @Test
    public void fetchOrCreateBruker_saveKallesVedNotFoundException() {
        brukerService.fetchOrCreateBruker("tullestring");
        verify(brukerRepository).save(any());
    }

    @Test
    public void saveBrukerTilDB_kasterExceptionNarDBConstrainBrytes() {
        when(brukerRepository.save(any(Bruker.class))).thenThrow(DataIntegrityViolationException.class);
        Assertions.assertThrows(ConstraintViolationException.class, () ->
                brukerService.saveBrukerTilDB(Bruker.builder().build()));
    }

    @Test
    public void leggTilFavoritter_medGrupperIDer() {
        Long ID = 1L;
        Testgruppe testgruppe = Testgruppe.builder().navn("gruppe").hensikt("hen").build();
        Bruker bruker = Bruker.builder().brukerId(BRUKERID).favoritter(new HashSet<>()).build();

        when(testgruppeRepository.findById(ID)).thenReturn(ofNullable(testgruppe));
        when(brukerRepository.findBrukerByBrukerId(BRUKERID)).thenReturn(Optional.of(bruker));
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
        Bruker bruker = Bruker.builder().brukerId(BRUKERID).build();
        Testgruppe testgruppe = Testgruppe.builder().navn("gruppe").id(ID).opprettetAv(bruker).hensikt("hen").build();
        Testgruppe testgruppe2 = Testgruppe.builder().navn("gruppe2").id(2L).opprettetAv(bruker).hensikt("hen2").build();
        bruker.getFavoritter().addAll(new ArrayList<>(List.of(testgruppe, testgruppe2)));

        testgruppe.setFavorisertAv(new HashSet<>(singletonList(bruker)));
        testgruppe2.setFavorisertAv(new HashSet<>(singletonList(bruker)));

        when(testgruppeRepository.findById(ID)).thenReturn(ofNullable(testgruppe));
        when(brukerRepository.findBrukerByBrukerId(BRUKERID)).thenReturn(Optional.of(bruker));
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

        verify(brukerRepository).findAllByOrderById();
    }

    @Test
    public void sletteBrukerFavoritterByGroupId() {

        long groupId = 1L;

        brukerService.sletteBrukerFavoritterByGroupId(groupId);

        verify(brukerRepository).deleteBrukerFavoritterByGroupId(groupId);
    }

    @BeforeEach
    public void setup() {
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(Jwt.withTokenValue("test")
                .claim("oid", BRUKERID)
                .claim("name", BRUKERNAVN)
                .claim("epost", EPOST)
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON)
                .build()));
    }
}