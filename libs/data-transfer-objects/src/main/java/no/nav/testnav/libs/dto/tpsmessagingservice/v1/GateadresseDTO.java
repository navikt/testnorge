package no.nav.testnav.libs.dto.tpsmessagingservice.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GateadresseDTO extends AdresseDTO {

    private String adresse;

    private String husnummer;

    private String gatekode;

    public String getHusnummer() {
        return StringUtils.isNumeric(husnummer) ?
                Integer.valueOf(husnummer).toString() : // Eliminates leading 0s
                husnummer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof GateadresseDTO)) {
            return false;
        }

        GateadresseDTO that = (GateadresseDTO) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(getAdresse(), that.getAdresse())
                .append(getHusnummer(), that.getHusnummer())
                .append(getGatekode(), that.getGatekode())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(getAdresse())
                .append(getHusnummer())
                .append(getGatekode())
                .toHashCode();
    }

    @Override
    public AdresseDTO toUppercase() {
        if (isNotBlank(getAdresse())) {
            setAdresse(getAdresse().toUpperCase());
        }
        if (isNotBlank(getHusnummer())) {
            setHusnummer(getHusnummer().toUpperCase());
        }
        return this;
    }
}
