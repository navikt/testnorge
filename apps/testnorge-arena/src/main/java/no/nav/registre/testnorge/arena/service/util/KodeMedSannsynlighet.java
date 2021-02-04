package no.nav.registre.testnorge.arena.service.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KodeMedSannsynlighet {

    private String kode;
    private Integer sannsynlighet;
}
