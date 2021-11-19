package no.nav.testnav.libs.dto.tpsmessagingservice.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class AdresseDTO {

    private String kommunenr;

    private LocalDateTime flyttedato;

    private LocalDateTime gyldigTilDato;

    private String postnr;

    private String tilleggsadresse;

    private String bolignr;

    private Boolean deltAdresse;

    private String matrikkelId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof AdresseDTO)) {
            return false;
        }

        AdresseDTO adresse = (AdresseDTO) o;

        return new EqualsBuilder()
                .append(getKommunenr(), adresse.getKommunenr())
                .append(getPostnr(), adresse.getPostnr())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getKommunenr())
                .append(getPostnr())
                .toHashCode();
    }

    public abstract AdresseDTO toUppercase();
}
