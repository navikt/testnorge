package no.nav.jpa;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static no.nav.jpa.HibernateConstants.SEQUENCE_STYLE_GENERATOR;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "T_BRUKER")
public class Bruker {

    @Id
    @GeneratedValue(generator = "brukerIdGenerator")
    @GenericGenerator(name= "brukerIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
            @Parameter(name = "sequence_name", value = "T_BRUKER_SEQ"),
            @Parameter(name = "initial_value", value = "100000000"),
            @Parameter(name = "increment_size", value = "1")
    })
    private Long id;

    private String navn;

    @ManyToMany(mappedBy = "brukere")
    private Set<Team> team = new HashSet<>();

}
