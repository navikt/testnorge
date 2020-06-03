package no.nav.registre.sdForvalter.provider.rs.v1;

import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import no.nav.registre.sdForvalter.consumer.rs.request.ereg.Adresse;
import no.nav.registre.sdForvalter.consumer.rs.request.ereg.EregMapperRequest;
import no.nav.registre.sdForvalter.consumer.rs.request.ereg.Navn;
import no.nav.registre.sdForvalter.database.model.AdresseModel;
import no.nav.registre.sdForvalter.database.model.EregModel;
import no.nav.registre.sdForvalter.database.model.GruppeModel;
import no.nav.registre.sdForvalter.database.model.OpprinnelseModel;
import no.nav.registre.sdForvalter.database.repository.EregRepository;
import no.nav.registre.sdForvalter.database.repository.GruppeRepository;
import no.nav.registre.sdForvalter.database.repository.OpprinnelseRepository;
import no.nav.registre.sdForvalter.domain.Ereg;
import no.nav.registre.sdForvalter.util.JsonTestHelper;

@RunWith(SpringRunner.class)
@AutoConfigureWireMock(port = 0)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.properties"
)
public class StaticDataControllerV1OrganisasjonIntegrationTest {
    private static final String EREG_API = "/api/v1/organisasjons/flatfil/";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EregRepository eregRepository;

    @Autowired
    private GruppeRepository gruppeRepository;

    @Autowired
    private OpprinnelseRepository opprinnelseRepository;

    private UrlPathPattern eregMapperUrlPattern;
    private GruppeModel wip;
    private OpprinnelseModel brreg;

    @Before
    public void setup() {
        eregMapperUrlPattern = urlPathMatching("(.*)/v1/organisasjoner");
        wip = gruppeRepository.save(new GruppeModel(null, "WIP", "Testest"));
        brreg = opprinnelseRepository.save(new OpprinnelseModel(null, "Brreg"));
    }

    @Test
    public void should_save_response_from_ereg_mapper_in_database() throws Exception {
        List<EregMapperRequest> eregMapperRequestList = Collections.singletonList(
                createEregMapperRequest("999999999", "BEDR", "N", "DOLLY TEST BEDR", null)
        );
        List<EregModel> expected = Collections.singletonList(
                createEregModel("999999999", "BEDR", "DOLLY TEST BEDR", null, wip, brreg)
        );

        assertResponseIsSavedInDatabase(eregMapperRequestList, expected);
    }

    @Test
    public void should_save_response_with_addresses_from_ereg_mapper_in_database() throws Exception {
        Adresse adresse = Adresse.builder()
                .adresser(Arrays.asList("Adresseveien 1", "Andre etasje"))
                .postnr("1111")
                .poststed("Stedet")
                .kommunenr("2222")
                .landkode("NOR")
                .build();
        Adresse forretningsadresse = Adresse.builder()
                .adresser(Arrays.asList("Forretningsadresse1", "Tredje etasje"))
                .postnr("1111")
                .poststed("Stedet")
                .kommunenr("2222")
                .landkode("NOR")
                .build();

        List<EregMapperRequest> eregMapperRequestList = Collections.singletonList( createEregMapperRequest(
                "999999992",
                "BEDR",
                "N",
                "DOLLY TEST BEDR",
                "dolly@dollys.com",
                "www.dolly.no",
                adresse,
                forretningsadresse));

        List<EregModel> expected = Collections.singletonList(createEregModel("999999992", "BEDR", "DOLLY TEST BEDR", null,
                wip, brreg, "dolly@dollys.com", "www.dolly.no", adresse, forretningsadresse)
        );

        assertResponseIsSavedInDatabase(eregMapperRequestList, expected);
    }

    @Test
    public void should_save_response_with_knytning_from_ereg_mapper_in_database() throws Exception {
        EregMapperRequest juridiskEnhet = createEregMapperRequest("888888888", "AS", "N", "DOLLY JURIDISK ENHET");
        List<Map<String, String>> knytninger = createKnytning("888888888", "ORGLNSSY");
        EregMapperRequest orgMedKnytning = createEregMapperRequest("999999999", "BEDR", "N", "DOLLY TEST BEDR", knytninger);

        List<EregMapperRequest> eregMapperRequestList =Arrays.asList(
                juridiskEnhet,
                orgMedKnytning
        );

        List<EregModel> expected = Collections.singletonList(
                createEregModel("999999999", "BEDR", "DOLLY TEST BEDR", createEregModel("888888888", "AS", "DOLLY JURIDISK ENHET", null , wip, brreg), wip, brreg)
        );

        assertResponseIsSavedInDatabase(eregMapperRequestList, expected);
    }

    @Test
    public void should_save_response_with_multiple_org_from_ereg_mapper_in_database() throws Exception {
        List<EregMapperRequest> eregMapperRequestList =Arrays.asList(
                createEregMapperRequest("999999999", "BEDR", "N", "DOLLY TEST BEDR", null),
                createEregMapperRequest("222222222", "BEDR", "N", "DOLLY TEST 2", null)
        );



        List<EregModel> expected = Arrays.asList(
                createEregModel("999999999", "BEDR", "DOLLY TEST BEDR", null, wip, brreg),
                createEregModel("222222222", "BEDR", "DOLLY TEST 2", null, wip, brreg)
        );

        assertResponseIsSavedInDatabase(eregMapperRequestList, expected);
    }

    private void assertResponseIsSavedInDatabase(List<EregMapperRequest> eregMapperRequests, List<EregModel> expected) throws Exception {
        // innholdet i flatfilen påvirker ikke testen.
        String flatfil = "HEADER 2020010100000AA A\nENH 999999999BEDRNNY  2020010120200101J\nNAVNN   DOLLY TEST BEDR\nTRAIER 0000001000000004\n";

        JsonTestHelper.stubPost(eregMapperUrlPattern, eregMapperRequests, objectMapper);
        mvc.perform(post(EREG_API)
                .content(flatfil)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Iterable<EregModel> eregRepositoryAll = eregRepository.findAll();
        assertThat(eregRepository.findAll())
                .hasSize(eregMapperRequests.size());

        assertThat(eregRepositoryAll)
                .usingElementComparatorIgnoringFields("id", "updatedAt", "createdAt")
                .containsAll(expected);
    }


    private EregMapperRequest createEregMapperRequest(String orgnr, String enhetstype, String endringsType, String navn, String epost, String internettAdresse, Adresse adresse, Adresse forretningsAdresse, List<Map<String, String>> knytninger) {
        return EregMapperRequest
                .builder()
                .orgnr(orgnr)
                .enhetstype(enhetstype)
                .endringsType(endringsType)
                .navn(Navn.builder().navneListe(Collections.singletonList(navn)).build())
                .knytninger(knytninger)
                .internetAdresse(internettAdresse)
                .epost(epost)
                .adresse(adresse)
                .forretningsAdresse(forretningsAdresse)
                .build();
    }

    private EregMapperRequest createEregMapperRequest(String orgnr, String enhetstype, String endringsType, String navn, String epost, String internettAdresse, Adresse adresse, Adresse forretningsAdresse) {
        return createEregMapperRequest(orgnr, enhetstype, endringsType, navn, epost, internettAdresse, adresse, forretningsAdresse, null);
    }

    private EregMapperRequest createEregMapperRequest(String orgnr, String enhetstype, String endringsType, String navn, List<Map<String, String>> knytninger) {
        return createEregMapperRequest(orgnr, enhetstype, endringsType, navn, null, null, null, null, knytninger);
    }

    private EregMapperRequest createEregMapperRequest(String orgnr, String enhetstype, String endringsType, String navn) {
        return createEregMapperRequest(orgnr, enhetstype, endringsType, navn, null);
    }

    private List<Map<String, String>> createKnytning(String orgnr, String type) {
        HashMap<String, String> map = new HashMap<>() {{
            put("orgnr", orgnr);
            put("type", type);
        }};
        return Collections.singletonList(map);
    }

    private EregModel createEregModel(String orgnr, String enhetstype, String navn, EregModel parent, GruppeModel gruppeModel, OpprinnelseModel opprinnelseModel, String epost, String internettAdresse, Adresse adresse, Adresse forretningsadresse) {
        EregModel eregModel = new EregModel();
        eregModel.setOrgnr(orgnr);
        eregModel.setEnhetstype(enhetstype);
        eregModel.setGruppeModel(gruppeModel);
        eregModel.setOpprinnelseModel(opprinnelseModel);
        eregModel.setNavn(navn);
        eregModel.setEpost(epost);
        eregModel.setInternetAdresse(internettAdresse);
        if (forretningsadresse != null) {
            eregModel.setForretningsAdresse(new AdresseModel(forretningsadresse));
        }
        if (adresse != null) {
            eregModel.setPostadresse(new AdresseModel(adresse));
        }
        eregModel.setParent(parent);

        return eregModel;
    }

    private EregModel createEregModel(String orgnr, String enhetstype, String navn, EregModel parent, GruppeModel gruppeModel, OpprinnelseModel opprinnelseModel) {
        return createEregModel(orgnr, enhetstype, navn, parent, gruppeModel, opprinnelseModel, null, null, null, null);
    }

    @After
    public void cleanUp() {
        reset();
        eregRepository.deleteAll();
        gruppeRepository.deleteAll();
        opprinnelseRepository.deleteAll();
    }
}