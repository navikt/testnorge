package no.nav.udistub.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class UdiPersonNavn {
    private String fornavn;
    private String mellomnavn;
    private String etternavn;
}
