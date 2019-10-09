package no.nav.registre.tss.consumer.rs.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
@Builder
@Getter
@AllArgsConstructor
public class Knytning {

    @Builder.Default
    private String type = "BEDRNSSY";
    @Builder.Default
    private String ansvarsandel = "";
    @Builder.Default
    private String fratreden = "";
    @NonNull
    private String orgnr;
    @Builder.Default
    private String valgtAv = "";
    @Builder.Default
    private String korrektOrgNr = "";
}
