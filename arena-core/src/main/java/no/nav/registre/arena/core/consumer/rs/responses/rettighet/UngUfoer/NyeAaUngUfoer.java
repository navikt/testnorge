package no.nav.registre.arena.core.consumer.rs.responses.rettighet.UngUfoer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

import no.nav.registre.arena.core.consumer.rs.request.Vilkaar;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NyeAaUngUfoer {

    private String avbruddKode;
    private String begrunnelse;
    private String beslutter;
    private LocalDate datoMottatt;
    private String fodselsnr;
    private LocalDate fraDato;
    private String miljoe;
    private String saksbehandler;
    private LocalDate tilDato;
    private String utfall;
    private String utskrift;
    private String vedtaktype;
    private List<Vilkaar> vilkaar;
}
