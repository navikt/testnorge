package no.nav.dolly.domain.jpa;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "T_TEST_IDENT")
public class Testident {

    @Id
    private String ident;

    @ManyToOne
    @JoinColumn(name = "TILHOERER_GRUPPE", nullable = false)
    private Testgruppe testgruppe;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDENT", referencedColumnName = "ident", insertable = false, updatable = false)
    private List<BestillingProgress> bestillingProgress;
}