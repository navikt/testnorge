package no.nav.dolly.domain.resultset.tpsf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * Medlemsvariabelen status er (value) lagringsstatus for skdmeldingen per (key) miljø.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendSkdMeldingTilTpsResponse {

    private String personId;
    private String skdmeldingstype;
    private Map<String, String> status; //Map<Environment, TPS respons statusmelding >
}
