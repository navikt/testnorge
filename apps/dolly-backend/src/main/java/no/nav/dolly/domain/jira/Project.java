package no.nav.dolly.domain.jira;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Project {

    private String expand;
    private List<Project> projects;
    private String self;
    private String id;
    private String key;
    private String name;
    private List<Issuetypes> issuetypes;

    public List<Project> getProjects() {
        if (projects == null) {
            projects = new ArrayList<>();
        }
        return projects;
    }

    public List<Issuetypes> getIssuetypes() {
        if (issuetypes == null) {
            issuetypes = new ArrayList<>();
        }
        return issuetypes;
    }
}