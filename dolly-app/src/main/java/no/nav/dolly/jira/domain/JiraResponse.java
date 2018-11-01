package no.nav.dolly.jira.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JiraResponse {

    private String id;
    private String key;
    private String self;
}
