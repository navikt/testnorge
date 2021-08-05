package no.nav.testnav.libs.domain.dto.arena.testnorge.tilleggsstoenad.reiseobligatorisksamling;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReiseObligatoriskSamling {

    @JsonAlias({ "KODE", "kode" })
    private ObligatoriskSamlingKoder kode;

    @JsonAlias({ "OVERORDNET", "overordnet" })
    private ObligatoriskSamlingOvKoder overordnet;

    @JsonAlias({ "VERDI", "verdi" })
    private String verdi;
}
