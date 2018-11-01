package no.nav.dolly.service;

import static java.lang.String.format;
import static no.nav.dolly.jira.JiraConsumer.createHttpHeaders;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.resultset.RsOpenAmResponse;
import no.nav.dolly.exceptions.JiraException;
import no.nav.dolly.jira.JiraConsumer;
import no.nav.dolly.jira.domain.AllowedValue;
import no.nav.dolly.jira.domain.Field;
import no.nav.dolly.jira.domain.Fields;
import no.nav.dolly.jira.domain.JiraResponse;
import no.nav.dolly.jira.domain.Project;

@Slf4j
@Service
public class OpenAmService {

    private static final String ISSUE_CREATE = "/rest/api/2/issue";
    private static final String METADATA = "/createmeta?projectKeys=DEPLOY&issuetypeIds=16001&expand=projects.issuetypes.fields";
    private static final String ATTACHMENTS = "/attachments";
    private static final String BROWSE = "/browse";

    private static final String FEILMELDING = "En feil oppsto. Bestilling kan ikke utføres.";
    private static final String FEILMELDING_UKJENT_MILJOE = "Angitt miljø eksisterer ikke.";

    @Autowired
    private JiraConsumer jiraConsumer;

    public RsOpenAmResponse opprettIdenter(List<String> identliste, String miljoe) {

        try {
            Fields fields = readOpenAmMetadata().getProjects().get(0).getIssuetypes().get(0).getFields();

            ResponseEntity<JiraResponse> createResponse = createIssue(miljoe, fields);

            ResponseEntity<String> attachmentResponse = createAttachment(identliste, createResponse);

            return RsOpenAmResponse.builder()
                    .miljoe(miljoe)
                    .status(attachmentResponse.getStatusCode().name())
                    .jira(HttpStatus.OK.value() == attachmentResponse.getStatusCodeValue() ?
                            format("%s%s/%s", jiraConsumer.getBaseUrl(), BROWSE, createResponse.getBody().getKey()) : null)
                    .build();
        } catch (JiraException e) {
            return RsOpenAmResponse.builder()
                    .miljoe(miljoe)
                    .status("FEIL")
                    .feilmelding(e.getStatusText())
                    .httpCode(e.getRawStatusCode())
                    .build();
        }
    }

    private ResponseEntity<String> createAttachment(List<String> identliste, ResponseEntity<JiraResponse> createResponse) {
        LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("file", new FileSystemResource(fileFromIdents(identliste, createResponse.getBody())));

        return jiraConsumer.excuteRequest(
                format("%s/%s%s", ISSUE_CREATE, createResponse.getBody().getKey(), ATTACHMENTS),
                HttpMethod.POST, new HttpEntity<>(params, createHttpHeaders(MediaType.MULTIPART_FORM_DATA, createResponse.getHeaders())), String.class);
    }

    private ResponseEntity<JiraResponse> createIssue(String miljoe, Fields fields) {
        String envId = null;

        if (fields == null || !isValid(fields.getCustomfield_14811()) || !isValid(fields.getProject()) || !isValid(fields.getIssuetype())) {
            log.error("En eller flere nødvendige felter i metadata er null.");
            throw new JiraException(HttpStatus.INTERNAL_SERVER_ERROR, FEILMELDING);
        }
        for (AllowedValue allowedValue : fields.getCustomfield_14811().getAllowedValues()) {
            if (miljoe.equals(allowedValue.getValue())) {
                envId = allowedValue.getId();
            }
        }
        if (envId == null) {
            log.error(FEILMELDING_UKJENT_MILJOE);
            throw new JiraException(HttpStatus.INTERNAL_SERVER_ERROR, FEILMELDING_UKJENT_MILJOE);
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
                new HttpEntity<>(request, createHttpHeaders(MediaType.APPLICATION_JSON)), JiraResponse.class);
    }

    private boolean isValid(Field field) {
        return field != null && !field.getAllowedValues().isEmpty();
    }

    private Project readOpenAmMetadata() {
        ResponseEntity<Project> metadata = jiraConsumer.excuteRequest(format("%s%s", ISSUE_CREATE, METADATA), HttpMethod.GET,
                new HttpEntity<>("", createHttpHeaders(MediaType.APPLICATION_JSON)), Project.class);
        return metadata.getBody();
    }

    private File fileFromIdents(List<String> identliste, JiraResponse jiraResponse) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile(format("OpenAM-%s-", jiraResponse.getKey()), ".txt");
            tempFile.deleteOnExit();

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tempFile));
            for (String ident : identliste) {
                bufferedWriter.write(format("%s;4;n;e;\n", ident));
            }
            bufferedWriter.close();

        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        return tempFile;
    }
}
