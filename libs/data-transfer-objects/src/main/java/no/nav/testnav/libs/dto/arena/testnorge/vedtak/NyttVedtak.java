package no.nav.testnav.libs.dto.arena.testnorge.vedtak;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@JsonSubTypes({
        @JsonSubTypes.Type(value = NyttVedtakAap.class),
        @JsonSubTypes.Type(value = NyttVedtakTiltak.class),
        @JsonSubTypes.Type(value = NyttVedtakTillegg.class)
})

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NyttVedtak implements Serializable {

    @JsonAlias({ "AVBRUDDSKODE", "AVBRUDD_KODE", "avbruddKode" })
    private String avbruddKode;

    @JsonAlias({ "BEGRUNNELSE", "begrunnelse" })
    private String begrunnelse;

    @JsonAlias({ "BESLUTTER", "beslutter" })
    private String beslutter;

    @JsonAlias({ "DATO_MOTTATT", "datoMottatt" })
    private LocalDate datoMottatt;

    @JsonAlias({ "FRA_DATO", "DATO_FRA", "fraDato" })
    private LocalDate fraDato;

    @JsonAlias({ "SAKSBEHANDLER", "saksbehandler" })
    private String saksbehandler;

    @JsonAlias({ "TIL_DATO", "DATO_TIL", "tilDato" })
    private LocalDate tilDato;

    @JsonAlias({ "UTFALL", "utfall" })
    private String utfall;

    @JsonAlias({ "VILKAAR", "vilkaar" })
    private List<Vilkaar> vilkaar;

    @JsonAlias({ "VEDTAKTYPE", "vedtaktype" })
    private String vedtaktype;

    @JsonIgnore
    public RettighetType getRettighetType() {
        return RettighetType.UKJENT;
    }
}
