package no.nav.pdl.forvalter.dto;

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
public class OrganisasjonAdresseDTO {

    private String id;
    private String adressetype;
    private List<String> adresselinjer;
    private String postnr;
    private String poststed;
    private String kommunenr;
    private String landkode;
    private String vegadresseId;

    public List<String> getAdresselinjer() {

        if (isNull(adresselinjer)) {
            adresselinjer = new ArrayList<>();
        }
        return adresselinjer;
    }
}