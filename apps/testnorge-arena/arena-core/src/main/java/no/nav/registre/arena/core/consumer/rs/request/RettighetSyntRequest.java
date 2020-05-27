package no.nav.registre.arena.core.consumer.rs.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RettighetSyntRequest {

    private LocalDate fraDato;

    private LocalDate tilDato;

    private String utfall;

    private LocalDate vedtakDato;

    private String vedtakTypeKode;
}
