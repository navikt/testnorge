package no.nav.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
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
@Table(name = "T_KODEVERK_FIELDS")
public class Kode {

    @Id
    @GeneratedValue(generator = "kodeverkFieldsIdGenerator")
    @GenericGenerator(name = "kodeverkFieldsIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
            @Parameter(name = "sequence_name", value = "T_KODEVERK_FIELDS_SEQ"),
            @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1")
    })
    private Long id;

    @Column(nullable = false, unique = true)
    private String navn;

    @Column(nullable = false, unique = true)
    private String term;

}
