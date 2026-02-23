package no.nav.registre.sdforvalter.provider.rs.v1;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.registre.sdforvalter.database.model.EregModel;
import no.nav.registre.sdforvalter.database.model.GruppeModel;
import no.nav.registre.sdforvalter.database.model.OpprinnelseModel;
import no.nav.registre.sdforvalter.database.repository.EregRepository;
import no.nav.registre.sdforvalter.database.repository.GruppeRepository;
import no.nav.registre.sdforvalter.database.repository.OpprinnelseRepository;
import no.nav.registre.sdforvalter.domain.Ereg;
import no.nav.registre.sdforvalter.domain.EregListe;
import no.nav.registre.sdforvalter.domain.Gruppe;
import no.nav.registre.sdforvalter.domain.Opprinnelse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DollySpringBootTest
@AutoConfigureMockMvc()
@AutoConfigureWireMock(port = 0)
//@Import(JwtDecoderConfig.class)
class StaticDataControllerV1EregIntegrationTest {

    private static final String EREG_API = "/api/v1/faste-data/ereg";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EregRepository eregRepository;

    @Autowired
    private OpprinnelseRepository opprinnelseRepository;

    @Autowired
    private GruppeRepository gruppeRepository;

    private EregListe create(Ereg... eregs) {
        return new EregListe(Arrays.asList(eregs));
    }

    private EregModel createEregModel(String orgnr, String enhetstype, GruppeModel gruppeModel) {
        return createEregModel(orgnr, enhetstype, null, gruppeModel);
    }

    private EregModel createEregModel(String orgnr, String enhetstype, OpprinnelseModel opprinnelseModel) {
        return createEregModel(orgnr, enhetstype, opprinnelseModel, null);
    }

    private EregModel createEregModel(String orgnr, String enhetstype, OpprinnelseModel opprinnelseModel, GruppeModel gruppeModel) {
        EregModel model = new EregModel();
        model.setOrgnr(orgnr);
        model.setEnhetstype(enhetstype);
        model.setOpprinnelseModel(opprinnelseModel);
        model.setGruppeModel(gruppeModel);
        return model;
    }

    private EregModel createEregModel(String orgnr, String enhetstype) {
        return createEregModel(orgnr, enhetstype, null, null);
    }

    private Ereg createEreg(String orgnr, String enhetstype, String opprinnelse) {
        return new Ereg(createEregModel(orgnr, enhetstype, new OpprinnelseModel(opprinnelse)), new ArrayList<>());
    }

    private Ereg createEreg(String orgnr, String enhetstype, Gruppe gruppe) {
        return new Ereg(createEregModel(orgnr, enhetstype, null, new GruppeModel(null, gruppe.getKode(), gruppe.getBeskrivelse())), new ArrayList<>());
    }

    private Ereg createEreg(String orgnr, String enhetstype) {
        return new Ereg(createEregModel(orgnr, enhetstype), new ArrayList<>());
    }

    @AfterEach
    void cleanUp() {
        eregRepository.deleteAll();
        gruppeRepository.deleteAll();
        opprinnelseRepository.deleteAll();
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void should_not_have_arbeidsforhold() throws Exception {
        EregModel as = createEregModel("999999999", "AS");
        EregModel ans = createEregModel("888888888", "ANS");

        eregRepository.saveAll(Arrays.asList(as, ans));

        String json = mvc.perform(get(EREG_API)
                        .contentType(MediaType.APPLICATION_JSON).with(jwt()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EregListe response = objectMapper.readValue(json, EregListe.class);
        assertThat(response.getListe()).hasSize(2);
        assertThat(response.getListe().get(0).isKanHaArbeidsforhold()).isFalse();
        assertThat(response.getListe().get(1).isKanHaArbeidsforhold()).isFalse();
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void should_have_arbeidsforhold() throws Exception {
        EregModel aafy = createEregModel("999999999", "AAFY");
        EregModel bedr = createEregModel("888888888", "BEDR");

        eregRepository.saveAll(Arrays.asList(aafy, bedr));

        String json = mvc.perform(get(EREG_API)
                        .contentType(MediaType.APPLICATION_JSON).with(jwt()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EregListe response = objectMapper.readValue(json, EregListe.class);
        assertThat(response.getListe()).hasSize(2);
        assertThat(response.getListe().get(0).isKanHaArbeidsforhold()).isTrue();
        assertThat(response.getListe().get(1).isKanHaArbeidsforhold()).isTrue();
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void should_only_get_EREGS() throws Exception {
        EregModel as = createEregModel("999999999", "AS");
        EregModel bedr = createEregModel("888888888", "BEDR");
        EregModel ans = createEregModel("888888881", "ANS");
        EregModel enk = createEregModel("888888882", "ENK");

        eregRepository.saveAll(Arrays.asList(as, bedr, ans, enk));

        String json = mvc.perform(get(EREG_API)
                        .contentType(MediaType.APPLICATION_JSON).with(jwt()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EregListe response = objectMapper.readValue(json, EregListe.class);
        assertThat(response.getListe()).containsOnly(
                new Ereg(as, new ArrayList<>()),
                new Ereg(bedr, new ArrayList<>()),
                new Ereg(enk, new ArrayList<>()),
                new Ereg(ans, new ArrayList<>())
        );
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void shouldGetEregsWithOpprinnelse() throws Exception {
        OpprinnelseModel altinn = opprinnelseRepository.save(new OpprinnelseModel("Altinn"));
        EregModel model = createEregModel("123456789", "BEDR", altinn);

        eregRepository.save(model);

        String json = mvc.perform(get(EREG_API)
                        .contentType(MediaType.APPLICATION_JSON).with(jwt()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EregListe response = objectMapper.readValue(json, EregListe.class);
        assertThat(response.getListe()).containsOnly(new Ereg(model, new ArrayList<>()));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void shouldAddEregSetToDatabase() throws Exception {
        Ereg ereg = createEreg("987654321", "BEDR");
        mvc.perform(post(EREG_API)
                        .content(objectMapper.writeValueAsString(create(ereg)))
                        .contentType(MediaType.APPLICATION_JSON).with(jwt()))
                .andExpect(status().isOk());

        assertThat(eregRepository.findAll())
                .hasSize(1)
                .first()
                .usingRecursiveComparison()
                .ignoringFields("id", "createdAt", "updatedAt")
                .isEqualTo(new EregModel(ereg, null, null, null));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void shouldGetEregWithGruppe() throws Exception {
        GruppeModel gruppeModel = gruppeRepository.save(new GruppeModel(
                null,
                "TestKode",
                "TestBeskrivelse"
        ));
        EregModel eregModel = createEregModel("987654321", "BEDR", gruppeModel);
        eregRepository.save(eregModel);

        String json = mvc.perform(get(EREG_API + "?gruppe=" + gruppeModel.getKode())
                        .contentType(MediaType.APPLICATION_JSON).with(jwt()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EregListe response = objectMapper.readValue(json, EregListe.class);
        assertThat(response.getListe()).containsOnly(new Ereg(eregModel, new ArrayList<>()));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void shouldOnlyGetEregWithGruppe() throws Exception {
        GruppeModel gruppeModel = gruppeRepository.save(new GruppeModel(
                null,
                "TestKode",
                "TestBeskrivelse"
        ));
        EregModel eregModel = createEregModel("123456789", "BEDR");
        EregModel eregWithGruppeModel = createEregModel("987654321", "BEDR", gruppeModel);

        eregRepository.saveAll(Arrays.asList(eregModel, eregWithGruppeModel));

        String json = mvc.perform(
                        get(EREG_API)
                                .param("gruppe", gruppeModel.getKode())
                                .contentType(MediaType.APPLICATION_JSON).with(jwt())
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EregListe response = objectMapper.readValue(json, EregListe.class);
        assertThat(response.getListe()).containsOnly(new Ereg(eregWithGruppeModel, new ArrayList<>()));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void shouldAddEregWithGruppe() throws Exception {
        GruppeModel gruppeModel = gruppeRepository.save(new GruppeModel(
                null,
                "TestKode",
                "TestBeskrivelse"
        ));
        Ereg ereg = createEreg("987654321", "BEDR", new Gruppe(gruppeModel));

        mvc.perform(post(EREG_API)
                        .content(objectMapper.writeValueAsString(create(ereg)))
                        .contentType(MediaType.APPLICATION_JSON).with(jwt()))
                .andExpect(status().isOk());

        Iterable<EregModel> iterable = eregRepository.findAll();
        assertThat(iterable).hasSize(1);
        assertThat(iterable.iterator().next().getGruppeModel())
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(gruppeModel);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void shouldAddOpprinnelseToDatabase() throws Exception {
        Opprinnelse altinn = new Opprinnelse("Altinn");
        var ereg123456789 = createEreg("123456789", "BEDR", altinn.getNavn());
        var ereg987654321 = createEreg("987654321", "BEDR", altinn.getNavn());
        mvc.perform(post(EREG_API)
                        .content(objectMapper.writeValueAsString(create(ereg123456789, ereg987654321)))
                        .contentType(MediaType.APPLICATION_JSON).with(jwt()))
                .andExpect(status().isOk());
        assertThat(Lists.newArrayList(opprinnelseRepository.findAll()))
                .hasSize(1)
                .first()
                .usingRecursiveComparison()
                .comparingOnlyFields("navn")
                .isEqualTo(new OpprinnelseModel(altinn));
    }

}
