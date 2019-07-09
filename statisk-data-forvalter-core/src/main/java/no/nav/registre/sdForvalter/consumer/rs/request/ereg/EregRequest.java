package no.nav.registre.sdForvalter.consumer.rs.request.ereg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EregRequest {

    @NotNull
    private String orgnr;

    private Navn navn;

    @NotNull
    private String enhetstype;

    private String epost;
    private String internetAdresse;

}
