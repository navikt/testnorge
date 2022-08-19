package no.nav.testnav.apps.tpservice.consumer.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LagreTpYtelseRequest {

    private List<String> miljoer;

    private String fnr;

    private String ordning;
    private TpYtelseType ytelseType;
    private LocalDate datoInnmeldtYtelseFom;
    private LocalDate datoYtelseIverksattFom;
    private LocalDate datoYtelseIverksattTom;

    public enum TpYtelseType {
        ALDER,
        UFORE,
        GJENLEVENDE,
        BARN,
        AFP,
        UKJENT
    }
}
