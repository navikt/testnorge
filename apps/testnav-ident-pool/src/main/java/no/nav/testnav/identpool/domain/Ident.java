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
@Table(name = "personidentifikator")
public class Ident {

    @Id
    @Column("id")
    @JsonIgnore
    private Long identity;

    @NotNull
    @Column("identtype")
    private Identtype identtype;

    @NotNull
    @Column("syntetisk")
    private Boolean syntetisk;

    @NotNull
    @Column("personidentifikator")
    private String personidentifikator;

    @NotNull
    @Column("rekvireringsstatus")
    private Rekvireringsstatus rekvireringsstatus;

    @NotNull
    @Column("foedselsdato")
    private LocalDate foedselsdato;

    @NotNull
    @Column("kjoenn")
    private Kjoenn kjoenn;

    @Column("rekvirert_av")
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
