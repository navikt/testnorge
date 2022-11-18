//package no.nav.registre.testnorge.generersyntameldingservice.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.web.servlet.MockMvc;
//
//import no.nav.testnav.libs.testing.JsonWiremockHelper;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@ExtendWith(MockitoExtension.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureWireMock(port = 0)
//@AutoConfigureMockMvc
//@TestPropertySource(locations = "classpath:application-test.yml")
//@ActiveProfiles("test")
//public class SyntControllerIntegrationTest {
//
//    @Autowired
//    private MockMvc mvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private final String tokenPath = "(.*)/token/oauth2/v2.0/token";
//    private final String tokenResponse = "{\"access_token\": \"dummy\"}";
//
//
//
//
//    @BeforeEach
//    public void setup() {
////        hodejegerenUrlPattern = "(.*)/hodejegeren/api/v1/alle-identer/" + staticDataPlaygroup;
////        levendeIdenterUrlPattern = "(.*)/hodejegeren/api/v1/levende-identer/" + staticDataPlaygroup;
////        tpsfGetMeldingerUrlPattern = "(.*)/tpsf/api/v1/endringsmelding/skd/meldinger/" + staticDataPlaygroup;
////        tpsfSendSkdmeldingerUrlPattern = "(.*)/tpsf/api/v1/endringsmelding/skd/send/" + staticDataPlaygroup;
////        tpOpprettPersonerUrlPattern = "(.*)/testnorge-tp/api/v1/orkestrering/opprettPersoner/" + ENVIRONMENT;
////
////        JsonWiremockHelper.builder(objectMapper).withUrlPathMatching("(.*)/v1/orkestrering/opprettPersoner/(.*)").stubPost();
//    }
//
//
//    @Test
//    void shouldInitiateIdent() throws Exception {
//
//        JsonWiremockHelper
//                .builder(objectMapper)
//                .withUrlPathMatching(tokenPath)
//                .withResponseBody(tokenResponse)
//                .stubPost();
//
//
//        mvc.perform(post("/api/v1/generer/amelding")
//                        .content(objectMapper.writeValueAsString(request))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//
//        JsonWiremockHelper
//                .builder(objectMapper)
//                .withUrlPathMatching(tokenPath)
//                .withResponseBody(tokenResponse)
//                .verifyPost();
//
//    }
//}
