package no.nav.registre.bisys.service;

import static no.nav.registre.bisys.service.SyntetiseringService.RELASJON_FAR;
import static no.nav.registre.bisys.service.SyntetiseringService.RELASJON_MOR;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import no.nav.bidrag.ui.exception.BidragRequestProcessingException;
import no.nav.registre.bisys.consumer.rs.BisysSyntetisererenConsumer;
import no.nav.registre.bisys.consumer.rs.HodejegerenConsumer;
import no.nav.registre.bisys.consumer.rs.request.BisysRequestAugments;
import no.nav.registre.bisys.consumer.rs.responses.SyntetisertBidragsmelding;
import no.nav.registre.bisys.consumer.rs.responses.relasjon.Relasjon;
import no.nav.registre.bisys.consumer.rs.responses.relasjon.RelasjonsResponse;
import no.nav.registre.bisys.consumer.ui.BisysUiSupport;
import no.nav.registre.bisys.consumer.ui.modules.BisysUiFatteVedtakConsumer;
import no.nav.registre.bisys.consumer.ui.modules.BisysUiSoknadConsumer;
import no.nav.registre.bisys.provider.requests.SyntetiserBisysRequest;

@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles("test")
public class SyntetiseringServiceTest {

  @Mock
  private HodejegerenConsumer hodejegerenConsumer;

  @Mock
  private BisysSyntetisererenConsumer syntetisererenConsumer;

  @Mock
  private BisysUiSupport navigationSupport;

  @Mock
  private BisysUiSoknadConsumer soknadConsumer;

  @Mock
  private BisysUiFatteVedtakConsumer fatteVedtakConsumer;

  @Mock
  private BisysRequestAugments bisysRequestAugments;

  @InjectMocks
  private SyntetiseringService syntetiseringService;


  private Long avspillergruppeId = 123L;
  private String miljoe = "t1";
  private SyntetiserBisysRequest syntetiserBisysRequest;
  private List<String> foedteIdenter;
  private String barn1 = "04041956789";
  private String barn2 = "03051712345";
  private String bidragsmottaker = "01016259875";
  private String bidragspliktig = "02056157925";
  private List<SyntetisertBidragsmelding> syntetiserteBidragsmeldinger;
  private List<Relasjon> relasjoner;

  @Before
  public void setUp() {
    foedteIdenter = new ArrayList<>(Arrays.asList(barn1, barn2));
    syntetiserBisysRequest =
        new SyntetiserBisysRequest(avspillergruppeId, miljoe, foedteIdenter.size());
    syntetiserteBidragsmeldinger =
        new ArrayList<>(Arrays.asList(SyntetisertBidragsmelding.builder().build(),
            SyntetisertBidragsmelding.builder().build()));
    relasjoner = new ArrayList<>(Arrays.asList(
        Relasjon.builder().fnrRelasjon(bidragsmottaker).typeRelasjon(RELASJON_MOR).build(),
        Relasjon.builder().fnrRelasjon(bidragspliktig).typeRelasjon(RELASJON_FAR).build()));
  }

  @Test
  public void shouldGenerateBidragsmeldinger() {
    when(hodejegerenConsumer.finnFoedteIdenter(avspillergruppeId)).thenReturn(foedteIdenter);
    when(syntetisererenConsumer.getSyntetiserteBidragsmeldinger(foedteIdenter.size()))
        .thenReturn(syntetiserteBidragsmeldinger);
    when(hodejegerenConsumer.hentRelasjonerTilIdent(barn1, miljoe))
        .thenReturn(RelasjonsResponse.builder().fnr(barn1).relasjoner(relasjoner).build());

    when(hodejegerenConsumer.hentRelasjonerTilIdent(barn2, miljoe))
        .thenReturn(RelasjonsResponse.builder().fnr(barn2).relasjoner(relasjoner).build());

    List<SyntetisertBidragsmelding> syntetiserteBidragsmeldinger =
        syntetiseringService.generateBidragsmeldinger(syntetiserBisysRequest);

    assertThat(syntetiserteBidragsmeldinger.get(0).getBarnetsFnr(), equalTo(barn1));
    assertThat(syntetiserteBidragsmeldinger.get(0).getBidragsmottaker(), equalTo(bidragsmottaker));
    assertThat(syntetiserteBidragsmeldinger.get(0).getBidragspliktig(), equalTo(bidragspliktig));
    assertThat(syntetiserteBidragsmeldinger.get(1).getBarnetsFnr(), equalTo(barn2));
    assertThat(syntetiserteBidragsmeldinger.get(1).getBidragsmottaker(), equalTo(bidragsmottaker));
    assertThat(syntetiserteBidragsmeldinger.get(1).getBidragspliktig(), equalTo(bidragspliktig));
  }

  @Test
  public void shouldLogOnTooFewIdenter() {
    Logger logger = (Logger) LoggerFactory.getLogger(SyntetiseringService.class);
    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);

    foedteIdenter.remove(foedteIdenter.size() - 1);
    syntetiserteBidragsmeldinger.remove(syntetiserteBidragsmeldinger.size() - 1);
    when(hodejegerenConsumer.finnFoedteIdenter(avspillergruppeId)).thenReturn(foedteIdenter);
    when(syntetisererenConsumer.getSyntetiserteBidragsmeldinger(foedteIdenter.size()))
        .thenReturn(syntetiserteBidragsmeldinger);
    when(hodejegerenConsumer.hentRelasjonerTilIdent(barn1, miljoe))
        .thenReturn(RelasjonsResponse.builder().fnr(barn1).relasjoner(relasjoner).build());

    List<SyntetisertBidragsmelding> syntetiserteBidragsmeldinger =
        syntetiseringService.generateBidragsmeldinger(syntetiserBisysRequest);

    assertThat(syntetiserteBidragsmeldinger.get(0).getBarnetsFnr(), equalTo(barn1));
    assertThat(syntetiserteBidragsmeldinger.get(0).getBidragsmottaker(), equalTo(bidragsmottaker));
    assertThat(syntetiserteBidragsmeldinger.get(0).getBidragspliktig(), equalTo(bidragspliktig));
    assertThat(listAppender.list.size(), is(CoreMatchers.equalTo(1)));
    assertThat(listAppender.list.get(0).toString(), containsString(
        "Fant ikke nok identer registrert med mor og far. Oppretter 1 bidragsmelding(er)."));
  }

  @Test
  public void shouldProcessBidragsmeldingerIfNoExceptionsOccur()
      throws BidragRequestProcessingException {
    when(hodejegerenConsumer.finnFoedteIdenter(avspillergruppeId)).thenReturn(foedteIdenter);
    when(syntetisererenConsumer.getSyntetiserteBidragsmeldinger(foedteIdenter.size()))
        .thenReturn(syntetiserteBidragsmeldinger);
    when(hodejegerenConsumer.hentRelasjonerTilIdent(barn1, miljoe))
        .thenReturn(RelasjonsResponse.builder().fnr(barn1).relasjoner(relasjoner).build());
    when(hodejegerenConsumer.hentRelasjonerTilIdent(barn2, miljoe))
        .thenReturn(RelasjonsResponse.builder().fnr(barn2).relasjoner(relasjoner).build());

    List<SyntetisertBidragsmelding> syntetiserteBidragsmeldinger =
        syntetiseringService.generateBidragsmeldinger(syntetiserBisysRequest);

    syntetiseringService.processBidragsmeldinger(syntetiserteBidragsmeldinger);

  }

  @Test
  public void shouldSkipToNextBidragsmeldingIfExceptionOccur()
      throws BidragRequestProcessingException {
    when(hodejegerenConsumer.finnFoedteIdenter(avspillergruppeId)).thenReturn(foedteIdenter);
    when(syntetisererenConsumer.getSyntetiserteBidragsmeldinger(foedteIdenter.size()))
        .thenReturn(syntetiserteBidragsmeldinger);
    when(hodejegerenConsumer.hentRelasjonerTilIdent(barn1, miljoe))
        .thenReturn(RelasjonsResponse.builder().fnr(barn1).relasjoner(relasjoner).build());
    when(hodejegerenConsumer.hentRelasjonerTilIdent(barn2, miljoe))
        .thenReturn(RelasjonsResponse.builder().fnr(barn2).relasjoner(relasjoner).build());

    List<SyntetisertBidragsmelding> syntetiserteBidragsmeldinger =
        syntetiseringService.generateBidragsmeldinger(syntetiserBisysRequest);

    syntetiseringService.processBidragsmeldinger(syntetiserteBidragsmeldinger);
  }
}
