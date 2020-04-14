package no.nav.registre.testnorge.elsam.consumer.rs.response.ereg;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(NON_NULL)
public class Forretningsadresse {

    private String adresselinje1;
    private String adresselinje2;
    private String adresselinje3;
    private String postnummer;
    private String poststed;
    private String landkode;
    private String kommunenummer;
    private Periode bruksperiode;
    private Periode gyldighetsperiode;

    public String hentFullAdresse() {
        StringBuilder fullAdresse = new StringBuilder();
        if (adresselinje1 != null) {
            fullAdresse.append(adresselinje1.trim());
        }
        if (adresselinje2 != null) {
            fullAdresse.append(adresselinje2.trim());
        }
        if (adresselinje3 != null) {
            fullAdresse.append(adresselinje3.trim());
        }
        return fullAdresse.toString();
    }
}
