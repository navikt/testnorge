package no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrganisasjonCreateDTO {

    private static final String IDENTIFIER = "urn:altinn:organization:identifier-no:%s";

    private List<String> data;

    public OrganisasjonCreateDTO(String orgnummer) {

        this.data = List.of(IDENTIFIER.formatted(orgnummer));
    }
}
