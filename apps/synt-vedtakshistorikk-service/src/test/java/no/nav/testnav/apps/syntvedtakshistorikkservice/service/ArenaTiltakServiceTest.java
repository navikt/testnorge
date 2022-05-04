//package no.nav.testnav.apps.syntvedtakshistorikkservice.service;
//
//import no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.RequestUtils;
//import no.nav.testnav.apps.syntvedtakshistorikkservice.service.util.ServiceUtils;
//import no.nav.testnav.libs.domain.dto.arena.testnorge.historikk.Vedtakshistorikk;
//import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.MockitoJUnitRunner;
//
//import java.time.LocalDate;
//import java.util.Collections;
//
//import static org.mockito.Mockito.when;
//
//@RunWith(MockitoJUnitRunner.class)
//public class ArenaTiltakServiceTest {
//
//    @Mock
//    private ServiceUtils serviceUtils;
//
//    @Mock
//    private RequestUtils requestUtils;
//
//    @Mock
//    private ArenaForvalterService arenaForvalterService;
//
//    @InjectMocks
//    private ArenaTiltakService arenaTiltakService;
//
//    private Vedtakshistorikk historikk;
//    private String fnr1 = "12345678910";
//    private String miljoe = "test";
//    private NyttVedtakTiltak senesteVedtak;
//
//    @Before
//    public void setUp() {
//        var tiltaksdeltakelse1 = NyttVedtakTiltak.builder().build();
//
//        tiltaksdeltakelse1.setFraDato(LocalDate.now());
//        tiltaksdeltakelse1.setTilDato(LocalDate.now().plusMonths(3));
//
//        senesteVedtak = tiltaksdeltakelse1;
//        var deltakelser = Collections.singletonList(tiltaksdeltakelse1);
//
//        historikk = Vedtakshistorikk.builder()
//                .tiltaksdeltakelse(deltakelser)
//                .build();
//    }
//
//
//    @Test
//    public void shouldFilterOutTiltaksdeltakelseWithoutTiltak(){
//        when(arenaForvalterService.opprettArbeidssoekerTiltaksdeltakelse(
//                fnr1, miljoe, senesteVedtak.getRettighetType(), LocalDate.now())).thenReturn()
//    }
//
//}
