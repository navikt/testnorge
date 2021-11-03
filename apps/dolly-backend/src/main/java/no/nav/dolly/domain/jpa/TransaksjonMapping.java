package no.nav.dolly.domain.jpa;

import static no.nav.dolly.domain.jpa.HibernateConstants.SEQUENCE_STYLE_GENERATOR;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TRANSAKSJON_MAPPING")
public class TransaksjonMapping {

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
}
