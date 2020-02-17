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

import no.nav.registre.sdForvalter.domain.Kilde;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "kilde")
@EqualsAndHashCode(callSuper = false)
public class KildeModel extends AuditModel {

    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    @Column(unique = true)
    private String navn;

    public KildeModel(String navn) {
        this.navn = navn;
    }

    public KildeModel(Kilde kilde) {
        this(kilde.getNavn());
    }
}
