package no.nav.dolly.domain.jira;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Fields {

    private Field summary;
    private Field issuetype;
    private Field attachment;
    private Field project;
    private Field customfield_14811;
}