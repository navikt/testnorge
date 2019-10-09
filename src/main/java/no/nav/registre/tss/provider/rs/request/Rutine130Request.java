package no.nav.registre.tss.provider.rs.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Rutine130Request {

    @NonNull
    private String idKode;

    @NonNull
    private String avdelingnr;

    @NonNull
    private String kodeAdressetype;

    private String kodeLand;
    private String kommunenr;
    private String postnr;
    private String datoAdresseFom;
    private String datoAdresseTom;
    private String gyldigAdresse;
}
