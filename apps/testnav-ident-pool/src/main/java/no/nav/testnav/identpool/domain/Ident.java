package no.nav.testnav.identpool.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "PERSONIDENTIFIKATOR")
public class Ident {

    @Id
    @Column("ID")
    @JsonIgnore
    private Long identity;

    @NotNull
    @Column("IDENTTYPE")
    private Identtype identtype;

    @NotNull
    @Column("SYNTETISK")
    private Boolean syntetisk;

    @NotNull
    @Column("PERSONIDENTIFIKATOR")
    private String personidentifikator;

    @NotNull
    @Column("REKVIRERINGSSTATUS")
    private Rekvireringsstatus rekvireringsstatus;

    @NotNull
    @Column("FOEDSELSDATO")
    private LocalDate foedselsdato;

    @NotNull
    @Column("KJOENN")
    private Kjoenn kjoenn;

    @Column("REKVIRERT_AV")
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
