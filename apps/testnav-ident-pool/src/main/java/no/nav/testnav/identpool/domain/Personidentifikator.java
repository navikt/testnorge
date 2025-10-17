package no.nav.testnav.identpool.domain;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "PERSONIDENTIFIKATOR2032")
public class Personidentifikator {

    @Id
    @Column("ID")
    private Long id;

    @NotNull
    @Column("IDENTTYPE")
    private Identtype identtype;

    @NotNull
    @Column("PERSONIDENTIFIKATOR")
    private String personidentifikator;

    @NotNull
    @Column("DATO_IDENTIFIKATOR")
    private String datoIdentifikator;

    @NotNull
    @Column("INDIVIDNUMMER")
    private Integer individnummer;

    @Column("FOEDSELSDATO")
    private LocalDate foedselsdato;

    @Column("ALLOKERT")
    private Boolean allokert;

    @Column("DATO_ALLOKERT")
    private LocalDate datoAllokert;

    @Version
    @Column("VERSJON")
    private Integer versjon;

    public boolean isAllokert() {
        return BooleanUtils.isTrue(allokert);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Personidentifikator that = (Personidentifikator) o;
        return Objects.equals(id, that.id) && identtype == that.identtype && Objects.equals(personidentifikator, that.personidentifikator) && Objects.equals(datoIdentifikator, that.datoIdentifikator) && Objects.equals(individnummer, that.individnummer) && Objects.equals(foedselsdato, that.foedselsdato) && Objects.equals(allokert, that.allokert) && Objects.equals(datoAllokert, that.datoAllokert);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, identtype, personidentifikator, datoIdentifikator, individnummer, foedselsdato, allokert, datoAllokert);
    }

    @Override
    public String toString() {
        return "Personidentifikator{" +
                "id=" + id +
                ", identtype=" + identtype +
                ", personidentifikator='" + personidentifikator + '\'' +
                ", datoIdentifikator='" + datoIdentifikator + '\'' +
                ", individnummer=" + individnummer +
                ", foedselsdato=" + foedselsdato +
                ", allokert=" + allokert +
                ", datoAllokert=" + datoAllokert +
                '}';
    }
}