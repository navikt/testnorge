package no.nav.registre.hodejegeren.mongodb.tpsStatusQuo.Personrelasjon;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Relasjonsinformasjon {

    private Integer antallRelasjoner;
    private String endringsDato;
    private String adresse;
    @JsonIgnore
    private String fnr;
    private String spesregType;
    private String typeAdresse;
    private String typeAdrBeskr;
    private String sivilstand;
    private Relasjoner relasjoner;
    private String sivilstandBeskr;
}
