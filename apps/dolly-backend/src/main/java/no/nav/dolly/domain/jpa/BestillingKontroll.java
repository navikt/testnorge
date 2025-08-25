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

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BESTILLING_KONTROLL")
public class BestillingKontroll implements Serializable {

    @Id
    private Long id;

    @Column("BESTILLING_ID")
    private Long bestillingId;

    @Column("STOPPET")
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
