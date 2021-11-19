package no.nav.testnav.libs.dto.tpsmessagingservice.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MatrikkeladresseDTO extends AdresseDTO {

    private String mellomnavn;

    private String gardsnr;

    private String bruksnr;

    private String festenr;

    private String undernr;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof MatrikkeladresseDTO)) {
            return false;
        }

        MatrikkeladresseDTO that = (MatrikkeladresseDTO) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(getMellomnavn(), that.getMellomnavn())
                .append(getGardsnr(), that.getGardsnr())
                .append(getBruksnr(), that.getBruksnr())
                .append(getFestenr(), that.getFestenr())
                .append(getUndernr(), that.getUndernr())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(getMellomnavn())
                .append(getGardsnr())
                .append(getBruksnr())
                .append(getFestenr())
                .append(getUndernr())
                .toHashCode();
    }

    @Override
    public AdresseDTO toUppercase() {
        if (isNotBlank(getMellomnavn())) {
            setMellomnavn(getMellomnavn().toUpperCase());
        }
        return this;
    }
}
