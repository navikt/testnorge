package no.nav.dolly.regression.scenarios.rest;

import static java.util.Collections.singletonList;

import java.io.IOException;
import java.time.LocalDate;
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
import com.fasterxml.jackson.databind.JavaType;

import no.nav.dolly.config.DollyObjectMapper;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.regression.InMemoryDbTestSetup;
import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;

public abstract class RestTestBase extends InMemoryDbTestSetup {

    @Autowired(required = false)
    private WebApplicationContext webApplicationContext;

    protected static final String TEAM_PROP_NAVN = "navn";
    protected static final String TEAM_PROP_EIER_IDENT = "eierNavIdent";

    protected static final String STANDARD_TEAM_NAVN = "team";
    protected static final String STANDARD_TEAM_BESK = "beskrivelse";
    protected static final String STANDARD_GRUPPE_NAVN = "testgruppe";
    protected static final String STANDARD_NAV_IDENT = "IDENT";
    protected static final String STANDARD_GRUPPE_HENSIKT = "hensikt";

    protected static final String STANDARD_PRINCIPAL = STANDARD_NAV_IDENT;

    private static final DollyObjectMapper MAPPER = new DollyObjectMapper();

    protected MockMvc mvcMock;
    protected Testgruppe standardTestgruppe;
    protected Bruker standardBruker;
    protected Team standardTeam;
    protected RsDollyBestillingRequest standardBestilling_u6 = new RsDollyBestillingRequest();
    protected List<String> standardEnvironments = singletonList("u6");


    @After
    public void after() {
        removeManyToManyRelationships();

        identRepository.deleteAll();
        gruppeRepository.deleteAll();
        teamRepository.deleteAll();
        brukerRepository.deleteAll();

        bestillingProgressRepository.deleteAll();
        bestillingRepository.deleteAll();
    }

    private void removeManyToManyRelationships(){
        List<Bruker> brukere = brukerRepository.findAllByOrderByNavIdent();
        brukere.forEach(b -> {
            b.setFavoritter(new HashSet<>());
            b.setTeams(new HashSet<>());
        });
        brukerRepository.saveAll(brukere);

        List<Testgruppe> grupper = gruppeRepository.findAllByOrderByNavn();
        grupper.forEach(g -> g.setFavorisertAv(new HashSet<>()));
        grupper.forEach(g -> g.setTestidenter(new HashSet<>()));
        gruppeRepository.saveAll(grupper);
    }

    @Before
    public void setupBruker() {
        mvcMock = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        standardBruker = brukerRepository.save(Bruker.builder()
                .navIdent(STANDARD_NAV_IDENT)
                .build()
        );

        standardTeam = teamRepository.save(Team.builder()
                .eier(standardBruker)
                .navn(STANDARD_TEAM_NAVN)
                .beskrivelse(STANDARD_TEAM_BESK)
                .datoOpprettet(LocalDate.now())
                .medlemmer(singletonList(standardBruker))
                .build()
        );

        standardTestgruppe = gruppeRepository.save(Testgruppe.builder()
                .navn(STANDARD_GRUPPE_NAVN)
                .hensikt(STANDARD_GRUPPE_HENSIKT)
                .opprettetAv(standardBruker)
                .sistEndretAv(standardBruker)
                .datoEndret(LocalDate.now())
                .teamtilhoerighet(standardTeam)
                .build()
        );

        standardBestilling_u6.setAntall(1);
        standardBestilling_u6.setEnvironments(standardEnvironments);

        /* Legger til securityContextHolder */
        SecurityContextHolder.getContext().setAuthentication(createTestOidcToken());
    }

    private OidcTokenAuthentication createTestOidcToken(){
        return new OidcTokenAuthentication(STANDARD_PRINCIPAL,null, null, null);
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
