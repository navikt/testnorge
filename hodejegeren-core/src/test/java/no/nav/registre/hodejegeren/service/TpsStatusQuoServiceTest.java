package no.nav.registre.hodejegeren.service;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.registre.hodejegeren.consumer.TpsfConsumer;

@RunWith(MockitoJUnitRunner.class)
public class TpsStatusQuoServiceTest {

    @InjectMocks
    private TpsStatusQuoService tpsStatusQuoService;

    @Mock
    private TpsfConsumer tpsfConsumer;

    private String aksjonsKode = "A0";
    private String environment = "Q11";
    private String fnr = "12345678901";
    private String routineName = "FS03-FDNUMMER-PERSDATA-O";
    private String jsonContent = "{\"response\":{\"data1\":{\"personStatus\":\"Død                             Fnr\",\"kortnavn\":\"HATT VAKKER\",\"datoInnvandret\":\"1953-05-12\",\"kommunenr\":\"\",\"epost\":{\"epostTidspunktReg\":\"\",\"epostSystem\":\"\",\"epostAdresse\":\"\",\"epostSaksbehandler\":\"\"},\"tlfPrivat\":{\"tlfSystem\":\"\",\"tlfTidspunktReg\":\"\",\"tlfNummer\":\"\",\"tlfSaksbehandler\":\"\"},\"tidligereKommunenr\":\"0219\",\"tknr\":\"0219\",\"tlfJobb\":{\"tlfSystem\":\"\",\"tlfTidspunktReg\":\"\",\"tlfNummer\":\"\",\"tlfSaksbehandler\":\"\"},\"bolignr\":\"\",\"datoDo\":\"2018-10-15\",\"datoUtvandret\":\"\",\"etternavn\":\"HATT\",\"postnr\":\"\",\"datoUmyndiggjort\":\"\",\"boPoststed\":\"\",\"utvandretTil\":\"\",\"boAdresse2\":\"\",\"mellomnavn\":\"\",\"innvandretFra\":\"ST.KITTS OG NEVIS\",\"giroInfo\":{\"giroNummer\":\"\",\"giroTidspunktReg\":\"\",\"giroSystem\":\"\",\"giroSaksbehandler\":\"\"},\"datoFlyttet\":\"2018-10-15\",\"postAdresse2\":\"\",\"postAdresse1\":\"\",\"datoSivilstand\":\"2018-10-15\",\"fnr\":12055310651,\"fornavn\":\"VAKKER\",\"sivilstand\":\"Gift\",\"postAdresse3\":\"\",\"datoStatsborger\":\"1953-05-12\",\"spesregType\":\"\",\"boAdresse1\":\"\",\"statsborger\":\"NORGE\",\"tlfMobil\":{\"tlfSystem\":\"\",\"tlfTidspunktReg\":\"\",\"tlfNummer\":\"\",\"tlfSaksbehandler\":\"\"}},\"status\":{\"kode\":\"00\",\"melding\":\" \",\"utfyllendeMelding\":\" \"},\"antallTotalt\":null}}";

    @Test
    public void getStatusQuo() throws IOException {

        JsonNode jsonNode = new ObjectMapper().readTree(jsonContent);

        List<String> feltNavn = new ArrayList<>();
        feltNavn.add("datoDo");

        when(tpsfConsumer.getTpsServiceRoutine(any(), any())).thenReturn(jsonNode);

        Map<String, String> statusQuoValues = tpsStatusQuoService.getStatusQuo(feltNavn, aksjonsKode, environment, fnr);

        assertEquals(1, statusQuoValues.size());
        assertEquals("2018-10-15", statusQuoValues.get("datoDo"));
    }

    @Test
    public void getStatusQuoRelasjon() throws IOException {
        String jsonContentRelasjon = "{\"response\":{\"data1\":{\"antallRelasjoner\":1,\"endringsDato\":\"2018-10-15\",\"adresse\":\"\",\"fnr\":12055310651,\"spesregType\":\"\",\"typeAdresse\":\"\",\"sivilstand\":\"GIFT\",\"relasjoner\":{\"relasjon\":{\"kortnavn\":\"DORULL LUGUBER\",\"datoDo\":\"\",\"mellomnavn\":\"\",\"etternavn\":\"DORULL\",\"adresseStatus\":0,\"spesregType\":\"\",\"fornavn\":\"LUGUBER\",\"fnrRelasjon\":\"01065500791\",\"typeRelasjon\":\"EKTE\"}}},\"status\":{\"kode\":\"00\",\"melding\":\" \",\"utfyllendeMelding\":\" \"},\"antallTotalt\":null}}";

        JsonNode jsonNode = new ObjectMapper().readTree(jsonContentRelasjon);

        List<String> feltNavn = new ArrayList<>();
        feltNavn.add("relasjon/typeRelasjon");
        feltNavn.add("relasjon/fnrRelasjon");

        when(tpsfConsumer.getTpsServiceRoutine(any(), any())).thenReturn(jsonNode);

        Map<String, String> statusQuoValues = tpsStatusQuoService.getStatusQuo(feltNavn, aksjonsKode, environment, fnr);

        assertEquals("EKTE", statusQuoValues.get("relasjon/typeRelasjon"));
        assertEquals("01065500791", statusQuoValues.get("relasjon/fnrRelasjon"));
    }

    @Test
    public void getStatusQuoKjerne() throws IOException {
        String jsonContentKjerne = "{\"response\":{\"data1\":{\"statsborgerskap\":\"NOR\",\"kjonn\":\"M\",\"fodselsnummerDetalj\":{\"fnrSystem\":\"\",\"fnrTidspunkt\":\"\",\"fnrSaksbehandler\":\"\"},\"bruker\":{\"sikkerhetsTiltak\":{\"sikrFom\":\"\",\"beskrSikkerhetsTiltak\":\"\",\"sikrTom\":\"\",\"opprettetSystem\":\"\",\"opprettetSaksbehandler\":\"\",\"opprettetTidspunkt\":\"\",\"typeSikkerhetsTiltak\":\"\"},\"NAVenhetDetalj\":{\"kodeNAVenhetBeskr\":\"FROGNER\",\"NAVenhetSaksbehandler\":\"AJOURHD\",\"datoNAVenhet\":\"2018-10-23\",\"NAVenhetTidspunkt\":\"2018-10-23\",\"NAVenhetSystem\":\"SKD\",\"kodeNAVenhet\":\"0312\"},\"umyndigDetalj\":{\"umyndigSaksbehandler\":\"\",\"umyndigSystem\":\"\",\"umyndigTidspunkt\":\"\"},\"diskresjonDetalj\":{\"diskresjonSaksbehandler\":\"\",\"diskresjonTidspunkt\":\"\",\"diskresjonSystem\":\"\",\"kodeDiskresjonBeskr\":\"\",\"datoDiskresjon\":\"\",\"kodeDiskresjon\":\"\"},\"postadresseNorgeNAV\":{\"beskrTypeAdresseNavNorge\":\"\",\"tilleggsLinje\":\"\",\"kommunenr\":\"\",\"husbokstav\":\"\",\"adrTidspunktReg\":\"\",\"datoFom\":\"\",\"kommuneNavn\":\"\",\"postboksnr\":\"\",\"poststed\":\"\",\"bolignr\":\"\",\"adrSaksbehandler\":\"\",\"datoTom\":\"\",\"postnr\":\"\",\"adrSystem\":\"\",\"gatekode\":\"\",\"husnr\":\"\",\"typeAdresseNavNorge\":\"\",\"beskrTypeTilleggsLinje\":\"\",\"typeTilleggsLinje\":\"\",\"gatenavn\":\"\",\"postboksAnlegg\":\"\",\"eiendomsnavn\":\"\"},\"datoUmyndiggjort\":\"\",\"bankkontoUtland\":{\"beskrBankValuta\":\"\",\"bankAdresse2\":\"\",\"bankAdresse1\":\"\",\"giroNrUtland\":\"\",\"regSaksbehandler\":\"\",\"bankLand\":\"\",\"bankValuta\":\"\",\"bankLandKode\":\"\",\"swiftKodeUtland\":\"\",\"regSystem\":\"\",\"bankNavnUtland\":\"\",\"iban\":\"\",\"regTidspunkt\":\"\",\"bankAdresse3\":\"\",\"bankKodeUtland\":\"\"},\"postadresseUtlandNAV\":{\"adresseType\":\"\",\"beskrAdrType\":\"\",\"adrSaksbehandler\":\"\",\"datoTom\":\"\",\"landKode\":\"\",\"adrTidspunktReg\":\"\",\"adrSystem\":\"\",\"datoFom\":\"\",\"land\":\"\",\"adresse3\":\"\",\"adresse1\":\"\",\"adresse2\":\"\"},\"relasjoner\":\"\",\"NAVenhet\":\"0312\"},\"sivilstand\":\"GIFT\",\"personstatusDetalj\":{\"kodePersonstatus\":\"BOSA\",\"psTidspunkt\":\"2018-10-23\",\"psSystem\":\"SKD\",\"datoPersonstatus\":\"2018-10-23\",\"kodePersonstatusBeskr\":\"Bosatt\",\"psSaksbehandler\":\"\"},\"fodselsdato\":\"1988-04-23\",\"datoDo\":\"\",\"bankkontoNorge\":{\"regSystem\":\"\",\"regSaksbehandler\":\"\",\"kontoNummer\":\"\",\"regTidspunkt\":\"\",\"banknavn\":\"\"},\"postAdresse\":{\"postLand\":\"\",\"postLandKode\":\"\",\"fullPostAdresse\":{\"adresseType\":\"\",\"landKode\":\"\",\"adrTidspunktReg\":\"\",\"datoFom\":\"\",\"adresse3\":\"\",\"adresse1\":\"\",\"adresse2\":\"\",\"poststed\":\"\",\"beskrAdrType\":\"\",\"adrSaksbehandler\":\"\",\"datoTom\":\"\",\"postnr\":\"\",\"adrSystem\":\"\",\"land\":\"\"},\"postAdresse2\":\"\",\"postAdresse1\":\"\",\"postPostnr\":\"\",\"postPoststed\":\"\",\"postAdresse3\":\"\"},\"fodestedDetalj\":{\"fodestedTidspunkt\":\"\",\"fodestedSystem\":\"\",\"fodestedSaksbehandler\":\"\"},\"identType\":\"FNR\",\"bostedsAdresse\":{\"boAdresse2\":\"\",\"boPoststed\":\"OSLO\",\"boAdresse1\":\"WALLDORFERSTRABE 1289\",\"boPostnr\":\"0264\",\"fullBostedsAdresse\":{\"adresseType\":\"OFFA\",\"offAdresse\":{\"bokstav\":\"\",\"husnr\":1289,\"gateNavn\":\"WALLDORFERSTRABE\"},\"kommunenr\":\"0301\",\"landKode\":\"NOR\",\"adrTidspunktReg\":\"2018-10-23\",\"datoFom\":\"1988-04-23\",\"kommuneNavn\":\"Oslo\",\"adresse1\":\"WALLDORFERSTRABE 1289\",\"tilleggsAdresseSKD\":\"\",\"adresse2\":\"\",\"tknr\":\"0312\",\"poststed\":\"OSLO\",\"bolignr\":\"\",\"beskrAdrType\":\"Offisiell adresse\",\"adrSaksbehandler\":\"AJOURHD\",\"datoTom\":\"\",\"matrAdresse\":{\"undernr\":\"\",\"gardsnr\":\"\",\"festenr\":\"\",\"mellomAdresse\":\"\",\"bruksnr\":\"\"},\"postnr\":\"0264\",\"adrSystem\":\"SKD\",\"tkNavn\":\"FROGNER\",\"land\":\"NORGE\"}},\"fodselsnummer\":23048801390,\"statsborgerskapDetalj\":{\"kodeStatsborgerskapBeskr\":\"NORGE\",\"sbSystem\":\"SKD\",\"sbTidspunkt\":\"2018-10-23\",\"kodeStatsborgerskap\":\"NOR\",\"sbSaksbehandler\":\"AJOURHD\",\"datoStatsborgerskap\":\"1988-04-23\"},\"datoDoDetalj\":{\"doSystem\":\"\",\"doSaksbehandler\":\"\",\"doTidspunkt\":\"\"},\"fodested\":\"\",\"personnavn\":{\"gjeldendePersonnavn\":\"HEST SMEKKER\",\"allePersonnavn\":{\"kortnavn\":\"HEST SMEKKER\",\"navnTidspunkt\":\"2018-10-23\",\"mellomnavn\":\"\",\"etternavn\":\"HEST\",\"navnSaksbehandler\":\"AJOURHD\",\"fornavn\":\"SMEKKER\",\"navnSystem\":\"SKD\"}},\"sivilstandDetalj\":{\"sivilstSaksbehandler\":\"AJOURHD\",\"kodeSivilstandBeskr\":\"Gift\",\"kodeSivilstand\":\"GIFT\",\"datoSivilstand\":\"2018-10-23\",\"sivilstTidspunkt\":\"2018-10-23\",\"sivilstSystem\":\"SKD\"}},\"status\":{\"kode\":\"00\",\"melding\":\" \",\"utfyllendeMelding\":\" \"},\"antallTotalt\":null}}";

        JsonNode jsonNode = new ObjectMapper().readTree(jsonContentKjerne);

        List<String> feltNavn = new ArrayList<>();
        feltNavn.add("bostedsAdresse/fullBostedsAdresse/adrSaksbehandler");
        feltNavn.add("bostedsAdresse/fullBostedsAdresse/offAdresse/husnr");

        when(tpsfConsumer.getTpsServiceRoutine(any(), any())).thenReturn(jsonNode);

        Map<String, String> statusQuoValues = tpsStatusQuoService.getStatusQuo(feltNavn, aksjonsKode, environment, fnr);

        assertEquals("AJOURHD", statusQuoValues.get("bostedsAdresse/fullBostedsAdresse/adrSaksbehandler"));
        assertEquals("1289", statusQuoValues.get("bostedsAdresse/fullBostedsAdresse/offAdresse/husnr"));

    }

    @Test
    public void getInfoOnRoutineName() throws IOException {
        JsonNode jsonNode = new ObjectMapper().readTree(jsonContent);

        when(tpsfConsumer.getTpsServiceRoutine(any(), any())).thenReturn(jsonNode);

        assertNull(tpsStatusQuoService.getTpsServiceRoutineCache());

        tpsStatusQuoService.getInfoOnRoutineName(routineName, aksjonsKode, environment, fnr);

        assertNotNull(tpsStatusQuoService.getTpsServiceRoutineCache());
        assertTrue(tpsStatusQuoService.getTpsServiceRoutineCache().containsKey(routineName));
        assertEquals(jsonNode, tpsStatusQuoService.getTpsServiceRoutineCache().get(routineName));
    }

    @Test
    public void getInfoHelper() throws IOException {
        List<String> feltnavn = new ArrayList<>();
        feltnavn.add("sivilstand");

        Map<String, Object> tpsRequestParameters = new HashMap<>();
        tpsRequestParameters.put("aksjonsKode", aksjonsKode);
        tpsRequestParameters.put("environment", environment);
        tpsRequestParameters.put("fnr", fnr);

        tpsStatusQuoService.getInfoHelper(routineName, aksjonsKode, environment, fnr);

        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        Mockito.verify(tpsfConsumer).getTpsServiceRoutine(eq(routineName), captor.capture());
        Map<String, Object> actualRequestParams = captor.getValue();

        assertEquals(aksjonsKode, actualRequestParams.get("aksjonsKode"));
        assertEquals(environment, actualRequestParams.get("environment"));
        assertEquals(fnr, actualRequestParams.get("fnr"));
    }
}
