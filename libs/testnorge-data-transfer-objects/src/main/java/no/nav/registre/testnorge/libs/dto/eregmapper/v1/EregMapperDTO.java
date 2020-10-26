package no.nav.registre.testnorge.libs.dto.eregmapper.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Value;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class EregMapperDTO {
    @JsonProperty(required = true)
    private String orgnr;
    private NavnDTO navn;
    @JsonProperty(required = true)
    private String enhetstype;
    private String endringsType;
    private String epost;
    private String internetAdresse;
    private AdresseDTO forretningsAdresse;
    private AdresseDTO adresse;
    private List<Map<String, String>> knytninger;
}
