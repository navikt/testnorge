package no.nav.udistub.provider.ws.cxf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

// See https://github.com/navikt/gandalf
public class NavStsRestClient {
    // ikke i bruk i FSS sonen
    private static final String API_KEY_HEADER = "x-nav-apiKey";
    private static final Logger LOG = LoggerFactory.getLogger(NavStsRestClient.class);
    private final RestTemplate restTemplate;
    private final Config config;

    public NavStsRestClient(RestTemplate restTemplate, Config config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }

    public Response getSystemSaml() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION,createAuthHeader(config));
            HttpEntity httpEntity = new HttpEntity(headers);
            return restTemplate.exchange(config.systemSamlPath, HttpMethod.GET,httpEntity,Response.class).getBody();

        } catch (HttpStatusCodeException ex) {
            LOG.error("Error retrieving toke from STS ",ex);
            throw ex;
        }
    }

    // See https://github.com/navikt/gandalf#issue-saml-token-based-on-oidc-token
    public Response exchangeForSaml(String b64EncodedToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION,createAuthHeader(config));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        var body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", "urn:ietf:params:oauth:grant-type:token-exchange");
        body.add("requested_token_type", "urn:ietf:params:oauth:token-type:saml2");
        body.add("subject_token_type", "urn:ietf:params:oauth:token-type:access_token");
        body.add("subject_token", b64EncodedToken);

        HttpEntity entity = new HttpEntity(body,headers);
        try {
            return restTemplate.exchange(config.exchangePath,HttpMethod.POST,entity,Response.class).getBody();
        } catch (HttpStatusCodeException ex) {
            LOG.error("ResponseBody: " + ex.getResponseBodyAsString());
            throw ex;
        }
    }

    public static class Response {
        public String access_token;
        public String issued_token_type;
        public String token_type;
        public int expires_in;

        public String decodedToken() {
            try {
                return new String(Base64.getUrlDecoder().decode(access_token));
            }
            catch (IllegalArgumentException ex) {
                return new String(Base64.getDecoder().decode(access_token));
            }
        }
    }

    public static class Config {
        public String systemUser;
        public String systemPassword;
        // det er mulig vi trenger det hvis vi flytter til GCP , da ligger STS-en back api gateway.
        // Jeg tror ikke vi trenger det for FSS sonen.
        public String apiKey;
        public String systemSamlPath;
        public String exchangePath;
    }

    private static String encodeAsBase64(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes());
    }
    private static String createAuthHeader(Config config) {
        return "Basic " + encodeAsBase64(config.systemUser + ":" + config.systemPassword);
    }
}
