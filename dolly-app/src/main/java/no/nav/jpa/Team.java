package no.nav.jpa;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

import java.util.HashSet;
import java.util.Set;

import static no.nav.jpa.HibernateConstants.SEQUENCE_STYLE_GENERATOR;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "T_TEAM")
public class Team {

    @Id
    @GeneratedValue(generator = "teamIdGenerator")
    @GenericGenerator(name= "teamIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
            @Parameter(name = "sequence_name", value = "T_TEAM_SEQ"),
            @Parameter(name = "initial_value", value = "100000000"),
            @Parameter(name = "increment_size", value = "1")
    })
    private Long id;

    private String navn;

    @ManyToMany( cascade = {CascadeType.MERGE, CascadeType.PERSIST })
    @JoinTable(name = "team_gruppe",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "gruppe_id"))
    private Set<Bruker> brukere = new HashSet<>();
}
