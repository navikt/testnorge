package no.nav.brregstub.database.domene;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "rolleoversikt")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rolleoversikt {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rolleoversikt_seq")
    @SequenceGenerator(name = "rolleoversikt_seq", sequenceName = "ROLLEOVERSIKT_SEQ", allocationSize = 1)
    private Long id;

    @NotNull(message = "fnr must not be null")
    @Column(unique = true)
    private String ident;

    @NotNull(message = "json must not be null")
    @Column(name = "json", columnDefinition = "text")
    private String json;

}
