package no.nav.testnav.libs.dto.skattekortservice.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Skattekortmelding {


    private String arbeidstakeridentifikator;
    private Resultatstatus resultatPaaForespoersel;
    private Skattekort skattekort;
    private List<Tilleggsopplysning> tilleggsopplysning;
    private Integer inntektsaar;

    public List<Tilleggsopplysning> getTilleggsopplysning() {

        if (isNull(tilleggsopplysning)) {
            tilleggsopplysning = new ArrayList<>();
        }
        return tilleggsopplysning;
    }

    @JsonIgnore
    public boolean isEmptyArbeidstakeridentifikator() {

        return isBlank(arbeidstakeridentifikator);
    }

    @JsonIgnore
    public boolean isEmptyInntektsaar() {

        return isNull(inntektsaar);
    }
}