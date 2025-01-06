package no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto;

import lombok.Data;

@Data
public class AltinnAuthorizedPartiesRequestDTO {

    private static final String IDENT_IDENTIFIKATOR = "urn:altinn:person:identifier-no";

    private String type;
    private String value;

    public AltinnAuthorizedPartiesRequestDTO(String ident) {

        this.type = IDENT_IDENTIFIKATOR;
        this.value = ident;
    }
}
