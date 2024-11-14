package no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class OrganisasjonDeleteDTO {

    private List<String> data;

    public OrganisasjonDeleteDTO(Map<String, String> organisasjon) {

        data = organisasjon.entrySet().stream()
                .map(entry -> "%s:%s".formatted(entry.getKey(), entry.getValue()))
                .toList();
    }
}