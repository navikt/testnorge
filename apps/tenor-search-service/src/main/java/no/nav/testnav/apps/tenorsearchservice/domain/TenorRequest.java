package no.nav.testnav.apps.tenorsearchservice.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
public class TenorRequest {

    public enum IdentifikatorType {FNR, DNR, FNR_TIDLIGERE_DNR}
    public enum Kjoenn {Mann, Kvinne}
    public enum Roller {DAGLIG_LEDER}
    public enum Personstatus {Bosatt, Doed, Forsvunnet, Foedselsregistrert, IkkeBosatt, Inaktiv, Midlertidig, Opphørt, Utflyttet}
    public enum Sivilstand {EnkeEllerEnkemann, Gift, GjenlevendePartner, RegistrertPartner, Separert, SeparertPartner, Skilt, SkiltPartner, Ugift, Uoppgitt}

    private List<Roller> roller;

    private IdentifikatorType identifikatorType;
    private DatoIntervall foedselsdato;
    private DatoIntervall doedsdato;
    private Kjoenn kjoenn;
    public Personstatus personstatus;
    public Sivilstand sivilstand;

    public List<Roller> getRoller() {

        if (isNull(roller)) {
            roller = new ArrayList<>();
        }
        return roller;
    }

    private String adresseNavn;

    @Data
    @NoArgsConstructor
    public static class DatoIntervall {

        private LocalDate fra;
        private LocalDate til;
    }
}
