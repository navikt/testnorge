package no.nav.registre.ereg.provider.rs.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class EregDataRequest {

    @NonNull
    private String orgId;
    @NonNull
    private String navn;
    @NonNull
    private String type;

    @NonNull
    private String endringsType;

    @JsonProperty("adresse")
    private Adresse adresse;
    @JsonProperty("forretningsAdresse")
    private Adresse forretningsAdresse;
    private String epost;
    private String internetAdresse;

    List<String> frivilligRegistreringerMVA;
    private Boolean harAnsatte;
    private String sektorKode;

    private String stiftelsesDato;

    private Telefon telefon;

    private String frivillighetsKode;

    private String nedleggelsesDato;
    private String eierskapskifteDato;
    private String oppstartsDato;
    @JsonProperty("målform")
    private Maalform maalform;
    private Boolean utelukkendeVirksomhetINorge;
    private Boolean heleidINorge;
    private Boolean fravalgAvRevisjonen;

    private UtenlandsRegister utenlandsRegister;

    private Map<String, String> statuser;
    @JsonProperty("kjønsfordeling")
    private Boolean kjoensfordeling;
    private UnderlagtHjemland underlagtHjemland;
    private Kapital kapital;
    @JsonProperty("næringskode")
    private Naeringskode naeringskode;
    @JsonProperty("formål")
    private String formaal;
}
