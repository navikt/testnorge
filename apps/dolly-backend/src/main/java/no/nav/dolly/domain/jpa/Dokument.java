package no.nav.dolly.domain.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "DOKUMENT")
public class Dokument implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(name = "VERSJON")
    private Long versjon;

    @Column(name = "BESTILLING_ID")
    private Long bestillingId;

    @Column(name = "DOKUMENT_TYPE")
    @Enumerated(value = EnumType.STRING)
    private DokumentType dokumentType;

    @Column(name = "SIST_OPPDATERT")
    @UpdateTimestamp
    private LocalDateTime sistOppdatert;

    @Column(name = "CONTENTS")
    private String contents;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Dokument dokument = (Dokument) o;

        return new EqualsBuilder()
                .append(id, dokument.id)
                .append(versjon, dokument.versjon)
                .append(bestillingId, dokument.bestillingId)
                .append(dokumentType, dokument.dokumentType)
                .append(sistOppdatert, dokument.sistOppdatert)
                .append(contents, dokument.contents)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(versjon)
                .append(bestillingId)
                .append(dokumentType)
                .append(sistOppdatert)
                .append(contents)
                .toHashCode();
    }

    public enum DokumentType {
        BESTILLING_DOKARKIV,
        BESTILLING_HISTARK,
        MAL_BESTILLING_DOKARKIV,
        MAL_BESTILLING_HISTARK,
        TEST_AUTH
    }
}