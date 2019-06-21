package no.nav.registre.ereg.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Component
public class JenkinsConsumer {

    private final RestTemplate restTemplate = new RestTemplate();
    private final UriTemplate jenkinsBEREG007Template;

    public JenkinsConsumer(@Value("${jenkins.rest.api.url}") String jenkinsUri) {
        jenkinsBEREG007Template = new UriTemplate(jenkinsUri + "/job/Start_BEREG007/");
    }

    public boolean send(String flatFile) {
        RequestEntity<String> requestEntity = RequestEntity.post(jenkinsBEREG007Template.expand())
                .body(createJobBody(flatFile));
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            log.warn("Kunne ikke starte / interagere med jenkins jobben for å lese inn EREG flatfil, kode: {}", responseEntity.getStatusCode());
            return false;
        }
        return true;
    }

    private String createJobBody(String flatfile) {

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss.SSS");

        String dateString = format.format(new Date());

        return String.format(
                "{" +
                        "\"jobParameters\":\"" +
                        "startTime='%s'," +
                        "modus=AJOURHOLD," +
                        "workUnit=100," +
                        "rekjoerFeiltyper=KNYTNING_FEIL," +
                        "stepSelection=2;3;4;5;6;7;8," +
                        "inputFileLocation=%s," +
                        "overrideSequenceControl=true\"" +
                        "}",
                dateString, flatfile
        );
    }

}
