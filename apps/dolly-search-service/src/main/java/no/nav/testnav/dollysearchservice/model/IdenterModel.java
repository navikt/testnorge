package no.nav.testnav.dollysearchservice.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.apache.commons.lang3.BooleanUtils;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class IdenterModel {
    String ident;
    String gruppe;
    Boolean historisk;

    public boolean isFolkeregisterident() {

        return "FOLKEREGISTERIDENT".equals(gruppe) &&
                BooleanUtils.isFalse(historisk);
    }
}
