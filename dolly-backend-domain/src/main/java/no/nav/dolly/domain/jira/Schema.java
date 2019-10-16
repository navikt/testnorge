package no.nav.dolly.domain.jira;

import lombok.Data;

@Data
public class Schema {
    private String type;
    private String custom;
    private String customId;
    private String items;
    private String system;
}
