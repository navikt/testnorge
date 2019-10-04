package no.nav.dolly.domain.resultset.tpsf.adresse;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonTypeName("Gateadresse")
@NoArgsConstructor
@AllArgsConstructor
public class RsGateadresse extends RsAdresse {

    private String gateadresse;

    private String husnummer;

    private String gatekode;

}
