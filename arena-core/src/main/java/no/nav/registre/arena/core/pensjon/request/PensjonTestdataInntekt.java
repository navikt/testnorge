package no.nav.registre.arena.core.pensjon.request;

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

    private int belop;
    private String fnr;
    private int fomAar;
    private List<String> miljoer;
    private boolean redusertMedGrunnbelop;
    private int tomAar;
}
