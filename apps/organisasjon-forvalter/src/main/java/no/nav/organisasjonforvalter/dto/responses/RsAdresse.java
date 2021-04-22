package no.nav.organisasjonforvalter.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsAdresse {

    private Long id;
    private AdresseType adressetype;
    private List<String> adresselinjer;
    private String postnr;
    private String poststed;
    private String kommunenr;
    private String landkode;
    private String vegadresseId;

    public enum AdresseType {FADR, PADR}
}
