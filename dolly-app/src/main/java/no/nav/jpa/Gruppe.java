package no.nav.jpa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

import java.util.List;
import java.util.Set;

import static no.nav.jpa.HibernateConstants.SEQUENCE_STYLE_GENERATOR;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "T_GRUPPER")
public class Gruppe {

    @Id
    @GeneratedValue(generator = "gruppeIdGenerator")
    @GenericGenerator(name= "gruppeIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
            @Parameter(name = "sequence_name", value = "T_GRUPPE_SEQ"),
            @Parameter(name = "initial_value", value = "100000000"),
            @Parameter(name = "increment_size", value = "1")
    })
    private Long id;

    private String navn;

    @OneToMany(mappedBy = "gruppe", orphanRemoval = true)
    private Set<Ident> identer;
}
