package no.nav.registre.sdForvalter.consumer.rs.request.ereg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

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

    private List<Map<String, String>> knytninger;

}
