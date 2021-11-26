package no.nav.dolly.domain.resultset;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsDollyProps {

    private String tpsfUrl;
    private String sigrunStubUrl;
    private String krrStubUrl;
    private String udiStubUrl;
    private String kodeverkUrl;
    private String arenaForvalterUrl;
    private String instdataUrl;
    private String aaregdataUrl;
    private String inntektstub;
}
