package no.nav.registre.elsam.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Lege {

    private String fnr;

    private String fornavn;
    private String mellomnavn;
    private String etternavn;

    @JsonProperty("herId")
    @JsonAlias("her_id")
    private String herId;

    @JsonProperty("hprId")
    @JsonAlias("hpr_id")
    private String hprId;

    private String epost;
    private String tlf;
    private Legekontor legekontor;

    private Adresse adresse;
    private String telefon;
}
