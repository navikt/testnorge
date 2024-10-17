package no.nav.testnav.levendearbeidsforholdansettelse.entity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ansettelse_logg")
public class AnsettelseLogg implements Persistable<Integer> {

    @Transient
    private boolean isNew;

    @Id
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column("organisasjonsnummer")
    private String organisasjonsnummer;

    @Size(max = 255)
    @NotNull
    @Column("folkeregisterident")
    private String folkeregisterident;

    @NotNull
    @Column("timestamp")
    private OffsetDateTime timestamp;

    @NotNull
    @Column("ansattfra")
    private LocalDate ansattfra;

    @Size(max = 255)
    @Column("arbeidsforhold_type")
    private String arbeidsforholdType;

    @Column("stillingsprosent")
    private Integer stillingsprosent;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof AnsettelseLogg that)) return false;

        return new EqualsBuilder().append(getId(), that.getId()).append(getOrganisasjonsnummer(), that.getOrganisasjonsnummer()).append(getFolkeregisterident(), that.getFolkeregisterident()).append(getTimestamp(), that.getTimestamp()).append(getAnsattfra(), that.getAnsattfra()).append(getArbeidsforholdType(), that.getArbeidsforholdType()).append(getStillingsprosent(), that.getStillingsprosent()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getId()).append(getOrganisasjonsnummer()).append(getFolkeregisterident()).append(getTimestamp()).append(getAnsattfra()).append(getArbeidsforholdType()).append(getStillingsprosent()).toHashCode();
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}