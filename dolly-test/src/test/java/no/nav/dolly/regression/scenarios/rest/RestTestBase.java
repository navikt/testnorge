package no.nav.dolly.regression.scenarios.rest;

import com.fasterxml.jackson.databind.JavaType;
import no.nav.dolly.config.DollyObjectMapper;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsDollyBestillingsRequest;
import no.nav.dolly.regression.InMememoryDbTestSetup;
import no.nav.dolly.testdata.builder.BrukerBuilder;
import no.nav.dolly.testdata.builder.TeamBuilder;
import no.nav.dolly.testdata.builder.TestgruppeBuilder;
import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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
    protected RsDollyBestillingsRequest standardBestilling_u6 = new RsDollyBestillingsRequest();
    protected List<String> standardEnvironments =  new ArrayList<>(Arrays.asList("u6"));

    protected String standardTeamnavn = "team";
    protected String standardTeamBesk = "beskrivelse";
    protected String standardGruppenavn = "testgruppe";
    protected String standardNavIdent = "ident";
    protected String standardGruppeHensikt = "hensikt";

    protected String standardPrincipal = standardNavIdent;

    @After
    public void after() {
        removeManyToManyRelationships();

        gruppeRepository.deleteAll();
        teamRepository.deleteAll();
        brukerRepository.deleteAll();
        identRepository.deleteAll();

        bestillingProgressRepository.deleteAll();
        bestillingRepository.deleteAll();
    }

    private void removeManyToManyRelationships(){
        List<Bruker> brukere = brukerRepository.findAll();
        brukere.forEach(b -> {
            b.setFavoritter(new HashSet<>());
            b.setTeams(new HashSet<>());
        });
        brukerRepository.saveAll(brukere);

        List<Testgruppe> grupper = gruppeRepository.findAll();
        grupper.forEach(g -> g.setFavorisertAv(new HashSet<>()));
        gruppeRepository.saveAll(grupper);
    }

    @Before
    public void setupBruker() {
        mvcMock = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        standardBruker = brukerRepository.save(BrukerBuilder.builder()
                .navIdent(standardNavIdent)
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

        standardTestgruppe = gruppeRepository.save(TestgruppeBuilder.builder()
                .navn(standardGruppenavn)
                .hensikt(standardGruppeHensikt)
                .opprettetAv(standardBruker)
                .sistEndretAv(standardBruker)
                .datoEndret(LocalDate.now())
                .teamtilhoerighet(standardTeam)
                .build().convertToRealTestgruppe()
        );

        standardBestilling_u6.setAntall(1);
        standardBestilling_u6.setEnvironments(standardEnvironments);

        /* Legger til securityContextHolder */
        SecurityContextHolder.getContext().setAuthentication(createTestOidcToken());
    }

    private OidcTokenAuthentication createTestOidcToken(){
        OidcTokenAuthentication token = new OidcTokenAuthentication(standardPrincipal,null, null, null);
        return token;
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
