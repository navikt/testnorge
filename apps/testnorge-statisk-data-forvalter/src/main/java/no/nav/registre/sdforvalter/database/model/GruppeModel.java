package no.nav.registre.sdforvalter.database.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

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