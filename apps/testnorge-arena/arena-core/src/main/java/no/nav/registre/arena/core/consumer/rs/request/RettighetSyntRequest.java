package no.nav.registre.arena.core.consumer.rs.request;

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
public class RettighetSyntRequest {

    private String fraDato;

    private String tilDato;

    private String utfall;

    private String vedtakDato;

    private String vedtakTypeKode;
}
