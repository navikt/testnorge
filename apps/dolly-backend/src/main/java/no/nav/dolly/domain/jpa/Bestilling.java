package no.nav.dolly.domain.jpa;

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

    @Column(name = "MAL_BESTILLING_NAVN")
    private String malBestillingNavn;

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

    @OneToMany(mappedBy = "bestilling", fetch = FetchType.LAZY)
    @Builder.Default
    private List<BestillingProgress> progresser = new ArrayList<>();

    @OneToMany(mappedBy = "bestillingId", fetch = FetchType.LAZY)
    @Builder.Default
    private List<BestillingKontroll> kontroller = new ArrayList<>();

    @OneToMany(mappedBy = "bestillingId", fetch = FetchType.LAZY)
    @Builder.Default
    private List<TransaksjonMapping> transaksjonmapping = new ArrayList<>();

    @Transient
    private String beskrivelse;
}