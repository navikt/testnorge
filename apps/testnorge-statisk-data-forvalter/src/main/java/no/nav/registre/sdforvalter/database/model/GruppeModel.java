package no.nav.registre.sdforvalter.database.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

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