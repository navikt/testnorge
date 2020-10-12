package no.nav.registre.testnorge.personexportapi.consumer.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class EndringsmeldingDTO {

    private static final String AARSAKSKODE_FOEDSEL = "01";
    private static final String AARSAKSKODE_INNVANDRING = "02";

    String fodselsdato;
    String personnummer;
    String fornavn;
    String mellomnavn;
    String slektsnavn;
    String aarsakskode;

    @JsonIgnore
    public boolean isFoedsel() {
        return aarsakskode != null && aarsakskode.equals(AARSAKSKODE_FOEDSEL);
    }

    @JsonIgnore
    public boolean isInnvandring() {
        return aarsakskode != null && aarsakskode.equals(AARSAKSKODE_INNVANDRING);
    }
}