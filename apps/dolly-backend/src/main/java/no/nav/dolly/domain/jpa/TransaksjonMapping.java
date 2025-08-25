package no.nav.dolly.domain.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("TRANSAKSJON_MAPPING")
public class TransaksjonMapping implements Serializable {

    @Id
    private Long id;

    @Column("BESTILLING_ID")
    private Long bestillingId;

    @Column("IDENT")
    private String ident;

    @Column("SYSTEM")
    private String system;

    @Column("MILJOE")
    private String miljoe;

    @Column("TRANSAKSJON_ID")
    private String transaksjonId;

    @Column("DATO_ENDRET")
    private LocalDateTime datoEndret;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        TransaksjonMapping that = (TransaksjonMapping) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(bestillingId, that.bestillingId)
                .append(ident, that.ident)
                .append(system, that.system)
                .append(miljoe, that.miljoe)
                .append(transaksjonId, that.transaksjonId)
                .append(datoEndret, that.datoEndret)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(bestillingId)
                .append(ident)
                .append(system)
                .append(miljoe)
                .append(transaksjonId)
                .append(datoEndret)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "TransaksjonMapping{" +
                "id=" + id +
                ", bestillingId=" + bestillingId +
                ", ident='" + ident + '\'' +
                ", system='" + system + '\'' +
                ", miljoe='" + miljoe + '\'' +
                ", transaksjonId='" + transaksjonId + '\'' +
                ", datoEndret=" + datoEndret +
                '}';
    }
}
