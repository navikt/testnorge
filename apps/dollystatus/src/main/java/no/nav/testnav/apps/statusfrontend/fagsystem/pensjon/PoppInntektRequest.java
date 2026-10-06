package no.nav.testnav.apps.statusfrontend.fagsystem.pensjon;

import java.util.List;

public record PoppInntektRequest(
        String fnr,
        Integer tomAar,
        Integer fomAar,
        Integer belop,
        Boolean redusertMedGrunnbelop,
        List<String> miljoer
) {
}
