package no.nav.dolly.domain.resultset.tpsf;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsSimpleRelasjoner {

    private RsRelasjon partner;

    private List<RsRelasjon> barn;
}
