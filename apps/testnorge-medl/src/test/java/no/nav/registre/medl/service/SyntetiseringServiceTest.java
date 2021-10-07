package no.nav.registre.medl.service;

import static no.nav.registre.medl.testutils.ResourceUtils.getResourceFileContent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.registre.medl.adapter.AktoerAdapter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import no.nav.registre.medl.consumer.rs.AktoerRegisteretConsumer;
import no.nav.registre.medl.consumer.rs.HodejegerenHistorikkConsumer;
import no.nav.registre.medl.consumer.rs.MedlSyntConsumer;
import no.nav.registre.medl.consumer.rs.response.MedlSyntResponse;
import no.nav.registre.medl.database.model.TAktoer;
import no.nav.registre.medl.database.model.TMedlemPeriode;
import no.nav.registre.medl.database.model.TStudieinformasjon;
import no.nav.registre.medl.database.repository.MedlemPeriodeRepository;
import no.nav.registre.medl.database.repository.StudieinformasjonRepository;
import no.nav.registre.medl.provider.rs.requests.SyntetiserMedlRequest;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;

@RunWith(MockitoJUnitRunner.class)
public class SyntetiseringServiceTest {

    @Mock
    private HodejegerenConsumer hodejegerenConsumer;

    @Mock
    private HodejegerenHistorikkConsumer hodejegerenHistorikkConsumer;

    @Mock
    private MedlSyntConsumer medlSyntConsumer;

    @Mock
    private AktoerRegisteretConsumer aktoerRegisteretConsumer;

    @Mock
    private AktoerAdapter aktoerAdapter;

    @Mock
    private MedlemPeriodeRepository medlemPeriodeRepository;

    @Mock
    private StudieinformasjonRepository studieinformasjonRepository;

    @InjectMocks
    private SyntetiseringService syntetiseringService;

    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private List<String> identer;

    @Before
    public void setUp() {
        identer = Arrays.asList("01010101010", "02020202020");
    }

    @Test
    public void shouldOppretteMedlemskapOK() {
        var syntetiserMedlRequest = new SyntetiserMedlRequest(avspillergruppeId, miljoe, 1);
        createMocksForTwo(syntetiserMedlRequest);
        var medlemPerioder = syntetiseringService.opprettMeldinger(syntetiserMedlRequest);
        assertEquals(2, medlemPerioder.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldOppretteMedlemskapMedUgyldigProsentfaktor() {
        var syntetiserMedlRequest = new SyntetiserMedlRequest(avspillergruppeId, miljoe, 1.2);
        syntetiseringService.opprettMeldinger(syntetiserMedlRequest);
    }

    @Test
    public void shouldOpprettDelvisMeldingOK() {
        createMocksForOne();

        var objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MedlSyntResponse syntetiserteMeldinger = null;
        try {
            syntetiserteMeldinger = objectMapper.readValue(getResourceFileContent("delvisMelding.json"), new TypeReference<MedlSyntResponse>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        var medlemPeriode = syntetiseringService.opprettDelvisMelding(syntetiserteMeldinger, identer.get(0), miljoe);
        assertNotNull(medlemPeriode);
        assertEquals("LAANEKASSEN", medlemPeriode.getKilde());
    }

    @Test(expected = HttpClientErrorException.class)
    public void shouldOpprettDelvisMeldingTomFraAktoer() {
        createMockFnrToAktoerEmpty();
        var objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MedlSyntResponse syntetiserteMeldinger = null;
        try {
            syntetiserteMeldinger = objectMapper.readValue(getResourceFileContent("delvisMelding.json"), new TypeReference<MedlSyntResponse>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        syntetiseringService.opprettDelvisMelding(syntetiserteMeldinger, identer.get(0), miljoe);
    }

    @Test(expected = HttpServerErrorException.class)
    public void shouldOpprettDelvisMeldingTomFraSynt() {
        createMockTomFraSynt();
        var objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MedlSyntResponse syntetiserteMeldinger = null;
        try {
            syntetiserteMeldinger = objectMapper.readValue(getResourceFileContent("delvisMelding.json"), new TypeReference<MedlSyntResponse>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        syntetiseringService.opprettDelvisMelding(syntetiserteMeldinger, identer.get(0), miljoe);
    }

    private void createMockFnrToAktoerEmpty() {
        when(aktoerRegisteretConsumer.lookupAktoerIdFromFnr(Collections.singletonList(identer.get(0)), miljoe)).thenReturn(Collections.EMPTY_MAP);
    }

    private void createMockTomFraSynt() {
        when(medlSyntConsumer.hentMedlemskapsmeldingerFromSyntRest(1)).thenReturn(Collections.EMPTY_LIST);
        HashMap<String, String> fnrToAktoerSingle = new HashMap<>();
        fnrToAktoerSingle.put(identer.get(0), "abc");
        when(aktoerRegisteretConsumer.lookupAktoerIdFromFnr(Collections.singletonList(identer.get(0)), miljoe)).thenReturn(fnrToAktoerSingle);
    }

    private void createMocksForOne() {
        var objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<MedlSyntResponse> syntetiserteMeldinger = null;
        try {
            syntetiserteMeldinger = objectMapper.readValue(getResourceFileContent("medlemskapsmelding.json"), new TypeReference<List<MedlSyntResponse>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert syntetiserteMeldinger != null;
        when(medlSyntConsumer.hentMedlemskapsmeldingerFromSyntRest(1)).thenReturn(Collections.singletonList(syntetiserteMeldinger.get(0)));
        HashMap<String, String> fnrToAktoerSingle = new HashMap<>();
        fnrToAktoerSingle.put(identer.get(0), "abc");
        when(aktoerRegisteretConsumer.lookupAktoerIdFromFnr(Collections.singletonList(identer.get(0)), miljoe)).thenReturn(fnrToAktoerSingle);
        when(aktoerAdapter.opprettAktoer(any(MedlSyntResponse.class), anyString(), anyString())).then((Answer<TAktoer>) invocation -> {
            Object[] args = invocation.getArguments();
            final String aktoerId = (String) args[1];
            final String ident = (String) args[2];
            return TAktoer.builder()
                    .id(1L)
                    .aktoerid(aktoerId)
                    .ident(ident)
                    .build();
        });

        when(studieinformasjonRepository.save(any(TStudieinformasjon.class))).then((Answer<TStudieinformasjon>) invocation -> {
            Object[] args = invocation.getArguments();
            TStudieinformasjon arg = (TStudieinformasjon) args[0];
            arg.setStudieinformasjonId(1L);
            return arg;
        });

        when(medlemPeriodeRepository.save(any(TMedlemPeriode.class))).then((Answer<TMedlemPeriode>) invocation -> {
            Object[] args = invocation.getArguments();
            TMedlemPeriode arg = (TMedlemPeriode) args[0];
            if (arg.getAktoerId() == 1L) {
                arg.setMedlemPeriodeId(1L);
            }
            return arg;
        });
    }

    private void createMocksForTwo(SyntetiserMedlRequest syntetiserMedlRequest) {
        when(hodejegerenConsumer.getLevende(
                syntetiserMedlRequest.getAvspillergruppeId()))
                .thenReturn(identer);

        var pageable = PageRequest.of(0, 1000);
        List<TMedlemPeriode> tMedlemPeriodes = new ArrayList<>(
                Arrays.asList(
                        TMedlemPeriode.builder()
                                .aktoerId(0L)
                                .type("PMMEDSKP")
                                .versjon("1")
                                .studieinformasjonId(1L)
                                .build(),
                        TMedlemPeriode.builder()
                                .aktoerId(1L)
                                .type("PMMEDSKP")
                                .versjon("0")
                                .studieinformasjonId(1L)
                                .build()
                ));
        Page<TMedlemPeriode> page = new PageImpl(tMedlemPeriodes);
        when(medlemPeriodeRepository.findAll(eq(pageable))).thenReturn(page);

        var objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<MedlSyntResponse> syntetiserteMeldinger = null;
        try {
            syntetiserteMeldinger = objectMapper.readValue(getResourceFileContent("medlemskapsmelding.json"), new TypeReference<List<MedlSyntResponse>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        when(medlSyntConsumer.hentMedlemskapsmeldingerFromSyntRest(identer.size())).thenReturn(syntetiserteMeldinger);

        HashMap<String, String> fnrToAktoer = new HashMap<>();
        fnrToAktoer.put(identer.get(0), "abc");
        fnrToAktoer.put(identer.get(1), "def");
        when(aktoerRegisteretConsumer.lookupAktoerIdFromFnr(identer, miljoe)).thenReturn(fnrToAktoer);

        when(aktoerAdapter.opprettAktoer(any(MedlSyntResponse.class), anyString(), anyString())).then((Answer<TAktoer>) invocation -> {
            Object[] args = invocation.getArguments();
            final String aktoerId = (String) args[1];
            final String ident = (String) args[2];
            TAktoer tAktoer = TAktoer.builder()
                    .aktoerid(aktoerId)
                    .ident(ident)
                    .build();
            if (identer.get(0).equals(ident)) {
                tAktoer.setId(1L);
            } else {
                tAktoer.setId(2L);
            }
            return tAktoer;
        });

        when(studieinformasjonRepository.save(any(TStudieinformasjon.class))).then((Answer<TStudieinformasjon>) invocation -> {
            Object[] args = invocation.getArguments();
            TStudieinformasjon arg = (TStudieinformasjon) args[0];
            if ("".equals(arg.getGodkjent())) {
                arg.setStudieinformasjonId(1L);
            } else {
                arg.setStudieinformasjonId(2L);
            }
            return arg;
        });

        when(medlemPeriodeRepository.save(any(TMedlemPeriode.class))).then((Answer<TMedlemPeriode>) invocation -> {
            Object[] args = invocation.getArguments();
            TMedlemPeriode arg = (TMedlemPeriode) args[0];
            if (arg.getAktoerId() == 1L) {
                arg.setMedlemPeriodeId(1L);
            } else {
                arg.setMedlemPeriodeId(2L);
            }
            return arg;
        });

        when(hodejegerenHistorikkConsumer.saveHistory(any())).thenReturn(identer);
    }
}
