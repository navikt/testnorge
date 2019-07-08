package no.nav.registre.ereg.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import no.nav.registre.ereg.consumer.rs.request.JenkinsCrumbRequest;

@Slf4j
@Component
public class JenkinsConsumer {

    private final RestTemplate restTemplate;
    private final UriTemplate jenkinsJobTemplate;
    private final UriTemplate jenkinsCrumbTemplate;
    private final Environment environment;
    private final String serverConfigString;

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

        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new BasicAuthenticationInterceptor(jenkinsUsername, jenkinsPassword));
        restTemplate.setInterceptors(interceptors);
    }

    private static Resource getFileResource(String content) throws IOException {
        Path tempFile = Files.createTempFile("ereg", ".txt");
        Files.write(tempFile, content.getBytes());
        File file = tempFile.toFile();
        return new FileSystemResource(file);
    }

    private String getCrumb() {
        JenkinsCrumbRequest crumbRequest = restTemplate.getForObject(jenkinsCrumbTemplate.expand(), JenkinsCrumbRequest.class);
        if (crumbRequest != null) {
            return crumbRequest.getCrumb();
        }
        throw new RuntimeException("Kunne ikke generere en crumb i Jenkins");
    }

    public boolean send(String flatFile, String env) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Jenkins-Crumb", getCrumb());

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("server", environment.getProperty(String.format(serverConfigString, env)));
        map.add("batchName", "BEREG007");
        map.add("workUnit", "100");
        map.add("FileName", "ereg_mapper.txt");
        map.add("overrideSequenceControl", "true");

        try {
            MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
            Resource fileResource = getFileResource(flatFile);
            String filename = fileResource.getFilename();
            ContentDisposition contentDisposition = ContentDisposition
                    .builder("form-data")
                    .name("input_file")
                    .filename(filename)
                    .build();
            fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
            fileMap.set("Content-Type", ContentType.TEXT_PLAIN.toString());
            HttpEntity<byte[]> fileEntity = new HttpEntity<>(flatFile.getBytes(), fileMap);
            map.add("input_file", fileEntity);
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
            return false;
        }

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.exchange(jenkinsJobTemplate.expand(), HttpMethod.POST, requestEntity, String.class);

        log.info("Response: {}", response);

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.warn("Kunne ikke starte / interagere med jenkins jobben for å lese inn EREG flatfil, kode: {}", response.getStatusCode());
            return false;
        } else return response.getStatusCode() == HttpStatus.CREATED;
    }
}
