package no.nav.registre.hodejegeren.mongodb.tpsStatusQuo.Kjerneinfo;

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
public class Kjerneinformasjon {

    private String statsborgerskap;
    private String kjonn;
    @JsonIgnore
    private Object fodselsnummerDetalj;
    @JsonIgnore
    private Object bruker;
    private String sivilstand;
    @JsonIgnore
    private Object personstatusDetalj;
    private String fodselsdato;
    private String datoDo;
    @JsonIgnore
    private Object bankkontoNorge;
    @JsonIgnore
    private Object postAdresse;
    @JsonIgnore
    private Object fodestedDetalj;
    private String identType;
    private BostedsAdresse bostedsAdresse;
    @JsonIgnore
    private String fodselsnummer;
    private StatsborgerskapDetalj statsborgerskapDetalj;
    @JsonIgnore
    private Object datoDoDetalj;
    @JsonIgnore
    private String fodested;
    private Personnavn personnavn;
    @JsonIgnore
    private Object sivilstandDetalj;
}
