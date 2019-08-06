package no.nav.registre.orkestratoren.consumer.rs.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstitusjonsoppholdResponse {

    private String personident;
    private HttpStatus status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object institusjonsopphold;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String feilmelding;
}
