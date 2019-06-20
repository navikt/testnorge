package no.nav.registre.orkestratoren.provider.rs.responses;

import java.util.List;

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
public class SletteFraAvspillerguppeResponse {

    private List<Long> slettedeMeldingIderFraTpsf;
}
