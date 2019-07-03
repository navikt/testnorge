package no.nav.registre.ereg.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import no.nav.registre.ereg.consumer.rs.request.JenkinsCrumbRequest;

@Slf4j
@Component
public class JenkinsConsumer {

    private final RestTemplate restTemplate;
    private final UriTemplate jenkinsJobTemplate;
    private final UriTemplate jenkinsCrumbTemplate;
    private final Environment environment;
    private final String serverConfigString;
    private final String jenkinsUsername;
    private final String jenkinsPassword;

    public JenkinsConsumer(
            RestTemplate restTemplate,
            Environment env,
            @Value("${jenkins.rest.api.url}") String jenkinsUri,
            @Value("${jenkins.password}") String jenkinsPassword,
            @Value("${jenkins.username}") String jenkinsUsername) {
        this.restTemplate = restTemplate;
        environment = env;
        jenkinsJobTemplate = new UriTemplate(jenkinsUri + "/view/Registre/job/Start_BEREG007/buildWithParameters");
        jenkinsCrumbTemplate = new UriTemplate(jenkinsUri + "/crumbIssuer/api/json");

        serverConfigString = "jenkins.server.%s";

        this.jenkinsUsername = jenkinsUsername;
        this.jenkinsPassword = jenkinsPassword;

        restTemplate.setInterceptors(Collections.singletonList(new BasicAuthenticationInterceptor(jenkinsUsername,
                jenkinsPassword)));
    }

    private static Resource createTempFlatFile(String flatFile) throws IOException {
        Path testFile = Files.createTempFile("ereg_mapper", ".txt");
        Files.write(testFile, flatFile.getBytes());
        return new FileSystemResource(testFile.toFile());
    }

    public boolean send(String flatFile, String env) {

        Resource file;
        try {
            file = createTempFlatFile(flatFile);
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
            return false;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Jenkins-Crumb", getCrumb());
//        headers.addAll(createHeaders(jenkinsUsername, jenkinsPassword));

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("server", environment.getProperty(String.format(serverConfigString, env)));
        map.add("batchName", "BEREG007");
        map.add("workUnit", "100");
        map.add("input_file", file);
        map.add("FileName", "ereg_mapper.txt");

        RequestEntity requestEntity = new RequestEntity<>(map, headers, HttpMethod.POST, jenkinsJobTemplate.expand());

        log.info("Request for jenkins jobb: {}", requestEntity);

        ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);

        log.info("Response: {}", response);

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.warn("Kunne ikke starte / interagere med jenkins jobben for å lese inn EREG flatfil, kode: {}", response.getStatusCode());
            return false;
        }
        return true;
    }

    private String getCrumb() {
        JenkinsCrumbRequest crumbRequest = restTemplate.getForObject(jenkinsCrumbTemplate.expand(), JenkinsCrumbRequest.class);
        if (crumbRequest != null) {
            return crumbRequest.getCrumb();
        }
        throw new RuntimeException("Kunne ikke generere en crumb i Jenkins");
    }

    private HttpHeaders createHeaders(String username, String password) {
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(Charset.forName("US-ASCII")));
            String authHeader = "Basic " + new String(encodedAuth);
            set("Authorization", authHeader);
        }};
    }
}
