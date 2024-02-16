package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLInntektsmeldingM;

import java.util.List;

@Data
@NoArgsConstructor
public class RsXml201812Response {

    private List<XMLInntektsmeldingM> meldinger;
}
