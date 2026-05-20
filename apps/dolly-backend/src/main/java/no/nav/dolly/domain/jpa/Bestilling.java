package no.nav.dolly.domain.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("bestilling")
public class Bestilling implements Serializable {

    @Id
    private Long id;

    @Version
    @Column("versjon")
    private Long versjon;

    @Column("gruppe_id")
    private Long gruppeId;

    @Column("ferdig")
    private boolean ferdig;

    @Column("miljoer")
    private String miljoer;

    @Column("antall_identer")
    private Integer antallIdenter;

    @Column("sist_oppdatert")
    private LocalDateTime sistOppdatert;

    @Column("stoppet")
    private boolean stoppet;

    @Column("feil")
    private String feil;

    @Column("opprettet_fra_id")
    private Long opprettetFraId;

    @Column("best_kriterier")
    private String bestKriterier;

    @Column("opprett_fra_identer")
    private String opprettFraIdenter;

    @Column("ident")
    private String ident;

    @Column("opprett_fra_gruppe")
    private Long opprettetFraGruppeId;

    @Column("gjenopprettet_fra_ident")
    private String gjenopprettetFraIdent;

    @Transient
    private Bruker bruker;

    @Column("bruker_id")
    private Long brukerId;

    @Column("pdl_import")
    private String pdlImport;

    @Column("kilde_miljoe")
    private String kildeMiljoe;

    @Column("nav_syntetisk_ident")
    private Boolean navSyntetiskIdent;

    @Transient
    private List<BestillingProgress> progresser;

    @Transient
    private String beskrivelse;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Bestilling that = (Bestilling) o;

        return new EqualsBuilder()
                .append(ferdig, that.ferdig)
                .append(stoppet, that.stoppet)
                .append(id, that.id)
                .append(versjon, that.versjon)
                .append(gruppeId, that.gruppeId)
                .append(miljoer, that.miljoer)
                .append(antallIdenter, that.antallIdenter)
                .append(sistOppdatert, that.sistOppdatert)
                .append(feil, that.feil)
                .append(opprettetFraId, that.opprettetFraId)
                .append(bestKriterier, that.bestKriterier)
                .append(opprettFraIdenter, that.opprettFraIdenter)
                .append(ident, that.ident)
                .append(opprettetFraGruppeId, that.opprettetFraGruppeId)
                .append(gjenopprettetFraIdent, that.gjenopprettetFraIdent)
                .append(bruker, that.bruker)
                .append(pdlImport, that.pdlImport)
                .append(kildeMiljoe, that.kildeMiljoe)
                .append(navSyntetiskIdent, that.navSyntetiskIdent)
                .append(beskrivelse, that.beskrivelse)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(versjon)
                .append(gruppeId)
                .append(ferdig)
                .append(miljoer)
                .append(antallIdenter)
                .append(sistOppdatert)
                .append(stoppet)
                .append(feil)
                .append(opprettetFraId)
                .append(bestKriterier)
                .append(opprettFraIdenter)
                .append(ident)
                .append(opprettetFraGruppeId)
                .append(gjenopprettetFraIdent)
                .append(bruker)
                .append(pdlImport)
                .append(kildeMiljoe)
                .append(navSyntetiskIdent)
                .append(beskrivelse)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "Bestilling{" +
                "id=" + id +
                ", versjon=" + versjon +
                ", gruppeId=" + gruppeId +
                ", ferdig=" + ferdig +
                ", miljoer='" + miljoer + '\'' +
                ", antallIdenter=" + antallIdenter +
                ", sistOppdatert=" + sistOppdatert +
                ", stoppet=" + stoppet +
                ", feil='" + feil + '\'' +
                ", opprettetFraId=" + opprettetFraId +
                ", bestKriterier='" + bestKriterier + '\'' +
                ", opprettFraIdenter='" + opprettFraIdenter + '\'' +
                ", ident='" + ident + '\'' +
                ", opprettetFraGruppeId=" + opprettetFraGruppeId +
                ", gjenopprettetFraIdent='" + gjenopprettetFraIdent + '\'' +
                ", bruker=" + bruker +
                ", pdlImport='" + pdlImport + '\'' +
                ", kildeMiljoe='" + kildeMiljoe + '\'' +
                ", navSyntetiskIdent=" + navSyntetiskIdent +
                ", beskrivelse='" + beskrivelse + '\'' +
                '}';
    }
}