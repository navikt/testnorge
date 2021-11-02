package no.nav.dolly.domain.resultset.pdlforvalter.doedsbo;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsPdlKontaktpersonMedIdNummer extends RsPdlAdressat {

    public static final String PERSON_MEDID = "PERSON_MEDID";

    private String idnummer;

    @Override
    public String getAdressatType() {
        return PERSON_MEDID;
    }
}
