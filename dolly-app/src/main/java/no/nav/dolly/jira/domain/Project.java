package no.nav.dolly.jira.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
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