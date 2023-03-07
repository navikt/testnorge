package no.nav.testnav.identpool.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "PERSONIDENTIFIKATOR")
public class Ident {
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
    @Column(name = "SYNTETISK")
    private Boolean syntetisk;

    @NotNull
    @Column(name = "PERSONIDENTIFIKATOR")
    private String personidentifikator;

    @NotNull
    @Column(name = "REKVIRERINGSSTATUS")
    @Enumerated(EnumType.STRING)
    private Rekvireringsstatus rekvireringsstatus;

    @NotNull
    @Column(name = "FINNES_HOS_SKATT")
    private boolean finnesHosSkatt;

    @NotNull
    @Column(name = "FOEDSELSDATO")
    private LocalDate foedselsdato;

    @NotNull
    @Column(name = "KJOENN")
    @Enumerated(EnumType.STRING)
    private Kjoenn kjoenn;

    @Column(name = "REKVIRERT_AV")
    private String rekvirertAv;

    @JsonIgnore
    public boolean isLedig() {
        return Rekvireringsstatus.LEDIG == getRekvireringsstatus();
    }

    @JsonIgnore
    public boolean isIBruk() {
        return Rekvireringsstatus.I_BRUK == getRekvireringsstatus();
    }

    public boolean isSyntetisk() {
        return getPersonidentifikator().charAt(2) > '3';
    }
}
