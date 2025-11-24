package no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizedPartyDTO {

    private String name;
    private String organizationNumber;
    private String unitType;
    private Boolean isDeleted;
    private List<String> authorizedResources;
    private List<AuthorizedPartyDTO> subunits;

    public List<String> getAuthorizedResources() {

        if (isNull(authorizedResources)) {
            authorizedResources = new ArrayList<>();
        }
        return authorizedResources;
    }

    public List<AuthorizedPartyDTO> getSubunits() {

        if (isNull(subunits)) {
            subunits = new ArrayList<>();
        }
        return subunits;
    }
}

