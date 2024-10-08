package no.nav.testnav.levendearbeidsforholdansettelse.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Data
@Entity
@Table(name = "jobb_parameter")
public class JobbParameter {

    @Id
    @Size(max = 255)
    @Column(name = "navn", nullable = false)
    private String navn;

    @Size(max = 255)
    @NotNull
    @Column(name = "tekst", nullable = false)
    private String tekst;

    @Size(max = 255)
    @Column(name = "verdi")
    private String verdi;

    @Column(name = "verdier")
    private String verdier;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof JobbParameter that)) return false;

        return new EqualsBuilder().append(getNavn(), that.getNavn()).append(getTekst(), that.getTekst()).append(getVerdi(), that.getVerdi()).append(getVerdier(), that.getVerdier()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getNavn()).append(getTekst()).append(getVerdi()).append(getVerdier()).toHashCode();
    }
}