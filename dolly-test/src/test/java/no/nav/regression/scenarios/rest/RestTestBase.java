package no.nav.regression.scenarios.rest;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import no.nav.config.DollyObjectMapper;
import no.nav.dolly.testdata.builder.BrukerBuilder;
import no.nav.dolly.testdata.builder.TeamBuilder;
import no.nav.dolly.testdata.builder.TestgruppeBuilder;
import no.nav.jpa.Bruker;
import no.nav.jpa.Team;
import no.nav.jpa.Testgruppe;
import no.nav.regression.InMememoryDbTestSetup;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

public abstract class RestTestBase extends InMememoryDbTestSetup {

    @Autowired(required = false)
    private WebApplicationContext webApplicationContext;

    private static final DollyObjectMapper MAPPER = new DollyObjectMapper();

    protected MockMvc mvcMock;

    protected final static String DATE_FORMAT = "yyyy-MM-dd";

    protected String teamPropNavn = "navn";
    protected String teamPropEierIdent = "eierNavIdent";

    protected Testgruppe standardTestgruppe;
    protected Bruker standardBruker;
    protected Team standardTeam;

    protected String standardTeamnavn = "team";
    protected String standardTeamBesk = "beskrivelse";
    protected String standardGruppenavn = "testgruppe";
    protected String standardNavnIdent = "ident";
    protected String standardGruppeHensikt = "hensikt";

    @Before
    public void setupBruker() {
        mvcMock = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        testGruppeRepository.deleteAll();
        teamRepository.deleteAll();
        brukerRepository.deleteAll();
        identRepository.deleteAll();

        standardBruker = brukerRepository.save(BrukerBuilder.builder()
                .navIdent(standardNavnIdent)
                .build().convertToRealBruker()
        );

        standardTeam = teamRepository.save(TeamBuilder.builder()
                .eier(standardBruker)
                .navn(standardTeamnavn)
                .beskrivelse(standardTeamBesk)
                .datoOpprettet(LocalDate.now())
                .medlemmer(new HashSet<>(Arrays.asList(standardBruker)))
                .build().convertToRealTeam()
        );

        standardTestgruppe = testGruppeRepository.save(TestgruppeBuilder.builder()
                .navn(standardGruppenavn)
                .hensikt(standardGruppeHensikt)
                .opprettetAv(standardBruker)
                .sistEndretAv(standardBruker)
                .datoEndret(LocalDate.now())
                .teamtilhoerighet(standardTeam)
                .build().convertToRealTestgruppe()
        );
    }

    protected static String convertObjectToJson(Object object) throws IOException {
        return MAPPER.writeValueAsString(object);
    }

    protected static <T> T convertMvcResultToObject(MvcResult mvcResult, Class<T> resultClass) throws IOException {
        return MAPPER.readValue(mvcResult.getResponse().getContentAsString(), resultClass);
    }

    protected static <T> List<T> convertMvcResultToList(MvcResult mvcResult, Class<T> resultClass) throws IOException {
        JavaType type = MAPPER.getTypeFactory().constructCollectionType(List.class, resultClass);
        return MAPPER.readValue(mvcResult.getResponse().getContentAsString(), type);
    }

    protected static <T> Set<T> convertMvcResultToSet(MvcResult mvcResult, Class<T> resultClass) throws IOException {
        JavaType type = MAPPER.getTypeFactory().constructCollectionType(Set.class, resultClass);
        return MAPPER.readValue(mvcResult.getResponse().getContentAsString(), type);
    }

    protected static <T> Collection<T> convertMvcResultToCollection(MvcResult mvcResult, Class<T> resultClass) throws IOException {
        JavaType type = MAPPER.getTypeFactory().constructCollectionType(Collection.class, resultClass);
        return MAPPER.readValue(mvcResult.getResponse().getContentAsString(), type);
    }

    protected static String getErrorMessage(MvcResult mvcResult) throws IOException {
        JavaType type = MAPPER.getTypeFactory().constructMapType(Map.class, String.class, String.class);
        Map<String, String> json = MAPPER.readValue(mvcResult.getResponse().getContentAsString(), type);
        return json.get("message");
    }

    protected static Map<String, String> getErrorMessageAtIndex(MvcResult mvcResult, int index) throws IOException {
        JavaType mapType = MAPPER.getTypeFactory().constructMapType(Map.class, String.class, String.class);
        JavaType listOfMapType = MAPPER.getTypeFactory().constructCollectionLikeType(List.class, mapType);

        List<Map<String, String>> listOfMap = MAPPER.readValue(mvcResult.getResponse().getContentAsString(), listOfMapType);
        return listOfMap.get(index);
    }
}
