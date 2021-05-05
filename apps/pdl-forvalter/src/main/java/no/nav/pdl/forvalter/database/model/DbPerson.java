package no.nav.pdl.forvalter.database.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.pdl.forvalter.database.JSONUserType;
import no.nav.pdl.forvalter.domain.PdlPerson;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "person")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TypeDef(name = "JsonType", typeClass = JSONUserType.class)
public class DbPerson {

    public static final String SEQUENCE_STYLE_GENERATOR = "org.hibernate.id.enhanced.SequenceStyleGenerator";

    @Id
    @GeneratedValue(generator = "bestillingKontrollIdGenerator")
    @GenericGenerator(name = "bestillingKontrollIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
            @Parameter(name = "sequence_name", value = "person_sequence"),
            @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1")
    })
    private Long id;

    private LocalDateTime updated;
    private String ident;

    @Type(type = "JsonType")
    private PdlPerson person;
}