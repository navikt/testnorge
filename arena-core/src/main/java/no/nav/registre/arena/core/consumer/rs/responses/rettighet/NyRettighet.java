package no.nav.registre.arena.core.consumer.rs.responses.rettighet;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NyRettighet {

    @JsonAlias({ "AVBRUDDSKODE", "avbruddKode" })
    private String avbruddKode;

    @JsonAlias({ "BEGRUNNELSE", "begrunnelse" })
    private String begrunnelse;

    @JsonAlias({ "BESLUTTER", "beslutter" })
    private String beslutter;

    @JsonAlias({ "DATO_MOTTATT", "datoMottatt" })
    private LocalDate datoMottatt;

    @JsonAlias({ "FODSELSNR", "fodselsnr" })
    private String fodselsnr;

    @JsonAlias({ "FRA_DATO", "fraDato" })
    private LocalDate fraDato;

    @JsonAlias({ "GJELDENDE_KONTONR", "gjeldendeKontonr" })
    private List<Object> gjeldendeKontonr;

    @JsonAlias({ "MILJOE", "miljoe" })
    private String miljoe;

    @JsonAlias({ "SAKSBEHANDLER", "saksbehandler" })
    private String saksbehandler;

    @JsonAlias({ "TIL_DATO", "tilDato" })
    private LocalDate tilDato;

    @JsonAlias({ "UTBETALINGSADRESSE", "utbetalingsadresse" })
    private List<Object> utbetalingsadresse;

    @JsonAlias({ "UTFALL", "utfall" })
    private String utfall;

    @JsonAlias({ "UTSKRIFT", "utskrift" })
    private String utskrift;

    @JsonAlias({ "VEDTAKTYPE", "vedtaktype" })
    private String vedtaktype;

    @JsonAlias({ "VILKAAR", "vilkaar" })
    private List<Vilkaar> vilkaar;
}
