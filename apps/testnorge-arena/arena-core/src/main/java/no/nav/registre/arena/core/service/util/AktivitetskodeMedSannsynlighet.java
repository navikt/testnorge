package no.nav.registre.arena.core.service.util;

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
public class AktivitetskodeMedSannsynlighet {

    private String aktivitetkode;
    private Integer sannsynlighet;
}
