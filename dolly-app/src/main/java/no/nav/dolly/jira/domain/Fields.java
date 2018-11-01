package no.nav.dolly.jira.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Fields {

    private Field summary;
    private Field issuetype;
    private Field attachment;
    private Field project;
    private Field customfield_14811;
}