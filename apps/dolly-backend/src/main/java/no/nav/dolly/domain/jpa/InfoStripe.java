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
@Table(name = "info_stripe")
public class InfoStripe implements Serializable {

    @Id
    @GeneratedValue(generator = "infoStripeIdGenerator")
    @GenericGenerator(name = "infoStripeIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
            @Parameter(name = "sequence_name", value = "info_stripe_seq"),
            @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1")
    })
    private Long id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String message;

    @Column
    private LocalDateTime start;

    @Column
    private LocalDateTime expires;
}
