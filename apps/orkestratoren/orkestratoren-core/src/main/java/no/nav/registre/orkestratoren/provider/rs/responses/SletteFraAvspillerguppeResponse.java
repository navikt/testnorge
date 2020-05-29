package no.nav.registre.orkestratoren.provider.rs.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SletteFraAvspillerguppeResponse {

    private List<Long> slettedeMeldingIderFraTpsf;
}
