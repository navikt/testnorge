package no.nav.dolly.bestilling.pdldata.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PdlResponse {

    private static final String FINNES_IKKE = "finnes ikke";

    private String ident;
    private String jsonNode;
    private HttpStatus status;
    private String feilmelding;

    @JsonIgnore
    public boolean isFinnesIkke() {

        return isNotBlank(feilmelding) && feilmelding.contains(FINNES_IKKE);
    }
}