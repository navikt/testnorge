package no.nav.registre.testnorge.elsam.consumer.rs.response.ereg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class EregNavn {

    private String navnelinje1;
    private String navnelinje2;
    private String navnelinje3;
    private String navnelinje4;
    private String navnelinje5;

    public String hentFulltNavn() {
        StringBuilder fulltnavn = new StringBuilder();
        if (navnelinje1 != null) {
            fulltnavn.append(navnelinje1.trim());
        }
        if (navnelinje2 != null) {
            fulltnavn.append(' ').append(navnelinje2.trim());
        }
        if (navnelinje3 != null) {
            fulltnavn.append(' ').append(navnelinje3.trim());
        }
        if (navnelinje4 != null) {
            fulltnavn.append(' ').append(navnelinje4.trim());
        }
        if (navnelinje5 != null) {
            fulltnavn.append(' ').append(navnelinje5.trim());
        }
        return fulltnavn.toString();
    }
}
