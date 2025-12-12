package no.nav.dolly.domain.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("bruker_favoritter")
public class BrukerFavoritter implements Serializable {

    @Column("bruker_id")
    private Long brukerId;

    @Column("gruppe_id")
    private Long gruppeId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BrukerFavoritter that = (BrukerFavoritter) o;

        return new EqualsBuilder()
                .append(brukerId, that.brukerId)
                .append(gruppeId, that.gruppeId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(brukerId)
                .append(gruppeId)
                .toHashCode();
    }

    @Override
    public String
    toString() {
        return "BrukerFavoritter{" +
                "brukerId=" + brukerId +
                ", gruppeId=" + gruppeId +
                '}';
    }
}
