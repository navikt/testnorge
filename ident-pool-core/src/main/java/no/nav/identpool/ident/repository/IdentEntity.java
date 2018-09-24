package no.nav.identpool.ident.repository;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.identpool.ident.domain.Identtype;
import no.nav.identpool.ident.domain.Rekvireringsstatus;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PERSONIDENTIFIKATOR")
public class IdentEntity {
    @Id
    @Column(name = "ID")
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "personidentifikator_seq")
    @SequenceGenerator(name = "personidentifikator_seq", sequenceName = "PERSONIDENTIFIKATOR_SEQ", allocationSize = 1)
    private Long identity;

    @NotNull
    @Column(name = "IDENTTYPE")
    @Enumerated(EnumType.STRING)
    private Identtype identtype;

    @NotNull
    @Column(name = "PERSONIDENTIFIKATOR")
    private String personidentifikator;

    @NotNull
    @Column(name = "REKVIRERINGSSTATUS")
    @Enumerated(EnumType.STRING)
    private Rekvireringsstatus rekvireringsstatus;

    @NotNull
    @Column(name = "FINNES_HOS_SKATT")
    private String finnesHosSkatt;

    @NotNull
    @Column(name = "FOEDSELSDATO")
    private LocalDate foedselsdato;

    @Column(name = "REKVIRERT_AV")
    private String rekvirertAv;
}
