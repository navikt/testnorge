package no.nav.registre.orkestratoren.consumer.rs.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SletteArenaResponse {
    @JsonProperty("slettet")
    private List<String> slettet;
    @JsonProperty("ikkeSlettet")
    private List<String> ikkeSlettet;
}
