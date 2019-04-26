package no.nav.registre.aareg.consumer.rs.responses.contents;

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
public class Arbeidstaker {

    @JsonProperty("aktoertype")
    private String aktoertype;

    @JsonProperty("ident")
    private String ident;

    @JsonProperty("identtype")
    private String identtype;
}
