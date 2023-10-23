package no.nav.registre.sdforvalter.database.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "gruppe")
@EqualsAndHashCode(callSuper = false)
public class GruppeModel extends AuditModel {

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Exclude
    private Long id;
    @NotNull
    @Column(unique = true)
    private String kode;
    @NotNull
    private String beskrivelse;
}