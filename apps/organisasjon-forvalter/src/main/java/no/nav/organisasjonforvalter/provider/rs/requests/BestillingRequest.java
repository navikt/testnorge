package no.nav.organisasjonforvalter.provider.rs.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.organisasjonforvalter.jpa.entity.Adresse;
import no.nav.organisasjonforvalter.jpa.entity.Organisasjon;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BestillingRequest {

    private String id;
    private String parentId;
    private String enhetstype;

    private String organisasjonsform;
    private String naeringskode;
    private String formaal;
    private String telefon;
    private String epost;
    private String nettside;
    private List<Adresse> adresser;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Adresse {

        private String adressetype;
        private String adresselinje1;
        private String adresselinje2;
        private String adresselinje3;
        private String adresselinje4;
        private String adresselinje5;
        private String postnr;
        private String kommunenr;
        private String landkode;
    }
}
