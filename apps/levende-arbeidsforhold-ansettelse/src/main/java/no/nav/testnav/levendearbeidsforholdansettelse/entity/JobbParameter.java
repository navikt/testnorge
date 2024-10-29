package no.nav.testnav.levendearbeidsforholdansettelse.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "jobb_parameter")
public class JobbParameter implements Persistable<String> {

    @Transient
    private boolean isNew;

    @Id
    @Size(max = 255)
    @Column("navn")
    private String navn;

    @Size(max = 255)
    @NotNull
    @Column("tekst")
    private String tekst;

    @Size(max = 255)
    @Column("verdi")
    private String verdi;

    @Column("verdier")
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

    @Override
    public String getId() {
        return navn;
    }

    @Override
    @JsonIgnore
    public boolean isNew() {
        return isNew;
    }
}