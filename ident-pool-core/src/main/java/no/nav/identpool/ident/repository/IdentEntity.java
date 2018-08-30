package no.nav.identpool.ident.repository;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import no.nav.identpool.ident.domain.Identtype;
import no.nav.identpool.ident.domain.Rekvireringsstatus;

@Data
@Entity
@AllArgsConstructor
@Table(name = "PERSONIDENTIFIKATOR")
public class IdentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "personidentifikator_seq")
    @SequenceGenerator(name = "personidentifikator_seq", sequenceName = "PERSONIDENTIFIKATOR_SEQ", allocationSize = 1)
    private Long identity;

    @NotNull
    @Column(name = "IDENTTYPE")
    private Identtype identtype;

    @NotNull
    @Column(name = "PERSONIDENTIFIKATOR")
    private String personidentifikator;

    @NotNull
    @Column(name = "Rekvireringsstatus")
    private Rekvireringsstatus rekvireringsstatus;

    @NotNull
    @Column(name = "FINNES_HOS_SKATT")
    private String finnesHosSkatt;
}
