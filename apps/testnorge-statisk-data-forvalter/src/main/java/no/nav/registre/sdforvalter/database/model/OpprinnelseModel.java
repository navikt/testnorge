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

import no.nav.registre.sdforvalter.domain.Opprinnelse;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "opprinnelse")
@EqualsAndHashCode(callSuper = false)
public class OpprinnelseModel extends AuditModel {

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Exclude
    private Long id;
    @NotNull
    @Column(unique = true)
    private String navn;

    public OpprinnelseModel(String navn) {
        this.navn = navn;
    }

    public OpprinnelseModel(Opprinnelse opprinnelse) {
        this(opprinnelse.getNavn());
    }
}
