package no.nav.dolly.domain.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.entity.infostripe.InfoStripeType;
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
@Table(name = "info_stripe")
public class InfoStripe implements Serializable {

    @Id
    private Long id;

    @Column("TYPE")
    private InfoStripeType type;

    @Column("MESSAGE")
    private String message;

    @Column("START")
    private LocalDateTime start;

    @Column("EXPIRES")
    private LocalDateTime expires;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InfoStripe that = (InfoStripe) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(type, that.type)
                .append(message, that.message)
                .append(start, that.start)
                .append(expires, that.expires)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(type)
                .append(message)
                .append(start)
                .append(expires)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "InfoStripe{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", message='" + message + '\'' +
                ", start=" + start +
                ", expires=" + expires +
                '}';
    }
}
