package no.nav.dolly.service;

import no.nav.dolly.MockedJwtAuthenticationTokenUtils;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

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
class BrukerServiceTest {

    private final static String BRUKERID = "123";

    @Mock
    private BrukerRepository brukerRepository;

    @Mock
    private TestgruppeRepository testgruppeRepository;

    @InjectMocks
    private BrukerService brukerService;

    @BeforeEach
    public void setup() {
        MockedJwtAuthenticationTokenUtils.setJwtAuthenticationToken();
    }

    @Test
    void fetchBruker_kasterIkkeExceptionOgReturnererBrukerHvisBrukerOrTeamBrukerErFunnet() {
        when(brukerRepository.findBrukerByBrukerId(any())).thenReturn(Optional.of(Bruker.builder().build()));
        Bruker b = brukerService.fetchBrukerOrTeamBruker("test");
        assertThat(b, is(notNullValue()));
    }

    @Test
    void fetchBruker_kasterExceptionHvisIngenBrukerOrTeamBrukerFunnet() {
        when(brukerRepository.findBrukerByBrukerId(any())).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () ->
                brukerService.fetchBrukerOrTeamBruker(BRUKERID));
    }

    @Test
    void fetchBrukerOrTeamBrukere() {
        when(brukerRepository.save(any())).thenReturn(Bruker.builder().brukertype(Bruker.Brukertype.AZURE).build());
        brukerService.fetchBrukere();
        verify(brukerRepository).findAllByOrderByBrukernavn();
    }

    @Test
    void fetchOrCreateBruker_saveKallesVedNotFoundException() {
        brukerService.fetchOrCreateBruker("tullestring");
        verify(brukerRepository).save(any());
    }

    @Test
    void leggTilFavoritter_medGrupperIDer() {
        Long ID = 1L;
        Testgruppe testgruppe = Testgruppe.builder().navn("gruppe").hensikt("hen").build();
        Bruker bruker = Bruker.builder().brukerId(BRUKERID).favoritter(new HashSet<>()).build();

        when(testgruppeRepository.findById(ID)).thenReturn(ofNullable(testgruppe));
        when(brukerRepository.findBrukerByBrukerId(BRUKERID)).thenReturn(Optional.of(bruker));
        when(brukerRepository.save(bruker)).thenReturn(bruker);

        Bruker hentetBruker = brukerService.leggTilFavoritt(ID);

        assertThat(hentetBruker, is(bruker));
        assertThat(hentetBruker.getFavoritter().size(), is(1));

        assertThat(hentetBruker.getFavoritter(), hasItem(both(
                hasProperty("navn", equalTo("gruppe"))).and(
                hasProperty("hensikt", equalTo("hen"))
        )));
    }

    @Test
    void fjernFavoritter_medGrupperIDer() {
        Long ID = 1L;
        Bruker bruker = Bruker.builder().brukerId(BRUKERID).build();
        Testgruppe testgruppe = Testgruppe.builder().navn("gruppe").id(ID).opprettetAv(bruker).hensikt("hen").build();
        Testgruppe testgruppe2 = Testgruppe.builder().navn("gruppe2").id(2L).opprettetAv(bruker).hensikt("hen2").build();
        bruker.getFavoritter().addAll(new ArrayList<>(List.of(testgruppe, testgruppe2)));

        testgruppe.setFavorisertAv(new HashSet<>(singletonList(bruker)));
        testgruppe2.setFavorisertAv(new HashSet<>(singletonList(bruker)));

        when(testgruppeRepository.findById(ID)).thenReturn(Optional.of(testgruppe));
        when(brukerRepository.findBrukerByBrukerId(BRUKERID)).thenReturn(Optional.of(bruker));
        when(brukerRepository.save(bruker)).thenReturn(bruker);

        Bruker hentetBruker = brukerService.fjernFavoritt(ID);

        assertThat(hentetBruker, is(bruker));
        assertThat(hentetBruker.getFavoritter().size(), is(1));

        assertThat(hentetBruker.getFavoritter(), hasItem(both(
                hasProperty("navn", equalTo("gruppe2"))).and(
                hasProperty("hensikt", equalTo("hen2"))
        )));

        assertThat(testgruppe.getFavorisertAv().isEmpty(), is(true));
    }

    @Test
    void sletteBrukerFavoritterByGroupId() {

        long groupId = 1L;

        brukerService.sletteBrukerFavoritterByGroupId(groupId);

        verify(brukerRepository).deleteBrukerFavoritterByGroupId(groupId);
    }
}