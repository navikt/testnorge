package no.nav.testnav.apps.templatesearchservice.consumers.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CurrentBrukerDTO(RepresentererTeamDTO representererTeam) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RepresentererTeamDTO(String brukerId) {
    }
}
