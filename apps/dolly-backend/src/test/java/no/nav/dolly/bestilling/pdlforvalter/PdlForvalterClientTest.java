package no.nav.dolly.bestilling.pdlforvalter;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlBostedsadresseHistorikk;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlDeltBosted.PdlDelteBosteder;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlFullmaktHistorikk;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlInnflyttingHistorikk;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktadresseHistorikk;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKontaktinformasjonForDoedsbo;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOppholdsadresseHistorikk;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlUtflyttingHistorikk;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlVergemaalHistorikk;
import no.nav.dolly.bestilling.pdlforvalter.domain.Pdldata;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testident.Master;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.pdlforvalter.RsPdldata;
import no.nav.dolly.domain.resultset.pdlforvalter.falskidentitet.PdlFalskIdentitet;
import no.nav.dolly.domain.resultset.pdlforvalter.utenlandsid.PdlUtenlandskIdentifikasjonsnummer;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfUtvidetBestilling;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.DollyPersonCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PdlForvalterClientTest {

    private static final String ENV = "q2";
    private static final String IDENT = "11111111111";
    private static final String HENDELSE_ID_SLETTING = "111";
    private static final String HENDELSE_ID_KONTAKT_DOEDSBO = "222";
    private static final String HENDELSE_ID_UTENLANDSID = "333";
    private static final String HENDELSE_ID_FALSKIDENTITETID = "444";
    private static final String FEIL_KONTAKT_DOEDSBO = "En feil har oppstått";
    private static final String FEIL_UTENLANDS_IDENT = "Opplysning er allerede innmeldt";
    private static final String FEIL_FALSK_IDENTITET = "Falsk id er fake";
    private static final String HENDLSE_ID = "hendelseId";

    @Mock
    private PdlForvalterConsumer pdlForvalterConsumer;

    @Mock
    private DollyPersonCache dollyPersonCache;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private ErrorStatusDecoder errorStatusDecoder;

    @Mock
    private PdlDataConsumer pdlDataConsumer;

    @InjectMocks
    private PdlForvalterClient pdlForvalterClient;

    @BeforeEach
    public void setup() {
        when(pdlForvalterConsumer.deleteIdent(eq(IDENT)))
                .thenReturn(ResponseEntity.ok(instance.objectNode().put(HENDLSE_ID, HENDELSE_ID_SLETTING)));

        when(mapperFacade.map(any(Person.class), eq(PdlOppholdsadresseHistorikk.class), any(MappingContext.class))).thenReturn(new PdlOppholdsadresseHistorikk());
        when(mapperFacade.map(any(Person.class), eq(PdlKontaktadresseHistorikk.class), any(MappingContext.class))).thenReturn(new PdlKontaktadresseHistorikk());
        when(mapperFacade.map(any(Person.class), eq(PdlBostedsadresseHistorikk.class), any(MappingContext.class))).thenReturn(new PdlBostedsadresseHistorikk());
        when(mapperFacade.map(any(Person.class), eq(PdlVergemaalHistorikk.class))).thenReturn((new PdlVergemaalHistorikk()));
        when(mapperFacade.map(any(Person.class), eq(PdlFullmaktHistorikk.class))).thenReturn((new PdlFullmaktHistorikk()));
        when(mapperFacade.map(any(Person.class), eq(PdlInnflyttingHistorikk.class))).thenReturn(new PdlInnflyttingHistorikk());
        when(mapperFacade.map(any(Person.class), eq(PdlUtflyttingHistorikk.class))).thenReturn(new PdlUtflyttingHistorikk());
        when(mapperFacade.map(any(Person.class), eq(PdlDelteBosteder.class))).thenReturn(new PdlDelteBosteder());
    }

    @Test
    public void gjenopprett_kontaktinformasjonDoedsbo_OK() {

        BestillingProgress progress = BestillingProgress.builder().master(Master.TPSF).build();

        when(mapperFacade.map(any(RsPdldata.class), eq(Pdldata.class))).thenReturn(Pdldata.builder()
                .kontaktinformasjonForDoedsbo(PdlKontaktinformasjonForDoedsbo.builder().build()).build());
        when(pdlForvalterConsumer.postKontaktinformasjonForDoedsbo(any(PdlKontaktinformasjonForDoedsbo.class),
                eq(IDENT))).thenReturn(ResponseEntity.ok(instance.objectNode().put(HENDLSE_ID, HENDELSE_ID_KONTAKT_DOEDSBO)));

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setPdlforvalter(RsPdldata.builder().build());
        request.setEnvironments(singletonList(ENV));
        request.setTpsf(RsTpsfUtvidetBestilling.builder().build());
        pdlForvalterClient.gjenopprett(request,
                DollyPerson.builder().hovedperson(IDENT).persondetaljer(List.of(Person.builder().ident(IDENT).build())).build(),
                progress, false);

        verify(mapperFacade).map(any(RsPdldata.class), eq(Pdldata.class));
        verify(pdlForvalterConsumer).deleteIdent(IDENT);
        verify(pdlForvalterConsumer).postKontaktinformasjonForDoedsbo(any(PdlKontaktinformasjonForDoedsbo.class), eq(IDENT));

        assertThat(progress.getPdlforvalterStatus(), is(equalTo("PdlForvalter&OK$KontaktinformasjonForDoedsbo&OK")));
    }

    @Test
    public void gjenopprett_kontaktinformasjonDoedsbo_Feil() {

        BestillingProgress progress = BestillingProgress.builder().master(Master.TPSF).build();

        when(mapperFacade.map(any(RsPdldata.class), eq(Pdldata.class))).thenReturn(Pdldata.builder()
                .kontaktinformasjonForDoedsbo(PdlKontaktinformasjonForDoedsbo.builder().build()).build());
        when(pdlForvalterConsumer.postKontaktinformasjonForDoedsbo(any(PdlKontaktinformasjonForDoedsbo.class), eq(IDENT)))
                .thenThrow(new HttpClientErrorException(INTERNAL_SERVER_ERROR, FEIL_KONTAKT_DOEDSBO));
        when(errorStatusDecoder.decodeRuntimeException(any(RuntimeException.class))).thenReturn("Feil: " + FEIL_KONTAKT_DOEDSBO);

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setPdlforvalter(RsPdldata.builder().build());
        request.setEnvironments(singletonList(ENV));
        request.setTpsf(RsTpsfUtvidetBestilling.builder().build());
        pdlForvalterClient.gjenopprett(request,
                DollyPerson.builder().hovedperson(IDENT).persondetaljer(List.of(Person.builder().ident(IDENT).build())).build(),
                progress, false);

        verify(mapperFacade).map(any(RsPdldata.class), eq(Pdldata.class));
        verify(pdlForvalterConsumer).deleteIdent(IDENT);
        verify(pdlForvalterConsumer).postKontaktinformasjonForDoedsbo(any(PdlKontaktinformasjonForDoedsbo.class), eq(IDENT));

        assertThat(progress.getPdlforvalterStatus(), is(equalTo("PdlForvalter&OK$KontaktinformasjonForDoedsbo&Feil= En feil har oppstått")));
    }

    @Test
    public void gjenopprett_utenlandsIdent_OK() {

        BestillingProgress progress = BestillingProgress.builder().master(Master.TPSF).build();

        when(mapperFacade.map(any(RsPdldata.class), eq(Pdldata.class))).thenReturn(Pdldata.builder()
                .utenlandskIdentifikasjonsnummer(singletonList(PdlUtenlandskIdentifikasjonsnummer.builder().build())).build());
        when(pdlForvalterConsumer.postUtenlandskIdentifikasjonsnummer(any(PdlUtenlandskIdentifikasjonsnummer.class), eq(IDENT)))
                .thenReturn(ResponseEntity.ok(instance.objectNode().put(HENDLSE_ID, HENDELSE_ID_UTENLANDSID)));

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setPdlforvalter(RsPdldata.builder().build());
        request.setEnvironments(singletonList(ENV));
        request.setTpsf(RsTpsfUtvidetBestilling.builder().build());
        pdlForvalterClient.gjenopprett(request,
                DollyPerson.builder().hovedperson(IDENT).persondetaljer(List.of(Person.builder().ident(IDENT).build())).build(),
                progress, false);

        verify(mapperFacade).map(any(RsPdldata.class), eq(Pdldata.class));
        verify(pdlForvalterConsumer).deleteIdent(IDENT);
        verify(pdlForvalterConsumer).postUtenlandskIdentifikasjonsnummer(any(PdlUtenlandskIdentifikasjonsnummer.class), eq(IDENT));

        assertThat(progress.getPdlforvalterStatus(), is(equalTo("PdlForvalter&OK$UtenlandskIdentifikasjonsnummer&OK")));
    }

    @Test
    public void gjenopprett_utenlandsIdent_Feil() {

        BestillingProgress progress = BestillingProgress.builder().master(Master.TPSF).build();

        when(mapperFacade.map(any(RsPdldata.class), eq(Pdldata.class))).thenReturn(Pdldata.builder()
                .utenlandskIdentifikasjonsnummer(singletonList(PdlUtenlandskIdentifikasjonsnummer.builder().build())).build());
        when(pdlForvalterConsumer.postUtenlandskIdentifikasjonsnummer(any(PdlUtenlandskIdentifikasjonsnummer.class), eq(IDENT)))
                .thenThrow(new HttpClientErrorException(HttpStatus.ALREADY_REPORTED, FEIL_UTENLANDS_IDENT));
        when(errorStatusDecoder.decodeRuntimeException(any(RuntimeException.class))).thenReturn("Feil: " + FEIL_UTENLANDS_IDENT);

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setPdlforvalter(RsPdldata.builder().build());
        request.setEnvironments(singletonList(ENV));
        request.setTpsf(RsTpsfUtvidetBestilling.builder().build());
        pdlForvalterClient.gjenopprett(request,
                DollyPerson.builder().hovedperson(IDENT).persondetaljer(List.of(Person.builder().ident(IDENT).build())).build(),
                progress, false);

        verify(mapperFacade).map(any(RsPdldata.class), eq(Pdldata.class));
        verify(pdlForvalterConsumer).deleteIdent(IDENT);
        verify(pdlForvalterConsumer).postUtenlandskIdentifikasjonsnummer(any(PdlUtenlandskIdentifikasjonsnummer.class), eq(IDENT));

        assertThat(progress.getPdlforvalterStatus(), is(equalTo("PdlForvalter&OK$UtenlandskIdentifikasjonsnummer&Feil= Opplysning er allerede innmeldt")));
    }

    @Test
    public void gjenopprett_falskidentitet_OK() {

        BestillingProgress progress = BestillingProgress.builder().master(Master.TPSF).build();

        when(mapperFacade.map(any(RsPdldata.class), eq(Pdldata.class))).thenReturn(Pdldata.builder()
                .falskIdentitet(PdlFalskIdentitet.builder().build()).build());
        when(pdlForvalterConsumer.postFalskIdentitet(any(PdlFalskIdentitet.class), eq(IDENT)))
                .thenReturn(ResponseEntity.ok(instance.objectNode().put(HENDLSE_ID, HENDELSE_ID_FALSKIDENTITETID)));

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setPdlforvalter(RsPdldata.builder().build());
        request.setEnvironments(singletonList(ENV));
        request.setTpsf(RsTpsfUtvidetBestilling.builder().build());
        pdlForvalterClient.gjenopprett(request,
                DollyPerson.builder().hovedperson(IDENT).persondetaljer(List.of(Person.builder().ident(IDENT).build())).build(),
                progress, false);

        verify(mapperFacade).map(any(RsPdldata.class), eq(Pdldata.class));
        verify(pdlForvalterConsumer).deleteIdent(IDENT);
        verify(pdlForvalterConsumer).postFalskIdentitet(any(PdlFalskIdentitet.class), eq(IDENT));

        assertThat(progress.getPdlforvalterStatus(), is(equalTo("PdlForvalter&OK$FalskIdentitet&OK")));
    }

    @Test
    public void gjenopprett_falskIdentitet_Feil() {

        BestillingProgress progress = BestillingProgress.builder().master(Master.TPSF).build();

        when(mapperFacade.map(any(RsPdldata.class), eq(Pdldata.class))).thenReturn(Pdldata.builder()
                .falskIdentitet(PdlFalskIdentitet.builder().build()).build());
        when(pdlForvalterConsumer.postFalskIdentitet(any(PdlFalskIdentitet.class), eq(IDENT)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_ACCEPTABLE, FEIL_FALSK_IDENTITET));
        when(errorStatusDecoder.decodeRuntimeException(any(RuntimeException.class))).thenReturn("Feil: " + FEIL_FALSK_IDENTITET);

        RsDollyBestillingRequest request = new RsDollyBestillingRequest();
        request.setPdlforvalter(RsPdldata.builder().build());
        request.setEnvironments(singletonList(ENV));
        request.setTpsf(RsTpsfUtvidetBestilling.builder().build());
        pdlForvalterClient.gjenopprett(request,
                DollyPerson.builder().hovedperson(IDENT).persondetaljer(List.of(Person.builder().ident(IDENT).build())).build(),
                progress, false);

        verify(mapperFacade).map(any(RsPdldata.class), eq(Pdldata.class));
        verify(pdlForvalterConsumer).deleteIdent(IDENT);
        verify(pdlForvalterConsumer).postFalskIdentitet(any(PdlFalskIdentitet.class), eq(IDENT));

        assertThat(progress.getPdlforvalterStatus(), is(equalTo("PdlForvalter&OK$FalskIdentitet&Feil= Falsk id er fake")));
    }
}