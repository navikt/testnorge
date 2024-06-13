package no.nav.skattekortservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SokosRequest {

    private String fnr;
    private String inntektsar;
    private String skattekort;
}