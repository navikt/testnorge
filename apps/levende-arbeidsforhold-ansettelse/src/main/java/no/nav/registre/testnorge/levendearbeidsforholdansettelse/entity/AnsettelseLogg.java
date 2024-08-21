package no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ansettelse_logg")
public class AnsettelseLogg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "organisasjonsnummer", nullable = false)
    private String organisasjonsnummer;

    @Size(max = 255)
    @NotNull
    @Column(name = "folkeregisterident", nullable = false)
    private String folkeregisterident;

    @NotNull
    @Column(name = "timestamp", nullable = false)
    private OffsetDateTime timestamp;

    @NotNull
    @Column(name = "ansattfra", nullable = false)
    private LocalDate ansattfra;

    @Size(max = 255)
    @Column(name = "arbeidsforhold_type")
    private String arbeidsforholdType;

    @Column(name = "stillingsprosent")
    private BigDecimal stillingsprosent;

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
}