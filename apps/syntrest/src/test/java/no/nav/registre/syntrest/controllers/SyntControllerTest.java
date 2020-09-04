package no.nav.registre.syntrest.controllers;

import no.nav.registre.syntrest.consumer.SyntConsumer;
import no.nav.registre.syntrest.domain.aap.AAP115Melding;
import no.nav.registre.syntrest.domain.aap.AAPMelding;
import no.nav.registre.syntrest.domain.aareg.Arbeidsforholdsmelding;
import no.nav.registre.syntrest.domain.bisys.Barnebidragsmelding;
import no.nav.registre.syntrest.domain.elsam.Historikk;
import no.nav.registre.syntrest.domain.frikort.FrikortKvittering;
import no.nav.registre.syntrest.domain.inst.Institusjonsmelding;
import no.nav.registre.syntrest.domain.medl.Medlemskapsmelding;
import no.nav.registre.syntrest.domain.popp.Inntektsmelding;
import no.nav.registre.syntrest.domain.sam.SamMelding;
import no.nav.registre.syntrest.domain.tp.TPmelding;
import no.nav.registre.syntrest.domain.tps.SkdMelding;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SyntControllerTest {

    @Mock
    private SyntConsumer consumer;

    private String aaregUrl = "dummy/generate_aareg";
    private String aap115Url = "dummy/generate_arena_aap/11_5/{numToGenerate}";
    private String aapUrl = "dummy/generate_arena_aap/ny_rettighet/{numToGenerate}";
    private String bisysUrl = "dummy/generate_bisys/{numToGenerate}";
    private String instUrl = "dummy/generate_inst/{numToGenerate}";
    private String medlUrl = "dummy/generate_medl/{numToGenerate}";
    private String arenaMeldekortUrl = "dummy/generate_meldekort/{numToGenerate}/{meldegruppe}";
    private String navEndringsmeldingUrl = "dummy/generate_nav/{numToGenerate}/{endringskode}";
    private String poppUrl = "dummy/generate_popp";
    private String samUrl = "dummy/generate_sam/{numToGenerate}";
    private String inntektUrl = "dummy/generate_inntekt";
    private String tpUrl = "dummy/generate_tp/{numToGenerate}";
    private String tpsUrl = "dummy/generate_tps/{numToGenerate}/{endringskode}";
    private String frikortUrl = "dummy/generate_frikort";
    private String elsamUrl = "dummy/generate_elsam/history_json";

    @InjectMocks
    private SyntController syntController;

    @Test
    public void aaregTest() throws NoSuchFieldException {
        FieldSetter.setField(syntController, syntController.getClass().getDeclaredField("aaregUrl"), aaregUrl);
        List<String> identer = new ArrayList<>();
        List<Arbeidsforholdsmelding> expectedResponse = new ArrayList<>();

        when(consumer.synthesizeData(any())).thenReturn(expectedResponse);

        ResponseEntity<List<Arbeidsforholdsmelding>> listResponseEntity = syntController.generateAareg(identer);

        verify(consumer).synthesizeData(any(RequestEntity.class));

        assertThat(listResponseEntity.getBody(), equalTo(expectedResponse));
    }

    @Test
    public void aap115Test() throws NoSuchFieldException {
        FieldSetter.setField(syntController, syntController.getClass().getDeclaredField("aap115Url"), aap115Url);
        int numToGenerate = 2;
        List<AAP115Melding> expectedResponse = new ArrayList<>(2);
        expectedResponse.add(new AAP115Melding());
        expectedResponse.add(new AAP115Melding());

        when(consumer.synthesizeData(any(RequestEntity.class))).thenReturn(expectedResponse);

        ResponseEntity<List<AAP115Melding>> listResponseEntity = syntController.generateAAP11_5(numToGenerate);
        verify(consumer).synthesizeData(any(RequestEntity.class));
        assertThat(listResponseEntity.getBody(), equalTo(expectedResponse));
    }
    @Test
    public void aapNyRettighetTest() throws NoSuchFieldException {
        FieldSetter.setField(syntController, syntController.getClass().getDeclaredField("aapUrl"), aapUrl);
        int numToGenerate = 2;
        List<AAPMelding> expectedResponse = new ArrayList<>(2);
        expectedResponse.add(new AAPMelding());
        expectedResponse.add(new AAPMelding());

        when(consumer.synthesizeData(any(RequestEntity.class))).thenReturn(expectedResponse);

        ResponseEntity<List<AAPMelding>> listResponseEntity = syntController.generateAAPNyRettighet(numToGenerate);
        verify(consumer).synthesizeData(any(RequestEntity.class));
        assertThat(listResponseEntity.getBody(), equalTo(expectedResponse));
    }

    @Test
    public void bisysTest() throws NoSuchFieldException {
        FieldSetter.setField(syntController, syntController.getClass().getDeclaredField("bisysUrl"), bisysUrl);
        int numToGenerate = 2;
        List<Barnebidragsmelding> expectedResponse = new ArrayList<>(2);
        expectedResponse.add(new Barnebidragsmelding());
        expectedResponse.add(new Barnebidragsmelding());

        when(consumer.synthesizeData(any(RequestEntity.class))).thenReturn(expectedResponse);

        ResponseEntity<List<Barnebidragsmelding>> listResponseEntity = syntController.generateBisys(numToGenerate);
        verify(consumer).synthesizeData(any(RequestEntity.class));
        assertThat(listResponseEntity.getBody(), equalTo(expectedResponse));
    }

    @Test
    public void instTest() throws NoSuchFieldException {
        FieldSetter.setField(syntController, syntController.getClass().getDeclaredField("instUrl"), instUrl);
        int numToGenerate = 2;
        List<Institusjonsmelding> expectedResponse = new ArrayList<>(2);
        expectedResponse.add(new Institusjonsmelding());
        expectedResponse.add(new Institusjonsmelding());

        when(consumer.synthesizeData(any(RequestEntity.class))).thenReturn(expectedResponse);

        ResponseEntity<List<Institusjonsmelding>> listResponseEntity = syntController.generateInst(numToGenerate);
        verify(consumer).synthesizeData(any(RequestEntity.class));
        assertThat(listResponseEntity.getBody(), equalTo(expectedResponse));
    }

    @Test
    public void medlTest() throws NoSuchFieldException {
        FieldSetter.setField(syntController, syntController.getClass().getDeclaredField("medlUrl"), medlUrl);
        int numToGenerate = 2;
        List<Medlemskapsmelding> expectedResponse = new ArrayList<>(2);
        expectedResponse.add(new Medlemskapsmelding());
        expectedResponse.add(new Medlemskapsmelding());

        when(consumer.synthesizeData(any(RequestEntity.class))).thenReturn(expectedResponse);

        ResponseEntity<List<Medlemskapsmelding>> listResponseEntity = syntController.generateMedl(numToGenerate);
        verify(consumer).synthesizeData(any(RequestEntity.class));
        assertThat(listResponseEntity.getBody(), equalTo(expectedResponse));
    }

    @Test
    public void arenaMeldekortTest() throws NoSuchFieldException {
        FieldSetter.setField(syntController, syntController.getClass().getDeclaredField("arenaMeldekortUrl"), arenaMeldekortUrl);
        int numToGenerate = 2;
        List<String> expectedResponse = new ArrayList<>(2);
        expectedResponse.add("<XML/>");
        expectedResponse.add("<XML/>");

        when(consumer.synthesizeData(any(RequestEntity.class))).thenReturn(expectedResponse);

        ResponseEntity<List<String>> listResponseEntity = syntController.generateMeldekort("ATTF", numToGenerate, null);
        verify(consumer).synthesizeData(any(RequestEntity.class));
        assertThat(listResponseEntity.getBody(), equalTo(expectedResponse));
    }

    @Test(expected = ResponseStatusException.class)
    public void arenaMeldekortFeilMeldegruppeTest() throws NoSuchFieldException {
        FieldSetter.setField(syntController, syntController.getClass().getDeclaredField("arenaMeldekortUrl"), arenaMeldekortUrl);
        int numToGenerate = 2;
        List<String> expectedResponse = new ArrayList<>(2);
        expectedResponse.add("<XML/>");
        expectedResponse.add("<XML/>");

        ResponseEntity<List<String>> listResponseEntity = syntController.generateMeldekort("Feil", numToGenerate, null);
        verify(consumer).synthesizeData(any(RequestEntity.class));
        assertThat(listResponseEntity.getBody(), equalTo(expectedResponse));
    }

    @Test
    public void navEndringsmeldingTest() throws NoSuchFieldException {
        FieldSetter.setField(syntController, syntController.getClass().getDeclaredField("navEndringsmeldingUrl"), navEndringsmeldingUrl);
        int numToGenerate = 2;
        List<String> expectedResponse = new ArrayList<>(2);
        expectedResponse.add("<XML/>");
        expectedResponse.add("<XML/>");

        when(consumer.synthesizeData(any(RequestEntity.class))).thenReturn(expectedResponse);

        ResponseEntity<List<String>> listResponseEntity = syntController.generateNavEndringsmelding("Z010", numToGenerate);
        verify(consumer).synthesizeData(any(RequestEntity.class));
        assertThat(listResponseEntity.getBody(), equalTo(expectedResponse));
    }

    @Test(expected = ResponseStatusException.class)
    public void navFeilEndringsmeldingTest() throws NoSuchFieldException {
        FieldSetter.setField(syntController, syntController.getClass().getDeclaredField("navEndringsmeldingUrl"), navEndringsmeldingUrl);
        int numToGenerate = 2;
        List<String> expectedResponse = new ArrayList<>(2);
        expectedResponse.add("<XML/>");
        expectedResponse.add("<XML/>");

        ResponseEntity<List<String>> listResponseEntity = syntController.generateNavEndringsmelding("Feil", numToGenerate);
        verify(consumer).synthesizeData(any(RequestEntity.class));
        assertThat(listResponseEntity.getBody(), equalTo(expectedResponse));
    }


    @Test
    public void poppTest() throws NoSuchFieldException{
        FieldSetter.setField(syntController, syntController.getClass().getDeclaredField("poppUrl"), poppUrl);
        List<String> fnrs = new ArrayList<>();
        fnrs.add("12345678910");
        fnrs.add("10987654321");

        List<Inntektsmelding> expectedResponse = new ArrayList<>(2);
        expectedResponse.add(new Inntektsmelding());
        expectedResponse.add(new Inntektsmelding());

        when(consumer.synthesizeData(any(RequestEntity.class))).thenReturn(expectedResponse);

        ResponseEntity<List<Inntektsmelding>> listResponseEntity = syntController.generateInntektsmelding(fnrs);
        verify(consumer).synthesizeData(any(RequestEntity.class));
        assertThat(listResponseEntity.getBody(), equalTo(expectedResponse));
    }

    @Test(expected = ResponseStatusException.class)
    public void poppFeilTest() throws NoSuchFieldException{
        FieldSetter.setField(syntController, syntController.getClass().getDeclaredField("poppUrl"), poppUrl);
        List<String> fnrs = new ArrayList<>();
        fnrs.add("12345678910");
        fnrs.add("10987654321");
        fnrs.add("næhh..");

        List<Inntektsmelding> expectedResponse = new ArrayList<>(2);
        expectedResponse.add(new Inntektsmelding());
        expectedResponse.add(new Inntektsmelding());

        ResponseEntity<List<Inntektsmelding>> listResponseEntity = syntController.generateInntektsmelding(fnrs);
        verify(consumer).synthesizeData(any(RequestEntity.class));
        assertThat(listResponseEntity.getBody(), equalTo(expectedResponse));
    }

    @Test
    public void samTest() throws NoSuchFieldException {
        FieldSetter.setField(syntController, syntController.getClass().getDeclaredField("samUrl"), samUrl);
        int numToGenerate = 2;
        List<SamMelding> expectedResponse = new ArrayList<>(2);
        expectedResponse.add(new SamMelding());
        expectedResponse.add(new SamMelding());

        when(consumer.synthesizeData(any(RequestEntity.class))).thenReturn(expectedResponse);

        ResponseEntity<List<SamMelding>> listResponseEntity = syntController.generateSamMelding(numToGenerate);
        verify(consumer).synthesizeData(any(RequestEntity.class));
        assertThat(listResponseEntity.getBody(), equalTo(expectedResponse));
    }

    @Test
    public void inntektTest() throws NoSuchFieldException {
        FieldSetter.setField(syntController, syntController.getClass().getDeclaredField("inntektUrl"), inntektUrl);
        Map<String, List<no.nav.registre.syntrest.domain.inntekt.Inntektsmelding>> fnrInntektMap = new HashMap<>();
        fnrInntektMap.put("12345678910", new ArrayList<>());
        fnrInntektMap.put("10987654321", new ArrayList<>());

        Map<String, List<no.nav.registre.syntrest.domain.inntekt.Inntektsmelding>> expectedResponse = new HashMap<>();
        expectedResponse.put("12345678910", new ArrayList<>());
        expectedResponse.put("10987654321", new ArrayList<>());

        when(consumer.synthesizeData(any(RequestEntity.class))).thenReturn(expectedResponse);

        ResponseEntity<Map<String, List<no.nav.registre.syntrest.domain.inntekt.Inntektsmelding>>> mapResponseEntity =
                syntController.generateInntektsMelding(fnrInntektMap);
        verify(consumer).synthesizeData(any(RequestEntity.class));
        assertThat(mapResponseEntity.getBody(), equalTo(expectedResponse));
    }

    @Test
    public void tpTest() throws NoSuchFieldException {
        FieldSetter.setField(syntController, syntController.getClass().getDeclaredField("tpUrl"), tpUrl);
        int numToGenerate = 2;
        List<TPmelding> expectedResponse = new ArrayList<>(2);
        expectedResponse.add(new TPmelding());
        expectedResponse.add(new TPmelding());

        when(consumer.synthesizeData(any(RequestEntity.class))).thenReturn(expectedResponse);

        ResponseEntity<List<TPmelding>> listResponseEntity = syntController.generateTPMelding(numToGenerate);
        verify(consumer).synthesizeData(any(RequestEntity.class));
        assertThat(listResponseEntity.getBody(), equalTo(expectedResponse));
    }

    @Test
    public void tpsTest() throws NoSuchFieldException {
        FieldSetter.setField(syntController, syntController.getClass().getDeclaredField("tpsUrl"), tpsUrl);
        int numToGenerate = 2;
        List<SkdMelding> expectedResponse = new ArrayList<>(2);
        expectedResponse.add(new SkdMelding());
        expectedResponse.add(new SkdMelding());

        when(consumer.synthesizeData(any(RequestEntity.class))).thenReturn(expectedResponse);

        ResponseEntity<List<SkdMelding>> listResponseEntity = syntController.generateSkdMelding("0110", numToGenerate);
        verify(consumer).synthesizeData(any(RequestEntity.class));
        assertThat(listResponseEntity.getBody(), equalTo(expectedResponse));
    }

    @Test(expected = ResponseStatusException.class)
    public void tpsFeilTest() throws NoSuchFieldException {
        FieldSetter.setField(syntController, syntController.getClass().getDeclaredField("tpsUrl"), tpsUrl);
        int numToGenerate = 2;
        List<SkdMelding> expectedResponse = new ArrayList<>(2);
        expectedResponse.add(new SkdMelding());
        expectedResponse.add(new SkdMelding());

        ResponseEntity<List<SkdMelding>> listResponseEntity = syntController.generateSkdMelding("Feil", numToGenerate);
        verify(consumer).synthesizeData(any(RequestEntity.class));
        assertThat(listResponseEntity.getBody(), equalTo(expectedResponse));
    }

    @Test
    public void frikortTest() throws NoSuchFieldException {
        FieldSetter.setField(syntController, syntController.getClass().getDeclaredField("frikortUrl"), frikortUrl);
        Map<String, Integer> fnrAntMeldingMap = new HashMap<>();
        fnrAntMeldingMap.put("12345678910", 2);
        fnrAntMeldingMap.put("10987651233", 3);
        Map<String, List<FrikortKvittering>> expectedResponse = new HashMap<>();
        expectedResponse.put("12345678910", new ArrayList<>(2));
        expectedResponse.put("10987651233", new ArrayList<>(3));

        when(consumer.synthesizeData(any(RequestEntity.class))).thenReturn(expectedResponse);

        ResponseEntity<Map<String, List<FrikortKvittering>>> mapResponseEntity = syntController.generateFrikort(fnrAntMeldingMap);
        verify(consumer).synthesizeData(any(RequestEntity.class));
        assertThat(mapResponseEntity.getBody(), equalTo(expectedResponse));
    }

    @Test
    public void elsamTest() throws NoSuchFieldException {
        FieldSetter.setField(syntController, syntController.getClass().getDeclaredField("elsamUrl"), elsamUrl);
        Map<String, String> fnrStartDatoMap = new HashMap<>();
        fnrStartDatoMap.put("12345678910", "2019-01-31");
        fnrStartDatoMap.put("10987651233", "2018-12-31");
        Map<String, Historikk> expectedResponse = new HashMap<>();
        expectedResponse.put("12345678910", new Historikk());
        expectedResponse.put("10987651233", new Historikk());

        when(consumer.synthesizeData(any(RequestEntity.class))).thenReturn(expectedResponse);

        ResponseEntity<Map<String, Historikk>> mapResponseEntity = syntController.generateSykemeldingHistorikk(fnrStartDatoMap);
        verify(consumer).synthesizeData(any(RequestEntity.class));
        assertThat(mapResponseEntity.getBody(), equalTo(expectedResponse));
    }
}
