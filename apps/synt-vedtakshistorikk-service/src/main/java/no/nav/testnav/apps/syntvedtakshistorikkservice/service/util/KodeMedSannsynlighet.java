package no.nav.testnav.apps.syntvedtakshistorikkservice.service.util;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KodeMedSannsynlighet {

    private String kode;
    private Integer sannsynlighet;
}
