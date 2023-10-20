package no.nav.brregstub.database.domene;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import static no.nav.brregstub.jpa.HibernateConstants.SEQUENCE_STYLE_GENERATOR;

@Entity
@Getter
@Setter
@Table(name = "hent_rolle")
public class HentRolle {

    @Id
    @GeneratedValue(generator = "hentRolleIdGenerator")
    @GenericGenerator(name = "hentRolleIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
            @org.hibernate.annotations.Parameter(name = "sequence_name", value = "HENTROLLE_SEQ"),
            @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
            @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
    })
    private Long id;

    @NotNull(message = "orgnr must not be null")
    @Column(unique = true)
    private Integer orgnr;

    @NotNull(message = "json must not be null")
    @Column(name = "json", columnDefinition = "text")
    private String json;

}
