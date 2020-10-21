package no.nav.identpool.fasit;

import static java.text.MessageFormat.format;
import static no.nav.identpool.fasit.RegexUtils.getValues;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FasitServiceImpl implements FasitService {

    static final Pattern NAME_PATTERN = Pattern.compile("\"name\": \"(?<value>.*?)\"", Pattern.MULTILINE);
    private static final String SCOPED_RESOURCE_URL = "{0}/api/v2/scopedresource?alias={1}&type={2}&environment={3}&application={4}&zone={5}";
    private static final String SCOPED_ENVIRONMENT_URL = "{0}/api/v2/environments?environmentclass={1}&page={2}&pr_page={3}";
    private final List<Mapper> mappers;
    private final RestConsumer consumer;

    @Value("https://fasit.adeo.no/")
    private String fasitUrl;

    @Override
    public String downloadFromUrl(String url, String fileName) throws IOException {
        String defaultPath = System.getProperty("java.io.tmpdir");
        return downloadFromUrl(url, defaultPath, fileName);
    }

    @Override
    public String downloadFromUrl(String url, String targetDirectory, String fileName) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, byte[].class);
        String filePath = targetDirectory.concat(fileName);

        if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            Files.write(Paths.get(filePath), responseEntity.getBody());
            return filePath;
        }
        return null;
    }

    @Override
    public <T extends FasitResource> T find(String alias, ResourceType type, String env, String application, Zone zone, Class<T> mappedType) {
        String asJson = consumer.get(format(SCOPED_RESOURCE_URL, fasitUrl, alias, type.getFasitType(), env, application, zone.name()));
        return mappers.stream()
                .filter(e -> e.supported(mappedType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Failed to find mapper for type: " + mappedType.getName()))
                .map(asJson, mappedType);
    }

    @Override
    public String findSecret(String ref) {
        return consumer.get(ref);
    }

    @Override
    public List<String> findEnvironmentNames(String environmentClass) {
        ArrayList<String> environments = new ArrayList<>();
        Integer pageSize = 30;
        Integer page = 0;
        List<String> environPage;
        do {
            String json = consumer.get(format(SCOPED_ENVIRONMENT_URL, fasitUrl, environmentClass, page.toString(), pageSize.toString()));
            environPage = getValues(json, NAME_PATTERN);
            environments.addAll(environPage);
            page += 1;
        } while (environPage.size() == pageSize);
        return environments;
    }
}