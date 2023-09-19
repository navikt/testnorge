package no.nav.testnav.libs.dto.tpsmessagingservice.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PersonMiljoeDTO {

    private String miljoe;
    private String ident;

    private PersonDTO person;

    private String status;
    private String melding;
    private String utfyllendeMelding;

    @JsonIgnore
    public boolean isOk() {

        return "OK".equals(status);
    }
}