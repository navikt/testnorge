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

import static no.nav.dolly.domain.jpa.HibernateConstants.SEQUENCE_STYLE_GENERATOR;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BESTILLING_KONTROLL")
public class BestillingKontroll implements Serializable {

    @Id
    @GeneratedValue(generator = "bestillingKontrollIdGenerator")
    @GenericGenerator(name = "bestillingKontrollIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
            @Parameter(name = "sequence_name", value = "BESTILLING_KONTROLL_SEQ"),
            @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1")
    })
    private Long id;

    @Column(name = "BESTILLING_ID", nullable = false)
    private Long bestillingId;

    @Column(name = "STOPPET", nullable = false)
    private boolean stoppet;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BestillingKontroll that = (BestillingKontroll) o;

        return new EqualsBuilder()
                .append(stoppet, that.stoppet)
                .append(id, that.id)
                .append(bestillingId, that.bestillingId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(bestillingId)
                .append(stoppet)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "BestillingKontroll{" +
                "id=" + id +
                ", bestillingId=" + bestillingId +
                ", stoppet=" + stoppet +
                '}';
    }
}
