package no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrganisasjonCreateDTO {

    public static final String ORGANISASJON_ID = "urn:altinn:organization:identifier-no";

    private List<String> data;

    public OrganisasjonCreateDTO(String orgnummer) {

        this.data = List.of("%s:%s".formatted(ORGANISASJON_ID, orgnummer));
    }
}
