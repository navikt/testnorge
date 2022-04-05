package no.nav.dolly.bestilling.pdlforvalter.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import no.nav.testnav.libs.dto.pdlforvalter.v1.deserialization.OppholdAnnetStedEnumDeserializer;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PdlOppholdsadresse extends PdlAdresse {

    @JsonDeserialize(using = OppholdAnnetStedEnumDeserializer.class)
    private OppholdAnnetSted oppholdAnnetSted;
    private UtenlandskAdresse utenlandskAdresse;
    private PdlMatrikkeladresse matrikkeladresse;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UtenlandskAdresse {

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
    }
}
