package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.consumer.jira.JiraConsumer;
import no.nav.dolly.domain.jira.AllowedValue;
import no.nav.dolly.domain.jira.Field;
import no.nav.dolly.domain.jira.Fields;
import no.nav.dolly.domain.jira.JiraResponse;
import no.nav.dolly.domain.jira.Project;
import no.nav.dolly.domain.resultset.RsOpenAmResponse;
import no.nav.dolly.exceptions.JiraException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAmService {

    private static final String ISSUE_CREATE = "/rest/api/2/issue";
    private static final String METADATA = "/createmeta";

    private static final String ATTACHMENTS = "/attachments";
    private static final String BROWSE = "/browse";
    private static final String FEILMELDING = "En feil oppsto. Bestilling kan ikke utføres.";
    private static final String FEILMELDING_UKJENT_MILJOE = "Angitt miljø eksisterer ikke.";

    private final JiraConsumer jiraConsumer;

    public RsOpenAmResponse opprettIdenter(List<String> identliste, String miljoe) {

        try {
            Fields fields = readOpenAmMetadata();

            ResponseEntity<JiraResponse> createResponse = createIssue(miljoe, fields);

            ResponseEntity<String> attachmentResponse = createAttachment(identliste, createResponse);

            return RsOpenAmResponse.builder()
                    .miljoe(miljoe)
                    .status(attachmentResponse.getStatusCode())
                    .httpCode(attachmentResponse.getStatusCode().value())
                    .message(HttpStatus.OK.value() == attachmentResponse.getStatusCodeValue() &&
                            createResponse.getBody() != null ?
                            format("%s%s/%s", jiraConsumer.getBaseUrl(), BROWSE, createResponse.getBody().getKey()) : null)
                    .build();
        } catch (JiraException e) {
            return RsOpenAmResponse.builder()
                    .miljoe(miljoe)
                    .status(e.getStatusCode())
                    .message(e.getStatusText())
                    .httpCode(e.getStatusCode().value())
                    .build();
        } catch (HttpStatusCodeException e) {
            log.error(e.getMessage(), e);
            return RsOpenAmResponse.builder()
                    .miljoe(miljoe)
                    .status(e.getStatusCode())
                    .message(FEILMELDING)
                    .httpCode(e.getStatusCode().value())
                    .build();
        }
    }

    private ResponseEntity<String> createAttachment(List<String> identliste, ResponseEntity<JiraResponse> createResponse) {
        LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("file", new FileSystemResource(createIdentsFile(identliste)));

        if (createResponse != null && createResponse.getBody() != null) {
            return jiraConsumer.excuteRequest(
                    format("%s/%s%s", ISSUE_CREATE, createResponse.getBody().getKey(), ATTACHMENTS),
                    HttpMethod.POST, new HttpEntity<>(params, jiraConsumer.createHttpHeaders(MediaType.MULTIPART_FORM_DATA, createResponse.getHeaders())), String.class);
        } else {
            log.error("Mottatt tom response body paa createIssue.");
            throw new JiraException(HttpStatus.INTERNAL_SERVER_ERROR, FEILMELDING);
        }
    }

    private ResponseEntity<JiraResponse> createIssue(String miljoe, Fields fields) {
        String envId = null;

        if (fields == null || isInvalid(fields.getCustomfield_14811()) || isInvalid(fields.getProject()) || isInvalid(fields.getIssuetype())) {
            log.error("En eller flere nødvendige felter i metadata er null.");
            throw new JiraException(HttpStatus.INTERNAL_SERVER_ERROR, FEILMELDING);
        }
        for (AllowedValue allowedValue : fields.getCustomfield_14811().getAllowedValues()) {
            if (miljoe.equals(allowedValue.getValue())) {
                envId = allowedValue.getId();
                break;
            }
        }
        if (envId == null) {
            throw new JiraException(HttpStatus.BAD_REQUEST, FEILMELDING_UKJENT_MILJOE);
        }

        String request = null;
        try {
            request = new JSONObject()
                    .put("fields", new JSONObject()
                            .put("summary", "Opprett testbrukere fra Dolly")
                            .put("project", new JSONObject()
                                    .put("id", fields.getProject().getAllowedValues().get(0).getId())
                                    .put("key", fields.getProject().getAllowedValues().get(0).getKey()))
                            .put("issuetype", new JSONObject()
                                    .put("id", fields.getIssuetype().getAllowedValues().get(0).getId()))
                            .put("customfield_14811", new JSONObject()
                                    .put("id", envId))
                    ).toString();
        } catch (JSONException e) {
            log.error(e.getMessage(), e);
        }

        return jiraConsumer.excuteRequest(ISSUE_CREATE, HttpMethod.POST,
                new HttpEntity<>(request, jiraConsumer.createHttpHeaders(MediaType.APPLICATION_JSON)), JiraResponse.class);
    }

    private boolean isInvalid(Field field) {
        return field == null || field.getAllowedValues().isEmpty();
    }

    private Fields readOpenAmMetadata() {
        MultiValueMap<String, String> queries = new LinkedMultiValueMap<>();

        queries.add("projectKeys", "DEPLOY");
        queries.add("issuetypeIds", "16001");
        queries.add("expand", "projects.issuetypes.fields");

        ResponseEntity<Project> metadata = jiraConsumer.getOpenAmMetadata(format("%s%s", ISSUE_CREATE, METADATA),
                new HttpEntity<>("", jiraConsumer.createHttpHeaders(MediaType.APPLICATION_JSON)),
                queries);

        if (metadata != null && metadata.getBody() != null && isMetadataNotEmpty(metadata)) {
            return metadata.getBody().getProjects().get(0).getIssuetypes().get(0).getFields();
        } else {
            log.error("En eller flere nødvendige felter i metadata er null.");
            throw new JiraException(HttpStatus.INTERNAL_SERVER_ERROR, FEILMELDING);
        }
    }

    private boolean isMetadataNotEmpty(ResponseEntity<Project> metadata) {
        return !metadata.getBody().getProjects().isEmpty() && !metadata.getBody().getProjects().get(0).getIssuetypes().isEmpty();
    }

    private File createIdentsFile(List<String> identliste) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("OpenAM-", ".txt");

            try (Writer writer = new OutputStreamWriter(new FileOutputStream(tempFile), UTF_8)) {
                for (String ident : identliste) {
                    writer.write(format("%s;4;n;e;%n", ident));
                }
            }

        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        return tempFile;
    }
}
