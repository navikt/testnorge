package no.nav.identpool.domain;

import static no.nav.identpool.domain.Rekvireringsstatus.I_BRUK;
import static no.nav.identpool.domain.Rekvireringsstatus.LEDIG;

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
import org.hibernate.annotations.Type;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Column(name = "PERSONIDENTIFIKATOR")
    private String personidentifikator;

    @NotNull
    @Column(name = "REKVIRERINGSSTATUS")
    @Enumerated(EnumType.STRING)
    private Rekvireringsstatus rekvireringsstatus;

    @NotNull
    @Type(type = "true_false")
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
        return LEDIG == getRekvireringsstatus();
    }

    @JsonIgnore
    public boolean isIBruk() {
        return I_BRUK == getRekvireringsstatus();
    }
}
