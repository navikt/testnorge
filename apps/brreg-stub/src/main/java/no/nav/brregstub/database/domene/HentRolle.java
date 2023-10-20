package no.nav.brregstub.database.domene;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "hent_rolle")
public class HentRolle {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hentrolle_seq")
    @SequenceGenerator(name = "hentrolle_seq", sequenceName = "HENTROLLE_SEQ", allocationSize = 1)
    private Long id;

    @NotNull(message = "orgnr must not be null")
    @Column(unique = true)
    private Integer orgnr;

    @NotNull(message = "json must not be null")
    @Column(name = "json", columnDefinition = "text")
    private String json;

}
