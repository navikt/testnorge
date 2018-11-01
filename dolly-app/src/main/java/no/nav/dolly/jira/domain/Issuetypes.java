package no.nav.dolly.jira.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Issuetypes {

    private String self;
    private String id;
    private String description;
    private String iconUrl;
    private String name;
    private Boolean subtask;
    private String expand;
    private Fields fields;
}