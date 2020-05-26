package no.nav.registre.elsam.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class Legekontor {

    private String navn;
    @JsonProperty("ereg_id")
    private String eregId;
    @JsonProperty("her_id")
    private String herId;
    private Adresse adresse;
}
