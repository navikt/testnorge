package no.nav.registre.frikort.provider.rs.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import no.nav.registre.frikort.consumer.rs.response.SyntFrikortResponse;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EgenandelRequest {

    private String ident;
    private List<SyntFrikortResponse> egenandeler;
}
