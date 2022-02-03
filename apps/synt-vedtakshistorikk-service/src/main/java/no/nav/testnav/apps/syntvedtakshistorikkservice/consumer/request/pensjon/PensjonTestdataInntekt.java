package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.pensjon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PensjonTestdataInntekt {

    private Integer belop;
    private String fnr;
    private Integer fomAar;
    private List<String> miljoer;
    private Boolean redusertMedGrunnbelop;
    private Integer tomAar;
}
