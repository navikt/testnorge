package no.nav.registre.testnorge.elsam.service;

import static no.nav.registre.testnorge.elsam.utils.TssUtil.buildLegeFromTssResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import no.nav.registre.elsam.domain.Lege;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.registre.testnorge.consumers.hodejegeren.response.PersondataResponse;
import no.nav.registre.testnorge.elsam.consumer.rs.AaregstubConsumer;
import no.nav.registre.testnorge.elsam.consumer.rs.ElsamSyntConsumer;
import no.nav.registre.testnorge.elsam.consumer.rs.EregConsumer;
import no.nav.registre.testnorge.elsam.consumer.rs.HodejegerenHistorikkConsumer;
import no.nav.registre.testnorge.elsam.consumer.rs.TSSConsumer;
import no.nav.registre.testnorge.elsam.consumer.rs.response.aareg.AaregResponse;
import no.nav.registre.testnorge.elsam.consumer.rs.response.ereg.EregResponse;
import no.nav.registre.testnorge.elsam.consumer.rs.response.synt.ElsamSyntResponse;
import no.nav.registre.testnorge.elsam.consumer.rs.response.tss.TssResponse;
import no.nav.registre.testnorge.elsam.utils.DatoUtil;

@RunWith(MockitoJUnitRunner.class)
public class SyntetiseringServiceTest {

    private static final String MININORGE_LEGEKONTOR_ORGNUMMER = "880756053";
    private static final int MINIMUM_ALDER = 18;

    @Mock
    private HodejegerenConsumer hodejegerenConsumer;

    @Mock
    private ElsamSyntConsumer elsamSyntConsumer;

    @Mock
    private TSSConsumer tssConsumer;

    @Mock
    private EregConsumer eregConsumer;

    @Mock
    private AaregstubConsumer aaregstubConsumer;

    @Mock
    private Random rand;

    @Mock
    private DatoUtil datoUtil;

    @Mock
    private SykmeldingService sykmeldingService;

    @Mock
    private HodejegerenHistorikkConsumer hodejegerenHistorikkConsumer;

    @InjectMocks
    private SyntetiseringService syntetiseringService;

    private ObjectMapper objectMapper;

    private Map<String, Long> miljoerMedLegeavspillergruppe;

    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private int antallIdenter = 2;

    private String legeFnr1 = "19098723229";
    private String legeFnr2 = "03126126084";
    private List<String> leger;
    private List<Lege> expectedLeger;
    private String fnr1 = "30073245199";
    private String fnr2 = "02128516002";
    private List<String> identer;
    private String orgnummer1 = "975293378";
    private String orgnummer2 = "974550075";

    @Before
    public void setUp() throws IOException {
        objectMapper = new ObjectMapper();

        identer = new ArrayList<>();
        identer.add(fnr1);
        identer.add(fnr2);

        leger = new ArrayList<>();
        leger.add(legeFnr1);
        leger.add(legeFnr2);

        expectedLeger = new ArrayList<>();
        Map<String, TssResponse> legerFraTss = hentLeger();
        for (TssResponse tssResponse : legerFraTss.values()) {
            expectedLeger.add(buildLegeFromTssResponse(tssResponse));
        }

        miljoerMedLegeavspillergruppe = new HashMap<>();
        miljoerMedLegeavspillergruppe.put(miljoe, avspillergruppeId);

        ReflectionTestUtils.setField(syntetiseringService, "miljoerMedLegeavspillergruppe", miljoerMedLegeavspillergruppe);
    }

    /**
     * Tester happypath til syntetiseringsløpet
     *
     * @throws IOException
     */
    @Test
    public void shouldSyntetisereSykemeldinger() throws IOException {
        when(hodejegerenConsumer.getLevende(avspillergruppeId, MINIMUM_ALDER)).thenReturn(identer);
        when(aaregstubConsumer.hentAlleIdenterIStub()).thenReturn(identer);
        when(hodejegerenConsumer.getAdresse(eq(miljoe), anyList())).thenReturn(hentAdresser());
        when(tssConsumer.hentLeger(miljoerMedLegeavspillergruppe.get(miljoe), miljoe)).thenReturn(ResponseEntity.ok().body(hentLeger()));
        when(eregConsumer.hentEregdataFraOrgnummer(MININORGE_LEGEKONTOR_ORGNUMMER)).thenReturn(hentEregdataPaaLegekontor());
        when(aaregstubConsumer.hentArbeidsforholdTilIdent(fnr1)).thenReturn(hentArbeidsforholdFnr1());
        when(aaregstubConsumer.hentArbeidsforholdTilIdent(fnr2)).thenReturn(hentArbeidsforholdFnr2());
        when(eregConsumer.hentEregdataFraOrgnummer(orgnummer1)).thenReturn(hentArbeidsgiversNavn1());
        when(eregConsumer.hentEregdataFraOrgnummer(orgnummer2)).thenReturn(hentArbeidsgiversNavn2());
        when(hodejegerenConsumer.getPersondata(fnr1, miljoe)).thenReturn(hentPersondataFnr1());
        when(hodejegerenConsumer.getPersondata(fnr2, miljoe)).thenReturn(hentPersondataFnr2());
        when(datoUtil.lagTilfeldigDatoIAnsettelsesperiode(any())).thenReturn(LocalDate.of(2011, 1, 1));
        when(elsamSyntConsumer.syntetiserSykemeldinger(any())).thenReturn(hentSyntetiseringsResponse());
        when(hodejegerenHistorikkConsumer.saveHistory(any())).thenReturn(identer);

        syntetiseringService.syntetiserSykemeldinger(avspillergruppeId, miljoe, antallIdenter);

        verify(hodejegerenConsumer).getLevende(avspillergruppeId, MINIMUM_ALDER);
        verify(aaregstubConsumer).hentAlleIdenterIStub();
        verify(hodejegerenConsumer).getAdresse(eq(miljoe), anyList());
        verify(tssConsumer).hentLeger(miljoerMedLegeavspillergruppe.get(miljoe), miljoe);
        verify(eregConsumer).hentEregdataFraOrgnummer(MININORGE_LEGEKONTOR_ORGNUMMER);
        verify(aaregstubConsumer).hentArbeidsforholdTilIdent(fnr1);
        verify(aaregstubConsumer).hentArbeidsforholdTilIdent(fnr2);
        verify(eregConsumer).hentEregdataFraOrgnummer(orgnummer1);
        verify(eregConsumer).hentEregdataFraOrgnummer(orgnummer2);
        verify(hodejegerenConsumer).getPersondata(fnr1, miljoe);
        verify(hodejegerenConsumer).getPersondata(fnr2, miljoe);
        verify(datoUtil, times(2)).lagTilfeldigDatoIAnsettelsesperiode(any());
        verify(elsamSyntConsumer).syntetiserSykemeldinger(any());
        verify(hodejegerenHistorikkConsumer).saveHistory(any());
    }

    private Map<String, JsonNode> hentAdresser() throws IOException {
        URL resource = Resources.getResource("testdata/hodejegeren_adresser_paa_identer.json");
        return objectMapper.readValue(resource, new TypeReference<Map<String, JsonNode>>() {
        });
    }

    private Map<String, TssResponse> hentLeger() throws IOException {
        URL resource = Resources.getResource("testdata/tss_response.json");
        return objectMapper.readValue(resource, new TypeReference<Map<String, TssResponse>>() {
        });
    }

    private EregResponse hentEregdataPaaLegekontor() throws IOException {
        URL resource = Resources.getResource("testdata/ereg_data_mininorge.json");
        return objectMapper.readValue(resource, new TypeReference<EregResponse>() {
        });
    }

    private AaregResponse hentArbeidsforholdFnr1() throws IOException {
        URL resource = Resources.getResource("testdata/arbeidsforhold_fnr1.json");
        return objectMapper.readValue(resource, new TypeReference<AaregResponse>() {
        });
    }

    private AaregResponse hentArbeidsforholdFnr2() throws IOException {
        URL resource = Resources.getResource("testdata/arbeidsforhold_fnr2.json");
        return objectMapper.readValue(resource, new TypeReference<AaregResponse>() {
        });
    }

    private EregResponse hentArbeidsgiversNavn1() throws IOException {
        URL resource = Resources.getResource("testdata/ereg_data_orgnummer1.json");
        return objectMapper.readValue(resource, new TypeReference<EregResponse>() {
        });
    }

    private EregResponse hentArbeidsgiversNavn2() throws IOException {
        URL resource = Resources.getResource("testdata/ereg_data_orgnummer2.json");
        return objectMapper.readValue(resource, new TypeReference<EregResponse>() {
        });
    }

    private PersondataResponse hentPersondataFnr1() throws IOException {
        URL resource = Resources.getResource("testdata/hodejegeren_persondata_fnr1.json");
        return objectMapper.readValue(resource, new TypeReference<PersondataResponse>() {
        });
    }

    private PersondataResponse hentPersondataFnr2() throws IOException {
        URL resource = Resources.getResource("testdata/hodejegeren_persondata_fnr2.json");
        return objectMapper.readValue(resource, new TypeReference<PersondataResponse>() {
        });
    }

    private Map<String, ElsamSyntResponse> hentSyntetiseringsResponse() throws IOException {
        URL resource = Resources.getResource("testdata/elsam_synt_response.json");
        return objectMapper.readValue(resource, new TypeReference<Map<String, ElsamSyntResponse>>() {
        });
    }
}