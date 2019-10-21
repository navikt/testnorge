package no.nav.registre.tss.consumer.rs.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class EregMapperRequest {

    private String orgnr;
    private Navn navn;
    @Builder.Default
    private String enhetstype = "BEDR";
    private List<Knytning> knytninger;

}
