package no.nav.dolly.domain.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.io.Serializable;
import java.time.LocalDateTime;

import static no.nav.dolly.domain.jpa.HibernateConstants.SEQUENCE_STYLE_GENERATOR;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TRANSAKSJON_MAPPING")
public class TransaksjonMapping implements Serializable {

    @Id
    @GeneratedValue(generator = "transaksjonMappingIdGenerator")
    @GenericGenerator(name = "transaksjonMappingIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
            @Parameter(name = "sequence_name", value = "TRANSAKSJON_MAPPING_SEQ"),
            @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1")
    })
    private Long id;

    @Column(name = "BESTILLING_ID")
    private Long bestillingId;

    @Column(name = "IDENT")
    private String ident;

    @Column(name = "SYSTEM")
    private String system;

    @Column(name = "MILJOE")
    private String miljoe;

    @Column(name = "TRANSAKSJON_ID")
    private String transaksjonId;

    @Column(name = "DATO_ENDRET")
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
