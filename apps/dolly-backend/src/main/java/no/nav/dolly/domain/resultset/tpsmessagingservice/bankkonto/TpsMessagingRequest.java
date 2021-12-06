package no.nav.dolly.domain.resultset.tpsmessagingservice.bankkonto;

import java.util.List;

public record TpsMessagingRequest(String ident,
                                  List<String> miljoer,
                                  Object body
) {
}
