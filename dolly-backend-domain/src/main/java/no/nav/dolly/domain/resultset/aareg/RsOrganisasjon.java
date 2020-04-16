package no.nav.dolly.domain.resultset.aareg;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsOrganisasjon extends RsAktoer {

    @ApiModelProperty(
            value = "Organisasjonsnummer, må finnes i EREG",
            required = true,
            position = 1
    )
    private String orgnummer;

    @Override
    public String getAktoertype() {
        return "ORG";
    }
}
