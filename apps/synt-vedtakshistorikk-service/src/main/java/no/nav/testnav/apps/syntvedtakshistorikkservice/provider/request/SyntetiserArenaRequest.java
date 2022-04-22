package no.nav.testnav.apps.syntvedtakshistorikkservice.provider.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SyntetiserArenaRequest {

    private String miljoe;
    private Integer antallNyeIdenter;
}