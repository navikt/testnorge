package no.nav.brregstub.database.domene;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "rolleoversikt")
public class Rolleoversikt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @GeneratedValue(generator = "rolleOversiktIdGenerator")
//    @GenericGenerator(name = "rolleOversiktIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
//            @org.hibernate.annotations.Parameter(name = "sequence_name", value = "ROLLEOVERSIKT_SEQ"),
//            @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
//            @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
//    })
    private Long id;

    @NotNull(message = "fnr must not be null")
    @Column(unique = true)
    private String ident;

    @NotNull(message = "json must not be null")
    @Column(name = "json", columnDefinition = "text")
    private String json;

}
