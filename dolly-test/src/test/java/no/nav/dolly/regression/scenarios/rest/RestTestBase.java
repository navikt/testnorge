package no.nav.dolly.regression.scenarios.rest;

import static java.util.Collections.singletonList;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.regression.InMemoryDbTestSetup;
import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class RestTestBase extends InMemoryDbTestSetup {

    protected static final String TEAM_PROP_NAVN = "navn";
    protected static final String TEAM_PROP_EIER_IDENT = "eierNavIdent";
    protected static final String STANDARD_TEAM_NAVN = "team";
    protected static final String STANDARD_TEAM_BESK = "beskrivelse";
    protected static final String STANDARD_GRUPPE_NAVN = "testgruppe";
    protected static final String STANDARD_NAV_IDENT = "IDENT";
    protected static final String STANDARD_GRUPPE_HENSIKT = "hensikt";
    protected static final String STANDARD_PRINCIPAL = STANDARD_NAV_IDENT;
    private static final ObjectMapper MAPPER = new ObjectMapper();
    protected MockMvc mvcMock;
    protected Testgruppe standardTestgruppe;
    protected Bruker standardBruker;
    protected Team standardTeam;
    protected RsDollyBestillingRequest standardBestilling_u6 = new RsDollyBestillingRequest();
    protected List<String> standardEnvironments = singletonList("u6");
    @Autowired(required = false)
    private WebApplicationContext webApplicationContext;

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

    @After
    public void after() {
        removeManyToManyRelationships();

        identTestRepository.deleteAll();
        gruppeTestRepository.deleteAll();
        teamTestRepository.deleteAll();
        brukerTestRepository.deleteAll();

        bestillingProgressTestRepository.deleteAll();
        bestillingTestRepository.deleteAll();
    }

    private void removeManyToManyRelationships() {
        List<Bruker> brukere = brukerTestRepository.findAllByOrderByNavIdent();
        brukere.forEach(b -> {
            b.setFavoritter(new HashSet<>());
            b.setTeams(new HashSet<>());
        });
        brukerTestRepository.saveAll(brukere);

        List<Testgruppe> grupper = gruppeTestRepository.findAllByOrderByNavn();
        grupper.forEach(g -> g.setFavorisertAv(new HashSet<>()));
        grupper.forEach(g -> g.setTestidenter(new HashSet<>()));
        gruppeTestRepository.saveAll(grupper);
    }

    @Before
    public void setupBruker() {
        mvcMock = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        standardBruker = brukerTestRepository.save(Bruker.builder()
                .navIdent(STANDARD_NAV_IDENT)
                .build()
        );

        standardTeam = teamTestRepository.save(Team.builder()
                .eier(standardBruker)
                .navn(STANDARD_TEAM_NAVN)
                .beskrivelse(STANDARD_TEAM_BESK)
                .datoOpprettet(LocalDate.now())
                .medlemmer(singletonList(standardBruker))
                .build()
        );

        standardTestgruppe = gruppeTestRepository.save(Testgruppe.builder()
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

    private OidcTokenAuthentication createTestOidcToken() {
        return new OidcTokenAuthentication(STANDARD_PRINCIPAL, null, null, null);
    }
}
