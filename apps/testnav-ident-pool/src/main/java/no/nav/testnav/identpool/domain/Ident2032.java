package no.nav.testnav.identpool.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "personidentifikator2032")
public class Ident2032 {

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Column("identtype")
    private Identtype identtype;

    @NotNull
    @Column("personidentifikator")
    private String personidentifikator;

    @NotNull
    @Column("dato_identifikator")
    private String datoIdentifikator;

    @NotNull
    @Column("individnummer")
    private Integer individnummer;

    @Column("foedselsdato")
    private LocalDate foedselsdato;

    @Column("allokert")
    private Boolean allokert;

    @Column("dato_allokert")
    private LocalDate datoAllokert;

    @Version
    @Column("versjon")
    private Integer versjon;

    @JsonIgnore
    public boolean isAllokert() {
        return BooleanUtils.isTrue(allokert);
    }

    @JsonIgnore
    public boolean isLedig() {
        return !isAllokert();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Ident2032 that = (Ident2032) o;
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