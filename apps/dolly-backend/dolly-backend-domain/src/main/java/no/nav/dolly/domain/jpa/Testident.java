package no.nav.dolly.domain.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TEST_IDENT")
public class Testident {

    public enum Master {PDL, TPSF}

    @Id
    private String ident;

    @Column (name = "IBRUK")
    private Boolean iBruk;

    @Column (name = "BESKRIVELSE")
    private String beskrivelse;

    @ManyToOne
    @JoinColumn(name = "TILHOERER_GRUPPE", nullable = false)
    private Testgruppe testgruppe;

    @Column(name = "MASTER")
    @Enumerated(EnumType.STRING)
    private Master master;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDENT", referencedColumnName = "ident", insertable = false, updatable = false)
    private List<BestillingProgress> bestillingProgress;

    public List<BestillingProgress> getBestillingProgress() {
        if (bestillingProgress == null) {
            bestillingProgress = new ArrayList<>();
        }
        return bestillingProgress;
    }

    @JsonIgnore
    public boolean isTpsf() {
        return getMaster() == Master.TPSF;
    }

    @JsonIgnore
    public boolean isPdl() {
        return getMaster() == Master.PDL;
    }
}