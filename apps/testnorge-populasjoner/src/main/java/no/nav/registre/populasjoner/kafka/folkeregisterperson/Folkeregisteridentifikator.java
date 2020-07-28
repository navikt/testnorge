package no.nav.registre.populasjoner.kafka.folkeregisterperson;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Folkeregisteridentifikator {

    private String identifikasjonsnummer;
    private Metadata metadata;

    /**
     * TODO: Update to only check if source (kilde) is "TENOR". This is currently not available in PDL
     *
     * @return is created by TENOR
     */
    @JsonIgnore
    public boolean isOpprettByTenor() {
        return metadata != null
                && metadata.getEndringer() != null
                && metadata.getEndringer().stream().anyMatch(value -> !value.getKilde().toLowerCase().contains("dolly") && value.getType().equals("OPPRETT"));
    }
}
