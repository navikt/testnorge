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
public class GenererArenaResponse {

    @JsonProperty("registrerteIdenter")
    private List<String> registrerteIdenter;
}
