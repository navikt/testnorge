package no.nav.dolly.jira.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AllowedValue {

    private String self;
    private String value;
    private String id;
    private String key;
    private String description;
    private String iconUrl;
    private String name;
    private Boolean subtask;
    private String avatarId;
}
