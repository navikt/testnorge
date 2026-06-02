package no.nav.dolly.consumer.teamkatalog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamkatalogDTO {

    private HttpStatus status;
    private String feilmelding;

    private String epost;
    private List<String> teamNavn;
    private List<Team> teams;

    public List<String> getTeamNavn() {

        if (isNull(teamNavn)) {
            teamNavn = new ArrayList<>();
        }
        return teamNavn;
    }

    public List<Team> getTeams() {

        if (isNull(teams)) {
            teams = new ArrayList<>();
        }
        return teams;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Team {

        private String name;
    }
}
