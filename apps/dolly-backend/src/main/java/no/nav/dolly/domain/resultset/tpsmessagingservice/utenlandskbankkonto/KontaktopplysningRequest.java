package no.nav.dolly.domain.resultset.tpsmessagingservice.utenlandskbankkonto;

import java.util.List;

public record KontaktopplysningRequest(String ident,
                                       List<String> miljoer,
                                       KontaktOpplysninger body
) {
}
