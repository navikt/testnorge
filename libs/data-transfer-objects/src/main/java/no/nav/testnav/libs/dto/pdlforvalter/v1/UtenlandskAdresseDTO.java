package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.stream.Stream;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UtenlandskAdresseDTO implements Serializable {

    private String adressenavnNummer;
    private String boenhet;
    private String bySted;
    private String bygning;
    private String bygningEtasjeLeilighet;
    private String distriktsnavn;
    private String etasjenummer;
    private String landkode;
    private String postboksNummerNavn;
    private String postkode;
    private String region;
    private String regionDistriktOmraade;

    public boolean isEmpty() {

        return Stream.of(adressenavnNummer,
                        boenhet,
                        bySted,
                        bygning,
                        bygningEtasjeLeilighet,
                        distriktsnavn,
                        etasjenummer,
                        landkode,
                        postboksNummerNavn,
                        postkode,
                        region,
                        regionDistriktOmraade)
                .allMatch(StringUtils::isBlank);
    }
}