package no.nav.dolly.bestilling.pdlforvalter;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.NorskIdent;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.pdlforvalter.Pdldata;
import no.nav.dolly.domain.resultset.pdlforvalter.RsPdldata;
import no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.PdlKontaktinformasjonForDoedsbo;
import no.nav.dolly.domain.resultset.pdlforvalter.folkeregister.PdlFolkeregisterIdent;
import no.nav.dolly.domain.resultset.pdlforvalter.utenlandsid.PdlUtenlandskIdentifikasjonsnummer;

@RunWith(MockitoJUnitRunner.class)
public class PdlForvalterClientTest {

    private static final String IDENT = "11111111111";
    private static final String HENDELSE_ID_FOLKEREGISTER = "{hendelsesId:\"1\"}";
    private static final String HENDELSE_ID_KONTAKT_DOEDSBO = "{hendelsesId:\"222\"}";
    private static final String HENDELSE_ID_UTENLANDSID = "{hendelsesId:\"333\"}";
    private static final String HENDELSE_ID_SLETTING = "{hendelsesId:\"444\"}";
    private static final String FEIL_FOLKEREGISTER_IDENT = "Feil i request";
    private static final String FEIL_KONTAKT_DOEDSBO = "En feil har oppstått";
    private static final String FEIL_UTENLANDS_IDENT = "Opplysning er allerede innmeldt";

    @Mock
    private PdlForvalterRestConsumer pdlForvalterRestConsumer;

    @Mock
    private MapperFacade mapperFacade;

    @InjectMocks
    private PdlForvalterClient pdlForvalterClient;

    @Before
    public void setup() {
        when(pdlForvalterRestConsumer.deleteIdent(eq(IDENT))).thenReturn(ResponseEntity.ok(HENDELSE_ID_SLETTING));
    }

    @Test
    public void gjenopprett_folkeregisterIdent_OK() {

        BestillingProgress progress = new BestillingProgress();

        when(mapperFacade.map(any(RsPdldata.class), eq(Pdldata.class))).thenReturn(Pdldata.builder().build());
        when(pdlForvalterRestConsumer.postFolkeregisterIdent(any(PdlFolkeregisterIdent.class))).thenReturn(ResponseEntity.ok(HENDELSE_ID_FOLKEREGISTER));

        pdlForvalterClient.gjenopprett(RsDollyBestilling.builder().pdlforvalter(
                RsPdldata.builder().build()).build(),
                NorskIdent.builder().ident(IDENT).build(), progress);

        verify(pdlForvalterRestConsumer).postFolkeregisterIdent(any(PdlFolkeregisterIdent.class));
        verify(mapperFacade).map(any(RsPdldata.class), eq(Pdldata.class));

        assertThat(progress.getPdlforvalterStatus(), is(equalTo("DeleteIdent&status: OK, hendelsesId: 444"
                + "$FolkeregisterIdent&status: OK, hendelsesId: 1")));
    }

    @Test
    public void gjenopprett_folkeregisterIdent_Feil() {

        BestillingProgress progress = new BestillingProgress();

        when(mapperFacade.map(any(RsPdldata.class), eq(Pdldata.class))).thenReturn(Pdldata.builder().build());
        when(pdlForvalterRestConsumer.postFolkeregisterIdent(any(PdlFolkeregisterIdent.class)))
                .thenThrow(new HttpClientErrorException(BAD_REQUEST, FEIL_FOLKEREGISTER_IDENT));

        pdlForvalterClient.gjenopprett(RsDollyBestilling.builder().pdlforvalter(
                RsPdldata.builder().build()).build(),
                NorskIdent.builder().ident(IDENT).build(), progress);

        verify(pdlForvalterRestConsumer).postFolkeregisterIdent(any(PdlFolkeregisterIdent.class));
        verify(mapperFacade).map(any(RsPdldata.class), eq(Pdldata.class));

        assertThat(progress.getPdlforvalterStatus(), is(equalTo("DeleteIdent&status: OK, hendelsesId: 444"
                + "$FolkeregisterIdent&status: Feil (400 Feil i request)")));
    }

    @Test
    public void gjenopprett_kontaktinformasjonDoedsbo_OK() {

        BestillingProgress progress = new BestillingProgress();

        when(mapperFacade.map(any(RsPdldata.class), eq(Pdldata.class))).thenReturn(Pdldata.builder()
                .kontaktinformasjonForDoedsbo(PdlKontaktinformasjonForDoedsbo.builder().build()).build());
        when(pdlForvalterRestConsumer.postFolkeregisterIdent(any(PdlFolkeregisterIdent.class))).thenReturn(ResponseEntity.ok(HENDELSE_ID_FOLKEREGISTER));
        when(pdlForvalterRestConsumer.postKontaktinformasjonForDoedsbo(any(PdlKontaktinformasjonForDoedsbo.class), eq(IDENT))).thenReturn(ResponseEntity.ok(HENDELSE_ID_KONTAKT_DOEDSBO));

        pdlForvalterClient.gjenopprett(RsDollyBestilling.builder().pdlforvalter(
                RsPdldata.builder().build()).build(),
                NorskIdent.builder().ident(IDENT).build(), progress);

        verify(mapperFacade).map(any(RsPdldata.class), eq(Pdldata.class));
        verify(pdlForvalterRestConsumer).postFolkeregisterIdent(any(PdlFolkeregisterIdent.class));
        verify(pdlForvalterRestConsumer).postKontaktinformasjonForDoedsbo(any(PdlKontaktinformasjonForDoedsbo.class), eq(IDENT));

        assertThat(progress.getPdlforvalterStatus(), is(equalTo("DeleteIdent&status: OK, hendelsesId: 444"
                + "$FolkeregisterIdent&status: OK, hendelsesId: 1$KontaktinformasjonForDoedsbo&status: OK, hendelsesId: 222")));
    }

    @Test
    public void gjenopprett_kontaktinformasjonDoedsbo_Feil() {

        BestillingProgress progress = new BestillingProgress();

        when(mapperFacade.map(any(RsPdldata.class), eq(Pdldata.class))).thenReturn(Pdldata.builder()
                .kontaktinformasjonForDoedsbo(PdlKontaktinformasjonForDoedsbo.builder().build()).build());
        when(pdlForvalterRestConsumer.postFolkeregisterIdent(any(PdlFolkeregisterIdent.class))).thenReturn(ResponseEntity.ok(HENDELSE_ID_FOLKEREGISTER));
        when(pdlForvalterRestConsumer.postKontaktinformasjonForDoedsbo(any(PdlKontaktinformasjonForDoedsbo.class), eq(IDENT)))
                .thenThrow(new HttpClientErrorException(INTERNAL_SERVER_ERROR, FEIL_KONTAKT_DOEDSBO));

        pdlForvalterClient.gjenopprett(RsDollyBestilling.builder().pdlforvalter(
                RsPdldata.builder().build()).build(),
                NorskIdent.builder().ident(IDENT).build(), progress);

        verify(mapperFacade).map(any(RsPdldata.class), eq(Pdldata.class));
        verify(pdlForvalterRestConsumer).postFolkeregisterIdent(any(PdlFolkeregisterIdent.class));
        verify(pdlForvalterRestConsumer).postKontaktinformasjonForDoedsbo(any(PdlKontaktinformasjonForDoedsbo.class), eq(IDENT));

        assertThat(progress.getPdlforvalterStatus(), is(equalTo("DeleteIdent&status: OK, hendelsesId: 444"
                + "$FolkeregisterIdent&status: OK, hendelsesId: 1$KontaktinformasjonForDoedsbo&status: Feil (500 En feil har oppstått)")));
    }

    @Test
    public void gjenopprett_utenlandsIdent_OK() {

        BestillingProgress progress = new BestillingProgress();

        when(mapperFacade.map(any(RsPdldata.class), eq(Pdldata.class))).thenReturn(Pdldata.builder()
                .utenlandskIdentifikasjonsnummer(PdlUtenlandskIdentifikasjonsnummer.builder().build()).build());
        when(pdlForvalterRestConsumer.postFolkeregisterIdent(any(PdlFolkeregisterIdent.class))).thenReturn(ResponseEntity.ok(HENDELSE_ID_FOLKEREGISTER));
        when(pdlForvalterRestConsumer.postUtenlandskIdentifikasjonsnummer(any(PdlUtenlandskIdentifikasjonsnummer.class), eq(IDENT)))
                .thenReturn(ResponseEntity.ok(HENDELSE_ID_UTENLANDSID));

        pdlForvalterClient.gjenopprett(RsDollyBestilling.builder().pdlforvalter(
                RsPdldata.builder().build()).build(),
                NorskIdent.builder().ident(IDENT).build(), progress);

        verify(mapperFacade).map(any(RsPdldata.class), eq(Pdldata.class));
        verify(pdlForvalterRestConsumer).postFolkeregisterIdent(any(PdlFolkeregisterIdent.class));
        verify(pdlForvalterRestConsumer).postUtenlandskIdentifikasjonsnummer(any(PdlUtenlandskIdentifikasjonsnummer.class), eq(IDENT));

        assertThat(progress.getPdlforvalterStatus(), is(equalTo("DeleteIdent&status: OK, hendelsesId: 444"
                + "$FolkeregisterIdent&status: OK, hendelsesId: 1$UtenlandskIdentifikasjonsnummer&status: OK, hendelsesId: 333")));
    }

    @Test
    public void gjenopprett_utenlandsIdent_Feil() {

        BestillingProgress progress = new BestillingProgress();

        when(mapperFacade.map(any(RsPdldata.class), eq(Pdldata.class))).thenReturn(Pdldata.builder()
                .utenlandskIdentifikasjonsnummer(PdlUtenlandskIdentifikasjonsnummer.builder().build()).build());
        when(pdlForvalterRestConsumer.postFolkeregisterIdent(any(PdlFolkeregisterIdent.class))).thenReturn(ResponseEntity.ok(HENDELSE_ID_FOLKEREGISTER));
        when(pdlForvalterRestConsumer.postUtenlandskIdentifikasjonsnummer(any(PdlUtenlandskIdentifikasjonsnummer.class), eq(IDENT)))
                .thenThrow(new HttpClientErrorException(HttpStatus.ALREADY_REPORTED, FEIL_UTENLANDS_IDENT));

        pdlForvalterClient.gjenopprett(RsDollyBestilling.builder().pdlforvalter(
                RsPdldata.builder().build()).build(),
                NorskIdent.builder().ident(IDENT).build(), progress);

        verify(mapperFacade).map(any(RsPdldata.class), eq(Pdldata.class));
        verify(pdlForvalterRestConsumer).postFolkeregisterIdent(any(PdlFolkeregisterIdent.class));
        verify(pdlForvalterRestConsumer).postUtenlandskIdentifikasjonsnummer(any(PdlUtenlandskIdentifikasjonsnummer.class), eq(IDENT));

        assertThat(progress.getPdlforvalterStatus(), is(equalTo("DeleteIdent&status: OK, hendelsesId: 444"
                + "$FolkeregisterIdent&status: OK, hendelsesId: "
                + "1$UtenlandskIdentifikasjonsnummer&status: Feil (208 Opplysning er allerede innmeldt)")));
    }
}