package no.nav.testnav.libs.dto.skattekortservice.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentifikatorForEnhetEllerPerson {

    private String organisasjonsnummer;
    private String personidentifikator;

    @JsonIgnore
    public boolean isAllEmpty() {

        return isBlank(organisasjonsnummer) &&
                isBlank(personidentifikator);
    }

    @JsonIgnore
    public boolean isAmbiguous() {

        return isNotBlank(organisasjonsnummer) &&
                isNotBlank(personidentifikator);
    }
}