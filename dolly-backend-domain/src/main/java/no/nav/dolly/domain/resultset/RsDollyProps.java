package no.nav.dolly.domain.resultset;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class RsDollyProps {

    private String tpsfUrl;
    private String sigrunStubUrl;
    private String krrStubUrl;
    private String udiStubUrl;
    private String kodeverkUrl;
    private String arenaForvalterUrl;
    private String instdataUrl;
}
