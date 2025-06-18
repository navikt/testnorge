package no.nav.dolly.domain.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("TEST_IDENT")
public class Testident implements Serializable {

    @Id
    @Column("ID")
    private Long id;

    @Version
    @Column("VERSJON")
    private Long versjon;

    @Column("IDENT")
    private String ident;

    @Column("IBRUK")
    private Boolean iBruk;

    @Column("BESKRIVELSE")
    private String beskrivelse;

//    @ManyToOne
//    @JoinColumn("TILHOERER_GRUPPE", nullable = false)
//    private Testgruppe testgruppe;
    @Column("TILHOERER_GRUPPE")
    private Long gruppeId;

    @Column("MASTER")
//    @Enumerated(EnumType.STRING)
    private Master master;

//    @OneToMany(fetch = FetchType.LAZY)
    @Builder.Default
//    @JoinColumn("IDENT", referencedColumn"ident", insertable = false, updatable = false)
    @Transient
    private List<BestillingProgress> bestillingProgress = new ArrayList<>();

    @JsonIgnore
    public boolean isPdl() {
        return getMaster() == Master.PDL;
    }

    @JsonIgnore
    public boolean isPdlf() {
        return getMaster() == Master.PDLF;
    }

    public enum Master {PDL, PDLF}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Testident testident = (Testident) o;

        return new EqualsBuilder()
                .append(id, testident.id)
                .append(versjon, testident.versjon)
                .append(ident, testident.ident)
                .append(iBruk, testident.iBruk)
                .append(beskrivelse, testident.beskrivelse)
                .append(gruppeId, testident.gruppeId)
                .append(master, testident.master)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(versjon)
                .append(ident)
                .append(iBruk)
                .append(beskrivelse)
                .append(gruppeId)
                .append(master)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "Testident{" +
                "id=" + id +
                ", versjon=" + versjon +
                ", ident='" + ident + '\'' +
                ", iBruk=" + iBruk +
                ", beskrivelse='" + beskrivelse + '\'' +
                ", testgruppe=" + gruppeId +
                ", master=" + master +
                '}';
    }
}