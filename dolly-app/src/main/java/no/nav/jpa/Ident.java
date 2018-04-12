package no.nav.jpa;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

import static no.nav.jpa.HibernateConstants.SEQUENCE_STYLE_GENERATOR;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "T_IDENT")
public class Ident {

    @Id
    @GeneratedValue(generator = "identIdGenerator")
    @GenericGenerator(name= "identIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
            @Parameter(name = "sequence_name", value = "T_IDENT_SEQ"),
            @Parameter(name = "initial_value", value = "100000000"),
            @Parameter(name = "increment_size", value = "1")
    })
    private Long id;

    private String ident;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "GRUPPE_ID", nullable = false)
    private Gruppe gruppe;
}
