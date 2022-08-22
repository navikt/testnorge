package no.nav.registre.testnorge.personsearchservice.domain;

import lombok.Value;

@Value
public class PdlResponse {
    Long numberOfItems;
    String response;
}
