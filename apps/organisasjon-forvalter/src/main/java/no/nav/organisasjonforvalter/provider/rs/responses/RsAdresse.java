package no.nav.organisasjonforvalter.provider.rs.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsAdresse {

    public enum AdresseType {FADR, PADR}

    private Long id;
    private AdresseType adressetype;
    private List<String> adresselinjer;
    private String postnr;
    private String poststed;
    private String kommunenr;
    private String landkode;
    private String vegadresseId;
}
