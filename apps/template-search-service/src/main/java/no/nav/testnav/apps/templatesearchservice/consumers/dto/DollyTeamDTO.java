package no.nav.testnav.apps.templatesearchservice.consumers.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DollyTeamDTO(String brukerId, String navn) {
}
