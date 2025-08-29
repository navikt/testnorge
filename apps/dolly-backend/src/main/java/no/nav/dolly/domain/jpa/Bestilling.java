package no.nav.dolly.domain.jpa;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BESTILLING")
public class Bestilling implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(name = "VERSJON")
    private Long versjon;

    @ManyToOne
    @JoinColumn(name = "GRUPPE_ID", nullable = false)
    private Testgruppe gruppe;

    @Column(name = "FERDIG", nullable = false)
    private boolean ferdig;

    @Column(name = "MILJOER", nullable = true)
    private String miljoer;

    @Column(name = "ANTALL_IDENTER", nullable = false)
    private Integer antallIdenter;

    @Column(name = "SIST_OPPDATERT", nullable = false)
    @UpdateTimestamp
    private LocalDateTime sistOppdatert;

    @Column(name = "STOPPET")
    private boolean stoppet;

    @Column(name = "FEIL")
    private String feil;

    @Column(name = "OPPRETTET_FRA_ID")
    private Long opprettetFraId;

    @Column(name = "BEST_KRITERIER")
    private String bestKriterier;

    @Column(name = "OPPRETT_FRA_IDENTER")
    private String opprettFraIdenter;

    @Column(name = "IDENT")
    private String ident;

    @Column(name = "OPPRETT_FRA_GRUPPE")
    private Long opprettetFraGruppeId;

    @Column(name = "GJENOPPRETTET_FRA_IDENT")
    private String gjenopprettetFraIdent;

    @ManyToOne
    @JoinColumn(name = "BRUKER_ID")
    private Bruker bruker;

    @Column(name = "PDL_IMPORT")
    private String pdlImport;

    @Column(name = "KILDE_MILJOE")
    private String kildeMiljoe;

    @Column(name = "NAV_SYNTETISK_IDENT")
    private Boolean navSyntetiskIdent;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "bestilling_id", updatable = false)
    @Builder.Default
    private List<BestillingProgress> progresser = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "bestilling_id", updatable = false)
    @Builder.Default
    private List<BestillingKontroll> kontroller = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "bestilling_id", updatable = false)
    @Builder.Default
    private List<TransaksjonMapping> transaksjonmapping = new ArrayList<>();

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