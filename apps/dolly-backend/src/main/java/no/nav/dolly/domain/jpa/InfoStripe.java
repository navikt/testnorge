package no.nav.dolly.domain.jpa;

import jakarta.persistence.*;
import lombok.*;
import no.nav.dolly.domain.resultset.entity.infostripe.InfoStripeType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "info_stripe")
public class InfoStripe implements Serializable {

    @Id
    @GeneratedValue(generator = "infoStripeIdGenerator")
    @GenericGenerator(
            name = "infoStripeIdGenerator",
            type = org.hibernate.id.enhanced.SequenceStyleGenerator.class,
            parameters = {
                    @Parameter(name = "sequence_name", value = "info_stripe_seq"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    private Long id;

    @Builder.Default
    @Column(nullable = false)
    private InfoStripeType type = InfoStripeType.INFO;

    @Column(nullable = false)
    private String message;

    @Column
    private LocalDateTime start;

    @Column
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
