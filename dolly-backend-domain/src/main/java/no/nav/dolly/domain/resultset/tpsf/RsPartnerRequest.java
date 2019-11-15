package no.nav.dolly.domain.resultset.tpsf;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsPartnerRequest extends RsRelasjon{

    private Integer partnerNr;
    private List<RsSivilstandRequest> sivilstander;
    private Boolean harFellesAdresse;
}