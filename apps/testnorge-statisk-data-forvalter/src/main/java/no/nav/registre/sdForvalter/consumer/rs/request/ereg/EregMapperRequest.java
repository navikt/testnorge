package no.nav.registre.sdForvalter.consumer.rs.request.ereg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.util.Strings;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.sdForvalter.domain.Ereg;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EregMapperRequest {


    @NotNull
    private String orgnr;

    private Navn navn;

    @NotNull
    private String enhetstype;
    private String endringsType = "N";

    private String epost;
    private String internetAdresse;

    private Adresse forretningsAdresse;
    private Adresse adresse;
    private List<Map<String, String>> knytninger;

    public EregMapperRequest(Ereg model, boolean update) {
        enhetstype = model.getEnhetstype();
        epost = model.getEpost();
        internetAdresse = model.getInternetAdresse();
        if (Strings.isNotBlank(model.getNavn())) {
            navn = Navn
                    .builder()
                    .redNavn(model.getRedigertNavn())
                    .navneListe(Collections.singletonList(model.getNavn()))
                    .build();
        }
        endringsType = update ? "E" : "N";
        orgnr = model.getOrgnr();
        if (model.getJuridiskEnhet() != null) {
            String type = new StringBuilder("    NSSY").replace(0, model.getEnhetstype().length(), model.getEnhetstype()).toString();
            knytninger = Collections.singletonList(new HashMap<>() {{
                put("orgnr", model.getJuridiskEnhet());
                put("type", type);
            }});
        }
        if (model.getForretningsAdresse() != null) {
            forretningsAdresse = new Adresse(model.getForretningsAdresse());
        }

        if (model.getPostadresse() != null) {
            adresse = new Adresse(model.getPostadresse());
        }
    }
}
