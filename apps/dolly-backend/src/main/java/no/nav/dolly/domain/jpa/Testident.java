package no.nav.dolly.domain.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static no.nav.dolly.domain.jpa.HibernateConstants.SEQUENCE_STYLE_GENERATOR;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TEST_IDENT")
public class Testident implements Serializable {

    @Id
    @GeneratedValue(generator = "gruppeIdGenerator")
    @GenericGenerator(name = "gruppeIdGenerator", strategy = SEQUENCE_STYLE_GENERATOR, parameters = {
            @Parameter(name = "sequence_name", value = "TEST_IDENT_SEQ"),
            @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1")
    })
    @Column(name = "ID")
    private Long id;

    @Version
    @Column(name = "VERSJON")
    private Long versjon;

    @Column(name = "IDENT")
    private String ident;

    @Column(name = "IBRUK")
    private Boolean iBruk;

    @Column(name = "BESKRIVELSE")
    private String beskrivelse;

    @ManyToOne
    @JoinColumn(name = "TILHOERER_GRUPPE", nullable = false)
    private Testgruppe testgruppe;

    @Column(name = "MASTER")
    @Enumerated(EnumType.STRING)
    private Master master;

    @OneToMany(fetch = FetchType.LAZY)
    @Builder.Default
    @JoinColumn(name = "IDENT", referencedColumnName = "ident", insertable = false, updatable = false)
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
                .append(testgruppe, testident.testgruppe)
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
                .append(testgruppe)
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
                ", testgruppe=" + testgruppe.getId() +
                ", master=" + master +
                '}';
    }
}