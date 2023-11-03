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
    private Long id;

    @NotNull(message = "fnr must not be null")
    @Column(unique = true)
    private String ident;

    @NotNull(message = "json must not be null")
    @Column(name = "json", columnDefinition = "text")
    private String json;

}
