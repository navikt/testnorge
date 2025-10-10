package no.nav.testnav.libs.data.pdlforvalter.v1;

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
import static org.apache.commons.lang3.StringUtils.isBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/*
 * Benyttes for å støtte adressedata på gammelt format
 */
public class UtenlandskAdresseIFrittFormatDTO implements Serializable {

        private List<String> adresselinjer;
        private String postkode;
        private String byEllerStedsnavn;
        private String landkode;

        public List<String> getAdresselinjer() {
            if (isNull(adresselinjer)) {
                adresselinjer = new ArrayList<>();
            }
            return adresselinjer;
        }

    @JsonIgnore
    public boolean isEmpty() {

        return adresselinjer.stream().allMatch(StringUtils::isBlank) &&
                isBlank(postkode) && isBlank(byEllerStedsnavn);
    }
}
