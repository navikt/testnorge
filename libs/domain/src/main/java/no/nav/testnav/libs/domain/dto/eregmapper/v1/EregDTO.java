package no.nav.testnav.libs.domain.dto.eregmapper.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EregDTO {

    @JsonProperty(required = true)
    private String orgnr;
    @JsonProperty
    private NavnDTO navn;
    @JsonProperty(required = true)
    private String enhetstype;
    @JsonProperty
    private String endringsType = "N";
    @JsonProperty
    private AdresseDTO adresse;
    @JsonProperty
    private AdresseDTO forretningsAdresse;
    @JsonProperty
    private String epost;
    @JsonProperty
    private String internetAdresse;
    @JsonProperty
    private List<String> frivilligRegistreringerMVA;
    @JsonProperty
    private Boolean harAnsatte;
    @JsonProperty
    private String sektorKode;
    @JsonProperty
    private String stiftelsesDato;
    @JsonProperty
    private TelefonDTO telefon;
    @JsonProperty
    private String frivillighetsKode;
    @JsonProperty
    private String nedleggelsesDato;
    @JsonProperty
    private String eierskapskifteDato;
    @JsonProperty
    private String oppstartsDato;
    @JsonProperty("målform")
    private Maalform maalform;
    @JsonProperty
    private Boolean utelukkendeVirksomhetINorge;
    @JsonProperty
    private Boolean heleidINorge;
    @JsonProperty
    private Boolean fravalgAvRevisjonen;
    @JsonProperty
    private UtenlandsRegisterDTO utenlandsRegister;
    @JsonProperty
    private Map<String, String> statuser;
    @JsonProperty("kjønsfordeling")
    private Boolean kjoensfordeling;
    @JsonProperty
    private UnderlagtHjemlandDTO underlagtHjemland;
    @JsonProperty
    private KapitalDTO kapital;
    @JsonProperty("næringskode")
    private NaeringskodeDTO naeringskode;
    @JsonProperty("formål")
    private String formaal;
    @JsonProperty
    private List<KnytningDTO> knytninger;
}
