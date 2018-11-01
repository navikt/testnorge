package no.nav.dolly.jira.domain;

import lombok.Data;

@Data
public class JiraResponse {

    private String id;
    private String key;
    private String self;
}
