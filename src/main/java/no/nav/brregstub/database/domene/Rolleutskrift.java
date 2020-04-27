package no.nav.brregstub.database.domene;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Table(name = "rolleutskrift")
public class Rolleutskrift {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull(message = "fnr must not be null")
    @Column(unique = true)
    private String ident;

    @NotNull(message = "json must not be null")
    @Column(name = "json", columnDefinition = "text")
    private String json;

}
