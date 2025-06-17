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
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("BESTILLING")
public class Bestilling implements Serializable {

    @Id
    private Long id;

    @Version
    @Column("VERSJON")
    private Long versjon;

//    @ManyToOne
//    @JoinColumn("GRUPPE_ID", nullable = false)
    @Transient
    private Testgruppe gruppe;

    @Column("GRUPPE_ID")
    private Long gruppeId;

    @Column("FERDIG")
    private boolean ferdig;

    @Column("MILJOER")
    private String miljoer;

    @Column("ANTALL_IDENTER")
    private Integer antallIdenter;

    @Column("SIST_OPPDATERT")
//    @UpdateTimestamp
    private LocalDateTime sistOppdatert;

    @Column("STOPPET")
    private boolean stoppet;

    @Column("FEIL")
    private String feil;

    @Column("OPPRETTET_FRA_ID")
    private Long opprettetFraId;

    @Column("BEST_KRITERIER")
    private String bestKriterier;

    @Column("OPPRETT_FRA_IDENTER")
    private String opprettFraIdenter;

    @Column("IDENT")
    private String ident;

    @Column("OPPRETT_FRA_GRUPPE")
    private Long opprettetFraGruppeId;

    @Column("GJENOPPRETTET_FRA_IDENT")
    private String gjenopprettetFraIdent;

//    @ManyToOne
//    @JoinColumn("BRUKER_ID")
    @Transient
    private Bruker bruker;

    @Column("BRUKER_ID")
    private Long brukerId;

    @Column("PDL_IMPORT")
    private String pdlImport;

    @Column("KILDE_MILJOE")
    private String kildeMiljoe;

    @Column("NAV_SYNTETISK_IDENT")
    private Boolean navSyntetiskIdent;

//    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinColumn("bestilling_id", updatable = false)
    @Transient
    private List<BestillingProgress> progresser;

//    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinColumn("bestilling_id", updatable = false)
//    @Builder.Default
//    private List<BestillingKontroll> kontroller = new ArrayList<>();

//    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinColumn("bestilling_id", updatable = false)
//    @Builder.Default
//    private List<TransaksjonMapping> transaksjonmapping = new ArrayList<>();

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
                .append(gruppe, that.gruppe)
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
                .append(gruppe)
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
                ", gruppe=" + gruppe +
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