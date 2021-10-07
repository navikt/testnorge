package no.nav.testnav.apps.persontilgangservice.client.altinn.v1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AccessDTO(
        @JsonProperty("Name")
        String name,
        @JsonProperty("Type")
        String type,
        @JsonProperty("OrganizationNumber")
        String organizationNumber,
        @JsonProperty("OrganizationForm")
        String organizationForm,
        @JsonProperty("Status")
        String status
) {

    public boolean isActive() {
        return status.equals("Active");
    }
}
