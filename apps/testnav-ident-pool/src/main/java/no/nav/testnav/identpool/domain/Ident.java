package no.nav.testnav.identpool.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Ident ident = (Ident) o;
        return Objects.equals(identity, ident.identity) && identtype == ident.identtype && Objects.equals(syntetisk, ident.syntetisk) && Objects.equals(personidentifikator, ident.personidentifikator) && rekvireringsstatus == ident.rekvireringsstatus && Objects.equals(foedselsdato, ident.foedselsdato) && kjoenn == ident.kjoenn && Objects.equals(rekvirertAv, ident.rekvirertAv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identity, identtype, syntetisk, personidentifikator, rekvireringsstatus, foedselsdato, kjoenn, rekvirertAv);
    }

    @Override
    public String toString() {
        return "Ident{" +
                "identity=" + identity +
                ", identtype=" + identtype +
                ", syntetisk=" + syntetisk +
                ", personidentifikator='" + personidentifikator + '\'' +
                ", rekvireringsstatus=" + rekvireringsstatus +
                ", foedselsdato=" + foedselsdato +
                ", kjoenn=" + kjoenn +
                ", rekvirertAv='" + rekvirertAv + '\'' +
                '}';
    }
}
