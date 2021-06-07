package no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLInntektsmeldingM;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsXml201812Response {
    private List<XMLInntektsmeldingM> meldinger;
}
