package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/*
 * Benyttes for å støtte adressedata på gammelt format
 */
public class PostadresseIFrittFormatDTO implements Serializable {

    private List<String> adresselinjer;
    private String postnummer;

    public List<String> getAdresselinjer() {
        if (isNull(adresselinjer)) {
            adresselinjer = new ArrayList<>();
        }
        return adresselinjer;
    }

    @JsonIgnore
    public boolean isEmpty() {

        return getAdresselinjer().stream().allMatch(StringUtils::isBlank);
    }
}

