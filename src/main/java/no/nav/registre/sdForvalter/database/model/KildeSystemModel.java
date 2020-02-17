package no.nav.registre.sdForvalter.database.model;


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

import no.nav.registre.sdForvalter.domain.KildeSystem;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "kilde_system")
@EqualsAndHashCode(callSuper = false)
public class KildeSystemModel extends AuditModel {

    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    @Column(unique = true)
    private String navn;

    public KildeSystemModel(String navn) {
        this.navn = navn;
    }

    public KildeSystemModel(KildeSystem kildeSystem) {
        this(kildeSystem.getNavn());
    }
}
