package no.nav.testnav.apps.syntvedtakshistorikkservice.service;

import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.InntektstubConsumer;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.OrgFasteDataServiceConsumer;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.fastedata.Organisasjon;
import no.nav.testnav.apps.syntvedtakshistorikkservice.domain.inntektstub.Inntektsinformasjon;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class InntektServiceTest {

    @Mock
    private InntektstubConsumer inntektstubConsumer;

    @Mock
    private OrgFasteDataServiceConsumer orgFasteDataServiceConsumer;

    @InjectMocks
    private InntektService inntektService;

    private static final String IDENT = "12345678910";

    @Test
    public void shouldNotOppretteInntekter(){
        when(orgFasteDataServiceConsumer.getOrganisasjoner()).thenReturn(Collections.emptyList());

        var response = inntektService.opprettetInntektPaaIdentFoerDato(IDENT, LocalDate.now());

        assertThat(response).isFalse();
    }

    @Test
    public void shouldOppretteInntekter(){
        when(orgFasteDataServiceConsumer.getOrganisasjoner()).thenReturn(Collections.singletonList(
                new Organisasjon("123", "BEDR", "Org", "456")));
        when(inntektstubConsumer.postInntekter(anyList())).thenReturn(Collections.singletonList(
                Inntektsinformasjon.builder().norskIdent(IDENT).build()));
        var response = inntektService.opprettetInntektPaaIdentFoerDato(IDENT, LocalDate.now());

        assertThat(response).isTrue();
    }
}
