package no.nav.registre.aaregstub.arbeidsforhold.consumer.rs.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DollyResponse {

    @JsonIgnore
    private HttpStatus httpStatus;

    @JsonProperty("statusPerMiljoe")
    private Map<String, String> statusPerMiljoe;
}
