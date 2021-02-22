package no.nav.registre.testnorge.domain.dto.arena.testnorge.request;

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
public class SyntRequest {

    private String fraDato;

    private String tilDato;

    private String utfall;

    private String vedtakDato;

    private String vedtakTypeKode;
}