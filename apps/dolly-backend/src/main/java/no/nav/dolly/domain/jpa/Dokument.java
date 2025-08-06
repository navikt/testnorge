package no.nav.dolly.domain.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("DOKUMENT")
public class Dokument implements Serializable {

    @Id
    private Long id;

    @Version
    @Column("VERSJON")
    private Long versjon;

    @Column("BESTILLING_ID")
    private Long bestillingId;

    @Column("DOKUMENT_TYPE")
    private DokumentType dokumentType;

    @Column("SIST_OPPDATERT")
    private LocalDateTime sistOppdatert;

    @Column("CONTENTS")
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
        MAL_BESTILLING_HISTARK
    }
}