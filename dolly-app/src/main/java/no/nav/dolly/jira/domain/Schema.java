package no.nav.dolly.jira.domain;

import lombok.Data;

@Data
public class Schema {
    private String type;
    private String custom;
    private String customId;
    private String items;
    private String system;
}
