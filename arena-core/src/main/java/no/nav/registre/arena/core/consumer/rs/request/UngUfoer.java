package no.nav.registre.arena.core.consumer.rs.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UngUfoer {

    private String begrunnelse;
    private LocalDate fraDato;
    private LocalDate datoMottatt;
    private String vedtaktype;
    private List<Vilkaar> vilkaar;
    private String utfall;
}
