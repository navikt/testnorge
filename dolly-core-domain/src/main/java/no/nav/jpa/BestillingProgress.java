package no.nav.jpa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import static no.nav.jpa.HibernateConstants.SEQUENCE_STYLE_GENERATOR;

@Entity
@Getter
@Setter
@Table(name = "T_BESTILLING_PROGRESS")
public class BestillingProgress {

    @Id
    @GeneratedValue(generator = "bestillingProgressIdGenerator")
    @GenericGenerator(name = "bestillingProgressIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
            @Parameter(name = "sequence_name", value = "T_BESTILLING_PROGRESS_SEQ"),
            @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1")
    })
    private Long id;

    private Long bestillingsId;

    private String ident;

    private String tpsfSuccessEnv;

    private String sigrunSuccessEnv;

    private String aaregSuccessEnv;

    private String feil;
}
