package no.nav.registre.frikort.provider.rs.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyntetiserFrikortResponse {

    private LeggPaaKoeStatus lagtPaaKoe;
    private String xml;
}
